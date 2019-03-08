package sample;

public class Filtering {

    private double filterPower;
    private String filterPIDDescription, filterSize, filterPID;

    public Filtering(double filterPower, String filterPIDDescription, String filterSize, String filterPID) {
        this.filterPower = filterPower;
        this.filterPIDDescription = filterPIDDescription;
        this.filterSize = filterSize;
        this.filterPID = filterPID;
    }
}
