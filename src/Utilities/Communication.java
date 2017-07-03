package Utilities;

import Client.Token;
import com.google.gson.Gson;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class Communication {

    private CurrentMatch match;
    private Gson json;

    public Communication() {
        this.match = CurrentMatch.GetInstance();
        this.json = new Gson();
    }

    // send and receive message
    public synchronized String MessageGate(Message message) {
        Token tokenThread = Token.GetInstance();
        ArrayList<Socket> socketList = new ArrayList<>();
        // send message with position to players
        for(int i = 0; i < match.getInGamePlayers().size(); i++) {
            // if it's not me
            if (!match.getPlayerPort().equals(match.getInGamePlayersPort().get(i))) {
                try {
                    socketList.add(new Socket(match.getInGamePlayersIP().get(i), match.getInGamePlayersPort().get(i)));
                    DataOutputStream out = new DataOutputStream(socketList.get(i).getOutputStream());
                    out.writeBytes(json.toJson(message) + "\n");
                }
                catch (Exception exec) {
                    System.out.println("Error of connection between players while socket try to send message...");
                    // remove player from server
                    tokenThread.SendRemovePlayerMessage();
                }
                try {
                    // if it's not me
                    BufferedReader serverRead = new BufferedReader(new InputStreamReader(socketList.get(i).getInputStream()));
                    String ack = json.fromJson(serverRead.readLine(), String.class);
                    socketList.get(0).close();
                    socketList.remove(0);
                    if (!ack.equals("ok")) {
                        return null;
                    }
                }
                catch (Exception exec) {
                    System.out.println("Error of connection between players while socket try to receive an acknowledge...");
                    // remove player from server
                    tokenThread.SendRemovePlayerMessage();
                }
            }
        }
        return "ok";
    }

    // send and receive message
    public synchronized ArrayList<Integer> CheckKillMessage(Message message) {
        Token tokenThread = Token.GetInstance();
        ArrayList<Socket> socketList = new ArrayList<>();
        ArrayList<Integer> pointList = new ArrayList<>();
        // send message with position to players
        for(int i = 0; i < match.getInGamePlayers().size(); i++) {
            // if it's not me
            if (!match.getPlayerPort().equals(match.getInGamePlayersPort().get(i))) {
                try {
                    socketList.add(new Socket(match.getInGamePlayersIP().get(i), match.getInGamePlayersPort().get(i)));
                    DataOutputStream out = new DataOutputStream(socketList.get(i).getOutputStream());
                    out.writeBytes(json.toJson(message) + "\n");
                }
                catch (Exception exec) {
                    System.out.println("Error of connection between players while socket try to send movement message...");
                    // remove player from server
                    tokenThread.SendRemovePlayerMessage();
                }
                try {
                    // if it's not me
                    BufferedReader serverRead = new BufferedReader(new InputStreamReader(socketList.get(i).getInputStream()));
                    Integer ack = json.fromJson(serverRead.readLine(), Integer.class);
                    socketList.get(0).close();
                    socketList.remove(0);
                    pointList.add(ack);
                }
                catch (Exception exec) {
                    System.out.println("Error of connection between players while socket try to receive movement acknowledge...");
                    // remove player from server
                    tokenThread.SendRemovePlayerMessage();
                }
            }
        }
        return pointList;
    }

    // send the token to other player
    public synchronized void SendToken() {
        Token tokenThread = Token.GetInstance();
        Socket socket = null;
        Pair<String, Integer> nextPlayer = FindPlayerInRing();
        try {
            socket = new Socket(nextPlayer.getKey(), nextPlayer.getValue());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            TokenObject token = new TokenObject(match.getToken());
            Message m = new Message(MessageIDs.TOKEN, json.toJson(token));
            out.writeBytes(json.toJson(m) + "\n");
        } catch (Exception e) {
            System.out.println("Error while socket try to send token...");
            // remove player from server
            tokenThread.SendRemovePlayerMessage();
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
                tokenThread.SendRemovePlayerMessage();
            }
        } catch (Exception e) {
            System.out.println("Error while socket try to receive an acknowledge about token sent...");
            // remove player from server
            tokenThread.SendRemovePlayerMessage();
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
