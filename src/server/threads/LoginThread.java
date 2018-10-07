package server.threads;
import java.net.*;
import java.util.ArrayList;
import java.io.*;

import ultility.Action;
import ultility.Response;
import ultility.Player;

public class LoginThread extends Thread{
    private ArrayList<Player> players;
    private ArrayList<String> nameList;
    private ArrayList<GameThread> gameThreadList;
    private Socket socket;
    public LoginThread(Socket socket, ArrayList<Player> players, ArrayList<String> nameList, ArrayList<GameThread> gameThreadList){
        this.players = players;
        this.nameList = nameList;
        this.gameThreadList = gameThreadList;
        this.socket = socket;
    }

    public void run(){
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        while(true){
            try {
                // 用户退出登录之后移除信息，发一个logout包？
                if (ois == null) {
                    ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                }
                Object obj = ois.readObject();
                System.out.println(obj.getClass());
                if(obj.getClass() == Action.class){
                    Action action = (Action)obj;
                    String userName = action.getUserName();
                    if (oos == null) {
                        oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    }
                    if (nameList.contains(userName)) {
                        // 返回客户端登录失败
                        Response r = new Response(Response.LOGIN);
                        r.setLoginResponse(Response.FAIL, userName + " is already connected!");
                        oos.writeObject(r);
                        oos.flush();
                        System.out.println( userName + " is already connected!");
                    }
                    else {
                        Player player = new Player(userName, socket, ois, oos);
                        synchronized (players) {
                            players.add(player);
                        }
                        synchronized (nameList) {
                            nameList.add(userName);
                        }
                        // 返回客户端登录成功
                        Response r = new Response(Response.LOGIN);
                        r.setLoginResponse(Response.SUCCESS, userName + " connect sucessful!");
                        oos.writeObject(r);
                        oos.flush();
                        System.out.println( userName + " connect sucessful!");
                        new HallThread(player, players, nameList, gameThreadList).start();
                        break;
                    }
                }
                
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
        }
    }
    
}