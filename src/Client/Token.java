package Client;

import Utilities.*;
import Game.TUIManager;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Token extends Thread {

    private static Token instance;
    private CurrentMatch match;
    private Communication communication;
    private Gson json;
    private boolean dying;
    private Player player;
    private TUIManager manager;

    //singleton
    public synchronized static Token GetInstance(){
        if (instance == null) {
            instance = new Token();
        }
        return instance;
    }

    private Token() {
        this.match = CurrentMatch.GetInstance();
        this.communication = new Communication();
        this.json = new Gson();
        this.dying = false;
        this.player = Player.GetInstance();
        this.manager = TUIManager.GetInstance();
    }

    public synchronized void setDying(boolean dying) {
        this.dying = dying;
    }

    public synchronized boolean isDying() {
        return dying;
    }

    public void run() {
        while (true) {
            StopToken();
            if (dying) {
                // remove player from all the lists of players
                SendRemovePlayerMessage();
                // send token and exit
                communication.SendToken();
                System.out.println("\nYOU LOOSE!!");
                System.exit(0);
            }
            // if there is a step, do it
            if (match.getCommand().size() == 1) {
                MovePlayer();
                // empty buffer input
                match.getCommand().remove(0);
            }
            // if just one player remain in game, stop the token ring system
            if (match.getInGamePlayers().size() == 1) {
                // wait players in timeout before exit
                Timeout timeout = new Timeout();
                timeout.start();
            }
            else {
                // send token
                communication.SendToken();
            }
        }
    }

    // it will set in wait the token
    public synchronized void StopToken() {
        try {
            wait();
            sleep(2000); // TODO
        }
        catch (Exception e) {
            System.out.println("Error while token thread was locking...");
            // remove player from server
            SendRemovePlayerMessage();
        }
    }

    // it will start the token when the first player will enter, and notify the token to stop to wait
    public synchronized void StartToken() {
        try {
            notify();
        }
        catch (Exception e) {
            System.out.println("Error while token thread was starting...");
            // remove player from server
            SendRemovePlayerMessage();
        }
    }

    // sent to all the player the step did, to check if player kill someone
    public synchronized void MovePlayer() {
        // wait the sent of the message
        Message message = new Message(MessageIDs.MOVE_PLAYER, json.toJson(match.getCoord()));
        ArrayList<Integer> points = communication.CheckKillMessage(message);
        if (points != null) {
            // add points to the score for kills
            // we are sure that just one player was killed
            for (int i = 0; i < points.size(); i++) {
                match.setScore(points.get(i));
                // if player have won
                if (match.HaveWon()) {
                    Won();
                    StopToken();
                }
            }
        }
        // something wrong
        else {
            System.out.println("Error while socket try to send a movement message to players...");
            SendRemovePlayerMessage();
        }
    }

    // sent to all the players in game that player is exiting
    public synchronized void SendRemovePlayerMessage() {
        // remove from the lists of players in game and server
        manager.RemovePlayer(match.getPlayerName(), match.getName(), match.getPlayerIP(), match.getPlayerPort());
        String ack = communication.MessageGate(new Message(MessageIDs.DEATH_PLAYER, json.toJson(player)));
        // wrong request
        if (ack == null) {
            // remove player from server
            System.out.println("Error while socket try to send a remove message to players...");
        }
        else {
            System.out.println("Exit from game...");
        }
    }

    // sent to all the player the it won
    public synchronized void Won() {
        // wait the sent of the message
        Message message = new Message(MessageIDs.WIN, json.toJson("won"));
        communication.MessageGate(message);
        // exit from game
        manager.RemovePlayer(match.getPlayerName(), match.getName(), match.getPlayerIP(), match.getPlayerPort());
        System.out.println("\nYOU WIN!!");
        System.exit(0);
    }
}
