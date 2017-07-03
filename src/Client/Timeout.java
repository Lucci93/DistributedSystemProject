package Client;

import Game.TUIManager;
import Utilities.CurrentMatch;

public class Timeout extends Thread {

    public void run() {
        CheckTimeout();
    }

    private void CheckTimeout() {
        try {
            System.out.println("Waiting players...");
            CurrentMatch match = CurrentMatch.GetInstance();
            sleep(15000);
            // player is already alone in game
            if (match.getInGamePlayers().size() < 2) {
                // remove from the list of players in game and server
                System.out.println("No player was arrived, you won, exit from game...");
                TUIManager.GetInstance().RemovePlayer(match.getPlayerName(), match.getName(), match.getPlayerIP(), match.getPlayerPort());
                System.exit(0);
            }
        }
        catch (Exception e) {
            System.out.println("Error while timeout thread was locking...");
            // remove player from server
            Token.GetInstance().SendRemovePlayerMessage();
        }
    }
}
