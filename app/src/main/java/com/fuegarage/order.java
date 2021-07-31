package com.fuegarage;

public class order {
    String carBlatte , carID ,key;
    Long start , end ;
    int numOfHours;

    public order() {
    }

    public order(String carBlatte, String carID, Long start, Long end, int numOfHours) {
        this.carBlatte = carBlatte;
        this.carID = carID;
        this.start = start;
        this.end = end;
        this.numOfHours = numOfHours;
    }

    public order(String carBlatte, String carID, String key, Long start, Long end, int numOfHours) {
        this.carBlatte = carBlatte;
        this.carID = carID;
        this.key = key;
        this.start = start;
        this.end = end;
        this.numOfHours = numOfHours;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getCarBlatte() {
        return carBlatte;
    }

    public String getCarID() {
        return carID;
    }

    public Long getStart() {
        return start;
    }

    public Long getEnd() {
        return end;
    }

    public int getNumOfHours() {
        return numOfHours;
    }
}
