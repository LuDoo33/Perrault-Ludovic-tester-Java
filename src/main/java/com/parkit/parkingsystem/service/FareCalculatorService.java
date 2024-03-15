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


		// TODO: Some tests are failing here. Need to check if this logic is correct
		double duration = (double) ((outHour - inHour) / 1000) / 60 / 60;
		//System.out.println("duration = " + duration + "," + "inHour = " + inHour + "," + "outHour = " + outHour + ", ");
		//boolean discount = ticket.isDiscount();
		//System.out.println("discount = " + discount);

		switch (ticket.getParkingSpot().getParkingType()) {
 
		case CAR: {
			if (duration <= 0.5) {
				ticket.setPrice(duration * Fare.CAR_FREE_RATE_FOR_MINUTES);
				System.out.println("CAR_FREE =" + ticket.getPrice());
				System.out.println("duration_car_free = " + duration);
				System.out.println("discount = " + discount);
				break;
			}
			if (duration > 0.5 && discount) {
				ticket.setPrice(duration  * (Fare.CAR_RATE_PER_HOUR - (Fare.CAR_RATE_PER_HOUR * 5 / 100)));
				System.out.println("duration_discount = " + duration + " , CAR_DISCOUNT =" + ticket.getPrice());
				System.out.println("discount = " + discount);
				break;
			}					
			ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
			System.out.println("CAR = " + ticket.getPrice());
			System.out.println("duration_car = " + duration);
			System.out.println("discount = " + discount);
			break;
		}
		case BIKE: {
			if (duration <= 0.5) {
				ticket.setPrice(duration * Fare.BIKE_FREE_RATE_FOR_MINUTES);
				System.out.println("BIKE_FREE = " + ticket.getPrice());
				System.out.println("duration_bike_free = " + duration);
				System.out.println("discount = " + discount);
				break;
			}
			if (duration > 0.5 && discount) {
				ticket.setPrice(duration  * (Fare.BIKE_RATE_PER_HOUR - (Fare.BIKE_RATE_PER_HOUR * 5 / 100)));
				System.out.println("duration_discount = " + duration + " , BIKE_DISCOUNT =" + ticket.getPrice());
				System.out.println("discount = " + discount);
				break;
			}
			ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
			System.out.println("BIKE =" + ticket.getPrice());
			System.out.println("duration_bike = " + duration);
			System.out.println("discount = " + discount);
			break;
		}

		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
				
	}
		
	public void calculateFare(Ticket ticket) {
		calculateFare(ticket,  false); 
		
	}
}


