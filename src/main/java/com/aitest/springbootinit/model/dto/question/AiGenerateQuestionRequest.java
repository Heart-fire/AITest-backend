package com.aitest.springbootinit.model.dto.question;

import lombok.Data;

import java.io.Serializable;

@Data
public class AiGenerateQuestionRequest implements Serializable {

    /**
     * id
     */
    private Long appId;

    /**
     * 题目数
     */
    int questionNumber;

    /**
     * 选项数
     */
    int optionNumber;

    private static final long serialVersionUID = 1L;
}
