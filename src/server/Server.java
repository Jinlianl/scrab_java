package server;
import java.net.*;

import server.threads.GameThread;
import server.threads.LoginThread;
import ultility.Player;
import java.io.*;
import java.util.ArrayList;

public class Server{
    private ArrayList<Player> players = new ArrayList<Player>();
    private ArrayList<String> nameList = new ArrayList<String>();
    private ArrayList<GameThread> gameThreadList = new ArrayList<GameThread>();

    private void run(int port) throws IOException{
        ServerSocket server = new ServerSocket(port);
        // start a login thread for socket connection
        while (true) {
            try {
                System.out.println("waiting for connection ...");
                Socket s = server.accept();
                LoginThread loginThread = new LoginThread(s, players, nameList, gameThreadList);
                loginThread.start();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) throws IOException {
        // Parsing service port and dictionary directory
        int port = 1234;
        if(args.length < 1){
            System.out.println("argument missed, port set to default "+port);
        }else{
            port = Integer.parseInt(args[0]);
        }
        //initialize for loading dictionary
        Server s = new Server();
        s.run(port);

    }
}