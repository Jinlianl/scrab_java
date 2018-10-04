package server;
import java.net.*;
import java.util.ArrayList;
import server.threads.*;
import java.io.*;
import ultility.Player;
import ultility.Action;
import ultility.Response;

public class Server{
    public static void main(String args[]) throws IOException {
        // Parsing service port and dictionary directory
        int port = 1234;
        if(args.length < 1){
            System.out.println("argument missed, port set to default "+port);
        }else{
            port = Integer.parseInt(args[0]);
        }
        ArrayList<String> nameList = new ArrayList<String>();
        ArrayList<Player> players = new ArrayList<Player>();
        //initialize for loading dictionary
        ServerSocket s = new ServerSocket(port);
        // start a login thread for socket connection
        LoginThread loginThread = new LoginThread(players,nameList,s);
        loginThread.start();
        
    }
}