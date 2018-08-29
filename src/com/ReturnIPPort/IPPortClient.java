package com.ReturnIPPort;

import com.basic.ErrorHandler;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;

public class IPPortClient {
    private String hostname = "localhost";
    private int port = 8008;
    private int localPort = 12345;
    private String msg = "NeedKnowIPPort";
    private PrintWriter pw;

    public IPPortClient(String hostname, String port, String localport, String msg) {
        if (hostname != null) {
            this.hostname = hostname;
        }
        if (port != null) {
            this.port = Integer.parseInt(port);
        }
        if (localport != null) {
            this.localPort = Integer.parseInt(localport);
        }
        if (msg != null) {
            this.msg = msg;
        }
    }

    public void StartClient() {
        try {
            Socket sk = new Socket();
            //设置socket
            sk.setReuseAddress(true);
            System.out.println("ReuseAddress:" + sk.getReuseAddress());
            System.out.println("绑定端口:"+new InetSocketAddress(localPort));
            sk.bind(new InetSocketAddress(localPort));
//            sk.bind(new InetSocketAddress(
//                    InetAddress.getLocalHost().getHostAddress(), localPort));
            //sk.setSoTimeout(15000);
            System.out.println("开始链接服务器:"+new InetSocketAddress(hostname, port));// + hostname + ":" + port);
            sk.connect(new InetSocketAddress(hostname, port));
            InputStream in = sk.getInputStream();
            //向服务器发送信息
            Writer out = new OutputStreamWriter(sk.getOutputStream(), "UTF-8");
            out.write(this.msg + "\r\n");
            out.flush();
            System.out.println("向服务器发送消息:" + msg);
            System.out.println("等待服务器信息,本地端口" + sk.getLocalPort() + sk.getReuseAddress());
            StringBuilder stringBuilder = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(in);
            for (int c = reader.read(); c != -1; c = reader.read()) {
                stringBuilder.append((char) c);
            }

            PortTest(); //尝试重绑端口

            String info = stringBuilder.toString();

            //开始打洞
            if (info != null && info.startsWith("Address")) {
                if (msg.contains("FirstClient")) {//FirstClient
                    String[] infos = info.split(":");
                    //目标外网地址
                    String ip = infos[1];
                    //目标外网端口
                    String port = infos[2];

                    doPenetration(ip, Integer.valueOf(port));
                } else {

                }
            }
            System.out.println(stringBuilder);
        } catch (Exception ex) {
            ErrorHandler.DoWhenError(ex, "无法链接到服务器");
        }

    }

    /*
     * portTest
     */
    private void PortTest() {
        try {
            //异步对目标发起连接
            new Thread() {
                public void run() {
                    try {
                        Socket newsocket = new Socket();
                        newsocket.setReuseAddress(true);
                        System.out.println("重绑端口:"+new InetSocketAddress(localPort));
                        newsocket.bind(new InetSocketAddress(localPort));

                        System.out.println("询问IPPort" + new InetSocketAddress(hostname,port));

                        newsocket.connect(new InetSocketAddress(hostname, port));

                        System.out.println("connect success");

                        BufferedReader b = new BufferedReader(
                                new InputStreamReader(newsocket.getInputStream()));
                        PrintWriter p = new PrintWriter(newsocket.getOutputStream());


                            p.write("msg\n");
                            p.flush();

                            String message = b.readLine();

                            System.out.println(message);

//                            pw.write(message + "\n");
//                            pw.flush();


                        p.close();
                        newsocket.close();
                    } catch (Exception e) {
                        ErrorHandler.DoWhenError(e, "重绑定端口出错");
                    }
                }
            }.start();
        } catch (Exception e) {
            ErrorHandler.DoWhenError(e, "打洞时出错");
        }
    }

    /*
     * 对目标服务器进行穿透
     */
    private void doPenetration(String ip, int port) {
        try {
            //异步对目标发起连接
            new Thread() {
                public void run() {
                    try {
                        System.out.println("Get Penetration Command:"+ip+":"+port);
                        Socket newsocket = new Socket();

                        newsocket.setReuseAddress(true);
                        newsocket.bind(new InetSocketAddress(
                                InetAddress.getLocalHost().getHostAddress(), localPort));

                        System.out.println("connect to " + new InetSocketAddress(ip, port));

                        newsocket.connect(new InetSocketAddress(ip, port));

                        System.out.println("connect success");

                        BufferedReader b = new BufferedReader(
                                new InputStreamReader(newsocket.getInputStream()));
                        PrintWriter p = new PrintWriter(newsocket.getOutputStream());

                        while (true) {

                            p.write("hello " + System.currentTimeMillis() + "\n");
                            p.flush();

                            String message = b.readLine();

                            System.out.println(message);

                            pw.write(message + "\n");
                            pw.flush();

                            if ("exit".equals(message)) {
                                break;
                            }

                            Thread.sleep(1000l);
                        }

                        b.close();
                        p.close();
                        newsocket.close();
                    } catch (Exception e) {
                        ErrorHandler.DoWhenError(e, "打洞时出错");
                    }
                }
            }.start();
        } catch (Exception e) {
            ErrorHandler.DoWhenError(e, "打洞时出错");
        }
    }
}
