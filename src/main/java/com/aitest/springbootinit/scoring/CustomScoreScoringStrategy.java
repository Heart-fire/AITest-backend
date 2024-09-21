package com.aitest.springbootinit.scoring;

import cn.hutool.json.JSONUtil;
import com.aitest.springbootinit.model.dto.question.QuestionContentDTO;
import com.aitest.springbootinit.model.entity.App;
import com.aitest.springbootinit.model.entity.Question;
import com.aitest.springbootinit.model.entity.ScoringResult;
import com.aitest.springbootinit.model.entity.UserAnswer;
import com.aitest.springbootinit.model.vo.QuestionVO;
import com.aitest.springbootinit.service.QuestionService;
import com.aitest.springbootinit.service.ScoringResultService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * 自定义测评类应用评分策略
 */
@ScoringStrategyConfig(appType = 0,scoringStrategy = 0)
public class CustomScoreScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;   // 题目表服务

    @Resource
    private ScoringResultService scoringResultService;   // 评分结果表服务

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Long appId = app.getId();
        // 1.根据ID查询到 [题目] 和 [题目结果] 信息
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class)
                        .eq(ScoringResult::getAppId, appId)
                        .orderByDesc(ScoringResult::getResultScoreRange)
        );
        /*
        * 如果 result 是 "A"，并且 optionCount 之前没有记录过 "A"，那么会初始化optionCount.put("A", 0)。
        * 然后每次遇到 result 为 "A" 的选项，optionCount 中的 "A" 对应的值会加 1。
        * 最终，optionCount 会记录 "A" 出现了多少次，"B" 出现了多少次，依此类推。
        * */
        // 2. 统计用户的总得分
        // 调用题目视图的 [对象转封装] 类型,然后拿到数据库中题目内容(JSON格式)
        int totalScore = 0;
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();
        // 遍历题目列表,题目类型为[QuestionContentDTO]类型
        for (QuestionContentDTO questionContentDTO : questionContent) {
            // 遍历答案列表
            for (String answer : choices) {
                // 遍历题目中的选项
                for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                    // 如果答案和选项的key匹配
                    if (option.getKey().equals(answer)) {
                        int score = Optional.of(option.getScore()).orElse(0);
                        totalScore += score;
                    }
                }
            }
        }

        // 3. 遍历得分结果，找到第一个用户分数大于得分范围的结果，作为最终结果
        ScoringResult maxScoringResult = scoringResultList.get(0);
        for (ScoringResult scoringResult : scoringResultList) {
            if (totalScore >= scoringResult.getResultScoreRange()){
                maxScoringResult = scoringResult;
                break;
            }
        }
        // 4. 构造返回值，填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        userAnswer.setResultScore(totalScore);
        return userAnswer;
    }
}
