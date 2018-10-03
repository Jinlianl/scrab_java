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
    private static final long serialVersionUID = 1L;
    private String userName;
    private int coor_x;
    private int coor_y;
    private char input;

    Action(String uName,int x, int y, char in){
        userName = uName;
        coor_x = x;
        coor_y = y;
        input = in;
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