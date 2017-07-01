package Client;

import Utilities.Comunication;
import com.google.gson.Gson;

public class Token extends Thread {

    private static Token instance;
    private CurrentMatch match;
    private InputManager inputManager;
    private Comunication comunication;
    private Gson json;
    private boolean wake;

    //singleton
    public synchronized static Token GetInstance(){
        if (instance == null) {
            instance = new Token();
        }
        return instance;
    }

    private Token() {
        this.inputManager = InputManager.GetInstance();
        this.match = CurrentMatch.GetInstance();
        this.comunication = new Comunication();
        this.json = new Gson();
        this.wake = true;
    }

    // check if process have the token or not
    public boolean isWake() {
        return wake;
    }

    public void run() {
        while (true) {
            StopToken();
            wake = true;
            // if there is a step, do it
            if (inputManager.getCommand().length == 1) {
                Step();
            }
            // if just one player remain in game, stop the token ring system
            if (match.getInGamePlayers().size() == 1) {
                wake = false;
                if (inputManager.isDying()) {
                    inputManager.StartInputManager();
                }
                // wait players in timeout before exit
                Timeout timeout = new Timeout();
                timeout.start();
                StopToken();

            }
            // send token
            comunication.SendToken();
            wake = false;
            if (inputManager.isDying()) {
                inputManager.StartInputManager();
            }
        }
    }

    // it will set in wait the token
    public synchronized void StopToken() {
        try {
            wait();
        }
        catch (Exception e) {
            System.out.println("Error while token thread was locking...");
            // remove player from server
            inputManager.SendRemovePlayerMessage();
        }
    }

    // it will start the token when the first player will enter, and notify the token to stop to wait
    public synchronized void StartToken() {
        try {
            notify();
            wake = true;
        }
        catch (Exception e) {
            System.out.println("Error while token thread was starting...");
            // remove player from server
            inputManager.SendRemovePlayerMessage();
        }
    }

    // there is a movement, do it
    private synchronized void Step() {
        // TODO
    }
}
