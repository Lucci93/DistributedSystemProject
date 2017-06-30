package Client;

import Game.TUIManager;
import Utilities.Message;
import Utilities.MessageIDs;
import Utilities.Player;
import com.google.gson.Gson;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class ServerPeer {

    private static ServerPeer instance;
    private Integer portAddress;
    private String IPAddress;
    private CurrentMatch match;
    private Integer score;
    private Pair<Integer, Integer> coord;
    private Token tokenThread;
    private Player player;
    private InputManager inputManagerThread;
    private BombManager bombManagerThread;
    private Gson json;
    private TUIManager manager;

    private ServerPeer(Integer portAddress, String IPAddress) {
        this.portAddress = portAddress;
        this.IPAddress = IPAddress;
        this.score = 0;
        this.player = Player.GetInstance();
        this.match = CurrentMatch.GetInstance();
        this.manager = TUIManager.GetInstance();
        this.coord = SetCoordinates();
        this.tokenThread = Token.GetInstance();
        this.inputManagerThread = InputManager.GetInstance();
        this.bombManagerThread = BombManager.GetInstance();
        this.json = new Gson();
    }

    private ServerPeer() {}

    //singleton initialization
    public synchronized static ServerPeer GetInstance(Integer portAddress, String IPAddress){
        if (instance == null) {
            instance = new ServerPeer(portAddress, IPAddress);
        }
        return instance;
    }

    public synchronized static ServerPeer GetInstance(){
        if (instance == null) {
            instance = new ServerPeer();
        }
        return instance;
    }



    public synchronized void StartServerPeer() {
        try {
            // initialize server socket
            ServerSocket serverSocket = new ServerSocket(portAddress);
            // if there are other player check the other position
            if (match.getInGamePlayers().size() > 1) {
                // search position
                CheckStartPosition();
                // notify that player is in game
                SendAddingPlayerMessage();
            }
            // start token thread
            tokenThread.start();
            // start input message thread
            inputManagerThread.start();
            // start bomb thread
            bombManagerThread.start();
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
            manager.RemovePlayer(match.getPlayerName(), match.getName(), match.GetPlayerIP(), match.GetPlayerPort());
            System.exit(0);
        }
    }

    // set start coordinates
    private synchronized Pair<Integer, Integer> SetCoordinates() {
        Integer x = new Random().nextInt(match.getSizeSide() + 1);
        Integer y = new Random().nextInt(match.getSizeSide() + 1);
        return new Pair<>(x, y);
    }

    // get coordinates
    public Pair<Integer, Integer> getCoord() {
        return coord;
    }

    // send and receive message
    private synchronized String MessageGate(MessageIDs id, String message) {
        ArrayList<Socket> socketList = new ArrayList<>();
        // send message with position to players
        for(int i = 0; i < match.getInGamePlayers().size(); i++) {
            // if it's not me
            if (!portAddress.equals(match.getInGamePlayersPort().get(i))) {
                try {
                    socketList.add(i, new Socket(match.getInGamePlayersIP().get(i), match.getInGamePlayersPort().get(i)));
                    DataOutputStream out = new DataOutputStream(socketList.get(i).getOutputStream());
                    Message m = new Message(id, message);
                    out.writeBytes(json.toJson(m) + "\n");
                }
                catch (Exception exec) {
                    System.out.println("Error of connection between players while socket try to send message...");
                    // remove player from server
                    manager.RemovePlayer(match.getPlayerName(), match.getName(), match.GetPlayerIP(), match.GetPlayerPort());
                    System.exit(0);
                }
                try {
                    // if it's not me
                    BufferedReader serverRead = new BufferedReader(new InputStreamReader(socketList.get(i).getInputStream()));
                    String ack = json.fromJson(serverRead.readLine(), String.class);
                    socketList.get(i).close();
                    if (!ack.equals("ok")) {
                        return null;
                    }
                }
                catch (Exception exec) {
                    System.out.println("Error of connection between players while socket try to receive an acknowledge...");
                    // remove player from server
                    manager.RemovePlayer(match.getPlayerName(), match.getName(), match.GetPlayerIP(), match.GetPlayerPort());
                    System.exit(0);
                }
            }
        }
        return "ok";
    }

    // check position of the player where to start
    private synchronized void CheckStartPosition() {
        boolean findCoord = false;
        while (findCoord) {
            String ack = MessageGate(MessageIDs.FIND_COORDINATES, json.toJson(coord));
            if (ack == null) {
                coord = SetCoordinates();
            }
            else {
                findCoord = true;
            }
        }
    }

    // sent to all the players in game that player is in game sending the token
    private synchronized void SendAddingPlayerMessage() {
        String ack = MessageGate(MessageIDs.ADD_PLAYER, json.toJson(player));
        // wrong request
        if (ack == null) {
            // remove player from server
            System.out.println("Error while socket try to send a adding message to player...");
            manager.RemovePlayer(match.getPlayerName(), match.getName(), match.GetPlayerIP(), match.GetPlayerPort());
            System.exit(0);
        }
        else {
            // there is just one add with two player, so start the token
            if (match.getInGamePlayers().size() == 2) {
                tokenThread.notify();
            }
        }
    }
}
