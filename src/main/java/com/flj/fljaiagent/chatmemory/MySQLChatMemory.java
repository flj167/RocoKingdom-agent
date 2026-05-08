package com.flj.fljaiagent.chatmemory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flj.fljaiagent.entity.ChatMemoryMessage;
import com.flj.fljaiagent.mapper.ChatMemoryMessageMapper;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 基于MySQL的对话存储
 */
@Component
public class MySQLChatMemory implements ChatMemory {

    private final ChatMemoryMessageMapper mapper;

    public MySQLChatMemory(ChatMemoryMessageMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void add(String conversationId, List<Message> messages) {
        if(messages==null || messages.isEmpty()){
            return;
        }
        //查询当前下一条对话的位置
        Long nextIndex=getNextIndex(conversationId);
        //批量插入message
        for(Message message:messages){
            ChatMemoryMessage entity = toEntity(conversationId, message, nextIndex++);
            mapper.insert(entity);
        }
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        if(lastN<=0){
            return List.of();
        }
        //返回最后N个
        List<ChatMemoryMessage> all = mapper.selectList(
                new LambdaQueryWrapper<ChatMemoryMessage>()
                        .eq(ChatMemoryMessage::getConversationId, conversationId)
                        .orderByAsc(ChatMemoryMessage::getMessageIndex)
        );

        if (all == null || all.isEmpty()) {
            return List.of();
        }

        return all.stream()
                .skip(Math.max(0, all.size() - lastN))
                .map(this::toMessage)
                .toList();
    }

    @Override
    public void clear(String conversationId) {
        mapper.delete(
                new LambdaQueryWrapper<ChatMemoryMessage>()
                        .eq(ChatMemoryMessage::getConversationId, conversationId)
        );
    }

    //计算下一条消息的序号
    private Long getNextIndex(String conversationId) {
        //查询到最新一条消息
        LambdaQueryWrapper<ChatMemoryMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMemoryMessage::getConversationId, conversationId)
                .orderByDesc(ChatMemoryMessage::getMessageIndex)
                .last("LIMIT 1");
        ChatMemoryMessage lastMessage = mapper.selectOne(queryWrapper);
        //返回下条消息的序号
        return lastMessage == null ? 0L : lastMessage.getMessageIndex() + 1;
    }
    //Spring AI的message-->>ChatMemoryMessage
    private ChatMemoryMessage toEntity(String conservationId, Message message, long index) {
        ChatMemoryMessage chatMemoryMessage = new ChatMemoryMessage();
        chatMemoryMessage.setConversationId(conservationId);
        chatMemoryMessage.setMessageIndex(index);
        chatMemoryMessage.setMessageType(message.getMessageType().getValue());
        chatMemoryMessage.setMessageText(message.getText());
        return chatMemoryMessage;
    }

    //ChatMemoeyMessage--->>Spring AI的message
    private Message toMessage(ChatMemoryMessage chatMemoryMessage){
        MessageType messageType = MessageType.valueOf(chatMemoryMessage.getMessageType().toUpperCase());
        //根据消息类型返回具体子类对象
        return switch (messageType) {
            case SYSTEM -> new SystemMessage(chatMemoryMessage.getMessageText());
            case USER -> new UserMessage(chatMemoryMessage.getMessageText());
            case ASSISTANT -> new AssistantMessage(chatMemoryMessage.getMessageText());
            default -> throw new IllegalArgumentException("未知的消息类型: " + chatMemoryMessage.getMessageType());
        };
    }
}
