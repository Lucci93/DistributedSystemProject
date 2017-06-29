package Client;

import Game.TUIManager;
import com.google.gson.Gson;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Peer extends Thread {

    private Socket connectionSocket;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;
    private CurrentMatch match;
    private TUIManager manager;
    private Gson json;
    private ServerPeer serverPeer;

    public Peer(Socket socket) {
        this.connectionSocket = socket;
        this.json =  new Gson();
        this.match = CurrentMatch.GetInstance();
        this.manager = TUIManager.GetInstance();
        this.serverPeer = ServerPeer.GetInstance();
    }

    public void run() {
        try {
            BufferedReader serverRead = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            Pair<MessageIDs, String> message = json.fromJson(serverRead.readLine(), Pair.class);
            // check the message
            switch(message.getKey()) {
                // move player
                case MOVE_PLAYER:
                    break;
                // at start search a coordinates to place player
                case FIND_COORDINATES:
                    System.out.println(message);
                    // send the acknowledge
                    SendMessage(FindCoordinates(message));
                    break;
                // bomb thrown alert
                case THROWN_BOMB:
                    break;
                // player added to list
                case ADD_PLAYER:
                    break;
                // alert players of die of one of them
                case DEATH_PLAYER:
                    break;
                // explosion of the bomb
                case BOMB_EXPLOSION:
                    break;
            }
            connectionSocket.close();
        }
        catch (Exception exec) {
            System.out.println("Error while peer thread was running...");
            // remove player from server
            manager.RemovePlayer(match.playerName, match.getName(), match.GetPlayerIP(), match.GetPlayerPort());
            System.exit(0);
        }
    }

    // check if coordinates of two player are the same
    private String FindCoordinates(Pair<MessageIDs, String> message) {
        Pair<Integer, Integer> coordinates = json.fromJson(message.getValue(), Pair.class);
        // if coordinates are the same return false
        if (coordinates.equals(serverPeer.getCoord())) {
            return json.toJson("same");
        }
        else {
            return  json.toJson("ok");
        }
    }

    // send a message via socket
    private void SendMessage(String ack) {
        try {
            // send the acknowledge in json
            DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
            out.writeBytes(ack + "\n");
            out.close();
        }
        catch (Exception e) {
            System.out.println("Error while peer thread was sending a message...");
            // remove player from server
            manager.RemovePlayer(match.playerName, match.getName(), match.GetPlayerIP(), match.GetPlayerPort());
            System.exit(0);
        }
    }

}
