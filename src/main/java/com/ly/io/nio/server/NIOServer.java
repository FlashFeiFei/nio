package com.ly.io.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    public static void main(String[] args) throws Exception {


        //创建server
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //绑定一个端口6666,在服务器监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);


        //得到一个selector对象
        Selector selector = Selector.open();
        //把 serverSocketChannel 注册到 selector 关心事件OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //循环等待客户端连接
        while (true) {

            //等待1秒钟，1s后如果没有链接事件发生，则返回
            if (selector.select(1000) == 0) {
                System.out.println("服务器等待1秒，无连接");
                continue;
            }

            //如果返回的不是 > 0,就获取到有相关事件发生的selectionKey集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            //通过selectionKey获取通道
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {

                //获取到某个用户的key
                SelectionKey key = iterator.next();

                //根据key，获取事件
                if (key.isAcceptable()) {
                    //如果是连接事件,给该客户端生成一个SocketChannel,此时accept（）不会阻塞
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    //将socketChannel注册到selector，关注事件为 OP_READ，同时给socketChannel关联一个Buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }

                if (key.isReadable()) {
                    //发生OP_READ事件
                    //获取channel
                    SocketChannel channel = (SocketChannel) key.channel();

                    //获取channel关联的buffer
                    ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                    channel.read(byteBuffer);

                    System.out.println("form 客户端 " + Arrays.toString(byteBuffer.array()));
                }

                //手动从结合中移除当前的selectKey，防止重复操作
                iterator.remove();
            }
        }


    }
}
