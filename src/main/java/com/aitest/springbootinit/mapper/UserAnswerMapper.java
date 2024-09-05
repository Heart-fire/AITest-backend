package com.aitest.springbootinit.mapper;

import com.aitest.springbootinit.model.dto.app.AppAnswerCountDTO;
import com.aitest.springbootinit.model.dto.app.AppAnswerResultCountDTO;
import com.aitest.springbootinit.model.entity.UserAnswer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Fen91
 * @description 针对表【user_answer(用户答题记录)】的数据库操作Mapper
 * @createDate 2024-08-28 16:27:01
 * @Entity com.aitest.springbootinit.model.entity.UserAnswer
 */
public interface UserAnswerMapper extends BaseMapper<UserAnswer> {
    /**
     *  热门应用统计
     * @return
     */
    List<AppAnswerCountDTO> doAppAnswerCount();

    /**
     *  应用回答统计
     */
    @Select("select resultName, count(resultName) as resultCount from user_answer\n" +
            "        where appId = #{appId} group by resultName order by resultCount desc\n")
    List<AppAnswerResultCountDTO> doAppAnswerResultCount(Long appId);
}




