package com.basic;

import com.ReturnIPPort.IPPortClient;
import com.ReturnIPPort.IPPortServer;



import java.net.InetAddress;
import java.net.*;
import java.io.*;
import  java.util.regex.*;

public class ProgramEntry {
    public static void main(String[] args) {
        verCppTest20180821(args);
    }

    public static void verCppTest20180821(String[] args) { //建立返回IP&PORT的服务器，连接后向客户端返回其公网ip&port
        if (args.length > 0) {
            switch (args[0]) {
                case "S":
                    if (args.length == 2) {
                        IPPortServer ipps = new IPPortServer(args[1]);
                        ipps.StartServer();
                    } else {
                        IPPortServer ipps = new IPPortServer(null);
                        ipps.StartServer();
                    }
                    break;
                case "C":
                    if (args.length == 3) {
                        IPPortClient ippc = new IPPortClient(args[1], args[2]);
                        ippc.StartClient();
                    } else {
                        IPPortClient ippc = new IPPortClient(null, null);
                        ippc.StartClient();
                    }
                    break;
            }

        }else{
            IPPortServer ipps = new IPPortServer(null);
            ipps.StartServer();
        }
    }


    public static void GetIP() throws Exception {
        InetAddress addr = InetAddress.getLocalHost();
        String ip = addr.getHostAddress().toString(); //获取本机ip
        String hostName = addr.getHostName().toString(); //获取本机计算机名称
        System.out.println(ip);
        System.out.println(hostName);
    }

    public static String getV4IP() {
        String ip = "";
        String chinaz = "http://ip.chinaz.com";

        StringBuilder inputLine = new StringBuilder();
        String read = "";
        URL url = null;
        HttpURLConnection urlConnection = null;
        BufferedReader in = null;
        try {
            url = new URL(chinaz);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            while ((read = in.readLine()) != null) {
                inputLine.append(read + "\r\n");
            }
            //System.out.println(inputLine.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }


        Pattern p = Pattern.compile("\\<dd class\\=\"fz24\">(.*?)\\<\\/dd>");
        Matcher m = p.matcher(inputLine.toString());
        if (m.find()) {
            String ipstr = m.group(1);
            ip = ipstr;
            //System.out.println(ipstr);
        }
        return ip;
    }
}

