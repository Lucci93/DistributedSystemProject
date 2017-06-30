package Utilities;

public class Player {

    private String name;
    private String ip;
    private Integer port;
    private static Player instance;

    //singleton
    public synchronized static Player GetInstance(String name, String ip, Integer port) {
        if (instance == null) {
            instance = new Player(name, ip, port);
        }
        return instance;
    }

    //general singleton
    public synchronized static Player GetInstance() {
        if (instance == null) {
            instance = new Player();
        }
        return instance;
    }

    // general constructor
    public Player() {}

    // first instance constructor
    public Player(String name, String ip, Integer port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized String getIp() {
        return ip;
    }

    public synchronized Integer getPort() {
        return port;
    }
}
