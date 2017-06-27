package Game;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Scanner;

// used to show text in terminal, to take decision
public class TUI {

    // run terminal
    public static void main(String[] args) {

        TUIManager manager = new TUIManager();
        Scanner scanner = new Scanner(System.in);
        boolean exitChoice = false;
        Integer portAddress = null;
        String IPAddress = null;
        // token for the token ring implementation
        String token = null;

        // get a server socket connection
        try {
            ServerSocket socket = new ServerSocket(0);
            portAddress = socket.getLocalPort();
            // get IPAddress
            IPAddress = InetAddress.getLocalHost().getHostAddress();
        }
        // if fail exit from application
        catch (Exception e) {
            System.out.println("Socket error...");
            System.exit(0);
        }

        System.out.println("Running game... \n");
        // start connection with server and display the menu interface
        manager.GameMenu();
        // show the list of matches
        manager.ListOfCurrentMatches();
        // wait for input
        while(!exitChoice) {
            String command = scanner.nextLine();
            switch (command.toUpperCase()) {
                // join in a match
                case "J":
                    System.out.println("Enter your name:");
                    String player = "";
                    while (player.length() < 1) {
                        player = scanner.nextLine();
                    }
                    System.out.println("Enter match name:");
                    String game = "";
                    while (game.length() < 1) {
                        game = scanner.nextLine();
                    }
                    String response = manager.AddPlayer(player, game, IPAddress, portAddress.toString());
                    if (response == "ok") {
                        ClearTUI();
                        exitChoice = true;
                        // TODO: game TUI
                    }
                    else {
                        ClearTUI();
                        // return to game menu
                        manager.GameMenu();
                        manager.ListOfCurrentMatches();
                    }
                    break;
                // new match
                case "N":
                    System.out.println("Enter your name:");
                    String namePlayer = "";
                    while (namePlayer.length() < 1) {
                        namePlayer = scanner.nextLine();
                    }
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
                    token = manager.CreateGame(namePlayer, nameGame, side, maxScore, IPAddress, portAddress.toString());
                    if (token != null) {
                        ClearTUI();
                        exitChoice = true;
                        // TODO: game TUI
                    }
                    else {
                        ClearTUI();
                        // return to game menu
                        manager.GameMenu();
                        manager.ListOfCurrentMatches();
                    }
                    break;
                // details of match
                case "D":
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
    }

    // clear the TUI
    public static void ClearTUI() {
        System.out.println("\b\b\b\b\b\b\b\b\b");
    }
}
