package xgcm.nio;

import io.netty.buffer.ByteBuf;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Iterator;

/**
 *
 *
 * 测试非阻塞式io 流
 * @Author YXG
 * @Date 2018-11-22 21:34
 */
public class TestNonBlokingNio {


    @Test
    public void client() throws  Exception{
       SocketChannel client = SocketChannel.open(new InetSocketAddress("127.0.0.1",8989));

       // 切换为非阻塞式
       client.configureBlocking(false);

       // 分配一个缓冲区
       ByteBuffer allocate = ByteBuffer.allocate(1024);

       // 当前时间
       allocate.put(new Date().toString().getBytes());
       // 切换为读模式
       allocate.flip();
       // 通过通道写入到服务器
       client.write(allocate);
       client.shutdownOutput();
       client.close();

    }



    @Test
    public void server () throws Exception {
        ServerSocketChannel open = ServerSocketChannel.open();


        // 绑定端口号
        open.bind(new InetSocketAddress("127.0.0.1",9999));
// 将服务器端设置为非阻塞式
        open.configureBlocking(false);
         // 获取选择器
        Selector selector = Selector.open();

        // 将channel 注册到选择器中
        // 第二个参数是选择监听什么事件(读，写，连接)
        //SelectionKey.OP_READ(1) ,OP_WRITE(4), OP_CONNECT(8), OP_ACCEPT(16);
        // 如果监听不止一个事件，可以用“或位” 操作符号连接
        int events = SelectionKey.OP_ACCEPT;
        open.register(selector,events);

        // 轮询获取选择器上已经 "准备就绪" 的事件
        while( true ) {
            selector.select();
            System.out.println("进入while");
            // 获取选择器中所有的事件(已就绪)
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                // 获取已经就绪的事件
                SelectionKey next =  iterator.next();
                iterator.remove();
                // 判断事件的类型
                // 接受连接事件
                if (next.isAcceptable()) {
                    // 若接受就绪获取客户端连接
                   ServerSocketChannel sChannel = (ServerSocketChannel) next.channel();
                   SocketChannel ssChannel = sChannel.accept();
                    // 将该通道设置为非阻塞
                    ssChannel.configureBlocking(false);
                    // 注册到选择器中
                    ssChannel.register(selector,SelectionKey.OP_READ);
                } else if (next.isReadable()) {
                    // 获取当前选择器读就绪状态的通道
                    SocketChannel  ssChannel = (SocketChannel) next.channel();
                    ByteBuffer allocate = ByteBuffer.allocate(1024);
                    int len = 0;
                    while ((ssChannel.read(allocate)) > 0) {
                        allocate.flip();
                        System.out.println(charset.decode(allocate).toString());
                    }

                    iterator.remove();

                    System.out.println(ssChannel.getClass().getName());
                }

            }

        }
    }

    public static Charset charset = Charset.forName("UTF-8");

}
