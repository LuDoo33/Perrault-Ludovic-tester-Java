package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
	public void calculateFare(Ticket ticket, boolean discount) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		long inHour = ticket.getInTime().getTime();
		long outHour = ticket.getOutTime().getTime();

		long duration = (outHour - inHour) / 60000; 
		if (duration < 30) {
			ticket.setPrice(0);
		} else {

			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR / 60); // yp. discount formula
				break;
			}
			case BIKE: {
				ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR / 60);
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
			if (discount) {
				ticket.setPrice(0.95 * ticket.getPrice());
			}

		}
	}

	public void calculateFare(Ticket ticket) { 
		calculateFare(ticket, false);

	}

}