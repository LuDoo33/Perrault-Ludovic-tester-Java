package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean vehicleExist){

        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        long in = ticket.getInTime().getTime();
        long out = ticket.getOutTime().getTime();
        double test = out - in;
        double duration = ((test / 1000) / 60) / 60 ;




        //TODO: Some tests are failing here. Need to check if this logic is correct
        //free 30 minutes
        if (duration <= 0.5) {
            ticket.setPrice(0);
        }else{
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    double price = (vehicleExist == true)? duration * Fare.CAR_RATE_PER_HOUR * 0.95 : duration*Fare.CAR_RATE_PER_HOUR ;
                    ticket.setPrice(price);
                    break;
                }
                case BIKE: {
                    ticket.setPrice((vehicleExist == true)? duration * Fare.BIKE_RATE_PER_HOUR * 0.95 : duration*Fare.BIKE_RATE_PER_HOUR);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
        }
    }
}