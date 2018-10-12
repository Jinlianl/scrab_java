/**
* The serializable class is used for object transport,
* load and transport via ObjectStream
* it contains the info of userID, input letter and coordinates
*
* @author Jinliang Liao
*/
package ultility;
import java.io.Serializable;

public class Action implements Serializable{
    public static final int LOGIN = 0;
    public static final int JOIN = 1;
    public static final int MOVE = 2;
    public static final int PASS = 3;
    public static final int JUDGE = 4;
    public static final int READY = 5;
    public static final int STARTED = 6;
    public static final int STARTGAME = 7;
    public static final int NEW = 8;
    public static final int ENDGAME = 9;
    public static final int LOGOUT = 10;
    public static final int INVITE = 11;
    private static final long serialVersionUID = 1L;
    
    private int actionType;
    private String userName;
    private int coor_x;
    private int coor_y;
    private String input;
    private String expectWord;
    private boolean agree;
    private int gameID;
    private String invitedID;


    public Action(int type){
        actionType = type;
    }

    /**
     * set the info for a join game request
     */
    public void setLoginInfo(String userName) {
        this.userName = userName;
    }
    /**
     * set the info for a join game request
     */
    public void setJoinGameInfo(String userName) {
        this.userName = userName;
    }

    public void setInviteID(String id) {
        this.invitedID = id;
    }

    /**
     * @param input the input to set
     */
    public void setMoveInfo(int x,int y,String input, String word) {
        this.input = input;
        this.coor_x = x;
        this.coor_y = y;
        this.expectWord = word;
    }

    public void setJudgeINfo(boolean b) {
        this.agree = b;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    /**
     * @return the actionType
     */
    public int getActionType() {
        return actionType;
    }
    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return the invitedID
     */
    public String getInvitedID() {
        return invitedID;
    }
    /**
     * @return the coor_x
     */
    public int getCoor_x() {
        return coor_x;
    }
    /**
     * @return the coor_y
     */
    public int getCoor_y() {
        return coor_y;
    }
    /**
     * @return the input
     */
    public String getInput() {
        return input;
    }
    /**
     * @return the expectWord
     */
    public String getExpectWord() {
        return expectWord;
    }

    public boolean isAgree() {
        return agree;
    }

    public int getGameID() {
        return gameID;
    }
}