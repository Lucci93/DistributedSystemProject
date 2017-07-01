package Client;

import Game.TUIManager;
import Utilities.Message;
import Utilities.MessageIDs;
import Utilities.TokenObject;
import com.google.gson.Gson;
import javafx.util.Pair;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Token extends Thread {

    private static Token instance;
    private CurrentMatch match;
    private TokenObject token;
    private TUIManager manager;
    private InputManager inputManager;
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
        this.manager = TUIManager.GetInstance();
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
            if (inputManager.getCommand().length == 1) {
                Step();
            }
            SendToken();
        }
    }

    // it will start the token when the first player will enter
    public synchronized void StopToken() {
        try {
            wake = false;
            wait();
        }
        catch (Exception e) {
            System.out.println("Error while token thread was locking...");
            // remove player from server
            manager.RemovePlayer(match.getPlayerName(), match.getName(), match.GetPlayerIP(), match.GetPlayerPort());
            System.exit(0);
        }
    }

    // it will start the token when the first player will enter
    public synchronized void StartToken() {
        try {
            notify();
            wake = true;
        }
        catch (Exception e) {
            System.out.println("Error while token thread was starting...");
            // remove player from server
            manager.RemovePlayer(match.getPlayerName(), match.getName(), match.GetPlayerIP(), match.GetPlayerPort());
            System.exit(0);
        }
    }

    // there is a movement, do it
    private synchronized void Step() {
        // TODO
    }

    // send the token to other player
    private synchronized void SendToken() {
        Socket socket = null;
        Pair<String, Integer> nextPlayer = FindPlayerInRing();
        try {
            socket = new Socket(nextPlayer.getKey(), nextPlayer.getValue());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            token = new TokenObject(match.getToken());
            Message m = new Message(MessageIDs.TOKEN, json.toJson(token));
            out.writeBytes(json.toJson(m) + "\n");
        } catch (Exception e) {
            System.out.println("Error while socket try to send token...");
            // remove player from server
            manager.RemovePlayer(match.getPlayerName(), match.getName(), match.GetPlayerIP(), match.GetPlayerPort());
            System.exit(0);
        }
        // receive an acknowledge
        try {
            BufferedReader serverRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String ack = json.fromJson(serverRead.readLine(), String.class);
            socket.close();
            // token sent, so now wait to receive it again
            if (!ack.equals("ok")) {
                System.out.println("Wrong token sent...");
                // remove player from server
                manager.RemovePlayer(match.getPlayerName(), match.getName(), match.GetPlayerIP(), match.GetPlayerPort());
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println("Error while socket try to receive an acknowledge about token sent...");
            // remove player from server
            manager.RemovePlayer(match.getPlayerName(), match.getName(), match.GetPlayerIP(), match.GetPlayerPort());
            System.exit(0);
        }
    }

    private synchronized Pair<String, Integer> FindPlayerInRing() {
        for (int i = 0; i < match.getInGamePlayers().size(); i++) {
            // find player position
            if (match.getInGamePlayers().get(i).equals(match.getPlayerName())) {
                // and take the next
                if (match.getInGamePlayers().size() == i+1) {
                    return new Pair<>(match.getInGamePlayersIP().get(0), match.getInGamePlayersPort().get(0));
                }
                // is is not the last in the list
                else {
                    return new Pair<>(match.getInGamePlayersIP().get(i+1), match.getInGamePlayersPort().get(i+1));
                }
            }
        }
        // impossible error
        return null;
    }
}
