package Client;

import Utilities.Communication;
import Utilities.CurrentMatch;
import Utilities.Message;
import Utilities.MessageIDs;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Bomb extends Thread {

    private Integer bomb;
    private Communication communication;
    private Gson json;
    private Token tokenThread;
    private CurrentMatch match;

    public Bomb(Integer bomb) {
        this.bomb = bomb;
        this.communication = new Communication();
        this.json = new Gson();
        this.tokenThread = Token.GetInstance();
        this.match = CurrentMatch.GetInstance();
    }

    public void run() {
        CheckTimeout();
    }

    private void CheckTimeout() {
        try {
            System.out.println("Start countdown!");
            int count = 5;
            while (count != 0) {
                System.out.println(count);
                sleep(1000);
                count--;
            }
            System.out.println("Bomb Exploded!!");
            // send explosion message
            Message message = new Message(MessageIDs.BOMB_EXPLOSION, json.toJson(bomb));
            ArrayList<Integer> points = communication.CheckKillMessage(message);
            // add points
            if (points != null) {
                // if was in your zone, die
                if (CurrentMatch.GetInstance().CheckArea(bomb)) {
                    tokenThread.setDying(true);
                }
                // add points to the score for kills
                // we are sure that just one player was killed
                for (int i = 0; i < points.size(); i++) {
                    match.setScore(points.get(i));
                    // if player have won
                    if (match.HaveWon()) {
                        tokenThread.Won();
                        tokenThread.StopToken();
                    }
                }
            }
            // something wrong
            else {
                System.out.println("Error while socket try to send a movement message to players...");
                tokenThread.SendRemovePlayerMessage();
            }
        }
        catch (Exception e) {
            System.out.println("Error while bomb thread was sleeping...");
            // remove player from server
            tokenThread.SendRemovePlayerMessage();
        }
    }
}
