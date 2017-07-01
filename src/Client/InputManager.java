package Client;

import Game.TUIManager;
import Utilities.Message;
import Utilities.MessageIDs;
import Utilities.Player;
import Utilities.Communication;
import com.google.gson.Gson;

import java.util.LinkedList;

public class InputManager extends Thread {

    private static InputManager instance;
    // FIFO list for bomb
    private LinkedList<Integer> fifoBombList;
    // arrey of one element of terminal commands
    private String[] command;
    private CurrentMatch match;
    private Player player;
    private TUIManager manager;
    private boolean dying;
    private Communication communication;
    private Gson json;

    private InputManager() {
        this.manager = TUIManager.GetInstance();
        this.match = CurrentMatch.GetInstance();
        this.player = Player.GetInstance();
        this.fifoBombList = new LinkedList<>();
        this.command = new String[1];
        this.communication = new Communication();
        this.dying = false;
        this.json = new Gson();
    }

    public boolean isDying() {
        return dying;
    }

    //singleton
    public synchronized static InputManager GetInstance(){
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }

    public void run() {
        while (true) {
            StopInputManager();
        }
    }

    // it will start the token when the first player will enter
    public synchronized void StopInputManager() {
        try {
            wait();
        }
        catch (Exception e) {
            System.out.println("Error while input manager thread was locking...");
            // remove player from server
            SendRemovePlayerMessage();
        }
    }

    // it will start the token when the first player will enter
    public synchronized void StartInputManager() {
        try {
            notify();
        }
        catch (Exception e) {
            System.out.println("Error while inout manager thread was locking...");
            // remove player from server
            SendRemovePlayerMessage();
        }
    }

    // sent to all the players in game that player is exiting
    public synchronized void SendRemovePlayerMessage() {
        Token tokenThread = Token.GetInstance();
        dying = true;
        // remove from the lists of players in game and server
        manager.RemovePlayer(match.getPlayerName(), match.getName(), match.getPlayerIP(), match.getPlayerPort());
        String ack = communication.MessageGate(new Message(MessageIDs.DEATH_PLAYER, json.toJson(player)));
        // if token is sending to another wait
        if (tokenThread.isWake()) {
            // wait that token is sent
            StopInputManager();
        }
        // wrong request
        if (ack == null) {
            // remove player from server
            System.out.println("Error while socket try to send a remove message to player...");
        }
        else {
            System.out.println("Exit from game...");
        }
        System.exit(0);
    }

    public synchronized LinkedList<Integer> getFifoBombList() {
        return fifoBombList;
    }

    public synchronized String[] getCommand() {
        return command;
    }
}
