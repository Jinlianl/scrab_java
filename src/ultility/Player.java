package ultility;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Player{
    String userName;
    Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    
    public Player(String userName, Socket socket, ObjectInputStream in, ObjectOutputStream out){
        this.userName = userName;
        this.socket = socket;
        this.oos = out;
        this.ois = in;
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

    public ObjectInputStream getOis() {
        return ois;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }
}