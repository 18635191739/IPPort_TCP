package com.ReturnIPPort;

import com.basic.ErrorHandler;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;

public class IPPortClient {
    private String hostname = "localhost";
    private int port = 8008;
    private int localPort = 12345;
    private String msg ="NeedKnowIPPort";

    public IPPortClient(String hostname, String port, String localport,String msg) {
        if (hostname != null) {
            this.hostname = hostname;
        }
        if (port != null) {
            this.port = Integer.parseInt(port);
        }
        if (localport != null) {
            this.localPort = Integer.parseInt(localport);
        }
        if(msg!=null){
            this.msg=msg;
        }
    }

    public void StartClient() {
        try {
                Socket sk = new Socket();
                //设置socket
                sk.setReuseAddress(true);
                System.out.println("ReuseAddress:" + sk.getReuseAddress());
                sk.bind(new InetSocketAddress(localPort));
//            sk.bind(new InetSocketAddress(
//                    InetAddress.getLocalHost().getHostAddress(), localPort));
                //sk.setSoTimeout(15000);
                System.out.println("开始链接服务器");
                sk.connect(new InetSocketAddress(hostname, port));
                InputStream in = sk.getInputStream();
                //向服务器发送信息
                Writer out = new OutputStreamWriter(sk.getOutputStream(),"UTF-8");
                out.write(this.msg+"\r\n");
                out.flush();
                System.out.println("向服务器发送消息:" + msg);
                System.out.println("等待服务器信息,本地端口" + sk.getLocalPort()+ sk.getReuseAddress());
                StringBuilder stringBuilder = new StringBuilder();
                InputStreamReader reader = new InputStreamReader(in);
                for (int c = reader.read(); c != -1; c = reader.read()) {
                    stringBuilder.append((char) c);
                }
                System.out.println(stringBuilder);

        } catch (Exception ex) {
            ErrorHandler.DoWhenError(ex, "无法链接到服务器");
        }

    }
}
