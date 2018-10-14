package server.threads;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import ultility.Response;

public class ListThread extends Thread{
    private ArrayList<String> nameList = new ArrayList<String>();
    private ObjectOutputStream oos;
    private String userId;
    // TODO: 计分系统

    public ListThread(ObjectOutputStream o,ArrayList<String> list, String id){
        oos = o;
        nameList = list;
        userId = id;
    }

    public void run() {
        while (!interrupted()) {
            if(nameList.size() < 1){
                continue;
            }
            try {
                Response r = new Response(Response.PLAYERLIST);
                String list = "";
                for(String name : nameList){
                    if(!name.equals(userId)){
                        list+= name+"\n";
                    }
                }
                r.setPlayerList(list);
                oos.writeObject(r);
                oos.flush();
                Thread.sleep(3000);
            }
            catch (InterruptedException e){
                System.out.println("Stop sending list");
            }
            catch (Exception e) {
                // e.printStackTrace();
                break;
            }
        }
    }

}