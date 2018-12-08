package xgcm.nio;

import io.netty.buffer.ByteBuf;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author YXG
 * @Date 2018-12-06 21:11
 */
public class NioTest2 {


    @Test
    public void server() throws Exception {

        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1",8989),1024);

        serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select(1000);
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            System.out.println("selector = " + selector);
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel socketChannel = server.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector,SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    Thread.sleep(3000);
                    SocketChannel client = (SocketChannel) selectionKey.channel();
                    String msg = doRead(client);
                    System.out.println("msg = " + msg);
                }

                iterator.remove();
            }



        }
    }

    public static String doRead(SocketChannel socketChannel) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int len = 0;
        String msg = "";
        try {
            while ((len=socketChannel.read(byteBuffer) ) > 0) {
                byteBuffer.flip();
                msg += new String(byteBuffer.array(),0,len,"UTF-8");
            }
        } catch (Exception e )
        {}
        return msg;
    }

    public static void doWrite(SocketChannel socketChannel,String msg) {
        try {
            byte[] data = msg.getBytes("UTF-8");
            ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
            byteBuffer.put(data);
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
