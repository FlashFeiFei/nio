package com.ly.io.nio.reactor;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class MultiAcceptor {

    private SubReactor subReactor;

    private SelectionKey selectionKey;

    public MultiAcceptor(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public MultiAcceptor addSub(SubReactor subReactor) {
        this.subReactor = subReactor;
        return this;
    }

    public void run() {
        try {
            ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);

            System.out.println("accept");
            subReactor.register(sc);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
