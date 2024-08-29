package com.aitest.springbootinit.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建应用表请求，前端需要传递的参数
 * 有些APP实体类是存数据库的，和前端传的字段不一样
 */
@Data
public class AppAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用名
     */
    private String appName;

    /**
     * 应用描述
     */
    private String appDesc;

    /**
     * 应用图标
     */
    private String appIcon;

    /**
     * 应用类型（0-得分类，1-测评类）
     */
    private Integer appType;

    /**
     * 评分策略（0-自定义，1-AI）
     */
    private Integer scoringStrategy;


}