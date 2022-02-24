package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inMilliseconds = ticket.getInTime().getTime();
        System.out.println(inMilliseconds);
        long outMilliseconds= ticket.getOutTime().getTime();
        System.out.println(outMilliseconds);

        //TODO: Some tests are failing here. Need to check if this logic is correct
        long duration =  (outMilliseconds - inMilliseconds) ;
        System.out.println("fare:" + duration);

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice((duration * Fare.CAR_RATE_PER_HOUR)/ 3600000);
                break;
            }
            case BIKE: {
                ticket.setPrice((duration * Fare.BIKE_RATE_PER_HOUR)/ 3600000);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}
