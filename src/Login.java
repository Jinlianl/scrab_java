import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import ultility.Response;
import ultility.Action;

public class Login {
    private JFrame window;
    private String host;
    private int port;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Login(String[] config) {
        this.host = config[0];
        this.port = Integer.parseInt(config[1]);
        this.BuildUpGUI();
        this.connect();
    }

    private void connect() {
        try {
            this.socket = new Socket(this.host, this.port);
            this.out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void BuildUpGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.window = new JFrame("Login");
        window.setSize(300, 270);
        window.setLocation(200, 200);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0,0,300,270);
        panel.setBackground(Color.ORANGE);

        JLabel label = new JLabel("USERNAME:");
        label.setBounds(50, 50, 200, 20);
        panel.add(label);

        final JTextField textField = new JTextField();
        textField.setBounds(50, 80, 200, 20);
        panel.add(textField);

        JButton button = new JButton("LOGIN");
        button.setBounds(50, 150, 200,20);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String username = textField.getText().toLowerCase();
                    System.out.println(username);
                    Action a = new Action(Action.LOGIN);
                    a.setLoginInfo(username);
                    out.writeObject(a);
                    out.flush();

                    System.out.println(a.getActionType());

                    if (in == null) {
                        in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                        System.out.println("new input stream");
                    }
                    Response r = (Response) in.readObject();
                    if (r != null) {
                        // 处理server回应
                        if (r.getStatus() == Response.SUCCESS) {
                            new GameHall(username, in, out);
                            window.setVisible(false);
                        }
                        else {
                            Object[] options ={"OK"};
                            JOptionPane.showOptionDialog(window, r.getMessage(), "Warning", JOptionPane.CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        panel.add(button);

        window.setContentPane(panel);
        window.setVisible(true);
    }

    public static void main(String[] args) {
        if(args.length > 0){
            new Login(args);
        }else{
            String[] defaultArgs = {"localhost","1234"};
            new Login(defaultArgs);
        }
        
    }
}
