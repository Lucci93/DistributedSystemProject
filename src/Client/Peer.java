package Client;


public class Peer {

    private static Peer instance;

    //singleton
    public synchronized static Peer GetInstance(){
        if (instance == null) {
            instance = new Peer();
        }
        return instance;
    }
}
