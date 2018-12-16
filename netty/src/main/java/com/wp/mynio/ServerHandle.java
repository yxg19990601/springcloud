package com.wp.mynio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @Author YXG
 * @Date 2018-12-11 21:21
 */
public class ServerHandle extends Thread{
    Selector selector ;


    public ServerHandle(int port) {
        try {
             selector  = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public void accept(SelectionKey key) {

    }

    public void read(SelectionKey key) {


    }

    @Override
    public void run() {
        while (true) {
            try {
                selector.select();
                System.out.println("ÊÂ¼þ¡£ ¡£¡£");
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key =  keys.next();
                    keys.remove();

                    if (key.isAcceptable()) {
                        ServerSocketChannel ssChannel= (ServerSocketChannel) key.channel();
                        SocketChannel sChannel = ssChannel.accept();
                        sChannel.configureBlocking(false);
                        sChannel.register(selector,SelectionKey.OP_READ);
                    } else if (key.isReadable()) {

                    }
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
