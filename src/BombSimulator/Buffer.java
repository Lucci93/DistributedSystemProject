package BombSimulator;

import java.util.List;

/**
 * Created by civi on 22/04/16.
 */
public interface Buffer<T extends Measurement> {
    void addNewMeasurement(T t);
    List<T> readAllAndClean();
}
