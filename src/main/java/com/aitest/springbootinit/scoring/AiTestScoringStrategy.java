package com.aitest.springbootinit.scoring;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.aitest.springbootinit.manager.AiManager;
import com.aitest.springbootinit.model.dto.question.QuestionAnswerDTO;
import com.aitest.springbootinit.model.dto.question.QuestionContentDTO;
import com.aitest.springbootinit.model.entity.App;
import com.aitest.springbootinit.model.entity.Question;
import com.aitest.springbootinit.model.entity.UserAnswer;
import com.aitest.springbootinit.model.vo.QuestionVO;
import com.aitest.springbootinit.service.QuestionService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ScoringStrategyConfig(appType = 1, scoringStrategy = 1)
public class AiTestScoringStrategy implements ScoringStrategy {
    @Resource
    private QuestionService questionService;

    @Resource
    private AiManager aiManager;

    // 结果图标
    String aaa = "https://xinhuo-store.oss-cn-hangzhou.aliyuncs.com/AI%E7%AD%94%E9%A2%98%E5%9B%BE%E7%89%87%E5%BA%93/%E6%B5%81%E7%A8%8B3.png";

//    @Resource
//    private RedissonClient redissonClient;

//    private static final String AI_ANSWER_LOCK = "AI_ANSWER_LOCK";


    // 创建本地缓存,只限于在这个功能内，让AI秒答-{初始容量、过期策略、最大容量}
    // redis缓存，容易被攻击，还贵，项目不考虑分布式和扩容
    private final Cache<String, String> answerCacheMap = Caffeine.newBuilder().initialCapacity(1024)
            // 缓存5分钟移除
            .expireAfterAccess(5L, TimeUnit.MINUTES)
            .build();

    /**
     * AI 评分系统消息
     */
    private static final String AI_TEST_SCORING_SYSTEM_MESSAGE = "你是一位专业严谨的判题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "题目和用户回答的列表：格式为 [{\"title\": \"题目\",\"answer\": \"用户回答\"}]\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1. 要求：需要给出一个明确的评价结果，包括评价名称（尽量简短）和评价描述（尽量详细，大于 200 字）\n" +
            "2. 严格按照下面的 json 格式输出评价名称和评价描述\n" +
            "```\n" +
            "{\"resultName\": \"评价名称\", \"resultDesc\": \"评价描述\"}\n" +
            "```\n" +
            "3. 返回格式必须为 JSON 对象";

    /**
     * AI 评分用户消息封装
     *
     * @param app
     * @param questionContentDTOList
     * @param choices
     * @return
     */
    private String getAiTestScoringUserMessage(App app, List<QuestionContentDTO> questionContentDTOList, List<String> choices) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        List<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();
        for (int i = 0; i < questionContentDTOList.size(); i++) {
            QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO();
            questionAnswerDTO.setTitle(questionContentDTOList.get(i).getTitle());
            questionAnswerDTO.setUserAnswer(choices.get(i));
            questionAnswerDTOList.add(questionAnswerDTO);
        }
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
        return userMessage.toString();
    }

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Long appId = app.getId();
        String jsonStr = JSONUtil.toJsonStr(choices);
        String cacheKey = buildCacheKey(appId, jsonStr);
        String answerJson = answerCacheMap.getIfPresent(cacheKey);
        // 命中缓存则直接返回结果
        if (StrUtil.isNotBlank(answerJson)) {
            UserAnswer userAnswer = JSONUtil.toBean(answerJson, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(jsonStr);
            return userAnswer;
        }
        // 定义锁
        // 可以避免多个请求同时调用 AI 服务
//        RLock lock = redissonClient.getLock(AI_ANSWER_LOCK + cacheKey);

//        try {
//            // 竞争分布式锁，等待3秒，15秒自动释放
//            boolean res = lock.tryLock(3, 15, TimeUnit.SECONDS);
//            if (!res){
//                // 只有抢到锁的业务才能执行 AI 调用
//                return null;
//            }
        // 1. 根据 id 查询到题目
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();
        // 2. 调用 AI 获取结果
        // 封装 Prompt
        String userMessage = getAiTestScoringUserMessage(app, questionContent, choices);
        // AI 生成
        String result = aiManager.doSyncStableRequest(AI_TEST_SCORING_SYSTEM_MESSAGE, userMessage);
        // 结果处理
        int start = result.indexOf("{");
        int end = result.lastIndexOf("}");
        String json = result.substring(start, end + 1);

        // 1. 构造答案对象并设置所有属性
        UserAnswer userAnswer = JSONUtil.toBean(json, UserAnswer.class);
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultPicture(aaa);
        // 2. 将完整的 UserAnswer 对象缓存到 answerCacheMap
        answerCacheMap.put(cacheKey, JSONUtil.toJsonStr(userAnswer));
        // 3. 直接返回答案对象
        return userAnswer;

//        }
//        finally {
//            // 锁不为空且必须是被锁状态，必须本人释放
//            if (lock != null && lock.isLocked()){
//                // 判断锁 是否 是当前线程的
//                if (lock.isHeldByCurrentThread()){
//                    lock.unlock();
//                }
//            }
//        }
    }

    /**
     * 构建缓存 Key
     *
     * @param appId
     * @param choicesStr
     * @return 用Hutool工具类,
     */
    // MD5 可以将任意长度的输入数据转换为一个固定长度（通常是 32 字符）的字符串。
    // 这样可以减少缓存键的长度，节省存储空间，同时保证键的唯一性和不可预测性。
    private String buildCacheKey(Long appId, String choicesStr) {
        return DigestUtil.md5Hex(appId + ":" + choicesStr);
    }
}
