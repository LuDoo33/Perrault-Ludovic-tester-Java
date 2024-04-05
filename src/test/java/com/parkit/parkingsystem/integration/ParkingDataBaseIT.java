package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {
	private static final Logger logger = LogManager.getLogger("ParkingDataBaseIT");
	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;
	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception{
		logger.info("Je rentre dans la méthode setUp() dans @BeforeAll");

		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
		dataBaseTestConfig.getConnection();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		logger.info("Je rentre dans la méthode setUpPerTest() dans @BeforeEach");
		dataBasePrepareService.clearDataBaseEntries();
		dataBasePrepareService.initDataBase();
	}

	@Test
	public void testParkingACar() throws Exception{
		logger.info("Je rentre dans la méthode testParkingACar()");
		when(inputReaderUtil.readSelection()).thenReturn(1);

		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		parkingService.processIncomingVehicle();

		Ticket geTicketSaved = ticketDAO.getTicket("ABCDEF");

		logger.info("geTicketSaved dans testParkingACar" + geTicketSaved);
		assertNotNull(geTicketSaved);

		boolean updatedParking = parkingSpotDAO.updateParking(geTicketSaved.getParkingSpot());
		assertTrue(updatedParking);
		//TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
	}

	@Test
	public void testParkingLotExit() throws Exception{
		logger.info("Je rentre dans la méthode testParkingLotExit()");
		
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		parkingService.processExitingVehicle();
		
		Ticket ticketSaved = ticketDAO.getLastTicket("ABCDEF");
		logger.info("ticketSaved" + ticketSaved);

		logger.info("ticketSaved outtime" + ticketSaved.getOutTime());

		boolean updatedTicket = ticketDAO.updateTicket(ticketSaved);
		assertTrue(updatedTicket);

		//TODO: check that the fare generated and out time are populated correctly in the database
	}

	@Test
	@DisplayName("testParkingLotExitRecurringUser()")
	public void testParkingLotExitRecurringUser() throws Exception {
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("GHIJKL");

		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (5 * 24 * 60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpotTicketOutDated = new ParkingSpot(1, ParkingType.CAR, false);
		double ticketOutDatedPrice = 24 * 5 * Fare.CAR_RATE_PER_HOUR;
		logger.debug("-------- discountPrice ", ticketOutDatedPrice);

		Ticket ticketOutDated = new Ticket();
		ticketOutDated.setInTime(inTime);
		ticketOutDated.setOutTime(outTime);
		ticketOutDated.setParkingSpot(parkingSpotTicketOutDated);
		ticketOutDated.setVehicleRegNumber("GHIJKL");
		ticketOutDated.setPrice(ticketOutDatedPrice);
		ticketDAO.saveTicket(ticketOutDated);

		ParkingSpot parkingSpotOpenedTicket = new ParkingSpot(2, ParkingType.CAR, false);

		Ticket openedTicket = new Ticket();
		Date inTimeOpenedTicket = new Date();
		inTimeOpenedTicket.setTime(System.currentTimeMillis() - (60 * 60 * 1000));

		openedTicket.setInTime(inTimeOpenedTicket);
		openedTicket.setVehicleRegNumber("GHIJKL");
		openedTicket.setParkingSpot(parkingSpotOpenedTicket);
		
		ticketDAO.saveTicket(openedTicket);

		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);


		parkingService.processExitingVehicle();

		Ticket ticketSavedForRecurringUser = ticketDAO.getLastTicket("GHIJKL");
		logger.info("ticketSavedForRecurringUser" + ticketSavedForRecurringUser);

		logger.info("getOutTime ticketSavedForRecurringUser " + ticketSavedForRecurringUser.getOutTime());

		int nbOfTickets = ticketDAO.getNbTicket("GHIJKL");
		logger.info("nbOfTickets " + nbOfTickets);
		
		double openedTicketPrice = ticketSavedForRecurringUser.getPrice();
		
		double expectedValue = Fare.CAR_RATE_PER_HOUR * 0.95;

		BigDecimal expected = new BigDecimal(expectedValue).setScale(2, RoundingMode.HALF_UP);

		BigDecimal returnedValue = new BigDecimal(openedTicketPrice).setScale(2, RoundingMode.HALF_UP);

		assertEquals(expected , returnedValue);

	}


}
