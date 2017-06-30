package Client;

import Game.TUIManager;

import java.util.LinkedList;

public class InputManager extends Thread {

    private static InputManager instance;
    // FIFO list for bomb
    private LinkedList<Integer> fifoBombList;
    // arrey of one element of terminal commands
    private String[] command;
    private CurrentMatch match;
    private TUIManager manager;

    private InputManager() {
        this.match = CurrentMatch.GetInstance();
        this.manager = TUIManager.GetInstance();
        this.fifoBombList = new LinkedList<>();
        this.command = new String[1];
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
            StartManager();
        }
    }

    private synchronized void StartManager() {
        try {
            wait();
        }
        catch (Exception e) {
            System.out.println("Error while token input manager thread was starting...");
            // remove player from server
            manager.RemovePlayer(match.getPlayerName(), match.getName(), match.GetPlayerIP(), match.GetPlayerPort());
            System.exit(0);
        }
    }

    public synchronized LinkedList<Integer> getFifoBombList() {
        return fifoBombList;
    }

    public synchronized String[] getCommand() {
        return command;
    }
}
