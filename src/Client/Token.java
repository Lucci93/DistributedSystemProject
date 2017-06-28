package Client;

public class Token {

    private static Token instance;
    private String id;

    private Token(String id) {
        this.id = id;
    }

    //singleton
    public synchronized static Token GetInstance(String id){
        if (instance == null) {
            instance = new Token(id);
        }
        return instance;
    }


}
