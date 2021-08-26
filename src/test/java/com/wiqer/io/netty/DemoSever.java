package com.wiqer.io.netty;

import com.wiqer.io.netty.channel.nio.NioSingleEventLoopGroup;
import com.wiqer.io.netty.channel.socket.NioSingleServerSocketChannel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.TimeUnit;

public class DemoSever {
    public void startNettyServer(int port) throws Exception {
        //boss绑定线程
        EventLoopGroup bossGroup = new NioSingleEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, bossGroup)
                    .channel(NioSingleServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //出来网络io事件，如记录日志、对消息编解码等
                    .childHandler(new ChildChannelHandler());
            //绑定端口，同步等待成功
            ChannelFuture future = bootstrap.bind(port).sync();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                bossGroup.shutdownGracefully (1000, 3000, TimeUnit.MILLISECONDS);
                //bossGroup.shutdownGracefully (1000, 3000, TimeUnit.MILLISECONDS);
            }));
            //等待服务器监听端口关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            //do nothing
            System.out.println("netty stop");
        } finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            //workerGroup.shutdownGracefully();
        }
    }
    /**
     * handler类
     */
    private class ChildChannelHandler extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel ch) {
            NodesServerHandler serverHandler = new NodesServerHandler();

            ByteBuf delimiter = Unpooled.copiedBuffer("$(..)$".getBytes());
            ch.pipeline()
                    .addLast(new DelimiterBasedFrameDecoder(1024, delimiter))
                    .addLast(new StringDecoder())
                    .addLast(serverHandler);
        }
    }
    public static void main(String[] args) throws Exception {
        new DemoSever().startNettyServer(1921);
    }
}
