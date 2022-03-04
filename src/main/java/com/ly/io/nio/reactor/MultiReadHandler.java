package com.ly.io.nio.reactor;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;

public class MultiReadHandler {

    private SelectionKey selectionKey;

    public MultiReadHandler(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public void run() {
        try {
            final SocketChannel sc = (SocketChannel) selectionKey.channel();
            //使用线程池，异步处理读请求
            ThreadPool.getPool().submit(new Runnable() {
                public void run() {
                    doRead(sc);
                }
            });

//            //处理完读请求，将通道注册为写
            Selector selector = selectionKey.selector();
            SelectionKey sk = sc.register(selector, SelectionKey.OP_WRITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doRead(SocketChannel ssc) {
        System.out.println("读取数据，然后做一些数据处理");

        try {

            Charset charset = Charset.forName("UTf-8");
            ByteBuffer buf = ByteBuffer.allocate(1024);
            buf.clear();
            int bytesRead = ssc.read(buf);
            while (bytesRead == 0) {
                bytesRead = ssc.read(buf);
            }
            buf.flip();
            String s = charset.decode(buf).toString();
            System.out.println("客户端收到的数据:" + s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
