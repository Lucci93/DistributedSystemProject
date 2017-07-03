package Utilities;

public class Coordinates {

    private int key;
    private int value;

    public Coordinates(int key, int value) {
        this.key = key;
        this.value = value;
    }

    public Coordinates(){}

    public boolean equals(Coordinates coord) {
        if (coord.key == this.key && coord.value == this.value) {
            return true;
        }
        return false;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
