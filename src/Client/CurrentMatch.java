package Client;

import java.util.ArrayList;

public class CurrentMatch {

    private static CurrentMatch instance;

    // name of the game
    private String name;
    // token identifier
    private String token;
    // player name
    String playerName;
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

    private CurrentMatch(String playerName, String name, String token, Integer sizeSide, ArrayList<String> inGamePlayers, Integer maxScore, ArrayList<String> inGamePlayersIP, ArrayList<Integer> inGamePlayersPort) {
        this.name = name;
        this.token = token;
        this.playerName = playerName;
        this.sizeSide = sizeSide;
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

    public String GetPlayerIP() {
        for (int i = 0; i < getInGamePlayers().size(); i++) {
            if (playerName.equals(getInGamePlayers().get(i))) {
                return getInGamePlayersIP().get(i);
            }
        }
        return null;
    }

    public String GetPlayerPort() {
        for (int i = 0; i < getInGamePlayers().size(); i++) {
            if (playerName.equals(getInGamePlayers().get(i))) {
                return getInGamePlayersPort().get(i).toString();
            }
        }
        return null;
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

    public synchronized void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public synchronized Integer getSizeSide() {
        return sizeSide;
    }

    public synchronized void setSizeSide(Integer sizeSide) {
        this.sizeSide = sizeSide;
    }

    public synchronized ArrayList<String> getInGamePlayers() {
        return inGamePlayers;
    }

    public synchronized void setInGamePlayers(ArrayList<String> inGamePlayers) {
        this.inGamePlayers = inGamePlayers;
    }

    public synchronized Integer getMaxScore() {
        return maxScore;
    }

    public synchronized void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }

    public synchronized ArrayList<String> getInGamePlayersIP() {
        return inGamePlayersIP;
    }

    public synchronized void setInGamePlayersIP(ArrayList<String> inGamePlayersIP) {
        this.inGamePlayersIP = inGamePlayersIP;
    }

    public synchronized ArrayList<Integer> getInGamePlayersPort() {
        return inGamePlayersPort;
    }

    public synchronized void setInGamePlayersPort(ArrayList<Integer> inGamePlayersPort) {
        this.inGamePlayersPort = inGamePlayersPort;
    }
}
