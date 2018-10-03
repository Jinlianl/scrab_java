package server.threads;
import java.net.*;
import java.util.ArrayList;
import java.io.*;

import ultility.*;

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
        while(true){
            System.out.println("waiting for connection ...");
            try {
                Socket socket = s.accept(); // Wait and accept a connection
                clientInput = socket.getInputStream();
                //get login info from client
                ois = new ObjectInputStream(new BufferedInputStream(clientInput));
                Object obj = ois.readObject();
                System.out.println(obj.getClass());
                if( obj.getClass() == Action.class){
                    Action action = (Action)obj;
                    String userName = action.getUserName();
                    Player player = new Player(userName, socket);
                    players.add(player);
                    nameList.add(userName);
                    System.out.println( userName + "connect sucessful!");
                }
                
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
        }
    }
    
}