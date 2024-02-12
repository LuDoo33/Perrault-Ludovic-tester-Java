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
		double duration = (double) ((outHour - inHour) / 1000) / 60 / 60;
		System.out.println("duration = " + duration + "," + "inHour = " + inHour + "," + "outHour = " + outHour + ", ");

		switch (ticket.getParkingSpot().getParkingType()) {

		case CAR: {
			if (duration <= 0.5) {
				ticket.setPrice(duration * Fare.CAR_FREE_RATE_FOR_MINUTES);
				System.out.println("CAR_FREE =" + ticket.getPrice());
				break;
			}
			ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
			System.out.println("CAR =" + ticket.getPrice());
			break;
		}
		case BIKE: {
			if (duration <=   0.5) {
				ticket.setPrice(duration * Fare.BIKE_FREE_RATE_FOR_MINUTES);
				System.out.println("BIKE_FREE =" + ticket.getPrice());
				break;
			}
			ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
			System.out.println("BIKE =" + ticket.getPrice());
			break;
		}

		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
	}
}