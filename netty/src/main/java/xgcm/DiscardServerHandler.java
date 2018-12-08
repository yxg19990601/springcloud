package xgcm;

/**
 * Hello world!
 *
 */
import io.netty.buffer.ByteBuf;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

/**
 * 处理服务器端通道
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter { // (1)


    /**
     * 每当有数据进入的时候进行数据读取
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        ByteBuf in = (ByteBuf) msg;
        try {

            String readData = in.toString(CharsetUtil.UTF_8);
            System.out.println("readData = " + readData);
           in =  in.writeBytes("来自服务器的消息!".getBytes(Charset.forName("UTF-8")));

            ctx.write(in);
        } finally {
            //ReferenceCountUtil.release(msg); // (2)
        }
        // 或者直接打印
        System.out.println("Yes, A new client in = " + ctx.name());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // 出现异常时关闭连接。
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("DiscardServerHandler.channelActive  有一个客户端已连接我 . ");
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
            System.out.println("DiscardServerHandler.channelReadComplete");
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}