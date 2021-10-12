package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class TicketDAOTest {

	private static TicketDAO ticketDAO;
	private static Ticket ticket;

	public static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	Connection con = null;
	private static final Logger logger = LogManager.getLogger("TicketDAOTest");

	private static ParkingSpot parkingSpot;

	@BeforeAll
	private static void setUp() throws Exception {
		ticketDAO = new TicketDAO();
		ticket = new Ticket();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;

	}

	@BeforeEach
	public void setUpPerTest() {
		try {
			con = dataBaseTestConfig.getConnection();
		} catch (Exception ex) {
			logger.error("Error connecting to data base", ex);

		}
	}

	@AfterEach
	private void tearDownPerTest() {
		dataBaseTestConfig.closeConnection(con);
	}

	/*
	 * check that this operation does not save the Ticket in DB
	 */
	@Test
	public void saveTicketTest() throws Exception {

		// GIVEN
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		outTime.setTime(System.currentTimeMillis());

		// WHEN
		ticketDAO.saveTicket(ticket);

		// THEN
		assertFalse(ticketDAO.saveTicket(ticket));

	}

	/*
	 * the following test should return an Exception when the date of inTime and
	 * outTime are null
	 */
	@Test
	public void saveTicketTest_shouldReturnException() {

		// GIVEN
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		try {
			Date inTime = new Date();
			inTime.setTime(0);
			Date outTime = new Date();
			outTime.setTime(0);

			// WHEN
			ticketDAO.saveTicket(ticket);

			// THEN
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			assertTrue(e.getMessage().contains("Error fetching next available slot"));
		}

	}

	/*
	 * Check that getTicket method take vehicle register number as parameter
	 */
	@Test
	public void getTicketTest() {

		// GIVEN
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

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
		Date outTime = new Date();
		ticket.setOutTime(outTime);
		ticket.setPrice(1.5);

		// WHEN
		boolean updateTicket = ticketDAO.updateTicket(ticket);

		// THEN
		assertTrue(updateTicket);
	}

	/*
	 * the updateTicket should failure when the date of out time is null
	 */

	@Test
	public void updateTicketTest_shouldReturnNullPointerException() {

		// GIVEN
		// ALL is already done in Before Each and Before All!

		// WHEN
		try {
			ticket.setOutTime(null);
			ticketDAO.updateTicket(ticket);
		} catch (NullPointerException e) {
			// THEN
			assertTrue(e instanceof NullPointerException);
			assertFalse(ticketDAO.updateTicket(ticket));
		}
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
	 * the following test of the vehicle Register Number should return an Exception
	 * and should not save it in the DB
	 */
	@Test
	public void isSavedTest_shouldReturnException() {

		// GIVEN
		ticket.setVehicleRegNumber(null);

		// WHEN
		try {
			ticketDAO.isSaved(ticket.getVehicleRegNumber());

		}
		// THEN
		catch (Exception e) {

			assertTrue(e instanceof IllegalArgumentException);
		}
		assertFalse(ticketDAO.isSaved(ticket.getVehicleRegNumber()));

	}

}
