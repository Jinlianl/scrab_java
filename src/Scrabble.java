import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Scrabble {
    private String username;
    private JFrame window;
    private JTextField[][] textFields = new JTextField[20][20];
    private JButton passButton;
    private JButton doneButton;
    private JLabel turnLabel;
    private JLabel[] scores = new JLabel[4];
    private int players;
    private String[] playerNames = new String[3];
    private int x = -1;
    private int y = -1;

    public Scrabble(String username) {
        this.username = username;
        this.players = 1;
        this.playerNames[0] = "Your";
        this.BuildUpGUI();
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
        // 向server发送希望大家评判的词，x,y,ID,char到服务器


        for (int i = 0; i < 20; i++)
            for (int j = 0; j < 20; j++) {
                if (textFields[i][j].getText().length() == 0) {
                    textFields[i][j].setEnabled(true);
                }
            }
    }

    private void Done() {
        if (x > -1 && y > -1) {
            textFields[x][y].setEnabled(false);
            String[] tokens = this.checkWords();
            if (tokens[0].length() > 1 && tokens[1].length() > 1) {
                Object[] options ={tokens[0], tokens[1], "None"};
                String message = "Which word do you want to submit for judgement?";
                int rc = JOptionPane.showOptionDialog(window, message, "Option", JOptionPane.CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                System.out.println(rc);
                if (rc  < 0) rc = options.length - 1;
                this.ChooseWord(options[rc].toString());
            }
            else if (tokens[0].length() < 2 && tokens[1].length() < 2) {
                this.ChooseWord("None");
            }
            else {
                String token;
                if (tokens[0].length() > 1) token = tokens[0]; else token = tokens[1];
                Object[] options ={token, "None"};
                String message = "Which word do you want to submit for judgement?";
                int rc = JOptionPane.showOptionDialog(window, message, "Option", JOptionPane.CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                System.out.println(rc);
                if (rc  < 0) rc = options.length - 1;
                this.ChooseWord(options[rc].toString());
            }

        }
        else {
            // 报错给客户端，没有填写东西，确定要pass吗？确定则执行pass方法。
        }
    }

    private void Pass() {
        if (x > -1 && y > -1) {
            // 让用户确定是否pass
        }
        else {
            // 直接pass
        }
    }

    private void BuildUpGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.window = new JFrame("Scrabble");
        window.setSize(800, 434);
        window.setLocation(200, 200);
        window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
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
        turnLabel.setText("Your Turn!");
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
                    Pass();
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
