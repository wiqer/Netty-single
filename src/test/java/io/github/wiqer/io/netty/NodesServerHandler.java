package test.java.io.github.wiqer.io.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.List;

public class NodesServerHandler extends SimpleChannelInboundHandler<String> {

    int count=0;
    long startTime = System.currentTimeMillis();    //获取开始时间
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws InterruptedException {

        if(count==0){
            startTime = System.currentTimeMillis();
            System.out.println("开始接收数据");
        }
        count++;
        if(count>=99999)System.out.println("程序运行时间：" + (System.currentTimeMillis() - startTime) + "ms,总条数："+count);
        //System.out.println(message);
        ByteBuf content = Unpooled.copiedBuffer("$(..)$ok\n", CharsetUtil.UTF_8);
        ctx.channel().writeAndFlush(content).sync();;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        ctx.close();
        super.channelInactive(ctx);
    }


}
