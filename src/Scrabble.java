import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Scrabble {
    private JFrame window;
    private JTextField[][] textFields = new JTextField[20][20];
    private JButton passButton;
    private JButton doneButton;
    private JLabel turnLabel;
    private JLabel[] scores = new JLabel[4];
    private int players;
    private String[] playerNames = new String[3];

    public Scrabble() {
        players = 1;
        playerNames[0] = "Your";
        this.BuildUpGUI();
    }

    private void BuildUpGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.window = new JFrame("Scrabble");
        window.setSize(800, 434);
        window.setLocation(200, 200);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0,0,800,434);
        panel.setBackground(Color.CYAN);

        this.setUpTextFields(panel);

        this.setUpButtons(panel);

        this.setUpLabels(panel);

        window.setContentPane(panel);
        window.setVisible(true);
    }

    private void setUpLabels(JPanel panel) {
        turnLabel = new JLabel();
        turnLabel.setText("XXX's Turn!");
        turnLabel.setHorizontalAlignment(0);
        turnLabel.setBounds(610+15,20,150,40);
        panel.add(turnLabel);

        for (int i = 0; i < players; i++) {
            JLabel score = new JLabel();
            if (i > 0) {
                score.setText(playerNames[i-1] + "'s score: 0");
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
        panel.add(doneButton);


        passButton = new JButton("Pass");
        passButton.setBounds(610+15, 310, 150, 40);
        panel.add(passButton);
    }

    private void setUpTextFields(JPanel panel) {
        for (int i = 0; i < 20; i++)
            for (int j = 0; j < 20; j++) {
                JTextField textField = new JTextField();
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
                                }
                        }
                        else {
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
