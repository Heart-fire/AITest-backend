package com.aitest.springbootinit.model.vo;

import cn.hutool.json.JSONUtil;
import com.aitest.springbootinit.model.entity.UserAnswer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户答题表视图
 *
 *
 * @from 
 */
@Data
public class UserAnswerVO implements Serializable {
    /**
     *
     */
    private Long id;

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 应用类型（0-得分类，1-角色测评类）
     */
    private Integer appType;

    /**
     * 评分策略（0-自定义，1-AI）
     */
    private Integer scoringStrategy;

    /**
     * 用户答案（JSON 数组）
     */
    private List<String> choices;

    /**
     * 评分结果 id
     */
    private Long resultId;

    /**
     * 结果名称，如物流师
     */
    private String resultName;

    /**
     * 结果描述
     */
    private String resultDesc;

    /**
     * 结果图标
     */
    private String resultPicture;

    /**
     * 得分
     */
    private Integer resultScore;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 封装类转对象
     * 将实体类中的 JSON 字符串解析回 VO 中的对象或集合，便于在前端展示。
     * @param userAnswerVO
     * @return
     */
    public static UserAnswer voToObj(UserAnswerVO userAnswerVO) {
        if (userAnswerVO == null) {
            return null;
        }
        UserAnswer userAnswer = new UserAnswer();
        BeanUtils.copyProperties(userAnswerVO, userAnswer);
        userAnswer.setChoices(JSONUtil.toJsonStr(userAnswerVO.getChoices()));
        return userAnswer;
    }

    /**
     * 对象转封装类
     * 将 VO 中的复杂结构转换为实体类中的简单字符串形式，便于在后端处理或存储
     * @param userAnswer
     * @return
     */
    public static UserAnswerVO objToVo(UserAnswer userAnswer) {
        if (userAnswer == null) {
            return null;
        }
        UserAnswerVO userAnswerVO = new UserAnswerVO();
        BeanUtils.copyProperties(userAnswer, userAnswerVO);
        userAnswerVO.setChoices(JSONUtil.toList(userAnswer.getChoices(), String.class));
        return userAnswerVO;
    }
}
