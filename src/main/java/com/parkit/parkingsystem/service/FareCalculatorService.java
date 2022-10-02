package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
	/**
	* 
	* @param ticket: the fare of the ticket according to the out time and the vehicle
	*/
	public void calculateFare(Ticket ticket) { 
        // Display an error if the out time is null or after in time
        if((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        } 
        
        Long inHour = ticket.getInTime().getTime() / 60000;
        Long outHour = ticket.getOutTime().getTime() / 60000;

        double duration = (outHour - inHour);
        duration = duration / 60;
        
        // Verify the duration is greater than 30 minutes
        if (duration > 0.5) {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                    break;
                }
                case BIKE: {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
        }
        else {
        	// Parking free if the time is less than 30 minutes
            ticket.setPrice(0.0);
        }
	}

}