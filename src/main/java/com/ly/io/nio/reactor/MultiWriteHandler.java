package com.ly.io.nio.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class MultiWriteHandler {
    private SelectionKey selectionKey;

    public MultiWriteHandler(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }


    private void doWrite(SocketChannel sc) {
        System.out.println("处理写。。。");
//        String str = "hello world";
//        ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes());
//
//        try{
//            sc.write(byteBuffer);
//        }catch (IOException e){
//            e.printStackTrace();
//        }
    }

    public void run() {
        try {
            final SocketChannel sc = (SocketChannel) selectionKey.channel();
            //使用线程池，异步处理写请求
            ThreadPool.getPool().submit(new Runnable() {
                public void run() {
                    doWrite(sc);
                }
            });
//            //写完后，将通道注册为读
            Selector selector = selectionKey.selector();
            SelectionKey sk = sc.register(selector, SelectionKey.OP_READ);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
