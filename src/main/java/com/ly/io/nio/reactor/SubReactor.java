package com.ly.io.nio.reactor;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

public class SubReactor {

    private Selector selector;

    private volatile boolean stop;


    public SubReactor() {
        try {
            selector = SelectorProvider.provider().openSelector();
            stop = false;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * 将主Reactor中的Channel注册到从Reactor中的selector
     *
     * @param sc
     */
    public void register(SocketChannel sc) {
        try {
            sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        ThreadPool.getPool().submit(new Runnable() {
            public void run() {
                System.out.println("从reactor开始启动了。。。。。");
                while (!stop) {
                    try {
                        int count = selector.select(1000);
                        if (count == 0) {
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
        });

    }


    private void disptach(SelectionKey key) {
        /**
         * 从Reactor只关心读和写事件
         */
        if (key.isValid()) {

            if (key.isReadable()) {
                new MultiReadHandler(key).run();
            }

            if (key.isWritable()) {
                new MultiWriteHandler(key).run();
            }
        }
    }
}
