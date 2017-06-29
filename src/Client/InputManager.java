package Client;

import java.util.LinkedList;

public class InputManager extends Thread {

    private static InputManager instance;
    // FIFO list for bomb
    private LinkedList<Integer> fifoBombList;
    // arrey of one element of terminal commands
    private String[] command;

    private InputManager() {
        this.fifoBombList = new LinkedList<>();
        this.command = new String[1];
    }


    //singleton
    public synchronized static InputManager GetInstance(){
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }

    public void run() {

    }
}
