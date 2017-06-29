package Client;

import Game.TUIManager;

public class Token extends Thread {

    private static Token instance;
    private CurrentMatch match;
    private TokenObject token;
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
        this.manager = TUIManager.GetInstance();
    }

    public void run() {
        try {
            if (match.getInGamePlayers().size() == 1) {
                token = new TokenObject(match.getToken());
            }
            wait();
        }
        catch (Exception e) {
            System.out.println("Error while token thread was running...");
            // remove player from server
            manager.RemovePlayer(match.playerName, match.getName(), match.GetPlayerIP(), match.GetPlayerPort());
            System.exit(0);
        }
    }
}
