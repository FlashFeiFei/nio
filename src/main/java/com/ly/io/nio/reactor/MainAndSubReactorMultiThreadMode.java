package com.ly.io.nio.reactor;

public class MainAndSubReactorMultiThreadMode {

    public static void main(String[] args) {

        /**
         * 初始化一个线程池，然后创建一个主reactor，并加入一个从reactor
         */
        ThreadPool.getPool().init(3);

        new MainReactor(8089).addSub(new SubReactor()).run();
    }
}
