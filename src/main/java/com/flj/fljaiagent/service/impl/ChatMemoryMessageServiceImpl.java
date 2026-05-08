package com.flj.fljaiagent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flj.fljaiagent.entity.ChatMemoryMessage;
import com.flj.fljaiagent.mapper.ChatMemoryMessageMapper;
import com.flj.fljaiagent.service.ChatMemoryMessageService;
import org.springframework.stereotype.Service;

/**
* @author HP
* @description 针对表【chat_memory_message】的数据库操作Service实现
* @createDate 2026-05-06 15:33:01
*/
@Service
public class ChatMemoryMessageServiceImpl extends ServiceImpl<ChatMemoryMessageMapper, ChatMemoryMessage>
    implements ChatMemoryMessageService{

}




