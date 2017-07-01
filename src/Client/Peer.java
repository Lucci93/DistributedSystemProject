package Client;

import Utilities.Message;
import Utilities.Player;
import Utilities.TokenObject;
import com.google.gson.Gson;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Peer extends Thread {

    private Socket connectionSocket;
    private CurrentMatch match;
    private Gson json;
    private InputManager inputManager;
    private Token tokenThread;

    public Peer(Socket socket) {
        this.connectionSocket = socket;
        this.json =  new Gson();
        this.match = CurrentMatch.GetInstance();
        this.inputManager = InputManager.GetInstance();
        this.tokenThread = Token.GetInstance();
    }

    public void run() {
        try {
            BufferedReader serverRead = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            Message message = json.fromJson(serverRead.readLine(), Message.class);
            // check the message
            switch(message.getId()) {
                // move player
                case MOVE_PLAYER:
                    break;
                // at start search a coordinates to place player
                case FIND_COORDINATES:
                    // send the acknowledge
                    SendMessage(FindCoordinates(json.fromJson(message.getMessage(), Pair.class)));
                    break;
                // bomb thrown alert
                case THROWN_BOMB:
                    break;
                // player added to list
                case ADD_PLAYER:
                    SendMessage(AddPlayer(json.fromJson(message.getMessage(), Player.class)));
                    break;
                // alert players of die of one of them
                case DEATH_PLAYER:
                    SendMessage(RemovePlayer(json.fromJson(message.getMessage(), Player.class)));
                    break;
                // explosion of the bomb
                case BOMB_EXPLOSION:
                    break;
                // token received
                case TOKEN:
                    SendMessage(CheckToken(json.fromJson(message.getMessage(), TokenObject.class)));
                    break;
                // player's win alert
                case WIN:
                    break;
                default:
                    System.out.println("Error while peer thread was running...");
                    // remove player from server
                    inputManager.SendRemovePlayerMessage();
            }
        }
        catch (Exception exec) {
            System.out.println("Error while peer thread was running...");
            // remove player from server
            inputManager.SendRemovePlayerMessage();
        }
    }

    // check if coordinates of two player are the same
    private synchronized String FindCoordinates(Pair message) {
        // if coordinates are the same return false
        if (message.equals(match.getCoord())) {
            return json.toJson("same");
        }
        else {
            return json.toJson("ok");
        }
    }

    // add player to the list of players in the current match
    private synchronized String AddPlayer(Player message) {
        match.AddPlayerToGame(message.getName(), message.getIp(), message.getPort());
        return json.toJson("ok");
    }

    private synchronized String CheckToken(TokenObject token) {
        // if there are the same token
        if (token.getId().equals(match.getToken())) {
            // wake up token
            tokenThread.StartToken();
            return json.toJson("ok");
        }
        else {
            return json.toJson("different");
        }
    }

    // add player to the list of players in the current match
    private synchronized String RemovePlayer(Player message) {
        match.RemovePlayerToGame(message.getName(), message.getIp(), message.getPort());
        return json.toJson("ok");
    }

    // send a message via socket
    private synchronized void SendMessage(String ack) {
        try {
            // send the acknowledge in json
            DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
            out.writeBytes(ack + "\n");
            out.close();
        }
        catch (Exception e) {
            System.out.println("Error while peer thread was sending a message...");
            // remove player from server
            inputManager.SendRemovePlayerMessage();
        }
    }

}
