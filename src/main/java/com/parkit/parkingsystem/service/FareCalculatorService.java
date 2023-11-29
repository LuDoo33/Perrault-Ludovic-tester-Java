package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void checkIfInTimeIsNotBeforeOutTime(Ticket ticket) throws IllegalArgumentException {

		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}
	}

	public void checkIfUnknownParkingType(Ticket ticket) throws NullPointerException {

		if (ticket.getParkingSpot().getParkingType() == null) {
			throw new NullPointerException("Unknown parking type" + ticket.getParkingSpot().getParkingType());
		}
	}

	public double calculateTime(Ticket ticket) {
		long inHour = ticket.getInTime().getTime();

		long outHour = ticket.getOutTime().getTime();
		double calculateDurationMinutes = (outHour - inHour) / (1000 * 60);

		return calculateDurationMinutes;
	}

	public double calculateFarePerType(Ticket ticket) {
		double fare;

		if (ticket.getParkingSpot().getParkingType() == ParkingType.CAR) {

			fare = Fare.CAR_RATE_PER_HOUR;

		} else {
			fare = Fare.BIKE_RATE_PER_HOUR;

		}
		return fare;
	}

	public void calculateFare(Ticket ticket, boolean discount) {
		double fare = calculateFarePerType(ticket);

		double timeSpentInHours = calculateTime(ticket) / 60;
		double timeSpentInMinutes = calculateTime(ticket);

		if ((timeSpentInMinutes > 30 && !discount) || (timeSpentInHours >= 1 && !discount)) {
			ticket.setPrice(timeSpentInHours * fare);

		} else if (timeSpentInHours > 0.5 && discount) {
			ticket.setPrice(timeSpentInHours * fare * 0.95);

		}

	}

	public void calculateFare(Ticket ticket) {

		FareCalculatorService fareCalculatorService = new FareCalculatorService();

		fareCalculatorService.calculateFare(ticket, false);

	}
}
