package com.aitest.springbootinit.model.dto.app;

import lombok.Data;

/**
 * 应用回答分布统计
 */
@Data
public class AppAnswerResultCountDTO {
    // 结果名称
    private String resultName;
    // 对应个数
    private String resultCount;
}
