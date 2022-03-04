# nio
学习java nio，手写一个简单版的netty

# reactor demo

用nio手写一个简易版的 主从多线程模式的io架构


## 一些知识点

- 不断有读写时间触发的原因“NIO的水平触发与边缘触发”
- 这里遇到的是水平触发，读监听缓冲区ByteBuffer里面有数据，所以一直触发读事件，取出缓冲区的数据，就可以。
- 取出缓冲区后，读事件不会多次触发了，可是写事件还是不断的在触发。原因是selector需要重新注册。


MultiReadHandler.class

```java
    public void run() {
        try {
            final SocketChannel sc = (SocketChannel) selectionKey.channel();
            //使用线程池，异步处理读请求
            ThreadPool.getPool().submit(new Runnable() {
                public void run() {
                    doRead(sc);
                }
            });

//            //处理完读请求，将通道注册为写,重点！要重新注册写
            Selector selector = selectionKey.selector();
            SelectionKey sk = sc.register(selector, SelectionKey.OP_WRITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```

MultiWriteHandler.class

```java
   public void run() {
        try {
            final SocketChannel sc = (SocketChannel) selectionKey.channel();
            //使用线程池，异步处理写请求
            ThreadPool.getPool().submit(new Runnable() {
                public void run() {
                    doWrite(sc);
                }
            });
//            //写完后，将通道注册为读,重点，要重新注册读！！
            Selector selector = selectionKey.selector();
            SelectionKey sk = sc.register(selector, SelectionKey.OP_READ);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```
