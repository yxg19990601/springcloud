package xgcm.nio;

import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 *
 *
 * 阻塞和非阻塞：
 *
 *
 *
 *选择器：
 *     把每一个通道注册到选择器上
 *     选择器监控这些io的状况（write ，read ）的状态
 *     请求事件完全准备就绪了之后再分配到服务器的线程上进行处理
 *
 *s使用 NIO 完成网络通信的三个核心：
 * 1. 通道（Channel）负责连接
 * 2. 缓冲区 （buffer） ： 负责数据的存取
 * 3. 选择器（Selector）：是SelectableChannel 的多路复用器，用于监控SelectableChannel的通讯状况
 *
 *
 * java.nio.channels.Channel 接口
 *      |-- SelectableChannel  抽象类
 *      |-- SocketChannel
 *      |-- ServerSocketChannel
 *      |-- DatagramChannel
 *
 *      |-- Pipe.SinkChannel
 *      |-- Pipe.SourceChannel
 *
 * @Author YXG
 * @Date 2018-11-19 21:47
 */
public class NioBlockingSocket {

    @Test
    public void server() throws  Exception{
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress("127.0.0.1", 8989));
        SocketChannel accept = server.accept();

        // 接受客户端发来的文件,并且存到本地
        FileChannel outChannel = FileChannel.open(Paths.get("F:/b.zip"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);


        ByteBuffer buf = ByteBuffer.allocate(1024);
        while (accept.read(buf) != -1) {
            buf.flip();
            outChannel.write(buf);
            buf.clear();
        }
        //accept.shutdownInput();

        buf.clear();

        buf = ByteBuffer.allocate(1024);
        buf.put("abc我已经收到了!".getBytes("UTF-8"));
        buf.flip();
        accept.write(buf);
        accept.shutdownOutput();
        outChannel.close();
        accept.close();
        server.close();
    }


    @Test
    public void clinet() throws  Exception{

        // 连接服务器
        SocketChannel client = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8989));

        FileChannel inChannel = FileChannel.open(Paths.get("D:/sql_temp.zip"),StandardOpenOption.READ);

        ByteBuffer buf = ByteBuffer.allocate(1024);
        while (inChannel.read(buf) != -1) {
            buf.flip();
            client.write(buf);
            buf.clear();
        }
        client.shutdownOutput();


        buf.clear();
       int len =-1;
       while ((len= client.read(buf)) != -1){
           buf.flip();
           byte[] bytes = new byte[len];
           buf.get(bytes);
           System.out.println(new String(bytes,"UTF-8"));
       }


        inChannel.close();
        client.close();

    }


// 1 2 4 8
// 1 2 4 8
    @Test
    public void test1() throws Exception{
        System.out.println(2|4);
    }
}
