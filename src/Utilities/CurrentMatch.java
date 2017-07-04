package Utilities;

import java.util.ArrayList;
import java.util.LinkedList;
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
    private Coordinates coord;
    // list of player in game
    private ArrayList<String> inGamePlayers;
    // score to win the match
    private Integer maxScore;
    // list of player in game IP address
    private ArrayList<String> inGamePlayersIP;
    // list of player in game port
    private ArrayList<Integer> inGamePlayersPort;
    // check if player have won
    private boolean win;
    // FIFO list for bomb
    private LinkedList<Integer> fifoBombList;
    // array of one element of terminal commands
    private String[] command;

    public CurrentMatch(String name, String token, String playerName, Integer sizeSide, ArrayList<String> inGamePlayers, Integer maxScore, ArrayList<String> inGamePlayersIP, ArrayList<Integer> inGamePlayersPort) {
        this.name = name;
        this.token = token;
        this.playerName = playerName;
        this.sizeSide = sizeSide;
        this.score = 0;
        this.coord = new Coordinates();
        this.inGamePlayers = inGamePlayers;
        this.maxScore = maxScore;
        this.inGamePlayersIP = inGamePlayersIP;
        this.inGamePlayersPort = inGamePlayersPort;
        this.win = false;
        this.fifoBombList = new LinkedList<>();
        // just one element
        this.command = new String[1];
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

    public synchronized void setCommand() {
        this.command = new String[1];
    }

    // add score to the total score
    public synchronized void setScore(Integer score) {
        this.score += score;
    }

    public synchronized Coordinates getCoord() {
        return coord;
    }

    public LinkedList<Integer> getFifoBombList() {
        return fifoBombList;
    }

    public String[] getCommand() {
        return command;
    }

    // set start coordinates
    public synchronized void setCoord() {
        Integer x = new Random().nextInt(sizeSide + 1);
        Integer y = new Random().nextInt(sizeSide + 1);
        coord = new Coordinates(x, y);
    }

    // move player in new coord
    public synchronized String Move(Coordinates step) {
        // check if player won't go over the grid
        if (step.getKey() + coord.getKey() < 0 || step.getKey() + coord.getKey() > sizeSide) {
            System.out.println("You can't go out of x bounds, retry");
            return null;
        }
        else if (step.getValue() + coord.getValue() < 0 || step.getValue() + coord.getValue() > sizeSide) {
            System.out.println("You can't go out of y bounds, retry");
            return null;
        }
        else {
            coord = new Coordinates(coord.getKey() + step.getKey(), coord.getValue() + step.getValue());
            return "ok";
        }
    }

    // check if player have won
    public synchronized boolean HaveWon() {
        if (maxScore <= score) {
            return true;
        }
        return false;
    }

    // set fifo list
    public synchronized void setFifoBombList(Double value) {
        // if list have a lot of bomb remove the last
        if (fifoBombList.size() == 10) {
            fifoBombList.removeLast();
        }
       fifoBombList.addFirst(value.intValue());
    }

    // check if player is in the area of the bomb
    public synchronized boolean CheckArea(Integer bombArea) {
        Integer area;
        // green
        if (coord.getKey() < sizeSide/2 && coord.getValue() > sizeSide) {
            area = 0;
        }
        // red
        else if (coord.getKey() > sizeSide/2 && coord.getValue() > sizeSide/2) {
            area = 1;
        }
        // blue
        else if (coord.getKey() < sizeSide/2 && coord.getValue() < sizeSide/2) {
            area = 2;
        }
        // yellow
        else {
            area = 3;
        }
        return area.equals(bombArea);
    }

    // check if player is in the area of the bomb
    public synchronized String Area(Integer bombArea) {
        if (!CheckArea(bombArea)) {
            switch (bombArea) {
                case 0:
                    return "\nBomb threw in green area!";
                case 1:
                    return "\nBomb threw in red area!";
                case 2:
                    return "\nBomb threw in blue area!";
                case 3:
                    return "\nBomb threw in yellow area!";
                default:
                    return null;
            }
        }
        // is in your area
        else {
            return "\nBomb threw in your area!";
        }
    }
}
