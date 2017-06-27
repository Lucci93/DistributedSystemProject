package Game;

import java.util.ArrayList;
import java.util.List;

public class Game {

    // name of the game
    private String name;
    // token identifier
    private String token;
    // size of a side of the map
    private Integer sizeSide;
    // list of player in game
    private List<String> inGamePlayers;
    // score to win the match
    private Integer maxScore;
    // list of player in game IP address
    private List<String> inGamePlayersIP;
    // list of player in game port
    private List<Integer> inGamePlayersPort;

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

    public List<String> getInGamePlayers() {
        return inGamePlayers;
    }

    public void setInGamePlayers(List<String> inGamePlayers) {
        this.inGamePlayers = inGamePlayers;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }

    public List<String> getInGamePlayersIP() {
        return inGamePlayersIP;
    }

    public void setInGamePlayersIP(List<String> inGamePlayersIP) {
        this.inGamePlayersIP = inGamePlayersIP;
    }

    public List<Integer> getInGamePlayersPort() {
        return inGamePlayersPort;
    }

    public void setInGamePlayersPort(List<Integer> inGamePlayersPort) {
        this.inGamePlayersPort = inGamePlayersPort;
    }
}
