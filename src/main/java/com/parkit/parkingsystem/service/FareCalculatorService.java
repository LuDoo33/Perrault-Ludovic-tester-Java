package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket) {

		if ((ticket.getOutTime() != null) || (ticket.getInTime().after(ticket.getOutTime()))) {

			// 1 - We convert In and Out time in milliseconds
			double inHour = ticket.getInTime().getTime();
			double outHour = ticket.getOutTime().getTime();

			// 2 - We calculate the elapsed time between In and Out Time
			double duration = (outHour - inHour);

			// 3 - We convert milliseconds to hours, and round to 3 decimals places
			duration = (((duration / 3600000) * 1000.0) / 1000.0);

			// 4.1 - We check if duration is over 30 minutes, if it's not parking is free
			// 4.2 - We calculate ticket price depending on Vehicle Type
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
			} else {
				ticket.setPrice(0.0);
			}
		} else {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}
	}
}