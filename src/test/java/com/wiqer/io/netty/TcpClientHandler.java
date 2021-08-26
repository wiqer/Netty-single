package com.wiqer.io.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TcpClientHandler   extends SimpleChannelInboundHandler<String> {
    public  static volatile int msgcount=0;
    public  static volatile int count=0;
    //收到服务器的消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if(TcpClientHandler.msgcount%1000==0)
            System.out.println("已经返回："+msgcount+"条消息");
        TcpClientHandler.msgcount++;
    }
    //连接成功
    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }
    //断开连接
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        //此处编写断开连接的业务逻辑
    }
}
