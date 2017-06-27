package Client;

public class BombManager {

    private static BombManager instance;

    //singleton
    public synchronized static BombManager GetInstance(){
        if (instance == null) {
            instance = new BombManager();
        }
        return instance;
    }
}
