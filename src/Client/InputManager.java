package Client;


import Game.TUI;
import Utilities.Coordinates;
import Utilities.CurrentMatch;

import java.util.Scanner;

public class InputManager extends Thread {

    private static InputManager instance;
    private CurrentMatch match;
    private Token tokenThread;
    Scanner scanner;

    //singleton
    public synchronized static InputManager GetInstance(){
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }

    private InputManager() {
        this.match = CurrentMatch.GetInstance();
        this.tokenThread = Token.GetInstance();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        // start timeout game
        if (match.getInGamePlayers().size() == 1) {
            Timeout timeout = new Timeout();
            timeout.start();
        }
        while (true) {
            // check players in game to start the text interface and wait for right coordinates
            if (match.getInGamePlayers().size() > 1) {
                TUI.ClearTUI();
                System.out.println("You are in coordinates (" + match.getCoord().getKey() + "," + match.getCoord().getValue() + ");");
                System.out.println("You have " + match.getFifoBombList().size() + " bomb left ready to throw;");
                System.out.println("- Move with 'U', 'D', 'L', 'R' to move 'UP', 'DOWN', 'LEFT', 'RIGHT';");
                System.out.println("- Thrown a bomb with B;");
                System.out.println("- Press Q to exit from the game.");
                String command = scanner.nextLine();
                // enter just if command buffer is empty
                if (match.getCommand().size() == 0) {
                    String ack;
                    switch (command.toUpperCase()) {
                        // move up
                        case "U":
                            ack = match.Move(new Coordinates(1, 0));
                            if (ack != null) {
                                match.getCommand().add(new Coordinates(1, 0));
                            }
                            break;
                        // move down
                        case "D":
                            ack = match.Move(new Coordinates(-1, 0));
                            if (ack != null) {
                                match.getCommand().add(new Coordinates(-1, 0));
                            }
                            break;
                        // move left
                        case "L":
                            ack = match.Move(new Coordinates(0, -1));
                            if (ack != null) {
                                match.getCommand().add(new Coordinates(0, -1));
                            }
                            break;
                        // move right
                        case "R":
                            ack = match.Move(new Coordinates(0, 1));
                            if (ack != null) {
                                match.getCommand().add(new Coordinates(0, 1));
                            }
                            break;
                        // exit from java application
                        case "Q":
                            tokenThread.SendRemovePlayerMessage();
                            System.exit(0);
                            break;
                        // thrown bomb
                        case "B":
                            Integer bomb = match.getFifoBombList().pop();
                            // TODO
                            break;
                        // retry
                        default:
                            System.out.println("Unknown command, retry!");
                            break;
                    }
                } else {
                    System.out.println("Wait, you can do just one step before the other");
                }
            }
        }
    }
}
