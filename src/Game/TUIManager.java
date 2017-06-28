package Game;

import com.google.gson.Gson;

import java.util.List;

// method to manage TUI
public class TUIManager {

    private TUIMethod tuiMethod;
    private Gson json;

    public TUIManager(String serverIPAddress, String serverPort) {
        this.tuiMethod = new TUIMethod(serverIPAddress, serverPort);
        this.json = new Gson();
    }

    // display the start menu interface if connection with server return a success
    public void GameMenu() {
        // try to connect with server
        String response = tuiMethod.StartConnection();

        if (response == "ok") {
            // start interface
            System.out.println("MAIN MENU");
            System.out.println("-------------------\n");
            System.out.println("Press J and enter the name of the match to join it;");
            System.out.println("Press N to create a new game;");
            System.out.println("Press D and enter the name of the match to display its particular;");
            System.out.println("Press Q to exit from game.\n");
        }
        else {
            // exit from java application
            System.out.println(response);
            System.exit(0);
        }
    }

    // print list of current matches if there are on terminal
    public void ListOfCurrentMatches() {
        String response = tuiMethod.GetListOfCurrentMatches();
        if (response == "error") {
            // exit from java application
            System.out.println("Connection with server failed while it was recovering the list of current matches...");
            System.exit(0);
        }
        else if (response == "empty") {
            System.out.println("THERE ARE NO MATCHES, press N to create a new one!\n");
        }
        else {
            // print the list of matches
            System.out.println("LIST OF CURRENT MATCHES");
            System.out.println("-------------------\n");
            List<String> listOfMatches = json.fromJson(response, List.class);
            response = "";
            // print on terminal the list of matches
            for (String name: listOfMatches) {
                response += "- " + name + "\n";
            }
            System.out.println(response);
        }
    }

    // print the detail of a particular match
    public void DetailsOfMatch(String matchName) {
        String response = tuiMethod.GetDetailsOfMatch(matchName);
        if (response == "error") {
            // exit from java application
            System.out.println("Connection with server failed while it was recovering the list of current matches...");
            System.exit(0);
        }
        else if (response == "empty") {
            System.out.println("Wrong or inexistent name, retry!");
        }
        else {
            // print the list of matches
            System.out.println("DETAILS");
            System.out.println("-------------------\n");
            Game game = json.fromJson(response, Game.class);
            // format game details
            response = "Game name: " + game.getName() + ",\nMap side size: " + game.getSizeSide() + ",\nIn-game players: \n";
            for (int i = 0; i < game.getInGamePlayers().size(); i ++) {
                response += "  - " + game.getInGamePlayers().get(i) + "\n";
            }
            response += "Max score: " + game.getMaxScore() + ".";
            System.out.println(response);
        }
    }

    // create a game
    public Game CreateGame(String playerName, String matchName, String sideSize, String maxScore, String IPAddress, String portAddress) {
        String response = tuiMethod.CreateANewGame(playerName, matchName, sideSize, maxScore, IPAddress, portAddress);
        if (response == "error") {
            System.out.println("Connection with server failed while it was creating the match...");
            System.exit(0);
        }
        else if (response == "fail") {
            System.out.println("Wrong parameters sent, retry!");
        }
        else {
            // print the list of matches
            System.out.println("START MATCH");
            System.out.println("-------------------\n");
            return json.fromJson(response, Game.class);
        }
        return null;
    }

    // add player in a created game
    public Game AddPlayer(String playerName, String matchName, String IPAddress, String portAddress) {
        String response = tuiMethod.AddPlayerInGame(playerName, matchName, IPAddress, portAddress);
        if (response == "error") {
            System.out.println("Connection with server failed while it was adding player in match...");
            System.exit(0);
        }
        else if (response == "fail") {
            System.out.println("Wrong parameters sent, player name already in use or max number of players reached, retry!");
        }
        else {
            // print the list of matches
            System.out.println("START MATCH");
            System.out.println("-------------------\n");
            return json.fromJson(response, Game.class);
        }
        return null;
    }

    // remove a player from a created game
    public void RemovePlayer(String playerName, String matchName, String IPAddress, String portAddress) {
        String response = tuiMethod.RemovePlayerFromMatch(playerName, matchName, IPAddress, portAddress);
        if (response == "error") {
            System.out.println("Connection with server failed while it was removing player from match...");
            System.exit(0);
        }
        else if (response == "empty") {
            System.out.println("Wrong parameters sent, retry!");
        }
        else {
            // exit from java application
            System.exit(0);
        }
    }
}
