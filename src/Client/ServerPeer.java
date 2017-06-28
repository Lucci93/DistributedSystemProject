package Client;

import Game.Game;
import javafx.util.Pair;

public class ServerPeer {

    private static ServerPeer instance;
    private String IPAddress;
    private Integer portAddress;
    private Game gameDetails;
    private Integer score;
    private Pair<Integer, Integer> coord;

    private ServerPeer(String IPAddress, Integer portAddress, Game gameDetails) {
        this.IPAddress = IPAddress;
        this.portAddress = portAddress;
        this.gameDetails = gameDetails;
        this.score = 0;
        this.coord = SetCoordinates();
    }

    //singleton
    public synchronized static ServerPeer GetInstance(String IPAddress, Integer portAddress, Game gameDetails){
        if (instance == null) {
            instance = new ServerPeer(IPAddress, portAddress, gameDetails);
        }
        return instance;
    }

    // set start coordinates
    private synchronized Pair<Integer, Integer> SetCoordinates() {
        return null;
    }
}
