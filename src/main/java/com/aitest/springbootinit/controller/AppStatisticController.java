package com.aitest.springbootinit.controller;

import com.aitest.springbootinit.common.BaseResponse;
import com.aitest.springbootinit.common.ErrorCode;
import com.aitest.springbootinit.common.ResultUtils;
import com.aitest.springbootinit.exception.ThrowUtils;
import com.aitest.springbootinit.mapper.UserAnswerMapper;
import com.aitest.springbootinit.model.dto.app.AppAnswerCountDTO;
import com.aitest.springbootinit.model.dto.app.AppAnswerResultCountDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/app/statistic")
@Slf4j
public class AppStatisticController {

    @Resource
    private UserAnswerMapper userAnswerMapper;

    /**
     * 热门应用统计
     *
     * @return
     */
    @GetMapping("/answer_count")
    public BaseResponse<List<AppAnswerCountDTO>> getAppAnswerCount() {
        return ResultUtils.success(userAnswerMapper.doAppAnswerCount());
    }

    @GetMapping("/answer_result_count")
    public BaseResponse<List<AppAnswerResultCountDTO>> getAppAnswerResultCount(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userAnswerMapper.doAppAnswerResultCount(appId));
    }

}
