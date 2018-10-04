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
            try {
                Action a = (Action) ois.readObject();
                if (a != null) {
                    int type = a.getActionType();
                    if (type == Action.JOIN) {
                        Response r = new Response(Response.JOIN);
                        r.setJoinStatus(Response.SUCCESS, "new game started");
                        oos.writeObject(r);
                        oos.flush();
                        // TODO: 同步更新一个GameThread，添加player，人满start()
                        GameThread t = new GameThread();
                        t.addPlayers(player);
                        t.start();
                        break;
                        // TODO: 在一场游戏结束后怎么重新把循环run起来
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
