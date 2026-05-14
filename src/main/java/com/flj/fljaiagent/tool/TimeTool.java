package com.flj.fljaiagent.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

public class TimeTool {
    @Tool(description = "Returns the current time, which must be called when the user asks 'What time is it now'")
    public String currentTime(){
        return "Time now:"+LocalDateTime.now().toString();
    }
}
