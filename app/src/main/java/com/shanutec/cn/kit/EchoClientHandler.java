package com.shanutec.cn.kit;

import android.util.Log;

import com.shanutec.cn.MainActivity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author 张海洋
 * @Date on 2018/10/10.
 * @org 上海..科技有限公司
 * @describe
 */
public class EchoClientHandler extends SimpleChannelInboundHandler<String> {
    OnMessageListener call;

    public EchoClientHandler(OnMessageListener call) {
        this.call = call;
    }

    /**
     * 向外部暴露一个接口
     */
    public interface OnMessageListener {
        void onMessage(ChannelHandlerContext type, int sign, String result);
    }
    public OnMessageListener onMessageListener;


    //客户端连接服务器 标志成功
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        call.onMessage(ctx, Config.TCP_CONN_SUCCESS, "连接成功");
    }
    //从服务器接收到数据后调用
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String result) throws Exception {
        call.onMessage(ctx, Config.TCP_RECEIVE, result);
    }

    //发生异常时被调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        Log.i("LLL", "onExceptionTip " + cause.getMessage());
        call.onMessage(ctx,Config.TCP_CONN_Exception_MSG,"异常消息提示,请查看Log");
        // 释放资源
        ctx.close();

    }
    //客户端断开
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (onMessageListener != null)
            onMessageListener.onMessage(ctx, Config.TCP_MANUAL_CLOSE, "客户端断开");
        if (ctx != null)
            ctx.close();
    }
    /**
     *    为了减少服务器端压力  默认服务器5s没有处理   服务端可以认为客户端挂了
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                Log.i("LLL", "------IdleState.ALL_IDLE------");

                if (Config.COUNT < 1 && Config.TCP_CONN_AGAIN) {
                    Config.COUNT++;
                } else {
                    onMessageListener.onMessage(ctx, Config.TCP_RECONN, "客户端断开");
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }







}
