package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.*;
import com.parkit.parkingsystem.dao.*;
import com.parkit.parkingsystem.model.*;
import com.parkit.parkingsystem.service.*;
import com.parkit.parkingsystem.util.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	@BeforeEach
	private void setUpPerTest() {

	}

	@Test
	public void processIncomingVehicleTest() throws Exception {
		// GIVEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(2);

		// WHEN
		parkingService.processIncomingVehicle();

		// THEN
		verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
		verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
		assertFalse(parkingSpot.isAvailable());

	}

	/*
	 * Get registered number of the vehicle
	 */
	@Test
	public void getVehichleRegNumberTest() throws Exception {
		// GIVEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		Ticket ticket = new Ticket();

		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		// WHEN
		ticket.setVehicleRegNumber(inputReaderUtil.readVehicleRegistrationNumber());
		parkingService.getVehichleRegNumber();

		// THEN
		verify(inputReaderUtil, atLeast(1)).readVehicleRegistrationNumber();
		assertEquals(ticket.getVehicleRegNumber(), "ABCDEF");
	}

	/*
	 * check if the parking spot is available
	 */
	@Test
	void getNextParkingNumberIfAvailableTest() {
		// GIVEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		ParkingSpot parkingSpot = new ParkingSpot(0, null, false);

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(2);

		// WHEN
		parkingSpot = parkingService.getNextParkingNumberIfAvailable();

		// THEN
		verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));
		assertTrue(parkingSpot.isAvailable());
	}

	/*
	 * Testing type of the vehicle should return a CAR
	 */
	@Test
	public void getVehichleTypeTest_shouldReturnCAR() {
		// GIVEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		when(inputReaderUtil.readSelection()).thenReturn(1);

		// WHEN
		parkingService.getVehichleType();

		// THEN
		assertEquals(ParkingType.CAR, parkingService.getVehichleType());
	}

	/*
	 * Testing type of the vehicle should return a BIKE
	 */
	@Test
	public void getVehichleTypeTest_shouldReturnBIKE() {
		// GIVEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		when(inputReaderUtil.readSelection()).thenReturn(2);

		// WHEN
		parkingService.getVehichleType();

		// THEN
		assertEquals(ParkingType.BIKE, parkingService.getVehichleType());
	}

	/*
	 * Testing type of the unknown vehicle should generate an
	 * IllegalArgumentException
	 */
	@Test
	public void getVehichleTypeTest_shouldThrowIllegalArgumentException_forUnknowParkingType() {
		// GIVEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		try {
			when(inputReaderUtil.readSelection()).thenReturn(0);

			// WHEN
			parkingService.getVehichleType();

			// THEN
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			assertTrue(e.getMessage().contains("Entered input is invalid"));
		}

	}

	/*
	 * Testing process exiting vehicle should update Ticket and Parking and set a
	 * parking spot as available
	 */
	@Test
	public void processExitingVehicleTest() throws Exception {

		// GIVEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		Ticket ticket = new Ticket();
		ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
		ticket.setOutTime(new Date());
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");

		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

		// WHEN
		parkingService.processExitingVehicle();

		// THEN
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
		assertTrue(parkingSpot.isAvailable());

	}

}
