package Client;

import Game.TUIManager;

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
    private TUIManager manager;

    private BombManager() {
        this.match = CurrentMatch.GetInstance();
        this.manager = TUIManager.GetInstance();
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
            manager.RemovePlayer(match.getPlayerName(), match.getName(), match.GetPlayerIP(), match.GetPlayerPort());
            System.exit(0);
        }
    }
}
