package server.threads;
import java.net.*;
import java.util.ArrayList;
import java.io.*;

import ultility.*;

class LoginThread extends Thread{
    private ArrayList<Player> players;
    private ArrayList<String> nameList;
    public LoginThread(ArrayList<Player> players, ArrayList<String> nameList){
        this.players = players;
        this.nameList = nameList;
    }

    public void run(){
        InputStream clientInput;
        ObjectInputStream ois;
        while(true){
            System.out.println("waiting for connection ...");
            Socket socket=s.accept(); // Wait and accept a connection
            try {
                clientInput = socket.getInputStream();
                clientOutput = socket.getOutputStream();
                //get login info from client
                ois = new ObjectInputStream(new BufferedInputStream(clientInput));
                Action obj = ois.readObject();
                if( obj.getActionType() == Action.LOGIN){
                    String userName = obj.getUserName();
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