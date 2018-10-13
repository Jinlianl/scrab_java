package server.threads;
import java.util.ArrayList;
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
        this.gameID = random.nextInt(100);
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
        while (judgeTurn != turn) {
            try {
                Player judgePlayer = players.get(judgeTurn);
                judgePlayer.getOos().writeObject(r);
                judgePlayer.getOos().flush();
                Action a = (Action) judgePlayer.getOis().readObject();
                if (a != null) {
                    if (a.isAgree()) {
                        judgeTurn = (judgeTurn + 1) % players.size();
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
            score.setScoreInfo(r.getExpectWord().length(), players.get(turn).getUserName() + " got score!", players.get(turn).getUserName());
        }
        else {
            score.setScoreInfo(0, players.get(judgeTurn).getUserName() + " did not agree.", players.get(turn).getUserName());
        }
        broadcast(score);
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
                        r.setEndGameMessage("One player has logged out. Game ends.");
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
                            r.setEndGameMessage(" All players passed. Game ends");
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