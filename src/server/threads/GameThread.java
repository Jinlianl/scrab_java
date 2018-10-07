package server.threads;
import java.net.*;
import java.util.ArrayList;
import java.io.*;

import ultility.Action;
import ultility.Response;
import ultility.Player;

public class GameThread extends Thread{
    private ArrayList<Player> players = new ArrayList<Player>();
    private int turn = 0;
    private boolean started = false;
    // TODO: 计分系统

    public boolean isStarted() {
        return started;
    }

    public void addPlayers(Player player) {
        players.add(player);
    }

    public int getPlayersNum() {
        return players.size();
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
                break;
            }
        }
    }

}