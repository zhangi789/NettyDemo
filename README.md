## NettyDemo
Netty 封装 功能：开启，关闭，断电重连，心跳等，具体请参考demo

## Usage
-----
```xml
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
