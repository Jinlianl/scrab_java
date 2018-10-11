package server.threads;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import ultility.Response;

public class ListThread extends Thread{
    private ArrayList<String> nameList = new ArrayList<String>();
    private ObjectOutputStream oos;
    // TODO: 计分系统

    public ListThread(ObjectOutputStream o,ArrayList<String> list){
        oos = o;
        nameList = list;
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
                Thread.sleep(3000);
            }
            catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

}