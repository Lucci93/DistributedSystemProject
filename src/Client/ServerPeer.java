package Client;

import Game.TUIManager;
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
    private InputManager inputManagerThread;
    private BombManager bombManagerThread;
    private Gson json;
    private TUIManager manager;

    private ServerPeer(Integer portAddress, String IPAddress) {
        this.portAddress = portAddress;
        this.IPAddress = IPAddress;
        this.score = 0;
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
            manager.RemovePlayer(match.playerName, match.getName(), match.GetPlayerIP(), match.GetPlayerPort());
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

    // check position of the player where to start
    private synchronized void CheckStartPosition() {
        ArrayList<Socket> socketList = new ArrayList<>();
        Integer count = 0;
        while (count != match.getInGamePlayers().size() -1) {
            count = 0;
            // send message with position to players
            for(int i = 0; i < match.getInGamePlayers().size(); i++) {
                // if it's not me
                if (!portAddress.equals(match.getInGamePlayersPort().get(i))) {
                    try {
                        socketList.add(i, new Socket(match.getInGamePlayersIP().get(i), match.getInGamePlayersPort().get(i)));
                        DataOutputStream out = new DataOutputStream(socketList.get(i).getOutputStream());
                        Message m = new Message(MessageIDs.FIND_COORDINATES, json.toJson(coord));
                        out.writeBytes(json.toJson(m) + "\n");
                    }
                    catch (Exception exec) {
                        System.out.println("Error of connection between players while socket try to send coordinates...");
                        // remove player from server
                        manager.RemovePlayer(match.playerName, match.getName(), match.GetPlayerIP(), match.GetPlayerPort());
                        System.exit(0);
                    }
                        try {
                        // if it's not me
                        BufferedReader serverRead = new BufferedReader(new InputStreamReader(socketList.get(i).getInputStream()));
                        String ack = json.fromJson(serverRead.readLine(), String.class);
                        socketList.get(i).close();
                        // if the coordinate exist try another
                        if (ack != "ok") {
                            coord = SetCoordinates();
                            count = 0;
                            break;
                        } else {
                            count++;
                        }
                    }
                    catch (Exception exec) {
                        System.out.println("Error of connection between players while socket try to receive coordinates acknowledge...");
                        // remove player from server
                        manager.RemovePlayer(match.playerName, match.getName(), match.GetPlayerIP(), match.GetPlayerPort());
                        exec.printStackTrace();
                        System.exit(0);
                    }
                }
            }
        }
    }
}
