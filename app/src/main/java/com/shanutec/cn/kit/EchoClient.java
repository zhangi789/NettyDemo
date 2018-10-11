package com.shanutec.cn.kit;

import android.util.Log;

import com.shanutec.cn.MainActivity;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;


import com.shanutec.cn.kit.Config.*;

/**
 * @author 张海洋
 * @Date on 2018/10/10.
 * @org 上海..科技有限公司
 * @describe
 */
public class EchoClient {
    private final String host;
    private final int port;
    EchoClientHandler mEchoClientHandler;
    EventLoopGroup nioEventLoopGroup = null;

    private EchoCallBack mCallBack;

    public interface EchoCallBack {

        void onConnSucess(ChannelHandlerContext type, String result);

        void onReceive(String result);

        void onExceptionTip(String result);

        void onManualClose(String result);

        void onReconn();

    }

    public EchoClient(String host, int port, final EchoCallBack mCallBack) {
        this.mCallBack = mCallBack;
        this.host = host;
        this.port = port;
        mEchoClientHandler = new EchoClientHandler(new EchoClientHandler.OnMessageListener() {
            @Override
            public void onMessage(ChannelHandlerContext type, int sign, String result) {
                switch (sign) {
                    case 1: // 连接成功
                        //在线
                        Config.COUNT = 0;
                        mCallBack.onConnSucess(type, result);
                        break;
                    case 2: // 接收数据处理
                        Config.COUNT = 0;
                        mCallBack.onReceive(result);
                        break;
                    case 3: // Netty抛出异常消息
                        Config.COUNT++;
                        mCallBack.onExceptionTip(result);
                        //断网
                        break;
                    case 4: // 手动关闭
                        Config.COUNT = 0;
                        mCallBack.onManualClose(result);
                        break;
                    case 5: // 提醒自动连接
                        mCallBack.onReconn();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void start() {
        try {
            //创建Bootstrap对象用来引导启动客户端
            Bootstrap bootstrap = new Bootstrap();
            //创建EventLoopGroup对象并设置到Bootstrap中，EventLoopGroup可以理解为是一个线程池，这个线程池用来处理连接、接受数据、发送数据
            nioEventLoopGroup = new NioEventLoopGroup();
            //创建InetSocketAddress并设置到Bootstrap中，InetSocketAddress是指定连接的服务器地址
            bootstrap.group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    //长连接
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                                 //添加一个ChannelHandler，客户端成功连接服务器后就会被执行
                                 @Override
                                 protected void initChannel(SocketChannel ch)
                                         throws Exception {
                                     ch.pipeline()
                                             .addLast(new IdleStateHandler(5, 5,5))
                                             .addLast(new StringDecoder())
                                             .addLast(new StringEncoder()).addLast(mEchoClientHandler);
                                 }
                             }

                    );
            // • 调用Bootstrap.connect()来连接服务器
            ChannelFuture f = bootstrap.connect().sync();
            // • 最后关闭EventLoopGroup来释放资源
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {

                nioEventLoopGroup.shutdownGracefully().sync();
                if (Config.TCP_CONN_AGAIN) {
                    try {
                        Log.i("LLL", "finally------------");
                        TimeUnit.SECONDS.sleep(5);
                        try {
                            //5之后重连
                            mCallBack.onReconn();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
