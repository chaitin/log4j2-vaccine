package com.chaitin.vaccine.loader;

import com.chaitin.vaccine.loader.utils.JavaVersionUtils;
import com.chaitin.vaccine.loader.utils.LogUtils;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.ServiceConfigurationError;

public class Application {
    protected Long pid;
    protected String agent;
    protected String action;

    public Application(Long pid, String agent, String action) {
        this.pid = pid;
        this.agent = agent;
        this.action = action;
    }

    protected String formatArg() throws UnsupportedEncodingException {
        String enc = "UTF-16";
        try {
            return String.format("%d:%s:%s",
                    this.pid,
                    URLEncoder.encode(this.agent, enc),
                    URLEncoder.encode(this.action, enc)
            );
        } catch (UnsupportedEncodingException e) {
            enc = "UTF-8";
            return String.format("%d:%s:%s",
                    this.pid,
                    URLEncoder.encode(this.agent, enc),
                    URLEncoder.encode(this.action, enc)
            );
        }

    }

    public void load() throws IOException
    {
        VirtualMachine virtualMachine = null;
        String pid = Long.toString(this.pid);

        //TODO jdk version > 9 will throw java.util.ServiceConfigurationError:
        try {
            virtualMachine = VirtualMachine.attach(pid);
            Thread.sleep(1000);
            if (virtualMachine != null) {
                String arg = formatArg();
                virtualMachine.loadAgent(this.agent,arg);
            }
        } catch (AttachNotSupportedException e) {
            System.err.println("[Vaccine] Error: attach api is not supported, " + e.getMessage());
        } catch (ServiceConfigurationError e){//jdk 9+ 由于attach api 整合在jdk中会报错，实际上可以忽略

        } catch (Exception e) {
            System.err.println("[Vaccine] Error: vaccine loaded failed, " + e.getMessage());
        } finally {
            if (virtualMachine != null) {
                virtualMachine.detach();
                System.err.println("[Vaccine] Agent Patch Finished");
            }
        }

    }
}

