package com.flj.fljaiagent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.flj.fljaiagent.mapper")
public class FljAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(FljAiAgentApplication.class, args);
    }

}
