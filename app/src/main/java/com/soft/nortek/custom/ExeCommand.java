package com.soft.nortek.custom;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Runtime.getRuntime;

/**
 * 在Android 代码中执行 命令:需要root 权限
 *
 */
public class ExeCommand {

    private static final String TAG = "ExeCommand";

    /**
     * shell进程
     */
    private Process process;
    //对应进程的3个流
    private BufferedReader successResult;
    private BufferedReader errorResult;
    private DataOutputStream os;
    /**
     * 是否同步，true：run会一直阻塞至完成或超时。false：run会立刻返回
     */
    private boolean isSynchronous;

    /**
     * 表示shell进程是否还在运行
     */
    private boolean isRunning = false;
    /**
     * 同步锁
     */
    ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * 保存执行结果
     */
    private StringBuffer result = new StringBuffer();

    /**
     * 构造函数
     *
     * @param synchronous true：同步，false：异步
     */
    public ExeCommand(boolean synchronous) {
        isSynchronous = synchronous;
    }

    /**
     * 默认构造函数，默认是同步执行
     */
    public ExeCommand() {
        isSynchronous = true;
    }

    /**
     * 还没开始执行，和已经执行完成 这两种情况都返回false
     *
     * @return 是否正在执行
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * @return 返回执行结果
     */
    public String getResult() {
        Lock readLock = lock.readLock();
        readLock.lock();
        String resultStr = null;
        try {
            Log.d(TAG, "getResult");
            resultStr=new  String(result);
        } catch (Exception e){
            Log.e(TAG,"getResult Exception:"+e.getMessage());
        }finally {
            readLock.unlock();
        }
        return resultStr;
    }

    /**
     * 执行命令
     * @param command ：命令
     * @return
     */
    public ExeCommand run(String command ) {
        return run(command,-1);
    }

    /**
     * 执行命令
     *
     * @param command eg: cat /sdcard/test.txt
     * @param maxTime 超时时间 (ms)
     * @return this
     */
    public ExeCommand run(String command, final int maxTime) {
        Log.d(TAG, "run command:" + command + ",maxtime:" + maxTime);
        if (command == null || command.length() == 0) {
            return this;
        }

        if(result!=null && result.length()>0){
            result=new StringBuffer();
        }

        try {
            // String SET_TX_STOP = "iwpriv\bwlan0\btx\b0";     //tx 1，停止tx发射
//            process = getRuntime().exec("sh");//看情况可能是su
            process = getRuntime().exec(command);//看情况可能是su
        } catch (Exception e) {
            Log.e(TAG, "run Exception: "+e.getMessage() );
            return this;
        }
        isRunning = true;
        successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
        errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        os = new DataOutputStream(process.getOutputStream());

        try {
            //向sh写入要执行的命令
            os.write(command.getBytes());
            os.writeBytes("\n");
            os.flush();

            os.writeBytes("exit\n");
            os.flush();

            os.close();
            //如果等待时间设置为非正，就不开启超时关闭功能
            if (maxTime > 0) {
                //超时就关闭进程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(maxTime);
                        } catch (Exception e) {
                        }
                        try {
                            int ret = process.exitValue();
                            Log.d(TAG, "exitValue Stream over"+ret);
                        } catch (IllegalThreadStateException e) {
                            Log.e(TAG,"take maxTime,forced to destroy process");
                            process.destroy();
                        }
                    }
                }).start();
            }

            //开一个线程来处理input流
            final Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    String line;
                    Lock writeLock = lock.writeLock();
                    try {
                        while ((line = successResult.readLine()) != null) {
                            line += "\n";
                            writeLock.lock();
                            result.append(line);
                            writeLock.unlock();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "read InputStream exception:" + e.toString());
                    } finally {
                        try {
                            successResult.close();
                            Log.d(TAG,"read InputStream over");
                        } catch (Exception e) {
                            Log.e(TAG,"close InputStream exception:" + e.toString());
                        }
                    }
                }
            });
            t1.start();

            //开一个线程来处理error流
            final Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    String line;
                    Lock writeLock = lock.writeLock();
                    try {
                        while ((line = errorResult.readLine()) != null) {
                            line += "\n";
                            writeLock.lock();
                            result.append(line);
                            writeLock.unlock();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "read ErrorStream exception:" + e.toString());
                    } finally {
                        try {
                            errorResult.close();
                            Log.d(TAG, "read ErrorStream over");
                        } catch (Exception e) {
                            Log.e(TAG, "read ErrorStream exception:" + e.toString());
                        }
                    }
                }
            });
            t2.start();

            Thread t3 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //等待执行完毕
                        t1.join();
                        t2.join();
                        process.waitFor();
                    } catch (Exception e) {
                        Log.e(TAG,"Thread Exception:"+e.getMessage());

                    } finally {
                        isRunning = false;
                        Log.d(TAG, "run command process end");
                    }
                }
            });
            t3.start();

            if (isSynchronous) {
                Log.i("auto", "run is go to end");
                t3.join();
                Log.i("auto", "run is end");
            }
        } catch (Exception e) {
            Log.e(TAG, "run command process exception:" + e.toString());
        }
        return this;
    }

}
