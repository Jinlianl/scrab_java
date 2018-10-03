import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public class GameHall {
    private String username;
    private JFrame window;
    private BufferedReader in;
    private BufferedWriter out;

    public GameHall(String username, BufferedReader in, BufferedWriter out) {
        this.username = username;
        this.in = in;
        this.out = out;
        this.BuildUpGUI();
    }

    private void BuildUpGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.window = new JFrame("Game Hall");
        window.setSize(300, 300);
        window.setLocation(400, 100);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0,0,300,300);
        panel.setBackground(Color.PINK);

        JLabel label1 = new JLabel("Who's online:");
        label1.setBounds(10,0,100,20);
        panel.add(label1);

        JTextArea textArea = new JTextArea();
        textArea.setEnabled(false);
        // TODO: 向server请求当前在线的玩家的名单，并定时更新
        textArea.setText("Ryan\nAmy\nBen");
        JScrollPane scroll = new JScrollPane(textArea);
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
                    // TODO：向server发送新游戏请求
                    new Scrabble(username, in, out);
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
                    // TODO：向server发送加入一个游戏请求
                    new Scrabble(username, in, out);
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
                try {
                    System.exit(0);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        panel.add(logout);

        window.setContentPane(panel);
        window.setVisible(true);
    }
}
