package server.threads;
import java.net.*;
import java.util.ArrayList;
import java.io.*;

import ultility.Action;
import ultility.Response;
import ultility.Player;

public class HallThread extends Thread{
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Player player;
    private ArrayList<Player> players;
    private ArrayList<String> nameList;
    private ArrayList<GameThread> gameThreadList;
    private Thread playerListThread;
    private Object lock;

    public HallThread(Player self, ArrayList<Player> p, ArrayList<String> n, ArrayList<GameThread> g) {
        player = self;
        players = p;
        nameList = n;
        gameThreadList = g;
        oos = player.getOos();
        ois = player.getOis();
        player.setHallThread(this);
        playerListThread = new ListThread(oos, nameList, player.getUserName());

    }

    public void setLock(Object lock) {
        this.lock = lock;
    }

    public void LockWait() {
        synchronized (this.lock) {
            try {
                System.out.println("I am locked");
                this.lock.wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastRoomPlayers(GameThread gThread){
        Response r = new Response(Response.ROOMLIST);
        String list = "";
        for(int i = 0; i < gThread.getPlayersNum();i++){
            list += gThread.getPlayers().get(i).getUserName()+"\n";
        }
        r.setRoomlist(list);
        for(int i = 0; i < gThread.getPlayersNum();i++){
            try {
                gThread.getPlayers().get(i).getOos().writeObject(r);
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
    }

    public void run() {
        // TODO: 时不时发送已登录用户信息
        try {
            playerListThread.start();
        } catch (Exception e) {
            //TODO: handle exception
        }
        while (true) {
            try {
                Action a = (Action) ois.readObject();
                if (a != null) {
                    int type = a.getActionType();
                    //System.out.println("server code:"+type);                    
                    if (type == Action.LOGOUT) {
                        playerListThread.interrupt();
                        synchronized (this.nameList) {
                            this.nameList.remove(player.getUserName());
                        }
                        synchronized (this.players) {
                            this.players.remove(player);
                        }
                        player.closeSocket();
                        break;
                    }
                    switch (type) {
                        case Action.NEW:
                            GameThread t = new GameThread();
                            t.setGameID();
                            t.addPlayers(player);
                            synchronized (this.gameThreadList) {
                                this.gameThreadList.add(t);
                            }
                            setLock(t.getLock());
                            Response r1 = new Response(Response.JOIN);
                            r1.setJoinStatus(Response.SUCCESS, "Waiting for start.", t.getGameID());
                            oos.writeObject(r1);
                            oos.flush();
                            System.out.println("game lobby created. Game ID : "+t.getGameID());
                            broadcastRoomPlayers(t);
                            break;
                        case Action.INVITE:
                            String inviteId = a.getInvitedID();
                            int gameID = a.getGameID();
                            for(Player p:players){
                                if(p.getUserName().equals(inviteId)){
                                    Response r = new Response(Response.INVITE);
                                    r.setInviteFrom(player.getUserName());
                                    r.setGameID(gameID);
                                    p.getOos().writeObject(r);
                                    p.getOos().flush();
                                }
                            }
                            break;
                        case Action.JOIN:
                            // 同步更新一个GameThread，添加player
                            Response r2 = new Response(Response.JOIN);
                            boolean success = false;
                            int ID = -1;
                            // 先判断是否有带房间ID
                            if(a.getGameID() > 0){
                                System.out.print("joining "+ a.getGameID());
                                for(int index = 0;index < gameThreadList.size();index++){
                                    if(gameThreadList.get(index).getGameID() == a.getGameID()){
                                        System.out.println("player enters ID: "+a.getGameID()+"room");
                                        synchronized (this.gameThreadList) {
                                            gameThreadList.get(index).addPlayers(player);
                                        }
                                        setLock(gameThreadList.get(index).getLock());
                                        // 更新其他玩家已加入游戏的列表
                                        broadcastRoomPlayers(gameThreadList.get(index));
                                        break;
                                    }
                                }
                                r2.setJoinStatus(Response.SUCCESS, "Waiting for start.", a.getGameID());
                            }else{
                                //如果没有邀请，将随机加入游戏
                                System.out.print("random joining "+ a.getGameID());
                                for (int i = 0; i < gameThreadList.size(); i++)
                                if (gameThreadList.get(i).getPlayersNum() < 4 && !gameThreadList.get(i).isStarted()) {
                                    synchronized (this.gameThreadList) {
                                        gameThreadList.get(i).addPlayers(player);
                                    }
                                    ID = gameThreadList.get(i).getGameID();
                                    success = true;
                                    setLock(gameThreadList.get(i).getLock());
                                    broadcastRoomPlayers(gameThreadList.get(i));
                                    break;
                                }
                                if (!success) {
                                    r2.setJoinStatus(Response.FAIL, "No game available.", -1);
                                }
                                else {
                                    
                                    r2.setJoinStatus(Response.SUCCESS, "Waiting for start.", ID);
                                }
                            }
                            oos.writeObject(r2);
                            oos.flush();
                            break;
                        case Action.STARTGAME:
                            // 只允许start new game的人start game
                            Response r3 = new Response(Response.STARTGAME);
                            int gID = a.getGameID();
                            GameThread gThread;
                            for(int index = 0;index < gameThreadList.size();index++){
                                if(gameThreadList.get(index).getGameID() == gID){
                                    gThread = gameThreadList.get(index);
                                    for (int j = gThread.getPlayersNum()-1; j >= 0; j--) {
                                        gThread.getPlayers().get(j).getOos().writeObject(r3);
                                        gThread.getPlayers().get(j).getOos().flush();
                                    }
                                    gThread.start();
                                    break;
                                }
                            }

                            //LockWait();
                            break;
                        case Action.STARTED:
                            System.out.println("GAME STARTED " + player.getUserName());
                            Response r4 = new Response(Response.STARTED);
                            oos.writeObject(r4);
                            oos.flush();
                            LockWait();
                        default:
                            break;
                    }
                }
            }
            catch (EOFException e){
                 System.out.println("steam closed");
            }
            catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
