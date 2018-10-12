import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

import ultility.Action;
import ultility.Response;

public class GameHall {
    private String username;
    private JFrame window;
    private JFrame waitNinviteWindow;
    private JTextArea userList;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int gameID = -1;
    private Object lock = new Object();
    private String[] playerNames; // the online player name list
    private JList<String> inviteList;

    public GameHall(String username, ObjectInputStream in, ObjectOutputStream out) {
        this.username = username;
        this.in = in;
        this.out = out;
        this.BuildUpGUI();
        this.BuildUpThread();
    }

    private void BuildUpThread() {
        Thread thread = new Thread() {
            public void run() {
                try {
                    while (true) {
                        Response r = (Response) in.readObject();
                        if (r != null) {
                            // 读取server发来的信息并处理。
                            int type = r.getResponseType();
                            switch (type) {
                            case Response.JOIN:
                                // 处理join信息
                                if (r.getStatus() == Response.SUCCESS) {
                                    gameID = r.getGameID();
                                    OpenWaitingWindow();
                                } else {
                                    // 弹窗提示用户
                                    Object[] options = { "OK" };
                                    JOptionPane.showOptionDialog(window, r.getMessage(), "Info",
                                            JOptionPane.CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
                                            options[0]);
                                }
                                break;
                            case Response.STARTED:
                                // 开始新游戏
                                synchronized (lock) {
                                    waitNinviteWindow.setVisible(false);
                                    new Scrabble(username, in, out, lock);
                                    System.gc();
                                    lock.wait();
                                }
                                break;
                            case Response.STARTGAME:
                                Action a = new Action(Action.STARTED);
                                out.writeObject(a);
                                out.flush();
                                break;
                            case Response.PLAYERLIST:
                                String listText = r.getPlayerList();
                                playerNames = listText.split("\n");
                                userList.setText(listText);
                                break;
                            case Response.INVITE:
                                // 获得某玩家邀请
                                // System.out.println(r.getInviteFrom());
                                if(AcceptInvitation(r.getInviteFrom())){
                                    Action joinAct = new Action(Action.JOIN);
                                    joinAct.setJoinGameInfo(username);
                                    out.writeObject(joinAct);
                                    out.flush();
                                }else{

                                }
                                break;
                            default:
                                break;
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private boolean AcceptInvitation(String fromID){
        // TODO: 判断是否同意邀请
        String[] option = {"Reject","Agree"};
        String msg = "Player "+fromID + " invite you to game";
        int selected = JOptionPane.showOptionDialog(window, msg, "Info", JOptionPane.CANCEL_OPTION,
                                            JOptionPane.QUESTION_MESSAGE, null, option, option[0]);
        if(selected == 1){
            return true;
        }
        return false;
    }

    private void OpenWaitingWindow() {
        // TODO: 打开等待开始界面，加入邀请功能
        if (this.playerNames.length > 0) {
            this.inviteList.setListData(playerNames);
        }
        waitNinviteWindow.setVisible(true);

    }

    private void Logout() {
        try {
            Action a = new Action(Action.LOGOUT);
            out.writeObject(a);
            out.flush();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void BuildUpGUI() {
        // Build up Game Hall
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.window = new JFrame("Game Hall");
        window.setSize(300, 300);
        window.setLocation(400, 100);
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.setResizable(false);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                Logout();
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0, 0, 300, 300);
        panel.setBackground(Color.PINK);

        JLabel label1 = new JLabel("Who's online:");
        label1.setBounds(10, 0, 100, 20);
        panel.add(label1);

        this.userList = new JTextArea();
        this.userList.setEnabled(false);
        JScrollPane scroll = new JScrollPane(this.userList);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBounds(10, 20, 100, 240);
        panel.add(scroll);

        JButton newGame = new JButton("Start a New Game");
        newGame.setBounds(120, 30, 160, 40);
        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // 向server发送新游戏请求
                    Action a = new Action(Action.NEW);
                    a.setJoinGameInfo(username);
                    out.writeObject(a);
                    out.flush();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        panel.add(newGame);

        JButton join = new JButton("Join a Game");
        join.setBounds(120, 120, 160, 40);
        join.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // 向server发送加入一个游戏请求
                    Action a = new Action(Action.JOIN);
                    a.setJoinGameInfo(username);
                    out.writeObject(a);
                    out.flush();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        panel.add(join);

        JButton logout = new JButton("Log out");
        logout.setBounds(120, 210, 160, 40);
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Logout();
            }
        });
        panel.add(logout);

        window.setContentPane(panel);



        // TODO:Build up waiting & invitation window
        this.waitNinviteWindow = new JFrame("Waiting");
        // waitNinviteWindow.setUndecorated(true);
        waitNinviteWindow.setSize(500, 200);
        waitNinviteWindow.setLocation(420, 200);
        waitNinviteWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        waitNinviteWindow.setResizable(true);

        JPanel wpanel = new JPanel();
        wpanel.setLayout(null);
        wpanel.setBounds(0, 0, 515, 223);
        // panel.add(wpanel);

        JButton start = new JButton("Start");
        start.setBounds(202, 130, 100, 40);
        wpanel.add(start);

        JButton invite = new JButton("Invite");
        invite.setBounds(67, 130, 119, 40);
        wpanel.add(invite);

        inviteList = new JList<>();
        inviteList.setBounds(67, 6, 125, 112);

        wpanel.add(inviteList);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(209, 6, 93, 112);
        wpanel.add(scrollPane);

        invite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Action a = new Action(Action.INVITE);
                    String selected = inviteList.getSelectedValue();
                    a.setInviteID(selected);
                    out.writeObject(a);
                    out.flush();
                    Object[] options = {"OK"};
                    JOptionPane.showOptionDialog(window, "invitation sent!", "Notice",
                                            JOptionPane.CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
                                            options[0]);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Action a = new Action(Action.STARTGAME);
                    a.setGameID(gameID);
                    out.writeObject(a);
                    out.flush();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
        window.setVisible(true);
        waitNinviteWindow.setContentPane(wpanel);
    }
}
