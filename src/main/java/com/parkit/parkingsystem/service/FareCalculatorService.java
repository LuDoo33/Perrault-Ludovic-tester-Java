package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        double duration = (outHour - inHour)/(1000.0*60*60); // Converting the duration time in hour

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
            	if (duration <= 0.5) { // Free fare if user out before 30 min
            		ticket.setPrice(0);
            	}
            	else if (ticket.isRecurentUser() == 1) { // 5% discount if user is recurent
            		ticket.setPrice(duration*0.95*Fare.CAR_RATE_PER_HOUR);            	
            	}
            	else {
            		ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR); // Normal fare
            	}
                break;
            }
            case BIKE: {
            	if (duration <= 0.5) { // Free fare if user out before 30 min
            		ticket.setPrice(0);
            	}
            	else if (ticket.isRecurentUser() == 1) { // 5% discount if user is recurent
            		ticket.setPrice(duration*0.95*Fare.BIKE_RATE_PER_HOUR);            	
            	}
            	else {
            		ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR); // Normal fare
            	}
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}