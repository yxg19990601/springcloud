package xgcm;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @Author YXG
 * @Date 2018-11-12 20:39
 */
public class DiscardClient {
    private String hostName;
    private int port;
    public  DiscardClient (String hostName,int port) {
        this.hostName = hostName;
        this.port  = port;
    }



    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();


         Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class).remoteAddress(
                    new InetSocketAddress(hostName,port)).handler(new ChannelInitializer<io.netty.channel.socket.SocketChannel>() {
                @Override
                protected void initChannel(io.netty.channel.socket.SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new DiscardClientHandler());
                }

            });
        try {
           ChannelFuture f = bootstrap.connect().sync();
           f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully().sync();
        }

    }
    public static void main(String[] args) throws Exception {
//        Channel channel = new Channel();

        new DiscardClient("192.168.83.1", 8080).start();
    }
}
