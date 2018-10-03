package ultility;

import java.net.Socket;

public class Player{
    String userName;
    Socket socket;
    
    public Player(String userName,Socket socket){
        this.userName = userName;
        this.socket = socket;
    }
    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }
    /**
     * @return the socket
     */
    public Socket getSocket() {
        return socket;
    }
}