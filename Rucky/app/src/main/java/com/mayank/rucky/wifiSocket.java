package com.mayank.rucky;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class wifiSocket implements Runnable{

    private String ip;
    private ArrayList<String> cmds;

    wifiSocket(ArrayList<String> cmds) {
        this.ip = "192.168.1.1";
        this.cmds = new ArrayList<>();
        this.cmds.addAll(cmds);
    }

    public void run() {
        try {
            int port = 5000;
            Socket socket = new Socket(ip, port);
            MainActivity.piConnected = true;
            while(MainActivity.piConnected) {
                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    for(int i = 0; i < cmds.size(); i++) {
                        out.println(cmds.get(i));
                    }
                    MainActivity.piConnected = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            socket.close();
        } catch (Exception e) {
            MainActivity.piConnected = false;
            e.printStackTrace();
        }
    }

}
