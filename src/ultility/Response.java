package ultility;

import java.io.Serializable;

public class Response implements Serializable {
    public static final int LOGIN = 0;
    public static final int JOIN = 1;
    public static final int MOVE = 2;
    public static final int PASS = 3;

    public static final int SUCCESS = 0;
    public static final int FAIL = 1;

    private static final long serialVersionUID = 1L;

    private int responseType;
    private String userName;
    private int coor_x;
    private int coor_y;
    private char input;
    private String expectWord;
    private String message;
    private int status;

    public Response (int type) {
        responseType = type;
    }

    public void setLoginResponse(int s, String m) {
        status = s;
        message = m;
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
}
