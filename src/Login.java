import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
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

            this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            this.out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            String received = in.readLine();
            if (received != null) {
                System.out.println("Message received: " + received);
            }

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
                    System.out.print(username);
                    Action a = new Action(Action.JOIN);
                    a.setJoinGameInfo(username);
                    out.writeObject(a);
                    out.flush();

                    Object obj = in.readObject();
                    if (obj != null) {
                        // TODO：等待server回应
                        new GameHall(username, in, out);
                        window.setVisible(false);
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
        new Login(args);
    }
}
