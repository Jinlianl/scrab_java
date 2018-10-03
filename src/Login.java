import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Login {
    private JFrame window;

    public Login() {
        this.BuildUpGUI();
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

        JTextField textField = new JTextField();
        textField.setBounds(50, 80, 200, 20);
        panel.add(textField);

        JButton button = new JButton("LOGIN");
        button.setBounds(50, 150, 200,20);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String word = textField.getText().toLowerCase();
                    System.out.print(word);
                    new GameHall();
                    window.setVisible(false);

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
        //new Login();
        new Scrabble();
    }
}
