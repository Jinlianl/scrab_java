package ultility;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import server.threads.HallThread;

public class Player{
    String userName;
    Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private HallThread hallThread;
    
    public Player(String userName, Socket socket, ObjectInputStream in, ObjectOutputStream out){
        this.userName = userName;
        this.socket = socket;
        this.oos = out;
        this.ois = in;
    }

    public void setHallThread(HallThread hallThread) {
        this.hallThread = hallThread;
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

    public HallThread getHallThread() {
        return hallThread;
    }
}