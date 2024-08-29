package com.aitest.springbootinit.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新题目表请求
 * 更新题目也一样只更新内容即可
 *
 * @from 
 */
@Data
public class QuestionUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 题目内容（json格式）
     */
    private QuestionContentDTO questionContent;

    private static final long serialVersionUID = 1L;
}