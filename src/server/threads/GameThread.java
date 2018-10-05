package server.threads;
import java.net.*;
import java.util.ArrayList;
import java.io.*;

import ultility.Action;
import ultility.Response;
import ultility.Player;

class GameThread extends Thread{
    private ArrayList<Player> players = new ArrayList<Player>();
    private int turn = 0;

    public void addPlayers(Player player) {
        players.add(player);
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
        int count = 0;
        int passCount = 0;
        boolean resetTurn = true;
        while (true) {
            // 判断是否结束一轮
            if(count < players.size()){
                count++;
                resetTurn = false;
            }else{
                count = 0;
                resetTurn = true;
            }
            if(resetTurn){
                passCount = 0;
            }
            try {
                
                Player currentPlayer = players.get(turn);
                //判断该玩家是否在线
                if(!currentPlayer.getSocket().isConnected()||currentPlayer.getSocket().isClosed()){
                    System.out.println(currentPlayer.getUserName() +" player if offline");
                    Response r = new Response(Response.LOGOUT);
                    broadcast(r);
                    // TODO：游戏结束.
                    break;
                }
                Action a = (Action) currentPlayer.getOis().readObject();
                if (a != null) {
                    int type = a.getActionType();
                    if (type == Action.LOGOUT) {
                        Response r = new Response(Response.LOGOUT);
                        broadcast(r);
                        // TODO：游戏结束.
                        break;
                    }
                    if(type == Action.PASS){
                        passCount++;
                        int remain = players.size() - passCount;
                        System.out.println("this turn now has "+passCount+" pass, remains " + remain);
                    }
                    if (type == Action.MOVE) {
                        Response r = new Response(Response.MOVE);
                        r.setMoveInfo(a.getCoor_x(), a.getCoor_y(), a.getInput());
                        broadcast(r);

                        if (!a.getExpectWord().contains(" ")) {
                            Response judge = new Response(Response.JUDGE);
                            judge.setJudgeInfo(a.getExpectWord(), currentPlayer.getUserName());
                            Judge(judge);
                        }
                    }
                   //该轮玩家全部pass,游戏结束
                    if(passCount == players.size()){
                        System.out.println("logout!");
                        Response r = new Response(Response.LOGOUT);
                        broadcast(r);
                        break;
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