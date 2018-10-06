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
    private ServerSocket s;
    public LoginThread(ArrayList<Player> players, ArrayList<String> nameList, ServerSocket serverSocket){
        this.players = players;
        this.nameList = nameList;
        this.s = serverSocket;
    }

    public void run(){
        InputStream clientInput;
        ObjectInputStream ois;
        ObjectOutputStream oos;
        while(true){
            System.out.println("waiting for connection ...");
            try {
                // TODO: 用户退出登录之后移除信息，发一个logout包？
                Socket socket = s.accept(); // Wait and accept a connection
                System.out.println("new connection");
                clientInput = socket.getInputStream();
                //get login info from client
                ois = new ObjectInputStream(new BufferedInputStream(clientInput));
                Object obj = ois.readObject();
                System.out.println(obj.getClass());
                if( obj.getClass() == Action.class){
                    Action action = (Action)obj;
                    String userName = action.getUserName();
                    oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
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
                        players.add(player);
                        nameList.add(userName);
                        // 返回客户端登录成功
                        Response r = new Response(Response.LOGIN);
                        r.setLoginResponse(Response.SUCCESS, userName + " connect sucessful!");
                        oos.writeObject(r);
                        oos.flush();
                        System.out.println( userName + " connect sucessful!");
                        new HallThread(player).start();
                    }
                }
                
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
        }
    }
    
}