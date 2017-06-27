package Client;

public class Token {

    private static Token instance;

    //singleton
    public synchronized static Token GetInstance(){
        if (instance == null) {
            instance = new Token();
        }
        return instance;
    }


}
