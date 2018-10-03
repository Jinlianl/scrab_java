/**
* The serializable class is used for object transport,
* load and transport via ObjectStream
* it contains the info of userID, input letter and coordinates
*
* @author Jinliang Liao
*/
package ultility;
import java.io.Serializable;

class Action implements Serializable{
    public static final int JOIN = 1;
    public static final int MOVE = 2;
    private static final long serialVersionUID = 1L;
    
    private String actionType;
    private String userName;
    private int coor_x;
    private int coor_y;
    private char input;
    private String expectWord;


    Action(int type){
        actionType = type;
    }

    /**
     * set the info for a join game request
     */
    public void setJoinGameInfo(String userName) {
        this.userName = userName;
    }

    /**
     * @param input the input to set
     */
    public void setMoveInfo(int x,int y,char input, String word) {
        this.input = input;
        this.coor_x = x;
        this.coor_y = y;
        this.expectWord = word;

    }


    /**
     * @return the actionType
     */
    public String getActionType() {
        return actionType;
    }
    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
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
    public char getInput() {
        return input;
    }
}