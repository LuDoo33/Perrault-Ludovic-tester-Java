package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class TicketDAOTest {

	private static TicketDAO ticketDAO;
	private static Ticket ticket;
	private static Date inTime;
	private static Date outTime;

	public DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

	@Mock
	private static ParkingSpot parkingSpot;

	@BeforeEach
	public void setUpPerTest() {
		ticketDAO = new TicketDAO();
		ticket = new Ticket();

	}

	/*
	 * Test that the ticket is not saved
	 */
	@Test
	public void saveTicketTest() {
		// GIVEN
		parkingSpot = mock(ParkingSpot.class);

		inTime = new Date(System.currentTimeMillis() - (60 * 60 * 1000));
		outTime = new Date();

		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setPrice(1.5);

		// WHEN
		boolean saved = ticketDAO.saveTicket(ticket);

		// THEN
		assertFalse(saved);
		verify(parkingSpot, times(1)).getId();
	}

	/*
	 * Check that getTicket method take vehicle reg number as parameter
	 */
	@Test
	public void getTicketTest() {
		// GIVEN
		parkingSpot = mock(ParkingSpot.class);
		

		when(parkingSpot.getId()).thenReturn(1);
		when(parkingSpot.getParkingType()).thenReturn(ParkingType.CAR);
		when(parkingSpot.isAvailable()).thenReturn(false);

		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");

		// WHEN
		Ticket ticketdaotest = ticketDAO.getTicket(ticket.getVehicleRegNumber());

		// THEN
		assertEquals(ticketdaotest.getVehicleRegNumber(), "ABCDEF");

	}

	@Test
	public void updateTicketTest() {
		// GIVEN
		ticket = new Ticket();

		outTime = new Date();
		ticket.setOutTime(outTime);
		ticket.setPrice(1.5);

		// WHEN
		boolean updateTicket = ticketDAO.updateTicket(ticket);

		// THEN
		assertTrue(updateTicket);
	}

	/*
	 * Check that a vehicle register number is for a recurring user
	 */
	@Test
	public void isRecurringTest_forRecurringUser_shouldReturnTrue() {
		// GIVEN
		ticket.setVehicleRegNumber("ABCDEF");

		// WHEN
		boolean isRecurring = ticketDAO.isRecurring(ticket.getVehicleRegNumber());

		// THEN
		assertTrue(isRecurring);
	}

	/*
	 * Check that a new vehicle register number is not for a recurring user
	 */
	@Test
	public void isRecurringTest_forNewUser_shouldReturnFalse() {
		// GIVEN
		ticket.setVehicleRegNumber("IMNEWUSER");

		// WHEN
		boolean isRecurring = ticketDAO.isRecurring(ticket.getVehicleRegNumber());

		// THEN
		assertFalse(isRecurring);
	}

	/*
	 * Check if vehicle Reg Number is saved
	 */
	@Test
	public void isSavedTest() {
		// GIVEN
		ticket.setVehicleRegNumber("ABCDEF");

		// WHEN
		boolean isSaved = ticketDAO.isSaved(ticket.getVehicleRegNumber());

		// THEN
		assertTrue(isSaved);
		System.out.println(isSaved);
	}

	/*
	 * Check if vehicle Reg Number is saved
	 */
	@Test
	public void isSavedTest_no() {
		// GIVEN
		ticket.setVehicleRegNumber(null);

		// WHEN
		try {
			ticketDAO.isSaved(ticket.getVehicleRegNumber());

		}
		// THEN
		catch (Exception e) {

			assertTrue(e instanceof IllegalArgumentException);
			assertFalse(ticketDAO.isSaved(ticket.getVehicleRegNumber()));
		}
	}

}
