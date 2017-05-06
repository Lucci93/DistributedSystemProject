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
    public synchronized static RESTMethod getInstance(){
        if(instance==null)
            instance = new RESTMethod();
        return instance;
    }

    // return all the names of the games that are playing in this moment
    public synchronized String[] getGamesNames() {
        // if there are games
        if (gamesList != null) {
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
    public synchronized Game getGameDetails(String nameGame) {
        // if there are games
        if (gamesList != null) {
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
    public synchronized String CreateNewGame(String name, Integer sizeSide, Integer maxScore, String IPAddress, Integer portAddress, String playerName) {
        // if game exist with same name
        for (int i = 0; i < gamesList.size(); i++) {
            if (name.equals(gamesList.get(i).getName())) {
                return null;
            }
        }
        // else
        Game newGame = new Game(name, sizeSide, playerName, maxScore, IPAddress, portAddress);
        // add game to list of games
        gamesList.add(newGame);
        // generate random token for game
        return new BigInteger(130, random).toString(32);
    }

    // add player to playing match
    public synchronized boolean AddPlayerInGame(String playerName, String gameName, String IPAddress, Integer portAddress) {
        // get the name of game for each games
        for (int i = 0; i < gamesList.size(); i++) {
            // if game name exist
            if (gameName.equals(gamesList.get(i).getName())) {
                // if user have the same name
                if (gamesList.get(i).getInGamePlayers().contains(playerName)) {
                    return false;
                }
                else {
                    // add player to game
                    gamesList.get(i).AddPlayerToGame(playerName, IPAddress, portAddress);
                    return true;
                }
            }
        }
        return false;
    }

    // remove a player from a game
    public synchronized void RemovePlayerInGame(String playerName, String gameName, String IPAddress, Integer portAddress) {
        // get the name of game for each games
        for (int i = 0; i < gamesList.size(); i++) {
            // if game name exist
            if (gameName.equals(gamesList.get(i).getName())) {
                // if user have the same name
                if (gamesList.get(i).getInGamePlayers().contains(playerName)) {
                    if (gamesList.get(i).getInGamePlayers().size() == 1) {
                        // if there is just one player delete the match
                        DeleateGame(gameName);
                        break;
                    }
                    else {
                        // remove player to game
                        gamesList.get(i).RemovePlayerToGame(playerName, IPAddress, portAddress);
                        break;
                    }
                }
            }
        }
    }

    public synchronized void DeleateGame(String gameName) {
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
