package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
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
	public void testParkingACar() {

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		int parkingNumber = 0;
		parkingNumber = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
		ParkingSpot parkingSpot = new ParkingSpot(parkingNumber, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setId(2);
		ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setPrice(0);
		ticket.setOutTime(null);

		parkingService.processIncomingVehicle();
		
		assertThat(parkingSpotDAO.updateParking(parkingSpot)).isTrue();
		assertThat(ticketDAO.saveTicket(ticket)).isFalse();
		assertThat(ticketDAO.getTicket("ABCDEF")).isNotNull();
	}

	@Test
	public void testParkingLotExit() {
		testParkingACar();

		Ticket ticket = ticketDAO.getTicket("ABCDEF");
		ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
		Date outTime = new Date();
		ticket.setOutTime(outTime);

		ParkingSpot parkingSpot = ticket.getParkingSpot();

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();
		 
		assertThat(ticketDAO.updateTicket(ticket)).isTrue();
		assertThat(parkingSpotDAO.updateParking(parkingSpot)).isTrue();
	}

	@Test
	public void testParkingLotExitRecurringUser() {
		testParkingACar();
		Ticket ticket = ticketDAO.getTicket("ABCDEF");
		ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
		Date outTime = new Date();
		ticket.setOutTime(outTime);		
				
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle(); 
		
		assertThat(ticketDAO.getNbTicket("ABCDEF")).isEqualTo(2);
		assertThat(ticketDAO.updateTicket(ticket)).isTrue(); 
	}

}
