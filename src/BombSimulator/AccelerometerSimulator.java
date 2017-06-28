package BombSimulator;
/**
 * Created by civi on 22/04/16.
 */
public class AccelerometerSimulator extends Simulator {

    private  double A1;
    private  double W1 = 6;
    private  double PHI1 = rnd.nextDouble();
    private  double A2;
    private  double W2 = 5;
    private  double PHI2 = rnd.nextDouble();
    private  double MOTIONPROB = 0.15;
    private  long WAITINGTIME = 100;

    public AccelerometerSimulator(String id, Buffer<Measurement> measurementsQueue){
        super(id, "accelerometer", measurementsQueue);
        A1 = rnd.nextInt(41)+30;
        A2 = rnd.nextInt(21)+20;

    }

    //use this constructor to initialize the accelerometer's simulator in your project
    public AccelerometerSimulator(Buffer<Measurement> measurementsQueue){
        this("acc", measurementsQueue);
    }

    @Override
    public void run() {

        boolean enoughTimePassed = false;

        long startingTime = System.currentTimeMillis();

        double i = 0.1, j=0.1;
        boolean inMotion;
        double randomAcceleration = 0.;

        while(!stopCondition){

            if(!enoughTimePassed){

                enoughTimePassed = System.currentTimeMillis() - startingTime > 7000;

            }

            inMotion = Math.random()<MOTIONPROB & enoughTimePassed;

            if(!inMotion)
                randomAcceleration = (int)(Math.random()*500);
            else{
                startingTime = System.currentTimeMillis();
                enoughTimePassed = false;
            }

            double currentAcceleration = getAcceleration(i, j, randomAcceleration, inMotion);
            addMeasurementToQueue(currentAcceleration);

            sleep(WAITINGTIME);

            i+=0.5;
            j+=0.8;

        }
    }

    private double getAcceleration(double t1, double t2, double randomAcceleration, boolean inMotion){

        double motionAcceleration = 0.;

        if(inMotion)
            motionAcceleration = A2*Math.sin(W2*t2+PHI2)+randomAcceleration+rnd.nextGaussian()*3;

        return A1 * Math.sin(W1*t1+PHI1)+50+rnd.nextGaussian()*0.2 +motionAcceleration;
    }
}
