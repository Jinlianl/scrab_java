package server.threads;
import java.net.*;
import java.util.ArrayList;
import java.io.*;

import ultility.Action;
import ultility.Response;
import ultility.Player;

public class ListThread extends Thread{
    private ArrayList<String> nameList = new ArrayList<String>();
    private Object lock = new Object();
    private ObjectOutputStream oos;
    // TODO: 计分系统

    public ListThread(ObjectOutputStream o,ArrayList<String> list){
        oos = o;
        nameList = list;
    }

    public int getPlayersNum() {
        return nameList.size();
    }

    public ArrayList<String> getPlayerList() {
        return nameList;
    }


    public void run() {
        while (true) {
            try {
                Response r = new Response(Response.PLAYERLIST);
                String list = "";
                for(String name : nameList){
                    list+= name+"\n";
                }
                r.setPlayerList(list);
                oos.writeObject(r);
                oos.flush();
                Thread.sleep(5000);
            }
            catch (Exception e) {
                e.printStackTrace();
                synchronized (this.lock) {
                    this.lock.notifyAll();
                }
                break;
            }
        }
    }

}