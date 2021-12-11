package com.chaitin.vaccine.loader;

import com.chaitin.vaccine.loader.erros.UsageException;
import com.chaitin.vaccine.loader.utils.JavaVersionUtils;
import com.chaitin.vaccine.loader.utils.LogUtils;
import com.chaitin.vaccine.loader.utils.ProcessUtils;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;

public class Command {
    protected PrintWriter ps;
    protected HelpFormatter formatter;
    protected Options options;

    public Command() {
        this.ps = new PrintWriter(System.err, true);
        this.formatter = new HelpFormatter();
        this.setupOptions();
    }

    protected void printHelp() {
        formatter.printHelp(
                ps,
                formatter.getWidth(),
                "vaccine [options] [pid]",
                "options: ",
                options,
                0,
                2,
                "pid: Process PID"
        );
    }

    protected void setupOptions() {
        options = new Options();
        options.addOption(Option
                .builder()
                .longOpt("agent")
                .hasArg()
                .desc("Agent jar")
                .argName("agent-jar-path")
                .required()
                .build());
        options.addOption(new Option("uninstall", "uninstall vaccine module"));
    }

    public void app(String[] args) {
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            List<String> arguments = cmd.getArgList();


            File agent = new File(cmd.getOptionValue("agent")).getAbsoluteFile();
            String action = cmd.hasOption("uninstall") ? "uninstall":"install";

            long pid = -1;
            boolean injectAll = false;

            if (arguments.size() != 1) {
                // select jvm process pid
                try {
                    pid = ProcessUtils.select(false, -1, null);
                } catch (InputMismatchException e) {
                    throw new UsageException("Please input an integer to select pid.");
                }
                if (pid < 0) {
                    throw new UsageException("Please select an available pid.");
                }
            }else{
                // "all" means inject all porcess
                if(arguments.get(0).equals("all")){
                    injectAll = true;
                }else{
                    pid = Integer.parseInt(arguments.get(0));
                }
            }

            if(injectAll){
                Map<Long, String> processMap = ProcessUtils.listProcessByJps(false);
                for(Long processPid : processMap.keySet()){
                    Application application = new Application(processPid, agent.getAbsolutePath(), action);
                    application.load();
                }
            }else{
                Application app = new Application(pid, agent.getAbsolutePath(),action);
                app.load();
            }
        } catch (IOException e) {
            LogUtils.error("Error: attach io error, " + e.getMessage());
        }
        catch (ParseException e) {
            printHelp();
        } catch (NumberFormatException e) {
            ps.println("Error: pid must be a number");
            printHelp();
        } catch (UsageException e) {
            ps.println("Error: " + e.getMessage());
            printHelp();
        }
    }

    public static void main(String[] args) {
        new Command().app(args);
    }
}

