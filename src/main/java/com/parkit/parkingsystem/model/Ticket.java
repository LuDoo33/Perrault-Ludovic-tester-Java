package com.parkit.parkingsystem.model;

import java.util.Date;

public class Ticket {
    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
    private Date inTime;
    private Date outTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getInTime() {
        Date inTime = this.inTime;
        return inTime;
    }

    public void setInTime(Date inTime) {
        if (inTime == null){
            this.inTime = null;
        }else{
            this.inTime = new Date(inTime.getTime());
        }
    }

    public Date getOutTime() {
        Date time = this.outTime;
        return time;
    }

    public void setOutTime(Date outTime) {
        if (outTime == null){
            this.outTime = null;
        }else{
            this.outTime = new Date(outTime.getTime());
        }
    }
}
