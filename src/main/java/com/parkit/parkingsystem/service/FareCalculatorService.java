package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
	if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
	    throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
	}

	long inHour = ticket.getInTime().getTime();
	long outHour = ticket.getOutTime().getTime();
	// TODO: Some tests are failing here. Need to check if this logic is correct
	long duration = outHour - inHour;

	switch (ticket.getParkingSpot().getParkingType()) {
	case CAR: {
	    ticket.setPrice((duration / 3600000) * Fare.CAR_RATE_PER_HOUR);
	    if (ticket.getPrice() == 0) {
		ticket.setPrice((duration / 60000) * Fare.CAR_RATE_PER_MINUTE);
	    }
	    break;
	}
	case BIKE: {
	    ticket.setPrice((duration / 3600000) * Fare.BIKE_RATE_PER_HOUR);
	    if (ticket.getPrice() == 0) {
		double price = (duration / 60000) * Fare.BIKE_RATE_PER_MINUTE;
		double roundPrice = Math.round(price * 100.0) / 100.00;
		ticket.setPrice(roundPrice);
	    }
	    break;
	}
	default:
	    throw new IllegalArgumentException("Unkown Parking Type");
	}
    }
}