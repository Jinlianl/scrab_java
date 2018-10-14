package server.threads;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.Random;

import ultility.Action;
import ultility.Player;
import ultility.Response;

public class GameThread extends Thread{
    private ArrayList<Player> players = new ArrayList<Player>();
    private int turn = -1;
    private boolean started = false;
    private Object lock = new Object();
    private int gameID;
    // TODO: 计分系统

    public void setGameID(){
        Random random = new Random();
        this.gameID = random.nextInt(1000);
    }
    /**
     * @return the gameID
     */
    public int getGameID() {
        return gameID;
    }

    public boolean isStarted() {
        return started;
    }

    public Object getLock() {
        return this.lock;
    }

    public void addPlayers(Player player) {
        // 判断是否已经存在该玩家
        for(Player p:players){
            if(player.getUserName().equals(p.getUserName())){
                System.out.println("player already in room");
                return;
            }
        }
        players.add(player);
    }

    public int getPlayersNum() {
        return players.size();
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    private void Judge(Response r) {
        int judgeTurn = (turn + 1) % players.size();
        System.out.println(r.getExpectWord());
        String word = r.getExpectWord();
        boolean first = true;
        boolean second = true;

        while (judgeTurn != turn) {
            try {
                Player judgePlayer = players.get(judgeTurn);
                judgePlayer.getOos().writeObject(r);
                judgePlayer.getOos().flush();
                Action a = (Action) judgePlayer.getOis().readObject();
                if (a != null) {
                    int judgment = a.getJudgement();
                    if (judgment !=1) {
                        //没有不同意
                        judgeTurn = (judgeTurn + 1) % players.size();
                        if(judgment ==2){
                            //只同意第一个,意味着不同意第二个,那么第二个就是FALSE
                            second=false;

                        }else if(judgment ==3) {
                            first=false;
                        }
                    }
                    else {
                        break;
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        Response score = new Response(Response.SCORE);
        if (judgeTurn == turn) {
            int points=0;
            String msg=" got score!";
            if (word.split(",").length>1){
                System.out.println("muti-words!");
                //more than one word
                if(first&&second){
                    points=word.length()-1;
                }else if(first){
                    points=word.split(",")[0].length();
                }else if(second){
                    points=word.split(",")[1].length();
                }else{
                    points=0;
                    msg=" did not get score!";
                }
            }else{
                points= word.length();
            }
            score.setScoreInfo(points, players.get(turn).getUserName() + msg, players.get(turn).getUserName());
            int newScore = players.get(turn).getScore()+points;
            players.get(turn).setScore(newScore);


        }
        else {
            score.setScoreInfo(0, players.get(judgeTurn).getUserName() + " did not agree.", players.get(turn).getUserName());
        }
        score.setScoreBoard(createBoard());
        broadcast(score);
    }

    private String createBoard(){
        String board = "";
        for(Player p :players){
            board += p.getUserName()+" : "+p.getScore()+"\n";
        }
        return board;
    }

    private void broadcast(Response r) {
        for (int i = 0; i < players.size(); i++) {
            try {
                players.get(i).getOos().writeObject(r);
                players.get(i).getOos().flush();
                System.out.println(players.get(i).getUserName());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void nextTurn() {
        turn = (turn + 1) % players.size();
        Response r = new Response(Response.TURN);
        r.setTurnMessage(players.get(turn).getUserName());
        broadcast(r);
    }

    public void run() {
        this.started = true;
        int passCount = 0;
        // TODO: 更好的确认所有客户端都已经开启的方案，目前是等2秒默认大家都会开起来了
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        nextTurn();
        while (true) {
            try {
                Player currentPlayer = players.get(turn);
                Action a = (Action) currentPlayer.getOis().readObject();
                if (a != null) {
                    int type = a.getActionType();
                    if (type == Action.ENDGAME) {
                        Response r = new Response(Response.ENDGAME);
                        // TODO: 显示胜利玩家
                        Collections.sort(players, new Comparator<Player>() {
                            @Override
                            public int compare(Player o1, Player o2) {
                                if(o1.getScore()>o2.getScore()){
                                    return 1;
                                }else{
                                    return -1;
                                }
                            }
                        });
                        String Winner = players.get(players.size()-1).getUserName();
                        r.setEndGameMessage("One player has logged out. Game ends"+","+Winner+" Wins!");
                        broadcast(r);
                        synchronized (this.lock) {
                            this.lock.notifyAll();
                        }
                        break;
                    }
                    if(type == Action.PASS){
                        passCount++;
                        System.out.println("this turn now has "+passCount+" pass, remains " + (players.size()-passCount));
                        //该轮玩家全部pass,游戏结束
                        if(passCount == players.size()){
                            System.out.println("End Game!");
                            Response r = new Response(Response.ENDGAME);
                            // TODO: 显示胜利玩家
                            Collections.sort(players, new Comparator<Player>() {
                                @Override
                                public int compare(Player o1, Player o2) {
                                    if(o1.getScore()>o2.getScore()){
                                        return 1;
                                    }else{
                                        return -1;
                                    }
                                }
                            });
                            String Winner = players.get(players.size()-1).getUserName();
                            r.setEndGameMessage("All players passed. Game ends"+","+Winner+" Wins!");
                            broadcast(r);
                            synchronized (this.lock) {
                                this.lock.notifyAll();
                            }
                            break;
                        }
                    }
                    if (type == Action.MOVE) {
                        passCount = 0;
                        Response r = new Response(Response.MOVE);
                        r.setMoveInfo(a.getCoor_x(), a.getCoor_y(), a.getInput());
                        broadcast(r);

                        if (!a.getExpectWord().contains(" ")) {
                            Response judge = new Response(Response.JUDGE);
                            judge.setJudgeInfo(a.getExpectWord(), currentPlayer.getUserName());
                            Judge(judge);
                        }
                    }
                    nextTurn();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                synchronized (this.lock) {
                    this.lock.notifyAll();
                }
                break;
            }
        }
    }

}