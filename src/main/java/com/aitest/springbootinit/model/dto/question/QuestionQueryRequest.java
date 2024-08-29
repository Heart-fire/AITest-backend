package com.aitest.springbootinit.model.dto.question;

import com.aitest.springbootinit.common.PageRequest;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询题目表请求
 *
 *
 * @from 
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 题目内容（json格式）
     */
    private String questionContent;

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * id
     */
    private Long notId;

    /**
     * 搜索词
     */
    private String searchText;

    private static final long serialVersionUID = 1L;
}