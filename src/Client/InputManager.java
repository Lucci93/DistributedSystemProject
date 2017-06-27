package Client;

public class InputManager {

    private static InputManager instance;

    //singleton
    public synchronized static InputManager GetInstance(){
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }
}
