package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

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
    public void calculateFareCar() {
	Date inTime = new Date();
	inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
	Date outTime = new Date();
	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

	ticket.setInTime(inTime);
	ticket.setOutTime(outTime);
	ticket.setParkingSpot(parkingSpot);
	fareCalculatorService.calculateFare(ticket);
	assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike() {
	Date inTime = new Date();
	inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
	Date outTime = new Date();
	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

	ticket.setInTime(inTime);
	ticket.setOutTime(outTime);
	ticket.setParkingSpot(parkingSpot);
	fareCalculatorService.calculateFare(ticket);
	assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType() {
	Date inTime = new Date();
	inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
	Date outTime = new Date();
	ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

	ticket.setInTime(inTime);
	ticket.setOutTime(outTime);
	ticket.setParkingSpot(parkingSpot);
	assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
	Date inTime = new Date();
	inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000)); // AJOUT D'1 HEURE
	Date outTime = new Date();
	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

	ticket.setInTime(inTime);
	ticket.setOutTime(outTime);
	ticket.setParkingSpot(parkingSpot);
	assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
	Date inTime = new Date();
	inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give 3/4th
								      // parking fare
	Date outTime = new Date();
	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

	ticket.setInTime(inTime);
	ticket.setOutTime(outTime);
	ticket.setParkingSpot(parkingSpot);
	fareCalculatorService.calculateFare(ticket);
	assertThat(ticket.getPrice()).isEqualTo((0.75 * Fare.BIKE_RATE_PER_HOUR));
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime() {
	// ARRANGE
	Date inTime = new Date();
	inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give 3/4th
								      // parking fare
	Date outTime = new Date();
	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

	ticket.setInTime(inTime);
	ticket.setOutTime(outTime);
	ticket.setParkingSpot(parkingSpot);

	// ACT
	fareCalculatorService.calculateFare(ticket);

	// ASSERT
	assertThat(ticket.getPrice()).isEqualTo((0.75 * Fare.CAR_RATE_PER_HOUR));
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime() {
	Date inTime = new Date();
	inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));// 24 hours parking time should give 24 *
									   // parking fare per hour
	Date outTime = new Date();
	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

	ticket.setInTime(inTime);
	ticket.setOutTime(outTime);
	ticket.setParkingSpot(parkingSpot);
	fareCalculatorService.calculateFare(ticket);
	assertThat(ticket.getPrice()).isEqualTo((24 * Fare.CAR_RATE_PER_HOUR));
    }

    @Test
    @DisplayName("Prix pour voiture moins de 30 minutes = 0 ")
    public void calculateFareCarWithLessThanThirtyMinutes() {
	// GIVEN - ARRANGE
	Date inTime = new Date();
	Date outTime = new Date();
	outTime.setTime(System.currentTimeMillis() + 30 * 60 * 1000); // HEURE ACTUELLE + 30 MINUTES

	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
	ticket.setInTime(inTime);
	ticket.setOutTime(outTime);
	ticket.setParkingSpot(parkingSpot);

	// WHEN - ACT
	fareCalculatorService.calculateFare(ticket);

	// THEN ASSERT
	assertThat(ticket.getPrice()).isEqualTo((0));
    }

    @Test
    @DisplayName("Prix pour moto moins de 30 minutes = 0 ")
    public void calculateFareBikeWithLessThanThirtyMinutes() {
	// GIVEN - ARRANGE
	Date inTime = new Date();
	Date outTime = new Date();
	outTime.setTime(System.currentTimeMillis() + 30 * 60 * 1000); // HEURE ACTUELLE + 30 MINUTES
	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
	ticket.setInTime(inTime);
	ticket.setOutTime(outTime);
	ticket.setParkingSpot(parkingSpot);

	// WHEN - ACT
	fareCalculatorService.calculateFare(ticket);

	// THEN - ASSERT
	assertThat(ticket.getPrice()).isEqualTo((0));
    }

    @Test
    @DisplayName("Reduction de 5% pour utilisateurs réguliers")
    public void calculateFivePercentDiscountForRecurringUsers() {
	// GIVEN - ARRANGE
	Date inTime = new Date();
	Date outTime = new Date();
	outTime.setTime(System.currentTimeMillis() + 60 * 60 * 1000); // HEURE ACTUELLE + 1H
	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
	ticket.setInTime(inTime);
	ticket.setOutTime(outTime);
	ticket.setParkingSpot(parkingSpot);

	// WHEN - ACT
	fareCalculatorService.calculateFareWithFivePercentDiscount(ticket);

	// THEN - ASSERT
	System.out.println(ticket.getPrice());
	double expectedPrice = (double) Fare.CAR_RATE_PER_HOUR - (Fare.CAR_RATE_PER_HOUR * 5 / 100);

	assertThat(ticket.getPrice()).isEqualTo(1 * Fare.CAR_RATE_PER_HOUR_DISCOUNT);
    }

}
