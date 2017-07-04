package Client;

import Game.TUIManager;
import Utilities.*;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Peer extends Thread {

    private Socket connectionSocket;
    private CurrentMatch match;
    private Gson json;
    private Token tokenThread;
    private TUIManager manager;

    public Peer(Socket socket) {
        this.connectionSocket = socket;
        this.json =  new Gson();
        this.match = CurrentMatch.GetInstance();
        this.tokenThread = Token.GetInstance();
        this.manager = TUIManager.GetInstance();
    }

    public void run() {
        try {
            BufferedReader serverRead = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            Message message = json.fromJson(serverRead.readLine(), Message.class);
            // check the message
            switch(message.getId()) {
                // move player
                case MOVE_PLAYER:
                    SendMessage(CheckCoordinates(json.fromJson(message.getMessage(), Coordinates.class)));
                    break;
                // at start search a coordinates to place player
                case FIND_COORDINATES:
                    // send the acknowledge
                    SendMessage(FindCoordinates(json.fromJson(message.getMessage(), Coordinates.class)));
                    break;
                // bomb thrown alert
                case THROWN_BOMB:
                    // send message of alert on terminal
                    System.out.println(match.Area(json.fromJson(message.getMessage(), Integer.class)));
                    // send the acknowledge
                    SendMessage(json.toJson("ok"));
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
                    SendMessage(CheckBomb(json.fromJson(message.getMessage(), Integer.class)));
                    break;
                // token received
                case TOKEN:
                    SendMessage(CheckToken(json.fromJson(message.getMessage(), TokenObject.class)));
                    break;
                // player's win alert
                case WIN:
                    SendMessage("ok");
                    manager.RemovePlayer(match.getPlayerName(), match.getName(), match.getPlayerIP(), match.getPlayerPort());
                    System.out.println("\nYOU LOOSE!!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Error while peer thread was checking message...");
                    // remove player from server
                    tokenThread.SendRemovePlayerMessage();
                    break;
            }
        }
        catch (Exception exec) {
            exec.printStackTrace();
            System.out.println("Error while peer thread was running...");
            // remove player from server
            tokenThread.SendRemovePlayerMessage();
        }
    }

    // check if coordinates of two player are the same
    private synchronized String FindCoordinates(Coordinates message) {
        // if coordinates are the same return false
        if (message.equals(match.getCoord())) {
            return json.toJson("same");
        }
        else {
            return json.toJson("ok");
        }
    }

    // check if bomb is exploded in the same area of player, to check if is killed
    private synchronized String CheckBomb(Integer message) {
        System.out.println("Bomb Exploded!!");
        // if bomb exploded in your area
        if (match.CheckArea(message) && !tokenThread.isDying()) {
            // if player was killed, wait token and exit from game
            tokenThread.setDying(true);
            // give a point to the enemy
            return json.toJson(1);
        }
        else {
            // player was not killed
            return json.toJson(0);
        }
    }

    // check if coordinates of two player are the same, to check if is killed
    private synchronized String CheckCoordinates(Coordinates message) {
        // if coordinates are the same and player is not killed yet, kill player
        if (message.equals(match.getCoord()) && !tokenThread.isDying()) {
            // if player was killed, wait token and exit from game
            tokenThread.setDying(true);
            // give a point to the enemy
            return json.toJson(1);
        }
        else {
            // player was not killed
            return json.toJson(0);
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
            tokenThread.SendRemovePlayerMessage();
        }
    }
}
