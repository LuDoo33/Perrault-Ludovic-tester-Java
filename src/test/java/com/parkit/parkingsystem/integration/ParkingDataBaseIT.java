package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static ParkingSpot parkingSpot;
//	private static Ticket ticket;

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();

//		ticket = new Ticket();
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void testProcessIncomingVehicle() {
		// GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processIncomingVehicle();

		// THEN
		/*
		 * check that a ticket is actually saved in DB and Parking table is updated with
		 * availability
		 */
		boolean saved = ticketDAO.isSaved("ABCDEF");
		assertEquals(true, saved);

		boolean available = parkingSpot.isAvailable();
		assertEquals(false, available);

	}

	@Test
	public void testProcessExitingVehicle() {
		// GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		parkingService.processIncomingVehicle();

		// THEN
		/*
		 * check that a ticket is actually saved in DB and Parking table is updated with
		 * availability
		 */
//		boolean saved = ticketDAO.isSaved("ABCDEF");
//		assertEquals(true, saved);
//
//		boolean available = parkingSpot.isAvailable();
//		assertEquals(false, available);



		// WHEN
		parkingService.processExitingVehicle();
//		ticket.setVehicleRegNumber(null);

		
		Ticket ticket2 = ticketDAO.getTicket("ABCDEF");
		
		
		Date generatedTime = ticket2.getOutTime();
		double faregenerated = ticket2.getPrice();

		assertNotNull(generatedTime);
		assertEquals(0.0, faregenerated);
	}

}
