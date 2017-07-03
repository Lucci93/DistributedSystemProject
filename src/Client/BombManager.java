package Client;

import BombSimulator.AccelerometerSimulator;
import BombSimulator.Buffer;
import BombSimulator.Measurement;
import Utilities.CurrentMatch;

import java.util.ArrayList;
import java.util.List;

public class BombManager extends Thread {

    private static BombManager instance;

    //singleton
    public synchronized static BombManager GetInstance(){
        if (instance == null) {
            instance = new BombManager();
        }
        return instance;
    }

    private CurrentMatch match;
    private Token tokenThread;
    private Buffer<Measurement> buffer;
    private List<Measurement> listOfMeasurement;
    private Double oldEma;
    private final double alpha = 0.5;
    private final double threshold = 0.5;

    private BombManager() {
        this.match = CurrentMatch.GetInstance();
        this.tokenThread = Token.GetInstance();
        this.listOfMeasurement = new ArrayList<>();
        this.oldEma = 0.0;
        this.buffer = new Buffer<Measurement>() {
            @Override
            public void addNewMeasurement(Measurement measurement) {
                listOfMeasurement.add(measurement);
            }

            @Override
            public List<Measurement> readAllAndClean() {
                List<Measurement> tmp = listOfMeasurement;
                listOfMeasurement.clear();
                return tmp;
            }
        };
    }

    public void run() {
        Thread threadSimulator = new Thread(new AccelerometerSimulator(match.getToken(), buffer));
        threadSimulator.start();
        while (true) {
            SleepBombManager();
            ArrayList<Measurement> measurements = new ArrayList<>(buffer.readAllAndClean());
            CalculateEMA(measurements);
        }
    }

    private synchronized void SleepBombManager() {
        try {
            sleep(1000);
        }
        catch (Exception e) {
            System.out.println("Error while bomb thread was sleeping...");
            // remove player from server
            tokenThread.SendRemovePlayerMessage();
        }
    }

    private synchronized void CalculateEMA(ArrayList<Measurement> measurements) {
        Double ema;
        for (int i = 0; i < measurements.size(); i++) {
            ema = oldEma + alpha*(measurements.get(i).getValue() - oldEma);
            if (ema - oldEma > threshold) {
                match.setFifoBombList(ema % 4);
            }
            oldEma = ema;
        }
    }
}
