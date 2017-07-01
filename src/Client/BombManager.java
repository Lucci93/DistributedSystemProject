package Client;

public class BombManager extends Thread {

    private static BombManager instance;

    //singleton
    public synchronized static BombManager GetInstance(){
        if (instance == null) {
            instance = new BombManager();
        }
        return instance;
    }

    private CurrentMatch match;
    private InputManager inputManager;

    private BombManager() {
        this.match = CurrentMatch.GetInstance();
        this.inputManager = InputManager.GetInstance();
    }

    public void run() {
        while (true) {
            StartBomb();
        }
    }

    private synchronized void StartBomb() {
        try {
            wait();
        }
        catch (Exception e) {
            System.out.println("Error while bomb thread was starting...");
            // remove player from server
            inputManager.SendRemovePlayerMessage();
        }
    }
}
