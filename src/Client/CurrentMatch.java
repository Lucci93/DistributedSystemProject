package Client;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Random;

public class CurrentMatch {

    private static CurrentMatch instance;

    // name of the game
    private String name;
    // token identifier
    private String token;
    // player name
    private String playerName;
    // size of a side of the map
    private Integer sizeSide;
    // current score point
    private Integer score;
    // current currentCoordinates
    private Pair<Integer, Integer> coord;
    // list of player in game
    private ArrayList<String> inGamePlayers;
    // score to win the match
    private Integer maxScore;
    // list of player in game IP address
    private ArrayList<String> inGamePlayersIP;
    // list of player in game port
    private ArrayList<Integer> inGamePlayersPort;

    public CurrentMatch(String name, String token, String playerName, Integer sizeSide, ArrayList<String> inGamePlayers, Integer maxScore, ArrayList<String> inGamePlayersIP, ArrayList<Integer> inGamePlayersPort) {
        this.name = name;
        this.token = token;
        this.playerName = playerName;
        this.sizeSide = sizeSide;
        this.score = 0;
        this.coord = new Pair<>(0,0);
        this.inGamePlayers = inGamePlayers;
        this.maxScore = maxScore;
        this.inGamePlayersIP = inGamePlayersIP;
        this.inGamePlayersPort = inGamePlayersPort;
    }

    private CurrentMatch() {}

    //singleton initialization
    public synchronized static CurrentMatch GetInstance(String playerName, String name, String token, Integer sizeSide, ArrayList<String> inGamePlayers, Integer maxScore, ArrayList<String> inGamePlayersIP, ArrayList<Integer> inGamePlayersPort){
        if (instance == null) {
            instance = new CurrentMatch(playerName, name, token, sizeSide, inGamePlayers, maxScore, inGamePlayersIP, inGamePlayersPort);
        }
        return instance;
    }

    //general singleton
    public synchronized static CurrentMatch GetInstance() {
        if (instance == null) {
            instance = new CurrentMatch();
        }
        return instance;
    }

    public synchronized String getPlayerIP() {
        for (int i = 0; i < getInGamePlayers().size(); i++) {
            if (playerName.equals(getInGamePlayers().get(i))) {
                return getInGamePlayersIP().get(i);
            }
        }
        return null;
    }

    public synchronized Integer getPlayerPort() {
        for (int i = 0; i < getInGamePlayers().size(); i++) {
            if (playerName.equals(getInGamePlayers().get(i))) {
                return getInGamePlayersPort().get(i);
            }
        }
        return null;
    }

    // add a player to game
    public synchronized void AddPlayerToGame(String playerName, String IPAddress, Integer portAddress) {
        inGamePlayers.add(playerName);
        inGamePlayersIP.add(IPAddress);
        inGamePlayersPort.add(portAddress);
    }

    // add a player to game
    public synchronized void RemovePlayerToGame(String playerName, String IPAddress, Integer portAddress) {
        inGamePlayers.remove(playerName);
        inGamePlayersIP.remove(IPAddress);
        inGamePlayersPort.remove(portAddress);
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized String getToken() {
        return token;
    }

    public synchronized void setToken(String token) {
        this.token = token;
    }

    public synchronized String getPlayerName() {
        return playerName;
    }

    public synchronized Integer getSizeSide() {
        return sizeSide;
    }

    public synchronized ArrayList<String> getInGamePlayers() {
        return inGamePlayers;
    }

    public synchronized Integer getMaxScore() {
        return maxScore;
    }

    public synchronized ArrayList<String> getInGamePlayersIP() {
        return inGamePlayersIP;
    }

    public synchronized ArrayList<Integer> getInGamePlayersPort() {
        return inGamePlayersPort;
    }

    public synchronized Integer getScore() {
        return score;
    }

    public synchronized void setScore(Integer score) {
        this.score = score;
    }

    public synchronized Pair<Integer, Integer> getCoord() {
        return coord;
    }

    // set start coordinates
    public synchronized void setCoord() {
        Integer x = new Random().nextInt(sizeSide + 1);
        Integer y = new Random().nextInt(sizeSide + 1);
        coord = new Pair<>(x, y);
    }

    // move player in new coord
    public synchronized void Move(Pair<Integer, Integer> step) {
        // check if player won't go over the grid
        if (step.getKey() + coord.getKey() < 0 || step.getKey() + coord.getKey() > sizeSide || step.getValue() + coord.getKey() < 0 || step.getValue() + coord.getKey() > sizeSide) {
            System.out.println("You can't go out of bounds, retry");
        }
        else {
            coord = new Pair<>(coord.getKey() + step.getKey(), coord.getValue() + step.getValue());
            // TODO
        }
    }

}
