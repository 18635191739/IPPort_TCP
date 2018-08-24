package com.ReturnIPPort;

import com.basic.ErrorHandler;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;

import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IPPortServer {
    private int port = 8008;
    private String firstAddress = "";
    private String secondAddress = "";
    private boolean sendPenetrationCommand;

    private ExecutorService pool = Executors.newFixedThreadPool(10);

    public IPPortServer(String port) {
        if (port != null) {
            this.port = Integer.parseInt(port);
        }
    }

    public void StartServer() {
        try {
            //创建serversocket
            ServerSocket ss = new ServerSocket(port, 10);
            int count = 0;
            System.out.println("服务器端口：" + String.valueOf(port));
            System.out.println("***服务器启动，等待客户端的连接***");
            while (true) {
                try {
                    Socket connection = ss.accept();
                    System.out.println("收到客户端链接请求，启动服务线程");
                    Callable<Void> task = new IPPortServerThread(connection);
//                    Thread task = new IPPortServerThread(connection);
                    pool.submit(task);
                    count++;
                } catch (Exception ex) {
                    ErrorHandler.DoWhenError(ex,"无法链接客户端");
                }
            }
        } catch (IOException ex) {
            ErrorHandler.DoWhenError(ex,"无法启动服务器");
        }
    }

    private class IPPortServerThread implements Callable<Void> {
        private Socket connection;

        IPPortServerThread(Socket connection) {
            this.connection = connection;
        }

        @Override
        public Void call() {
            try {
                //获取客户端地址
                InetAddress addr = connection.getInetAddress();
                //等待客户端发言
                BufferedReader read = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = read.readLine()) != null) {
                    System.out.println("客户端说：" + line);

                    //给客户端发消息
                    if (line.contains("NeedKnowIPPort")) {
                        Writer out = new OutputStreamWriter(connection.getOutputStream());
//                out.write("Hello,This is SKServer"+ new Date().toString());
                        out.write("Your IP is:" + addr.getHostAddress() + " Your Port is:"
                                + connection.getPort());
                        out.flush();
                        System.out.println("向客户端发送消息" + new Date().toString());
                        break;
                    } else if (line.contains("FirstClient")) {
                        firstAddress = addr.getHostAddress() + ":" + connection.getPort();
                        int counter = 0; //计时器
                        while (true)//等待下达打洞命令
                        {
                            counter++;
                            if (sendPenetrationCommand) {
                                Writer out = new OutputStreamWriter(connection.getOutputStream());
                                out.write("Address:" + secondAddress);
                                out.flush();
                                System.out.println("向客户端发送打洞命令" + new Date().toString());
                                sendPenetrationCommand = false;//重置打洞命令标志
                                break;
                            }
                            if(counter>=60){ //60秒超时时间
                                break;
                            }
                            try {
                                Thread.sleep(1000);//每秒刷新一次
                            }
                            catch (InterruptedException ex){
                                ErrorHandler.DoWhenError(ex,"线程sleep出错");
                            }
                        }

                        break;
                    } else if (line.contains("SecondClient")) {
                        secondAddress = addr.getHostAddress() + ":" + connection.getPort();
                        Writer out = new OutputStreamWriter(connection.getOutputStream());
                        out.write("Address:" + firstAddress);
                        out.flush();
                        System.out.println("向客户端发送打洞命令" + new Date().toString());
                        sendPenetrationCommand = true; //向一号客户端链接线程下达打洞命令
                        break;
                    } else {
                        break;
                    }
                }


            } catch (IOException ex) {
                ErrorHandler.DoWhenError(ex,"无法写入客户端");
            } finally {
                try {
                    System.out.println("关闭链接,并结束线程");
                    connection.close();
                } catch (IOException ex) {
                    ErrorHandler.DoWhenError(ex,"无法关闭链接");
                }
            }
            return null;
        }
    }
}
