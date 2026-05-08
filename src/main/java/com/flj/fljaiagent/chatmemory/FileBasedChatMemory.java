package com.flj.fljaiagent.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于文件的对话存储
 */
public class FileBasedChatMemory implements ChatMemory {

    //文件路径
    private final String BASE_DIR;

    //创建kryo实例化
    private static final Kryo kryo = new Kryo();

    //配置kryo
    static {
        kryo.setRegistrationRequired(false);//不需要手动注册
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());//设置实例化策略
    }

    //初始化文件
    public FileBasedChatMemory(String dir) {
        BASE_DIR = dir;
        //创建根目录
        File baseDir = new File(dir);
        if (!baseDir.exists()) {
            baseDir.mkdirs();//递归创建目录
        }
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        //获取对话消息
        List<Message> conversationMessages = getOrCreateConversation(conversationId);
        //添加新的消息
        conversationMessages.addAll(messages);
        //保存对话消息
        saveConvasation(conversationId, conversationMessages);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<Message> messages = getOrCreateConversation(conversationId);
        //用stream skip跳过
        return messages.stream()
                .skip(Math.max(0, messages.size() - lastN))//注意要大于等于0
                .toList();
    }

    @Override
    public void clear(String conversationId) {
        //获取文件
        File file = getConversationFile(conversationId);
        if (file.exists()) {
            file.delete();
        }
    }

    //根据对话Id创建文件
    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo");
    }

    //从文件中获取对话消息
    private List<Message> getOrCreateConversation(String conversationId) {
        //获取文件
        File file = getConversationFile(conversationId);
        List<Message> messages = new ArrayList<>();
        //从文件中读取消息
        if (file.exists()) {
            try (Input input = new Input(new FileInputStream(file));) {
                messages = kryo.readObject(input, ArrayList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

    //把对话消息保存到文件中
    private void saveConvasation(String conversationId, List<Message> messages) {
        //获取文件
        File file = getConversationFile(conversationId);
        //把数据写入文件中
        try (Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output, messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
