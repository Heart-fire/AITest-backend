package com.aitest.springbootinit.model.dto.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建题目表请求
 *
 *
 * @from 
 */
@Data
public class QuestionAddRequest implements Serializable {

    /**
     * 题目内容（json格式）
     */
    private QuestionContentDTO questionContent;

    /**
     * 应用 id
     */
    private Long appId;


    private static final long serialVersionUID = 1L;
}