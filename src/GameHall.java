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
    private JTextArea userList;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int gameID = -1;
    private Object lock = new Object();

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
                                    }
                                    else {
                                        // 弹窗提示用户
                                        Object[] options = {"OK"};
                                        JOptionPane.showOptionDialog(window, r.getMessage(), "Info", JOptionPane.CANCEL_OPTION,
                                                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                    }
                                    break;
                                case Response.STARTGAME:
                                    // 开始新游戏
                                    synchronized (lock) {
                                        new Scrabble(username, in, out, lock);
                                        System.gc();
                                        lock.wait();
                                    }
                                    break;
                                default:
                                    break;
                            }

                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void OpenWaitingWindow() {
        // TODO: 打开等待开始界面
        try {
            Action a = new Action(Action.STARTGAME);
            a.setGameID(gameID);
            out.writeObject(a);
            out.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Logout() {
        try {
            Action a = new Action(Action.LOGOUT);
            out.writeObject(a);
            out.flush();
            System.exit(0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void BuildUpGUI() {
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
        panel.setBounds(0,0,300,300);
        panel.setBackground(Color.PINK);

        JLabel label1 = new JLabel("Who's online:");
        label1.setBounds(10,0,100,20);
        panel.add(label1);

        this.userList = new JTextArea();
        this.userList.setEnabled(false);
        JScrollPane scroll = new JScrollPane(this.userList);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBounds(10, 20, 100, 240);
        panel.add(scroll);

        JButton newGame = new JButton("Start a New Game");
        newGame.setBounds(120, 30, 160,40);
        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // 向server发送新游戏请求
                    Action a = new Action(Action.NEW);
                    a.setJoinGameInfo(username);
                    out.writeObject(a);
                    out.flush();
//                    Response r = (Response) in.readObject();
//                    System.out.println(r);
//                    if (r != null) {
//                        // 处理server回应
//                        if (r.getStatus() == Response.SUCCESS) {
//                            new Scrabble(username, in, out);
//                            System.gc();
//                        }
//                        else {
//                            Object[] options ={"OK"};
//                            JOptionPane.showOptionDialog(window, r.getMessage(), "Warning", JOptionPane.CANCEL_OPTION,
//                                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
//                        }
//                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        panel.add(newGame);

        JButton join = new JButton("Join a Game");
        join.setBounds(120, 120, 160,40);
        join.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // 向server发送加入一个游戏请求
                    Action a = new Action(Action.JOIN);
                    a.setJoinGameInfo(username);
                    out.writeObject(a);
                    out.flush();
                    //new Scrabble(username, in, out);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        panel.add(join);

        JButton logout = new JButton("Log out");
        logout.setBounds(120, 210, 160,40);
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Logout();
            }
        });
        panel.add(logout);

        window.setContentPane(panel);
        window.setVisible(true);
    }
}
