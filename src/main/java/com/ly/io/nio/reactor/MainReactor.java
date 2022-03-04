package com.ly.io.nio.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MainReactor {

    /**
     * 维护一个从Reactor
     */
    private SubReactor subReactor;

    private int port;


    private Selector selector;

    private ServerSocketChannel servChannel;

    private volatile boolean stop;

    public MainReactor(int port) {
        try {
            selector = Selector.open();
            servChannel = ServerSocketChannel.open();
            //非阻塞设置
            servChannel.configureBlocking(false);
            //端口已经buffer设置
            servChannel.socket().bind(new InetSocketAddress(port), 1024);
            //channel注册到selector
            SelectionKey sk = servChannel.register(selector, SelectionKey.OP_ACCEPT);
            stop = false;
            this.port = port;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public MainReactor addSub(SubReactor subReactor) {
        this.subReactor = subReactor;
        this.subReactor.run();
        return this;
    }

    public void run() {
        System.out.println("主reactor开始启动了,监听端口：" + port + ".......");
        while (!stop) {
            try {
                int count = selector.select(1000);
                if(count == 0){
                    continue;
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        disptach(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null)
                                key.channel().close();
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void disptach(SelectionKey key) {
        if (key.isValid()) {
            /**
             * 主Reactor只关心Accept事件
             */
            if (key.isAcceptable()) {
                new MultiAcceptor(key).addSub(this.subReactor).run();
            }

//            //如果未使用了从Reactor
//            if (this.subReactor == null) {
//                if (key.isReadable()) {
//                    new MultiReadHandler(key).run();
//                }
//                if (key.isWritable()) {
//                    new MultiWriteHandler(key).run();
//                }
//            }

        }
    }
}
