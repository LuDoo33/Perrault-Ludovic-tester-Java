package com.parkit.parkingsystem.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
	private static final Logger logger = LogManager.getLogger("FareCalculatorService");

	Ticket ticket = new Ticket();

	int priceToZero = 0;

	/*
	 * public double hoursSpentInParking(Ticket ticket, long inHour, long outHour) {
	 * this.inHour = inHour; 
	 * this.outHour = outHour;
	 * 
	 * double calculateDurationHours = (((((outHour - inHour) / 1000) / 60)) / 60);
	 * 
	 * return calculateDurationHours; }
	 */

	public double hoursSpentInParking(Ticket ticket) {
		long inHour = ticket.getInTime().getTime();

		long outHour = ticket.getOutTime().getTime();
		double calculateDurationHours = (((((outHour - inHour) / 1000) / 60)) / 60);

		return calculateDurationHours;
	}

	public double minutesSpentInParking(Ticket ticket) {
		//logger.info("Entrée dans la méthode minutesSpentInParking");
		long inHour = ticket.getInTime().getTime();

		long outHour = ticket.getOutTime().getTime();
		double calculateDurationMin = ((((outHour - inHour) / 1000) / 60));

		return calculateDurationMin;
	}

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

	public void calculateFareCarWithDiscount(Ticket ticket, boolean discount) {

		double hoursSpentInParking = hoursSpentInParking(ticket);
		if (hoursSpentInParking > 0.5 && discount && ticket.getParkingSpot().getParkingType() == ParkingType.CAR) {
			try {
				double initialFare = hoursSpentInParking * Fare.CAR_RATE_PER_HOUR;
				double reducedFare = initialFare * 0.95;
				ticket.setPrice(reducedFare);
				System.out.println("prix réduit " + ticket.getPrice() + " €");
				logger.info("Dans la méthode calculateFareCarWithDiscount");

			} catch (Exception e) {
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		}

	}

	public void calculateFareBikeWithDiscount(Ticket ticket, boolean discount) {
		double hoursSpentInParking = hoursSpentInParking(ticket);

		if (hoursSpentInParking > 0.5 && discount && ticket.getParkingSpot().getParkingType() == ParkingType.BIKE) {
			try {
				double initialPrice = hoursSpentInParking * Fare.BIKE_RATE_PER_HOUR;
				double reducedPrice = initialPrice * 0.95;
				ticket.setPrice(reducedPrice);
			} catch (Exception e) {

				throw new IllegalArgumentException("Unkown Parking Type");
			}
		}
	}

	public void freeParkingTimeForCars(Ticket ticket) {
		double minutesSpentInParking = minutesSpentInParking(ticket);

		if (minutesSpentInParking <= 30 && ticket.getParkingSpot().getParkingType() == ParkingType.CAR) {
			try {
				ticket.setPrice(minutesSpentInParking * priceToZero);
				System.out.println("prix pour une durée inf à 30 min : " + ticket.getPrice() + " €");
			} catch (Exception e) {

				throw new IllegalArgumentException("Unkown Parking Type");
			}

		}
	}

	public void freeParkingTimeForBikes(Ticket ticket) {
		double minutesSpentInParking = minutesSpentInParking(ticket);

		if (minutesSpentInParking <= 30 && ticket.getParkingSpot().getParkingType() == ParkingType.BIKE) {
			try {
				ticket.setPrice(minutesSpentInParking * priceToZero);
				System.out.println("prix pour une durée inf à 30 min : " + ticket.getPrice() + " €");
			} catch (Exception e) {

				throw new IllegalArgumentException("Unkown Parking Type");
			}

		}
	}

	public void calculateFareCar(Ticket ticket) {
		double hoursSpentInParking = hoursSpentInParking(ticket);

		if (hoursSpentInParking >= 1 && ticket.getParkingSpot().getParkingType() == ParkingType.CAR) {
			try {
				ticket.setPrice(hoursSpentInParking * Fare.CAR_RATE_PER_HOUR);
				System.out.println(
						"prix pour une durée sup à 30 min et inf à 60 min ou sup à 1h : " + ticket.getPrice() + " €");

			} catch (Exception e) {
				throw new IllegalArgumentException("Unkown Parking Type");

			}

		}
	}

	public void calculateFareCarLessThanOneHour(Ticket ticket) {
		double minutesSpentInParking = minutesSpentInParking(ticket);
		double hoursSpentInParking = minutesSpentInParking / 60;

		if (minutesSpentInParking > 30 && minutesSpentInParking < 60
				&& ticket.getParkingSpot().getParkingType() == ParkingType.CAR) {
			try {
				ticket.setPrice(hoursSpentInParking * Fare.CAR_RATE_PER_HOUR);
				System.out.println(
						"prix pour une durée sup à 30 min et inf à 60 min ou sup à 1h : " + ticket.getPrice() + " €");

			} catch (Exception e) {
				throw new IllegalArgumentException("Unkown Parking Type");

			}

		}
	}

	public void calculateFareBikeLessThanOneHour(Ticket ticket) {
		double minutesSpentInParking = minutesSpentInParking(ticket);
		double hoursSpentInParking = minutesSpentInParking / 60;

		if (minutesSpentInParking > 30 && minutesSpentInParking < 60
				&& ticket.getParkingSpot().getParkingType() == ParkingType.BIKE) {
			try {
				ticket.setPrice(hoursSpentInParking * Fare.BIKE_RATE_PER_HOUR);
				System.out.println(
						"prix pour une durée sup à 30 min et inf à 60 min ou sup à 1h : " + ticket.getPrice() + " €");

			} catch (Exception e) {
				throw new IllegalArgumentException("Unkown Parking Type");

			}

		}
	}

	public void calculateFareBike(Ticket ticket) {
		double hoursSpentInParking = hoursSpentInParking(ticket);
		double minutesSpentInParking = minutesSpentInParking(ticket);

		if (minutesSpentInParking > 30 && minutesSpentInParking < 60
				|| hoursSpentInParking >= 1 && ticket.getParkingSpot().getParkingType() == ParkingType.BIKE) {
			try {
				ticket.setPrice(hoursSpentInParking * Fare.BIKE_RATE_PER_HOUR);
				System.out.println(
						"prix pour une durée sup à 30 min et inf à 60 min ou sup à 1h : " + ticket.getPrice() + " €");

			} catch (Exception e) {
				throw new IllegalArgumentException("Unkown Parking Type");

			}
		}
	}

}
/*
 * public void calculateFare(Ticket ticket, boolean discount) { /*if
 * ((ticket.getOutTime() == null) ||
 * (ticket.getOutTime().before(ticket.getInTime()))) { throw new
 * IllegalArgumentException("Out time provided is incorrect:" +
 * ticket.getOutTime().toString()); }
 * 
 * int priceToZero = 0;
 */

/*
 * long inHour = ticket.getInTime().getTime();
 * 
 * long outHour = ticket.getOutTime().getTime();
 */

// TODO: Some tests are failing here. Need to check if this logic is correct

/*
 * double calculateDurationInMinutes = ((((outHour - inHour) / 1000) / 60));
 * System.out.println("Temps en minutes : " + calculateDurationInMinutes +
 * " min");
 * 
 * double calculateDurationHours = calculateDurationInMinutes / 60;
 * System.out.println("Temps en heures : " + calculateDurationHours + " h");
 */

/*
 * if (calculateDurationHours > 0.5 && discount) { switch
 * (ticket.getParkingSpot().getParkingType()) { case CAR: {
 * 
 * double initialFare = calculateDurationHours * Fare.CAR_RATE_PER_HOUR; double
 * reducedFare = initialFare * 0.95; ticket.setPrice(reducedFare);
 * System.out.println("prix réduit " + ticket.getPrice() + " €"); break; }
 * 
 * case BIKE: { double initialPrice = calculateDurationHours *
 * Fare.BIKE_RATE_PER_HOUR; double reducedPrice = initialPrice * 0.95;
 * ticket.setPrice(reducedPrice); break; }
 * 
 * default: throw new IllegalArgumentException("Unkown Parking Type"); } }
 */
/*
 * else if (calculateDurationInMinutes <= 30) {
 * 
 * switch (ticket.getParkingSpot().getParkingType()) { case CAR: {
 * ticket.setPrice(calculateDurationInMinutes * priceToZero);
 * System.out.println("prix pour une durée inf à 30 min : " + ticket.getPrice()
 * + " €"); break; } case BIKE: { ticket.setPrice(calculateDurationInMinutes *
 * priceToZero); break; } default: throw new
 * IllegalArgumentException("Unkown Parking Type"); } }
 */
/*
 * else if(calculateDurationInMinutes>30&&calculateDurationInMinutes<60||
 * calculateDurationHours>=1)
 * 
 * {
 * 
 * switch (ticket.getParkingSpot().getParkingType()) {
 * 
 * case CAR: ticket.setPrice(calculateDurationHours * Fare.CAR_RATE_PER_HOUR);
 * System.out.println(
 * "prix pour une durée sup à 30 min et inf à 60 min ou sup à 1h : " +
 * ticket.getPrice() + " €"); break;
 * 
 * case BIKE: { ticket.setPrice(calculateDurationHours *
 * Fare.BIKE_RATE_PER_HOUR); break; }
 * 
 * default: throw new IllegalArgumentException("Unkown Parking Type"); }
 * 
 * }
 * 
 * }
 * 
 * public void calculateFare(Ticket ticket) {
 * 
 * FareCalculatorService fareCalculatorService = new FareCalculatorService();
 * 
 * fareCalculatorService.calculateFare(ticket, false);
 * 
 * }
 * 
 * }
 */
