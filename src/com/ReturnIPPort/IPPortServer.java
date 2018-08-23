package com.ReturnIPPort;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;

import java.net.Socket;
import java.util.Date;

public class IPPortServer {
    private int port=8008;
    public IPPortServer(String port){
        if (port!=null){
            this.port = Integer.parseInt(port);
        }
    }
    public void StartServer(){
        try{
            //创建serversocket
            ServerSocket ss = new ServerSocket(port, 10);int count = 0;
            System.out.println("服务器端口："+String.valueOf(port));
            System.out.println("***服务器启动，等待客户端的连接***");
            while (true) {
                try {
                    Socket connection = ss.accept();
                    System.out.println("收到客户端链接请求，启动服务线程");
                    Thread task = new IPPortServerThread(connection);
                    task.start();
                    count++;
                } catch (Exception ex) {
                    System.out.println("无法链接客户端");
                    System.out.println("Errorlog:" + ex.toString());
                    System.out.println("StackTrace:");
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            System.out.println("无法启动服务器");
            System.out.println("Errorlog:" + ex.toString());
            System.out.println("StackTrace:");
            ex.printStackTrace();

        }
    }

    private static class IPPortServerThread extends Thread {
        private Socket connection;


        IPPortServerThread(Socket connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                InetAddress addr = connection.getInetAddress();
                Writer out = new OutputStreamWriter(connection.getOutputStream());
//                out.write("Hello,This is SKServer"+ new Date().toString());
                out.write("Your IP is:" + addr.getHostAddress() + " Your Port is:" + connection.getPort());
                out.flush();
                System.out.println("向客户端发送消息" + new Date().toString());
            } catch (IOException ex) {
                System.out.println("无法写入客户端");
                System.out.println("Errorlog:" + ex.toString());
                System.out.println("StackTrace:");
                ex.printStackTrace();
            } finally {
                try {
                    System.out.println("关闭链接");
                    connection.close(); //不关闭链接客户端的getInputStream无法解析
                } catch (IOException ex) {

                }
            }
        }
    }
}
