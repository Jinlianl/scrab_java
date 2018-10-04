package server.threads;
import java.net.*;
import java.util.ArrayList;
import java.io.*;

import ultility.Action;
import ultility.Response;
import ultility.Player;

class HallThread extends Thread{
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Player player;

    public HallThread(Player self) {
        player = self;
        oos = player.getOos();
        ois = player.getOis();
    }

    public void run() {
        while (true) {
            System.out.println("hahahahahahahaha");
            try {
                Action a = (Action) ois.readObject();
                if (a != null) {
                    int type = a.getActionType();
                    System.out.println(type);
                    if (type == Action.JOIN) {
                        Response r = new Response(Response.JOIN);
                        r.setJoinStatus(Response.SUCCESS, "new game started");
                        System.out.println(r);
                        oos.writeObject(r);
                        oos.flush();
                        // TODO: 同步更新一个GameThread，添加player，人满start()
                        GameThread t = new GameThread();
                        t.addPlayers(player);
                        t.start();
                        t.join();
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
