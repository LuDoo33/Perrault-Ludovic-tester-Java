package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket, boolean discount) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		int priceToZero = 0;

		// String vehicleRegNumber = " ";

		long inHour = ticket.getInTime().getTime();

		long outHour = ticket.getOutTime().getTime();

		// TODO: Some tests are failing here. Need to check if this logic is correct

		double calculateDurationInMinutes = ((((outHour - inHour) / 1000) / 60));
		System.out.println("Temps en minutes : " + calculateDurationInMinutes + " min");

		double calculateDurationHours = calculateDurationInMinutes / 60;
		System.out.println("Temps en heures : " + calculateDurationHours + " h");

		// ticket.setVehicleRegNumber("");

		if (calculateDurationHours > 0.5 && discount) {
			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {

				double initialFare = calculateDurationHours * Fare.CAR_RATE_PER_HOUR;
				double reducedFare = initialFare * 0.95;
				ticket.setPrice(reducedFare);
				System.out.println("prix réduit " + ticket.getPrice() + " €");
				break;
			}

			case BIKE: {
				double initialPrice = calculateDurationHours * Fare.BIKE_RATE_PER_HOUR;
				double reducedPrice = initialPrice * 0.95;
				ticket.setPrice(reducedPrice);
				break;
			}

			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		}

		else if (calculateDurationInMinutes <= 30) {

			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				ticket.setPrice(calculateDurationInMinutes * priceToZero);
				System.out.println("prix pour une durée inf à 30 min : " + ticket.getPrice() + " €");
				break;
			}
			case BIKE: {
				ticket.setPrice(calculateDurationInMinutes * priceToZero);
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		}

		else if (calculateDurationInMinutes > 30 && calculateDurationInMinutes < 60 || calculateDurationHours >= 1) {

			switch (ticket.getParkingSpot().getParkingType()) {
			
			case CAR:
				ticket.setPrice(calculateDurationHours * Fare.CAR_RATE_PER_HOUR);
				System.out.println(
						"prix pour une durée sup à 30 min et inf à 60 min ou sup à 1h : " + ticket.getPrice() + " €");
				break;

			case BIKE: {
				ticket.setPrice(calculateDurationHours * Fare.BIKE_RATE_PER_HOUR);
				break;
			}

			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}

		}

	}

	public void calculateFare(Ticket ticket) {

		FareCalculatorService fareCalculatorService = new FareCalculatorService();

		fareCalculatorService.calculateFare(ticket, false);

	}

}
