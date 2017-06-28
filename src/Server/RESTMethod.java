package Server;

import Game.Game;

import java.util.ArrayList;
import java.util.List;
import java.security.SecureRandom;
import java.math.BigInteger;

public class RESTMethod {

    private List<Game> gamesList;
    private static RESTMethod instance;
    // secure random number for token
    private SecureRandom random = new SecureRandom();

    public RESTMethod() {

        gamesList = new ArrayList<Game>();
    }

    //singleton
    public synchronized static RESTMethod GetInstance(){
        if (instance == null) {
            instance = new RESTMethod();
        }
        return instance;
    }

    // return all the names of the games that are playing in this moment
    public synchronized String[] GetGamesNames() {
        // if there are games
        if (!gamesList.isEmpty()) {
            String[] InGamesArray = new String[gamesList.size()];
            // get the name of game for each games
            for (int i = 0; i < gamesList.size(); i++) {
                InGamesArray[i] = gamesList.get(i).getName();
            }
            return InGamesArray;
        }
        else {
            return null;
        }
    }

    // get details on a specific match
    public synchronized Game GetGameDetails(String nameGame) {
        // if there are games
        if (!gamesList.isEmpty()) {
            // get the name of game for each games
            for (int i = 0; i < gamesList.size(); i++) {
                // if game name exist return it
                if (nameGame.equals(gamesList.get(i).getName())) {
                    return gamesList.get(i);
                }
            }
            return null;
        }
        else {
            return null;
        }
    }

    // create a new game
    public synchronized Game CreateNewGame(String name, Integer sizeSide, Integer maxScore, String IPAddress, Integer portAddress, String playerName) {
        // if game exist with same name
        for (int i = 0; i < gamesList.size(); i++) {
            if (name.equals(gamesList.get(i).getName())) {
                return null;
            }
        }
        // else
        String token = new BigInteger(130, random).toString(32);
        Game newGame = new Game(name, token, sizeSide, playerName, maxScore, IPAddress, portAddress);
        // add game to list of games
        gamesList.add(newGame);
        // generate random token for game
        return newGame;
    }

    // add player to playing match
    public synchronized Game AddPlayerInGame(String playerName, String gameName, String IPAddress, Integer portAddress) {
        // get the name of game for each games
        for (int i = 0; i < gamesList.size(); i++) {
            // if game name exist
            if (gameName.equals(gamesList.get(i).getName())) {
                // if user have the same name
                if (gamesList.get(i).getInGamePlayers().contains(playerName)) {
                    return null;
                }
                else {
                    // if there is a cell free to place player
                    if (gamesList.get(i).getInGamePlayers().size() < (gamesList.get(i).getSizeSide() * gamesList.get(i).getSizeSide())) {
                        // add player to game
                        gamesList.get(i).AddPlayerToGame(playerName, IPAddress, portAddress);
                        return gamesList.get(i);
                    }
                }
            }
        }
        return null;
    }

    // remove a player from a game
    public synchronized boolean RemovePlayerInGame(String playerName, String gameName, String IPAddress, Integer portAddress) {
        // get the name of game for each games
        for (int i = 0; i < gamesList.size(); i++) {
            // if game name exist
            if (gameName.equals(gamesList.get(i).getName())) {
                // if user have the same name
                if (gamesList.get(i).getInGamePlayers().contains(playerName)) {
                    if (gamesList.get(i).getInGamePlayers().size() == 1) {
                        // if there is just one player delete the match
                        DeleteGame(gameName);
                        return true;
                    }
                    else {
                        // remove player to game
                        gamesList.get(i).RemovePlayerToGame(playerName, IPAddress, portAddress);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public synchronized void DeleteGame(String gameName) {
        // get the name of game for each games
        for (int i = 0; i < gamesList.size(); i++) {
            // if game name exist
            if (gameName.equals(gamesList.get(i).getName())) {
                gamesList.remove(gamesList.get(i));
                break;
            }
        }
    }
}
