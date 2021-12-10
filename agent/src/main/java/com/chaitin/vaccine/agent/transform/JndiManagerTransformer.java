package com.chaitin.vaccine.agent.transform;

import javassist.*;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class JndiManagerTransformer implements ClassFileTransformer {
    private Instrumentation inst;
    private static String JndiManagerClassName = "org.apache.logging.log4j.core.net.JndiManager";

    private static String JndiManagerLookupMethodName = "lookup";

    public JndiManagerTransformer(Instrumentation inst){
        this.inst = inst;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if(className.replace("/",".").equals(JndiManagerClassName)){
            System.out.println("[Vaccine] Start Patch JndiManager Lookup Method!");
            CtClass ctClass = null;
            CtMethod ctMethod = null;
            try{
                // 初始化classPool
                ClassPool classPool = new ClassPool();
                classPool.appendSystemPath();
                if (loader != null) {
                    classPool.appendClassPath(new LoaderClassPath(loader));
                }

                // 构造CtClass
                ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));

                // 获取lookup方法
                for(CtMethod method:ctClass.getMethods()){
                    if(method.getName().equals(JndiManagerLookupMethodName)){
                        ctMethod = method;
                        break;
                    }
                }

                // 修改lookup方法
                assert ctMethod != null;
                ctMethod.setBody("return null;");
                // 返回字节码
                System.out.println("[Vaccine] Patch JndiManager Lookup Success!");
                return ctClass.toBytecode();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (ctClass != null) {
                    ctClass.detach();
                }
            }
        }else{
            return classfileBuffer;
        }

        return classfileBuffer;
    }

    public void retransform() {
        Class<?>[] loadedClasses = inst.getAllLoadedClasses();
        for (Class<?> clazz : loadedClasses) {
            if (clazz.getName().replace("/", ".").equals(JndiManagerClassName)) {
                System.out.println("[Vaccine] Find Loaded JndiManager Lookup Method!");
                try {
                        inst.retransformClasses(clazz);
                    } catch (Throwable t) {
                        System.out.println("failed to retransform class " + clazz.getName() + ": " + t.getMessage());
                    }
            }
        }
    }

}
