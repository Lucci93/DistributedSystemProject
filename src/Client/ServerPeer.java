package Client;


import Utilities.*;
import Game.TUI;
import com.google.gson.Gson;

import javax.print.DocFlavor;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerPeer extends Thread {

    private static ServerPeer instance;
    private CurrentMatch match;
    private Player player;
    private Token tokenThread;
    private Communication communication;
    private Gson json;
    private InputManager inputThread;

    private ServerPeer() {
        this.player = Player.GetInstance();
        this.match = CurrentMatch.GetInstance();
        this.tokenThread = Token.GetInstance();
        this.communication = new Communication();
        this.match.setCoord();
        this.json = new Gson();
        this.inputThread = InputManager.GetInstance();
    }

    //singleton initialization
    public synchronized static ServerPeer GetInstance(){
        if (instance == null) {
            instance = new ServerPeer();
        }
        return instance;
    }

    public void run() {
        // start socket server
        Start();
    }

    public synchronized void Start() {
        try {
            // initialize server socket
            ServerSocket serverSocket = new ServerSocket(match.getPlayerPort());
            // if there are other player check the other position
            if (match.getInGamePlayers().size() > 1) {
                // search position
                CheckStartPosition();
                // notify that player is in game
                SendAddingPlayerMessage();
            }
            inputThread.start();
            while (true) {
                // if some message will arrive, a thread will be launched to manage the message
                Socket socket = serverSocket.accept();
                Peer peerThread = new Peer(socket);
                peerThread.start();
            }
        }
        catch (Exception e) {
            System.out.println("Error while socket was in listening...");
            // remove player from server
            tokenThread.SendRemovePlayerMessage();
        }
    }

    // check position of the player where to start
    private synchronized void CheckStartPosition() {
        boolean findCoord = false;
        while (!findCoord) {
            Message message = new Message(MessageIDs.FIND_COORDINATES, json.toJson(match.getCoord()));
            String ack = communication.MessageGate(message);
            if (ack == null) {
                match.setCoord();
            }
            else {
                findCoord = true;
            }
        }
    }

    // sent to all the players in game that player is in game
    private synchronized void SendAddingPlayerMessage() {
        Message message = new Message(MessageIDs.ADD_PLAYER, json.toJson(player));
        String ack = communication.MessageGate(message);
        // wrong request
        if (ack == null) {
            // remove player from server
            System.out.println("Error while socket try to send a adding message to player...");
            tokenThread.SendRemovePlayerMessage();
        }
        else {
            // there is just one add with two player, so start the token
            if (match.getInGamePlayers().size() == 2) {
                tokenThread.StartToken();
            }
        }
    }
}
