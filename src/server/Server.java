package server;
import java.net.*;
import server.threads.*;
import java.io.*;

public class Server{
    public static void main(String args[]) throws IOException {
        // Parsing service port and dictionary directory
        int port = 1234;
        if(args.length < 1){
            System.out.println("argument missed, port set to default "+port);
        }else{
            port = Integer.parseInt(args[0]);
        }
        //initialize for loading dictionary
        ServerSocket s = new ServerSocket(port);
        // start a login thread for socket connection
        LoginThread loginThread = new LoginThread(s);
        loginThread.start();
        
    }
}