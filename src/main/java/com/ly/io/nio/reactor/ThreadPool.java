package com.ly.io.nio.reactor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    public static final ThreadPool pool = new ThreadPool();

    private boolean init=false;

    private  static ExecutorService executorService;

    private ThreadPool(){};

    public synchronized void init(int size){
        if(!init){
            executorService = Executors.newFixedThreadPool(size);
            init = true;
        }else {
            System.out.println("线程池已经初始化了");
        }
    }

    public static ThreadPool getPool(){
        return pool;
    }

    public void submit(Runnable runnable){
        if(init){
            executorService.submit(runnable);
        }else{
            throw new RuntimeException("没有初始化");
        }
    }


}
