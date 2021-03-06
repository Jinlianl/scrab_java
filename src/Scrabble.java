import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import ultility.Action;
import ultility.Response;
import java.util.ArrayList;

public class Scrabble {
    private JFrame window;
    private JTextField[][] textFields = new JTextField[20][20];
    private JButton passButton;
    private JButton doneButton;
    private JLabel turnLabel;
    private JTextArea scoreBoard;
    private JLabel[] scores = new JLabel[4];
    private ArrayList<String> playerNames = new ArrayList<String>();

    private int x = -1;
    private int y = -1;
    private String username;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Thread thread;

    private Object lock;
    private JLabel lblNewLabel;

    public Scrabble(String username, ObjectInputStream in, ObjectOutputStream out, Object lock) {
        this.username = username;
        this.in = in;
        this.out = out;
        this.lock = lock;
        this.playerNames.add(username);
        this.BuildUpGUI();
        this.BuildUpThread();
    }

    private void BuildUpThread() {
        thread = new Thread() {
            public void run() {
                EndTurn();
                while (true) {
                    try {
                        Response r = (Response) in.readObject();
                        if (r != null) {
                            // 读取server发来的信息并处理。
                            int type = r.getResponseType();
                            if (type == Response.ENDGAME) {
                                // TODO: 游戏结束显示胜利玩家
                                if (window.isVisible()) {
                                    synchronized (lock) {
                                        Object[] options = {"OK"};
                                        JOptionPane.showOptionDialog(window, r.getMessage(), "Info", JOptionPane.CANCEL_OPTION,
                                                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                        window.dispose();
                                        lock.notify();
                                    }
                                }
                                break;
                            }
                            switch (type) {
                                case Response.TURN:
                                    // System.out.println("RECEIVE TURN");
                                    if (r.getTurn().equals(username)) {
                                        // 设定为自己的turn，可以操作了
                                        turnLabel.setText("Your Turn!");
                                        StartTurn();
                                    } else {
                                        turnLabel.setText(r.getTurn() + "'s Turn!");
                                    }
                                    break;
                                case Response.JUDGE:
                                    Object[] options = {"Agree", "Disagree"};
                                    String[] words = r.getExpectWord().split(",");
                                    if(words.length>1){
                                        //more than one word
                                        options = new String[]{"Agree All", "Disagree","Agree "+words[0],"Agree "+words[1]};
                                    }
                                    String message = r.getUserName() + " wants to get score by " + r.getExpectWord() + ".";
                                    int rc = JOptionPane.showOptionDialog(window, message, "Option", JOptionPane.CANCEL_OPTION,
                                            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                    Action a = new Action(Action.JUDGE);
                                    // 直接关闭视为同意
                                    // System.out.println("option"+rc);

                                    if (rc <=0) {
                                        //全部同意
                                        a.setJudgeINfo(0);
                                    } else if(rc ==1){
                                        //不同意
                                        a.setJudgeINfo(1);
                                    }else if(rc ==2){
                                        //同意第一个词
                                        a.setJudgeINfo(2);
                                    }else if (rc==3){
                                        //同意第二个词
                                        a.setJudgeINfo(3);
                                    }
                                    out.writeObject(a);
                                    out.flush();
                                    break;
                                case Response.MOVE:
                                    textFields[r.getCoor_x()][r.getCoor_y()].setText(r.getInput());
                                    textFields[r.getCoor_x()][r.getCoor_y()].setEnabled(false);
                                    textFields[r.getCoor_x()][r.getCoor_y()].setBackground(Color.YELLOW);
                                    break;
                                case Response.SCORE:
                                    String msg = r.getMessage();
                                    // System.out.print(r.getScoreboard());
                                    scoreBoard.setText(r.getScoreboard());
                                    Object[] option = {"OK"};
                                    JOptionPane.showOptionDialog(window, msg, "Info", JOptionPane.CANCEL_OPTION,
                                            JOptionPane.QUESTION_MESSAGE, null, option, option[0]);
                                    if (r.getScore() > 0) {
                                        for (int i = 0; i < playerNames.size(); i++) {
                                            if (r.getUserName().equals(playerNames.get(i))) {
                                                String oldScore = scores[i].getText().split(":")[1].replace(" ", "");
                                                String newScore = String.valueOf(Integer.parseInt(oldScore) + r.getScore());
                                                scores[i].setText(scores[i].getText().replace(oldScore, newScore));
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    catch (Exception e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(window,"Oops!Fail to connect to the server,please try again!","Warning",JOptionPane.WARNING_MESSAGE);
                        break;
                    }
                }
            }
        };
        thread.start();
    }

    private void Ready() {
        try {
            Action a = new Action(Action.READY);
            out.writeObject(a);
            out.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void StartTurn() {
        boolean isFirstTrun = true;
        x = -1;
        y = -1;
        doneButton.setEnabled(true);
        passButton.setEnabled(true);
        for (int i = 0; i < 20; i++)
            for (int j = 0; j < 20; j++) {
                if (textFields[i][j].getText().length() > 0) {
                    isFirstTrun = false;
                    if (i-1 >= 0 && textFields[i-1][j].getText().length() == 0) {
                        textFields[i-1][j].setEnabled(true);
                        textFields[i-1][j].setBackground(Color.CYAN);
                    }
                    if (j-1 >= 0 && textFields[i][j-1].getText().length() == 0) {
                        textFields[i][j-1].setEnabled(true);
                        textFields[i][j-1].setBackground(Color.CYAN);
                    }
                    if (i+1 < 20 && textFields[i+1][j].getText().length() == 0) {
                        textFields[i+1][j].setEnabled(true);
                        textFields[i+1][j].setBackground(Color.CYAN);
                    }
                    if (j+1 < 20 && textFields[i][j+1].getText().length() == 0) {
                        textFields[i][j+1].setEnabled(true);
                        textFields[i][j+1].setBackground(Color.CYAN);
                    }
                }
            }
        if (isFirstTrun) {
            for (int i = 9; i < 11; i++)
                for (int j = 9; j < 11; j++) {
                    textFields[i][j].setEnabled(true);
                    textFields[i][j].setBackground(Color.CYAN);
                }
        }
    }

    private void EndTurn() {
        doneButton.setEnabled(false);
        passButton.setEnabled(false);
        for (int i = 0; i < 20; i++)
            for (int j = 0; j < 20; j++) {
                if (textFields[i][j].getText().length() == 0) {
                    textFields[i][j].setEnabled(false);
                    textFields[i][j].setBackground(Color.LIGHT_GRAY);
                }
            }
    }

    private String[] checkWords() {
        String[] results = {"", ""};
        for (int i = y; i < 20; i++) {
            String ch = textFields[x][i].getText();
            if (ch.length() > 0) {
                results[0] += ch;
                continue;
            }
            break;
        }
        for (int i = y-1; i >= 0; i--) {
            String ch = textFields[x][i].getText();
            if (ch.length() > 0) {
                results[0] = ch + results[0];
                continue;
            }
            break;
        }

        for (int i = x; i < 20; i++) {
            String ch = textFields[i][y].getText();
            if (ch.length() > 0) {
                results[1] += ch;
                continue;
            }
            break;
        }
        for (int i = x-1; i >= 0; i--) {
            String ch = textFields[i][y].getText();
            if (ch.length() > 0) {
                results[1] = ch + results[1];
                continue;
            }
            break;
        }

        return results;
    }

    private void ChooseWord(String word) {
        EndTurn();

        try {
            Action a = new Action(Action.MOVE);
            a.setMoveInfo(x, y, textFields[x][y].getText(), word);
            out.writeObject(a);
            out.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Done() {
        if (x > -1 && y > -1) {
            String[] tokens = this.checkWords();
            // TODO: 由单选变成多选，相应的投票也需要改，可以用checkbox实现多选
            if (tokens[0].length() > 1 && tokens[1].length() > 1) {
                String Combine = tokens[0]+","+tokens[1];
                Object[] options ={tokens[0], tokens[1],Combine, "Do not submit"};
                String message = "Which word do you want to submit for judgement?";
                int rc = JOptionPane.showOptionDialog(window, message, "Option", JOptionPane.CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (rc  < 0) rc = options.length - 1;
                this.ChooseWord(options[rc].toString());
            }
            else if (tokens[0].length() < 2 && tokens[1].length() < 2) {
                this.ChooseWord("Do not submit");
            }
            else {
                String token;
                if (tokens[0].length() > 1) token = tokens[0]; else token = tokens[1];
                Object[] options ={token, "Do not submit"};
                String message = "Which word do you want to submit for judgement?";
                int rc = JOptionPane.showOptionDialog(window, message, "Option", JOptionPane.CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (rc  < 0) rc = options.length - 1;
                this.ChooseWord(options[rc].toString());
            }

        }
        else {
            // 报错给客户端，没有填写东西，确定要pass吗？确定则执行pass方法。
            Object[] options ={"Sure", "Cancel"};
            String message = "You have NOT filled a box. \n Do you want to PASS?";
            int rc = JOptionPane.showOptionDialog(window, message, "Warning", JOptionPane.CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (rc == 0) this.Pass();
        }
    }

    private void confirmPass() {
        if (x > -1 && y > -1) {
            // 让用户确定是否pass
            Object[] options ={"Sure", "Cancel"};
            String message = "You HAVE filled a box. \n Do you want to PASS?";
            int rc = JOptionPane.showOptionDialog(window, message, "Warning", JOptionPane.CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (rc == 0) this.Pass();
        }
        else {
            this.Pass();
        }
    }

    private void Pass() {
        System.out.println("PASS");
        for (int i = 0; i < 20; i++)
            for (int j = 0; j < 20; j++) {
                textFields[i][j].setEnabled(false);
            }
        doneButton.setEnabled(false);
        passButton.setEnabled(false);

        try {
            Action a = new Action(Action.PASS);
            out.writeObject(a);
            out.flush();
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(window,"Oops!Fail to connect to the server,please try again!","Warning",JOptionPane.WARNING_MESSAGE);
            e.printStackTrace();
        }
    }

    private void BuildUpGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.window = new JFrame("Scrabble");
        window.setSize(800, 434);
        window.setLocation(200, 200);
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.setResizable(false);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    synchronized (lock) {
                        Action a = new Action(Action.ENDGAME);
                        out.writeObject(a);
                        out.flush();
                        window.setVisible(false);
                        lock.notify();
                        window.dispose();
                    }
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0,0,800,434);
        panel.setBackground(Color.CYAN);

        this.setUpTextFields(panel);

        this.setUpButtons(panel);

        this.setUpLabels(panel);

        window.setContentPane(panel);
        
        scoreBoard = new JTextArea();
        scoreBoard.setBackground(Color.CYAN);
        scoreBoard.setBounds(625, 168, 150, 130);
        scoreBoard.setEditable(false);
        panel.add(scoreBoard);
        
        lblNewLabel = new JLabel("Score Board:");
        lblNewLabel.setBounds(625, 142, 77, 16);
        panel.add(lblNewLabel);
        
        window.setVisible(true);
    }

    private void setUpLabels(JPanel panel) {
        turnLabel = new JLabel();
        turnLabel.setText("Your Turn!");
        turnLabel.setHorizontalAlignment(0);
        turnLabel.setBounds(610+15,20,150,40);
        panel.add(turnLabel);

        for (int i = 0; i < playerNames.size(); i++) {
            JLabel score = new JLabel();
            if (i > 0) {
                scoreBoard.append(playerNames.get(i) + "'s score: 0");
                score.setText(playerNames.get(i) + "'s score: 0");
            }
            else {
                score.setText("Your score: 0");
            }
            score.setBounds(610+15, 80+40*i, 150, 20);
            score.setHorizontalAlignment(0);
            scores[i] = score;
            panel.add(score);
        }

    }

    private void setUpButtons(JPanel panel) {
        doneButton = new JButton("Done");
        doneButton.setBounds(610+15, 360,150,40);
        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                   Done();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        panel.add(doneButton);


        passButton = new JButton("Pass");
        passButton.setBounds(610+15, 310, 150, 40);
        passButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    confirmPass();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        panel.add(passButton);
    }

    private void setUpTextFields(JPanel panel) {
        for (int i = 0; i < 20; i++)
            for (int j = 0; j < 20; j++) {
                final JTextField textField = new JTextField();
                textField.setBackground(Color.CYAN);
                textField.setBounds(5+30*i, 7+20*j, 30, 20);
                textField.setHorizontalAlignment(0);
                textField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        super.keyTyped(e);
                        String str = textField.getText();
                        //System.out.print(str);
                        if (str.length() > 0) {
                            if (str.substring(str.length()-1).matches("[a-zA-Z]")) {
                                //System.out.print(str.substring(str.length()-1));
                                str = str.substring(str.length()-1).toUpperCase();
                            }
                            else {
                                if (str.length() > 1) {
                                    //System.out.print(str.substring(0,1));
                                    str = str.substring(0,1);
                                }
                                else {
                                    str = "";
                                }
                            }
                            textField.setText(str);
                            for (int i = 0; i < 20; i++)
                                for (int j = 0; j < 20; j++) {
                                    if (!textFields[i][j].equals(textField)) {
                                        textFields[i][j].setEnabled(false);
                                    }
                                    else {
                                        x = i;
                                        y = j;
                                    }
                                }
                        }
                        else {
                            x = -1;
                            y = -1;
                            for (int i = 0; i < 20; i++)
                                for (int j = 0; j < 20; j++) {
                                    textFields[i][j].setEnabled(true);
                                }
                        }
                    }
                });

                panel.add(textField);

                textFields[i][j] = textField;
            }
    }
}
