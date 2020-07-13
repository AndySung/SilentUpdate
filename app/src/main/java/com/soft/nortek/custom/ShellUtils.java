package com.soft.nortek.custom;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/***
 * 调用
    ShellUtils.CommandResult result1 = ShellUtils.execCommand("/system/bin/ping -c 10 192.168.10.22",false);
    Log.i(TAG,"result1.result = " + result1.result);
    Log.i(TAG,"result1.successMsg = " + result1.successMsg);
    Log.i(TAG,"result1.errorMsg = " + result1.errorMsg);

    ShellUtils.CommandResult result2 = ShellUtils.execCommand(“ls”,false);
    Log.i(TAG,"result2.result = " + result2.result);
    Log.i(TAG,"result2.successMsg = " + result2.successMsg);
    Log.i(TAG,"result2.errorMsg = " + result2.errorMsg);

    出错

    注意调用的方法： ShellUtils.execCommand(“ls”,false);和 ShellUtils.execCommand(“ls”,true);的区别

    false,是指该命令不需要su权限，root权限。
    true,表示该命令需要su权限和root权限。

    但是在大部分机器中，app是没有root的权限的，所以有些命令是无法执行的。
    报错：
    su：not allowed
    start：must be root

    目前没有找到App能获取到root权限的方法。故，目前只能执行一些不需要root权限的命令。
    原文链接：https://blog.csdn.net/Sunxiaolin2016/java/article/details/100974661
 ***/
public class ShellUtils {
    private static final String TAG = "ShellUtils";

    public static final String COMMAND_SU = "su";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

    private ShellUtils() {
        throw new AssertionError();
    }

    /**
     * 查看是否有了root权限
     *
     * @return
     */
    public static boolean checkRootPermission() {
        return execCommand("echo root", true, false).result == 0;
    }

    /**
     * 执行shell命令，默认返回结果
     *
     * @param command
     *            command
     * @param isRoot
     *              need root permission
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[] { command }, isRoot, true);
    }

    /**
     * 执行shell命令，默认返回结果
     *
     * @param commands
     *            command list
     * @param isRoot
     *              need root permission
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(List<String> commands,
                                            boolean isRoot) {
        return execCommand(
                commands == null ? null : commands.toArray(new String[] {}),
                isRoot, true);
    }

    /**
     * 执行shell命令，默认返回结果
     *
     * @param commands
     *            command array
     * @param isRoot
     *              need root permission
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        return execCommand(commands, isRoot, true);
    }

    /**
     * execute shell command
     *
     * @param command
     *            command
     * @param isRoot
     *              need root permission
     * @param isNeedResultMsg
     *            whether need result msg
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String command, boolean isRoot,
                                            boolean isNeedResultMsg) {
        return execCommand(new String[] { command }, isRoot, isNeedResultMsg);
    }

    /**
     * execute shell commands
     *
     * @param commands
     *            command list
     * @param isRoot
     *              need root permission
     * @param isNeedResultMsg
     *              result
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(List<String> commands,
                                            boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(
                commands == null ? null : commands.toArray(new String[] {}),
                isRoot, isNeedResultMsg);
    }

    /**
     * execute shell commands
     *
     * @param commands
     *              command array
     * @param isRoot
     *              need root permission
     * @param isNeedResultMsg
     *              result
     * @return <ul>
     *         <li>if isNeedResultMsg is false, {@link CommandResult#successMsg}
     *         is null and {@link CommandResult#errorMsg} is null.</li>
     *         <li>if {@link CommandResult#result} is -1, there maybe some
     *         excepiton.</li>
     *         </ul>
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot,
                                            boolean isNeedResultMsg) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, null, null);
        }

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;

        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                // donnot use os.writeBytes(commmand), avoid chinese charset
                // error
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();
            result = process.waitFor();
            // get command result
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(
                        process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s);
                }
                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(result, successMsg == null ? null
                : successMsg.toString(), errorMsg == null ? null
                : errorMsg.toString());
    }

    /**
     * 运行结果
     * <ul>
     * <li>{@link CommandResult#result} means result of command, 0 means normal,
     * else means error, same to excute in linux shell</li>
     * <li>{@link CommandResult#successMsg} means success message of command
     * result</li>
     * <li>{@link CommandResult#errorMsg} means error message of command result</li>
     * </ul>
     *
     * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a>
     *         2013-5-16
     */
    public static class CommandResult {
        /** 运行结果 **/
        public int result;
        /** 运行成功结果 **/
        public String successMsg;
        /** 运行失败结果 **/
        public String errorMsg;

        public CommandResult(int result) {
            this.result = result;
        }

        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
           // Log.i("result:","result"+successMsg);
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }
    }
}
