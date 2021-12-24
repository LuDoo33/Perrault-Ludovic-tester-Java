package com.parkit.parkingsystem.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
		double duration = (double) (outHour - inHour) / 3600000;

		if (duration > 0.5) {
			BigDecimal bd;
			double price;
			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				bd = new BigDecimal(duration * Fare.CAR_RATE_PER_HOUR).setScale(2, RoundingMode.HALF_UP);
				price = bd.doubleValue();
				ticket.setPrice(price);
				break;
			}
			case BIKE: {
				bd = new BigDecimal(duration * Fare.BIKE_RATE_PER_HOUR).setScale(2, RoundingMode.HALF_UP);
				price = bd.doubleValue();
				ticket.setPrice(price);
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		} else ticket.setPrice(0.0);
		
	}
}