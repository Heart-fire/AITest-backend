package com.aitest.springbootinit.scoring;

import com.aitest.springbootinit.common.ErrorCode;
import com.aitest.springbootinit.exception.BusinessException;
import com.aitest.springbootinit.model.entity.App;
import com.aitest.springbootinit.model.entity.UserAnswer;
import com.aitest.springbootinit.model.enums.AppScoringStrategyEnum;
import com.aitest.springbootinit.model.enums.AppTypeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Deprecated
public class ScoringStrategyContext {
    @Resource
    private CustomScoreScoringStrategy customScoreScoringStrategy;
    @Resource
    private CustomTestScoringStrategy customTestScoringStrategy;

    /**
     * 评分
     * @param choiceList
     * @param app
     * @return
     * @throws Exception
     */
    public UserAnswer doScore(List<String> choiceList, App app) throws Exception{
        // 应用类型（0-得分类，1-测评类）
        AppTypeEnum appTypeEnum = AppTypeEnum.getEnumByValue(app.getAppType());
        // 评分策略（0-自定义，1-AI）
        AppScoringStrategyEnum appScoringStrategyEnum = AppScoringStrategyEnum.getEnumByValue(app.getScoringStrategy());
        if (appTypeEnum == null || appScoringStrategyEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
        }
        // 根据不同的应用类别和评分策略，选择对应的策略执行
        switch (appTypeEnum){
            case SCORE:
                switch (appScoringStrategyEnum){
                    case CUSTOM:
                        return customScoreScoringStrategy.doScore(choiceList, app);
                    case AI:
                        break;
                }
                break;
            case TEST:
                switch (appScoringStrategyEnum){
                    case CUSTOM:
                        return customTestScoringStrategy.doScore(choiceList, app);
                    case AI:
                        break;
                }
                break;
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR,"应用配置有误，未找到匹配的策略");
    }
}
