package ultility;

import java.io.Serializable;

public class Response implements Serializable {
    public static final int LOGIN = 0;
    public static final int JOIN = 1;
    public static final int MOVE = 2;
    public static final int TURN = 3;
    public static final int JUDGE = 4;
    public static final int SCORE = 5;
    public static final int STARTGAME = 7;
    public static final int ENDGAME = 9;
    public static final int LOGOUT = 10;

    public static final int SUCCESS = 0;
    public static final int FAIL = 1;

    private static final long serialVersionUID = 2L;

    private int responseType;
    private String userName;
    private int coor_x;
    private int coor_y;
    private String input;
    private String expectWord;
    private String message;
    private int status;
    private int score;
    private int gameID;

    public Response (int type) {
        responseType = type;
    }

    public void setLoginResponse(int s, String m) {
        status = s;
        message = m;
    }

    public void setJoinStatus(int s, String m, int g) {
        status = s;
        message = m;
        gameID = g;
        // TODO: 游戏初始化其他信息，例如本场玩家等
    }

    public void setScoreInfo(int s, String m, String user) {
        score = s;
        message = m;
        userName = user;
    }

    public void setTurnMessage(String name) {
        userName = name;
    }

    public void setMoveInfo(int x,int y,String input) {
        this.input = input;
        this.coor_x = x;
        this.coor_y = y;
    }

    public void setJudgeInfo(String word, String user) {
        this.userName = user;
        this.expectWord = word;
    }

    public void setEndGameMessage(String message) {
        this.message = message;
    }

    public int getResponseType() {
        return responseType;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getScore() {
        return score;
    }

    public String getTurn() {
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
    public String getInput() {
        return input;
    }
    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }
    /**
     * @return the expectWord
     */
    public String getExpectWord() {
        return expectWord;
    }

    public int getGameID() {
        return gameID;
    }
}
