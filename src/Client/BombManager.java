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
    private final double threshold = 50;

    private BombManager() {
        this.match = CurrentMatch.GetInstance();
        this.tokenThread = Token.GetInstance();
        this.listOfMeasurement = new ArrayList<>();
        this.oldEma = 0.0;
        this.buffer = new Buffer<Measurement>() {
            @Override
            public synchronized void addNewMeasurement(Measurement measurement) {
                listOfMeasurement.add(measurement);
            }

            @Override
            public synchronized List<Measurement> readAllAndClean() {
                List<Measurement> tmp = new ArrayList<>(listOfMeasurement);
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
            if (!measurements.isEmpty()) {
                CalculateEMA(measurements);
            }
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
        if (oldEma != 0.0) {
            ema = oldEma + alpha * (CalculateAverage(measurements) - oldEma);
        }
        // base case
        else {
            ema = CalculateAverage(measurements);
        }
        if (ema - oldEma > threshold) {
            // calculate area
            boolean isFull = match.setFifoBombList(ema % 4);
            if (!isFull) {
                System.out.println("You have " + match.getFifoBombList().size() + " bomb left ready to throw;");
            }
        }
        oldEma = ema;
    }

        // calculate the average of the measurement each second
    private Double CalculateAverage(List <Measurement> measurements) {
        Double sum = 0.0;
        if(!measurements.isEmpty()) {
            for (Measurement measure : measurements) {
                sum += measure.getValue();
            }
            return sum / measurements.size();
        }
        return sum;
    }
}
