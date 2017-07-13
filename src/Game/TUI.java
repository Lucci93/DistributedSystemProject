package Game;

import Client.*;
import Utilities.Coordinates;
import Utilities.CurrentMatch;
import Utilities.Game;
import Utilities.Player;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Scanner;

// used to show text in terminal, to take decision
public class TUI {

    // run terminal
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        boolean exitChoice = false;
        Integer portAddress = null;
        String IPAddress = null;
        Game gameDetails = null;
        String playerName = "";
        String serverIPAddress = "";
        String serverPort = "";
        /* TODO
        // set server address
        System.out.println("Enter the IP of the server:");
        while (serverIPAddress.length() < 1) {
            serverIPAddress = scanner.nextLine();
        }
        // set server port
        System.out.println("Enter the port address:");
        while (serverPort.length() < 1) {
            serverPort = scanner.nextLine();
        }
        */
        // set player name
        System.out.println("Enter your in-game name:");
        while (playerName.length() < 1) {
            playerName = scanner.nextLine();
        }

        serverIPAddress = "localhost";
        serverPort = "8080";
        // create the TUI manager
        TUIManager manager = TUIManager.GetInstance(serverIPAddress, serverPort);

        // get a new socket
        try {
            // get random port
            ServerSocket socket = new ServerSocket(0);
            socket.setReuseAddress(true);
            portAddress = socket.getLocalPort();
            // get IPAddress
            IPAddress = InetAddress.getLocalHost().getHostAddress();
            socket.close();
        }
        // if fail exit from application
        catch (Exception e) {
            System.out.println("Socket error...");
            System.exit(0);
        }

        // MAIN MENU

        System.out.println("Running game... \n");
        // start connection with server and display the menu interface
        manager.GameMenu();
        // show the list of matches
        manager.ListOfCurrentMatches();
        // wait for input
        while (!exitChoice) {
            String command = scanner.nextLine();
            switch (command.toUpperCase()) {
                // join in a match
                case "J":
                    System.out.println("Enter match name:");
                    String game = "";
                    while (game.length() < 1) {
                        game = scanner.nextLine();
                    }
                    gameDetails = manager.AddPlayer(playerName, game, IPAddress, portAddress);
                    if (gameDetails != null) {
                        // start the game
                        exitChoice = true;
                    } else {
                        ClearTUI();
                        // return to game menu
                        manager.GameMenu();
                        manager.ListOfCurrentMatches();
                    }
                    break;
                // new match
                case "N":
                    System.out.println("Enter match name:");
                    String nameGame = "";
                    while (nameGame.length() < 1) {
                        nameGame = scanner.nextLine();
                    }
                    System.out.println("Enter side size of the map:");
                    String side = "";
                    while (side.length() < 1) {
                        side = scanner.nextLine();
                    }
                    System.out.println("Enter the max score of the game to win the match:");
                    String maxScore = "";
                    while (maxScore.length() < 1) {
                        maxScore = scanner.nextLine();
                    }
                    gameDetails = manager.CreateGame(playerName, nameGame, side, maxScore, IPAddress, portAddress);
                    // start the game
                    if (gameDetails != null) {
                        exitChoice = true;
                    } else {
                        ClearTUI();
                        // return to game menu
                        manager.GameMenu();
                        manager.ListOfCurrentMatches();
                    }
                    break;
                // details of match
                case "D":
                    System.out.println("Enter match name:");
                    String name = "";
                    while (name.length() < 1) {
                        name = scanner.nextLine();
                    }
                    // print the match with this name
                    manager.DetailsOfMatch(name);
                    // wait to exit
                    String action = "";
                    while (!action.equals("Q")) {
                        System.out.println("\nPress Q to turn back to main menu...");
                        action = scanner.nextLine();
                        action = action.toUpperCase();
                    }
                    ClearTUI();
                    // return to game menu
                    manager.GameMenu();
                    manager.ListOfCurrentMatches();
                    break;
                // exit from java application
                case "Q":
                    System.exit(0);
                    break;
                // retry
                default:
                    System.out.println("Unknown command, retry!");
                    break;
            }
        }

        // START GAME
        Player.GetInstance(playerName, IPAddress, portAddress);
        CurrentMatch.GetInstance(gameDetails.getName(), gameDetails.getToken(), playerName, gameDetails.getSizeSide(), gameDetails.getInGamePlayers(), gameDetails.getMaxScore(), gameDetails.getInGamePlayersIP(), gameDetails.getInGamePlayersPort());
        Token tokenThread = Token.GetInstance();
        BombManager bombManagerThread = BombManager.GetInstance();
        ServerPeer serverPeer = ServerPeer.GetInstance();
        // start token thread
        tokenThread.start();
        // start bomb thread
        bombManagerThread.start();
        // start peer server
        serverPeer.start();
        // start input
        while (true);
    }

    // clear the TUI
    public static void ClearTUI() {
        System.out.println("\b\b\b\b\b\b\b\b\b");
    }
}
