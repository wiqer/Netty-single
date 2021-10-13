package io.github.wiqer.io.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class DemoTcpClient {
    //属性
    EventLoopGroup group ;
    Bootstrap bootstrap ;
    //private volatile int count;
    public DemoTcpClient() {
        /*怕单线程扛不住*/
        group = new NioEventLoopGroup(1);
        StringEncoder stringEncoder = new StringEncoder();
        StringDecoder stringDecoder=new StringDecoder();
        ByteBuf delimiterBased= Unpooled.copiedBuffer("$(..)$".getBytes());
        bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                        //得到pipeline
                        ChannelPipeline pipeline = ch.pipeline();
                        //加入相关handler
                        //解码器是有先后顺序的,有状态的的解码器不可以单例使用
                        pipeline.addLast(new DelimiterBasedFrameDecoder(2*1024*1024,delimiterBased));
                        pipeline.addLast("decoder",stringDecoder);
                        pipeline.addLast("encoder", stringEncoder);
                        pipeline.addLast(new IdleStateHandler(0,0,30));
                        //加入自定义的handler
                        pipeline.addLast(new TcpClientHandler());
                    }
                });
    }

    public void run(int n,String host, int port) throws Exception{
        try {

            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            //得到channel

            Channel channel = channelFuture.channel();
            System.out.println("-------" + channel.localAddress()+ "--------");

            int counts=0;
            while (true) {

                long startTime = System.currentTimeMillis();    //获取开始时间
                for(int i=n;i<1000;i+=8){
                    TcpClientHandler.count++;
//                   //不使用StringEncoder编码器可以直接使用Buf发送
//                    ByteBuf buf= Unpooled.copiedBuffer(i+">^ω^<", CharsetUtil.UTF_8);
//                    channel.writeAndFlush(buf);
                    channel.writeAndFlush(i+"aaaaaa"+ "$(..)$");
                    channel.id();
//                    finally {
//                        buf.release();
//                    }
                    //buf.release();
//                    while (GroupChatClientHandler.msgcount+8<GroupChatClientHandler.count){
//                        Thread.sleep(10);
//                    }

                }
                //channel.flush();
                long endTime = System.currentTimeMillis();    //获取结束时间
                System.out.println("程序运行时间：" + (endTime - startTime) + "ms,GroupChatClientHandler.msgcount="
                        + TcpClientHandler.msgcount+",count="+ TcpClientHandler.count+"添加次数+"+(++counts));    //输出程序运行时间
                while (TcpClientHandler.count> TcpClientHandler.msgcount)
                    Thread.sleep(1);
            }

//            //客户端需要输入信息，创建一个扫描器
//            Scanner scanner = new Scanner(System.in);
//            while (scanner.hasNextLine()) {
//                String msg = scanner.nextLine();
//                //通过channel 发送到服务器端
//                channel.writeAndFlush(msg + "\r\n");
//            }
        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        //new GroupChatClient("127.0.0.1", 36668).run();
        runMuls(1);
    }
    public  static  void  runMul(int startPort,int n){
        EventExecutorGroup multipleGroup = new DefaultEventExecutorGroup(n);//业务线程池

        for (int i=0; i<n;i++) {
            int nIndex=i;
            multipleGroup.execute(() -> {
                try {
                    new DemoTcpClient().run(nIndex,"127.0.0.1", startPort+nIndex);;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
    public  static  void  runMuls(int n){
        EventExecutorGroup multipleGroup = new DefaultEventExecutorGroup(n);//业务线程池

        for (int i=0; i<n;i++) {
            int nIndex=i;
            multipleGroup.execute(() -> {
                try {
                    runMul(1921,1);;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
