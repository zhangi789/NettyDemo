package com.shanutec.cn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.shanutec.cn.kit.Config;
import com.shanutec.cn.kit.EchoClient;

import io.netty.channel.ChannelHandlerContext;

/**
 * host 和port  请填写自己真实项目参数
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

   // host = "52.82.57.48";
    String host = "";
    //port=8000
    int port = 0;

    private Button  btn_conn;
    private Button  btn_break;
    ChannelHandlerContext mChannelHandlerContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initviews();
       // conn(host, port);
    }

    private void initviews() {
        btn_conn=(Button) findViewById(R.id.btn_conn);
        btn_conn.setOnClickListener(this);
        btn_break=(Button) findViewById(R.id.btn_fjdf);
        btn_break.setOnClickListener(this);
    }

    public void conn(final String host, final int port) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                new EchoClient(host, port, new EchoClient.EchoCallBack() {
                    @Override
                    public void onConnSucess(ChannelHandlerContext type,String result) {
                        mChannelHandlerContext=type;
                        Log.i("LLL", "onConnSucess " + result);
                    }

                    @Override
                    public void onReceive(String result) {
                        Log.i("LLL", "onReceive "+"\r\n" + result);
                    }

                    @Override
                    public void onExceptionTip(String result) {
                        Log.i("LLL", "onExceptionTip " + result);
                    }

                    @Override
                    public void onManualClose(String result) {
                        Log.i("LLL", "onManualClose " + result);
                    }

                    @Override
                    public void onReconn() {
                        Log.i("LLL", "onReconn-------------------------------------- ");
                        conn(host, port);
                    }
                }).start();
            }
        }).start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_conn:

                conn(host, port);
                Log.i("LLL","开始连接中。请稍等。。。。。。。。。。。。");

                break;


            case R.id.btn_fjdf:
                Log.i("LLL","关闭555555");
                if (mChannelHandlerContext != null) {
                    Log.i("LLL", "关闭4444444");
                    mChannelHandlerContext.channel().close();
                    Config.TCP_CONN_AGAIN = false;
                } else {
                    Log.i("LLL", "关闭555555");
                }

                break;
        }
    }
}
