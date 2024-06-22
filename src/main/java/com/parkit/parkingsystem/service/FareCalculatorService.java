package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean discount){
    	
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double timeMs = (double) (ticket.getOutTime().getTime() - ticket.getInTime().getTime()); //Milliseconds

        double duration = timeMs/3600000; //Hours
        

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration <= 0.5 ? 0 : ( discount ? 0.95*(duration * Fare.CAR_RATE_PER_HOUR) : duration * Fare.CAR_RATE_PER_HOUR));
                break;
            }
            case BIKE: {
                ticket.setPrice(duration <= 0.5 ? 0 : ( discount ? 0.95*(duration * Fare.BIKE_RATE_PER_HOUR) : duration * Fare.BIKE_RATE_PER_HOUR));
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
    
    public void calculateFare(Ticket ticket){
    	
    	calculateFare(ticket,false);
    	
    }
}