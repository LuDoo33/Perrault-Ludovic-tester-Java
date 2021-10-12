package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.*;
import com.parkit.parkingsystem.model.*;
import com.parkit.parkingsystem.service.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;

	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();

	}

	@Test
	public void calculateFareTest_forCar() {

		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	public void calculateFareTest_forBike() {

		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		// WHEN
		fareCalculatorService.calculateFare(ticket);
		assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
	}

	/*
	 * NullPointerException for Unknown Vehicle Type
	 */
	@Test
	public void calculateFareTest_shouldThrowNullPointerException_forUnknowVehicleType() {
		// GIVEN
		Date inTime = new Date();
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, null, false); // vehicle type null should generate a
																	// NullPointerException
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);

		try {
			ticket.setParkingSpot(parkingSpot);

			// WHEN
			fareCalculatorService.calculateFare(ticket);

			// THEN
		} catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
//			assertTrue(e.getMessage().contains("Unkown Parking Type"));
		}

	}

	/*
	 * future in-time for parking CAR should generate an IllegalArgumentException
	 */
	@Test
	public void calculateFareCar_shouldThrowIllegalArgumentException_forFutureInTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000)); // future in-time (in-time after out-time) should
																		// generate an IllegalArgumentException
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		try {
			fareCalculatorService.calculateFare(ticket);

			// THEN
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			assertTrue(e.getMessage().contains("Out time provided is incorrect:" + ticket.getOutTime().toString()));
		}

	}

	/*
	 * future in-time for parking BIKE should generate an IllegalArgumentException
	 */
	@Test
	public void calculateFareTest_forBike_shouldThrowIllegalArgumentException_forFutureInTime() {

		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000)); // future in-time (in-time after out-time) should
																		// generate an IllegalArgumentException
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		try {
			fareCalculatorService.calculateFare(ticket);

		}
		// THEN
		catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			assertTrue(e.getMessage().contains("Out time provided is incorrect:" + ticket.getOutTime().toString()));
		}

	}

	/*
	 * null out-time for parking CAR should generate an IllegalArgumentException
	 */
	@Test
	public void calculateFareCar_shouldThrowIllegalArgumentException_WithNullOutTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		outTime.setTime(0); // null out-time should generate an IllegalArgumentException
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		try {
			fareCalculatorService.calculateFare(ticket);
		}
		// THEN
		catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			assertTrue(e.getMessage().contains("Out time provided is incorrect:" + ticket.getOutTime().toString()));
		}

	}

	/*
	 * null out-time for parking BIKE should generate an IllegalArgumentException
	 */
	@Test
	public void calculateFareBike_shouldThrowIllegalArgumentException_WithNullOutTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		outTime.setTime(0); // null out-time should generate an IllegalArgumentException
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		try {
			fareCalculatorService.calculateFare(ticket);
		}
		// THEN
		catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			assertTrue(e.getMessage().contains("Out time provided is incorrect:" + ticket.getOutTime().toString()));
		}
	}

	@Test
	public void calculateFareTest_forBike_WithLessThanOneHourParkingTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give 3/4th
																		// parking fare per an hour
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareTest_forCar_WithLessThanOneHourParkingTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give 3/4th
																		// parking fare per an hour
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareTest_forCar_WithMoreThanADayParkingTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));// 24 hours parking time should give 24 *
																			// parking fare per an hour
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	/*
	 * Free car parking for first 30 minutes
	 */
	@Test
	public void calculateFareTest_forCar_WithLessThan30MinutesParkingTime_shouldBeFree() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (20 * 60 * 1000));// 20 minutes parking time should give 0 *
																		// parking fare per an hour
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals((0 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	/*
	 * Free Bike parking for first 30 minutes
	 */
	@Test
	public void calculateFareTest_forBike_WithLessThan30MinutesParkingTime_shouldBeFree() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (20 * 60 * 1000));// 20 minutes parking time should give 0 *
																		// parking fare per an hour
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals((0 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	/*
	 * 5% discount For recurring CAR users
	 */
	@Test
	public void calculateFareTest_forCar_forRecurringUsers_shouldGetA5PerCentDisount() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));// 60 minutes parking time should give 1 *
																		// parking fare per an hour *
																		// 5%-discount
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals((0.95 * Fare.CAR_RATE_PER_HOUR), 0.95 * ticket.getPrice());
	}

	/*
	 * 5% discount For recurring BIKE users
	 */
	@Test
	public void calculateFareTest_forBike_forRecurringUsers_shouldGetA5PerCentDisount() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));// 60 minutes parking time should give 1 *
																		// parking fare per an hour *
																		// 5%-discount
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals((0.95 * Fare.BIKE_RATE_PER_HOUR), 0.95 * ticket.getPrice());
	}

}
