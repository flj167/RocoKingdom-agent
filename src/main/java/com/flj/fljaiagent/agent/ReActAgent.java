package com.flj.fljaiagent.agent;

import com.flj.fljaiagent.util.MarkdownUtil;

/**
 * 继承BaseAgent,把每个步骤分解成思考和行动
 * think():思考是否要执行行动
 * act():执行行动
 */
public abstract class ReActAgent extends BaseAgent {
    /**
     * 思考是否要执行行动
     */
    public abstract boolean think();

    /**
     * 执行行动
     */
    public abstract  String act();

    /**
     * 单步操作
     * @return
     */
    @Override
    public String step(){
        try {
            //先思考后执行
            boolean shouldAct = think();
            if(!shouldAct){
                //不调工具 = LLM已给出最终答案，直接结束
                setState(AgentState.FINISHED);
                return MarkdownUtil.toHtml(getLastAssistantText());
            }
            return act();
        } catch (Exception e) {
            e.printStackTrace();
            return "步骤执行失败，失败信息:"+e.getMessage();
        }
    }

}
