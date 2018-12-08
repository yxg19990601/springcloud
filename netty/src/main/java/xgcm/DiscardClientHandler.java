package xgcm;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @Author YXG
 * @Date 2018-11-12 21:33
 */
public class DiscardClientHandler extends SimpleChannelInboundHandler<ByteBuf> {



    // 数据读事件
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        System.out.println("Client received: " + byteBuf.toString(CharsetUtil.UTF_8));
    }


    // 有通道连接事件
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("DiscardClientHandler.channelActive");
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!",CharsetUtil.UTF_8));
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("DiscardclinetHandler.channelReadComplete");
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }


    // 异常事件
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
