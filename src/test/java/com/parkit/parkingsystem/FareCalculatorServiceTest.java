package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;
	private Date inTime;
	private Date outTime;
	private ParkingSpot parkingSpot;
	private int id;
	private int price;
	private String vehicleRegNumber;

	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket(id, parkingSpot, vehicleRegNumber, price, inTime, outTime);
	}

	@Test
	public void calculateFareCarWhenLastForOneHour() {
		inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		outTime = new Date();
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
	}

	@Test
	public void calculateFareBike() {
		inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		outTime = new Date();
		parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
	}

	@Test
	public void calculateFareUnknownTypeThrowsException() {
		inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		outTime = new Date();
		parkingSpot = new ParkingSpot(1, null, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(NullPointerException.class, () -> fareCalculatorService.checkIfUnknownParkingType(ticket));
	}

	@Test
	public void calculateFareBikeWithFutureInTime() {
		inTime = new Date();
		inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
		outTime = new Date();
		parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(IllegalArgumentException.class,
				() -> fareCalculatorService.checkIfInTimeIsNotBeforeOutTime(ticket));
	}

	@Test
	public void calculateFareBikeWithLessThanOneHourParkingTime() {
		inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give 3/4th
																		// parking fare
		outTime = new Date();
		parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithLessThanOneHourParkingTime() {
		inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give 3/4th
																		// parking fare
		outTime = new Date();
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareWithLessThan30minutesParkingTime() {

		inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000));
		outTime = new Date();
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(0, ticket.getPrice());
	}

	@Test
	public void calculateFareWithMoreThanADayParkingTime() {
		inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));// 24 hours parking time should give 24 *
																			// parking fare per hour
		outTime = new Date();
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareWithDiscountTest() {

		inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		outTime = new Date();
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		double expectedValue = Fare.CAR_RATE_PER_HOUR * 0.95;
		BigDecimal expected = new BigDecimal(expectedValue).setScale(2, RoundingMode.HALF_UP);
		fareCalculatorService.calculateFare(ticket, true);

		double actualValue = ticket.getPrice();
		BigDecimal actual = new BigDecimal(actualValue).setScale(2, RoundingMode.HALF_UP);

		assertEquals(expected, actual);
	}

}
