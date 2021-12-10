package com.chaitin.vaccine.agent;

import com.chaitin.vaccine.agent.transform.JndiManagerTransformer;

import java.io.UnsupportedEncodingException;
import java.lang.instrument.Instrumentation;
import java.net.URLDecoder;

public class Agent {
    /**
     * 解码传递参数
     * @param arg
     * @return
     * @throws UnsupportedEncodingException
     */
    protected static String decodeArg(String arg) throws UnsupportedEncodingException {
        try {
            return URLDecoder.decode(arg, "UTF-16");
        } catch (UnsupportedEncodingException e) {
            return URLDecoder.decode(arg, "UTF-8");
        }
    }

    /**
     * 启动时加载的agent入口方法
     *
     * @param agentArg 启动参数
     * @param inst     {@link Instrumentation}
     */
    public static void premain(String agentArg, Instrumentation inst) {
        try {
            System.out.println("[Vaccine] Premain Agent");
            JndiManagerTransformer jndiManagerTransformer = new JndiManagerTransformer(inst);
            inst.addTransformer(jndiManagerTransformer, true);
            jndiManagerTransformer.retransform();
        }catch (Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * attach 机制加载 agent
     *
     * @param agentArg 启动参数
     * @param inst     {@link Instrumentation}
     */
    public static void agentmain(String agentArg, Instrumentation inst) {
        try {
            System.out.println("[Vaccine] Attach Agent");
            JndiManagerTransformer jndiManagerTransformer = new JndiManagerTransformer(inst);
            inst.addTransformer(jndiManagerTransformer, true);
            jndiManagerTransformer.retransform();
        }catch (Throwable e){
            System.out.println("[Vaccine] Error "+ e.getMessage());
            e.printStackTrace();
        }
    }
}
