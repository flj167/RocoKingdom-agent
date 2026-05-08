package com.flj.fljaiagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flj.fljaiagent.entity.ChatMemoryMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMemoryMessageMapper extends BaseMapper<ChatMemoryMessage> {
}