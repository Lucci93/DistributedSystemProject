package Utilities;

import java.util.ArrayList;

public class Game {

    // name of the game
    private String name;
    // token identifier
    private String token;
    // size of a side of the map
    private Integer sizeSide;
    // list of player in game
    private ArrayList<String> inGamePlayers;
    // score to win the match
    private Integer maxScore;
    // list of player in game IP address
    private ArrayList<String> inGamePlayersIP;
    // list of player in game port
    private ArrayList<Integer> inGamePlayersPort;

    // for the server
    public Game(String name, String token, Integer sizeSide, String playerName, Integer maxScore, String IPAddress, Integer portAddress) {
        this.name = name;
        this.token = token;
        this.sizeSide = sizeSide;
        this.maxScore = maxScore;
        this.inGamePlayers = new ArrayList<>();
        this.inGamePlayersIP = new ArrayList<>();
        this.inGamePlayersPort = new ArrayList<>();
        // insert first player after initialization
        inGamePlayers.add(playerName);
        // insert IP address of the first player first player
        inGamePlayersIP.add(IPAddress);
        // insert port of first player
        inGamePlayersPort.add(portAddress);
    }

    // add a player to game
    public void AddPlayerToGame(String playerName, String IPAddress, Integer portAddress) {
        inGamePlayers.add(playerName);
        inGamePlayersIP.add(IPAddress);
        inGamePlayersPort.add(portAddress);
    }

    // add a player to game
    public void RemovePlayerToGame(String playerName, String IPAddress, Integer portAddress) {
        inGamePlayers.remove(playerName);
        inGamePlayersIP.remove(IPAddress);
        inGamePlayersPort.remove(portAddress);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getSizeSide() {
        return sizeSide;
    }

    public void setSizeSide(Integer sizeSide) {
        this.sizeSide = sizeSide;
    }

    public ArrayList<String> getInGamePlayers() {
        return inGamePlayers;
    }

    public void setInGamePlayers(ArrayList<String> inGamePlayers) {
        this.inGamePlayers = inGamePlayers;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }

    public ArrayList<String> getInGamePlayersIP() {
        return inGamePlayersIP;
    }

    public void setInGamePlayersIP(ArrayList<String> inGamePlayersIP) {
        this.inGamePlayersIP = inGamePlayersIP;
    }

    public ArrayList<Integer> getInGamePlayersPort() {
        return inGamePlayersPort;
    }

    public void setInGamePlayersPort(ArrayList<Integer> inGamePlayersPort) {
        this.inGamePlayersPort = inGamePlayersPort;
    }
}
