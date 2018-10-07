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
    private ArrayList<Player> players;
    private ArrayList<String> nameList;
    private ArrayList<GameThread> gameThreadList;

    public HallThread(Player self, ArrayList<Player> p, ArrayList<String> n, ArrayList<GameThread> g) {
        player = self;
        players = p;
        nameList = n;
        gameThreadList = g;
        oos = player.getOos();
        ois = player.getOis();
    }

    public void run() {
        // TODO: 时不时发送已登录用户信息
        while (true) {
            try {
                Action a = (Action) ois.readObject();
                if (a != null) {
                    int type = a.getActionType();
                    //System.out.println(type);
                    if (type == Action.LOGOUT) {
                        synchronized (this.nameList) {
                            this.nameList.remove(player.getUserName());
                        }
                        synchronized (this.players) {
                            this.players.remove(player);
                        }
                        break;
                    }
                    switch (type) {
                        case Action.NEW:
                            GameThread t = new GameThread();
                            t.addPlayers(player);
                            synchronized (this.gameThreadList) {
                                this.gameThreadList.add(t);
                            }
                            Response r1 = new Response(Response.JOIN);
                            r1.setJoinStatus(Response.SUCCESS, "Waiting for start.", this.gameThreadList.size()-1);
                            oos.writeObject(r1);
                            oos.flush();
                            break;
                        case Action.JOIN:
                            // 同步更新一个GameThread，添加player
                            int i = 0;
                            for (; i < gameThreadList.size(); i++)
                                if (gameThreadList.get(i).getPlayersNum() < 4 && !gameThreadList.get(i).isStarted()) {
                                    synchronized (this.gameThreadList) {
                                        gameThreadList.get(i).addPlayers(player);
                                    }
                                    break;
                                }
                            Response r2 = new Response(Response.JOIN);
                            if (i == gameThreadList.size()) {
                                r2.setJoinStatus(Response.FAIL, "No game available.", -1);
                            }
                            else {
                                r2.setJoinStatus(Response.SUCCESS, "Waiting for start.", i);
                            }
                            oos.writeObject(r2);
                            oos.flush();
                            break;
                        case Action.STARTGAME:
                            Response r3 = new Response(Response.STARTGAME);
                            oos.writeObject(r3);
                            oos.flush();
                            this.gameThreadList.get(a.getGameID()).start();
                            this.gameThreadList.get(a.getGameID()).join();
                            break;
                        default:
                            break;
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
