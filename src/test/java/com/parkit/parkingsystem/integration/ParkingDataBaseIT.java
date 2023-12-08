package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @Mock
	private static ParkingService parkingService;
    
    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
	private void setUpPerTest() throws Exception {
		dataBasePrepareService.clearDataBaseEntries();
		
		Mockito.when(inputReaderUtil.readSelection()).thenReturn(1);
		Mockito.when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		
		
		 parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);


		assertThat(inputReaderUtil.readSelection()).isEqualTo(1);
		assertThat(inputReaderUtil.readVehicleRegistrationNumber()).isEqualTo("ABCDEF");
		Mockito.verify(inputReaderUtil, Mockito.times(1)).readSelection();
		Mockito.verify(inputReaderUtil, Mockito.times(1)).readVehicleRegistrationNumber();
	}

		
		@DisplayName("Testing a ticket is saved in the DB and parking table is updated")
		@Test
		public void testParkingACar() throws Exception {

			
			ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			parkingService.processIncomingVehicle();

			// TODO: check that a ticket is actually saved in DB and Parking table is
			// updated with availability

			Ticket ticket = ticketDAO.getTicket("ABCDEF");

			assertThat(ticket.getParkingSpot().getParkingType()).isEqualTo(ParkingType.CAR);
			assertThat(ticket).isNotNull();
			assertThat(ticket.getId()).isEqualTo(1);
			assertThat(ticket.getVehicleRegNumber()).isEqualTo("ABCDEF");
			assertThat(ticket.getInTime()).isNotNull();
			assertThat(ticketDAO.getNbTicket("ABCDEF")).isEqualTo(1);
			assertThat(ticket.getParkingSpot().isAvailable()).isFalse();
			assertThat(parkingSpotDAO.updateParking(ticket.getParkingSpot())).isTrue();

		}
	

	@DisplayName("Testing fare generated and out time populated in the DB")
	@Test
	public void testParkingLotExit() throws Exception {

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		parkingService.processIncomingVehicle();

		try {
			TimeUnit.SECONDS.sleep(1);
			parkingService.processExitingVehicle();
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Ticket ticket = ticketDAO.getTicket("ABCDEF");

		assertThat(ticket.getOutTime()).isNotNull();
		assertThat(ticket.getOutTime()).isAfter(ticket.getInTime());
		assertThat(ticketDAO.updateTicket(ticket)).isTrue();
		assertThat(ticket.getPrice()).isNotNull().isEqualTo(0.0);
	} 

	@DisplayName("Testing the fare calculation for a recurring user")
	@Test
	public void testParkingLotExitRecurringUser() throws Exception {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		Ticket ticket1 = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Date inTimeTicket1 = new Date();
		inTimeTicket1.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTimeTicket1 = new Date();
		outTimeTicket1.setTime(System.currentTimeMillis());

		ticket1.setParkingSpot(parkingSpot);
		ticket1.setInTime(inTimeTicket1);
		ticket1.setOutTime(outTimeTicket1);
		ticket1.setVehicleRegNumber("ABCDEF");

		ticketDAO.saveTicket(ticket1);

		parkingService.processIncomingVehicle();

		Ticket ticket2 = ticketDAO.getTicket("ABCDEF");

		Date inTimeTicket2 = new Date();
		inTimeTicket2.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTimeTicket2 = new Date();
		outTimeTicket2.setTime(System.currentTimeMillis());

		ticket2.setInTime(inTimeTicket2);
		ticket2.setOutTime(outTimeTicket2);

		ticketDAO.updateTicket(ticket2);

		try {
			TimeUnit.SECONDS.sleep(1);
			parkingService.processExitingVehicle();
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ticket2 = ticketDAO.getTicket("ABCDEF");

		assertThat(ticketDAO.getNbTicket("ABCDEF")).isEqualTo(2);
		assertThat(ticket2).isNotNull();
		assertThat(ticket2.getPrice()).isEqualTo(0.95 * Fare.CAR_RATE_PER_HOUR);

	}

	@DisplayName("Testing if no existing spot available")
	@Test
	public void testUpdateTicketNoSpotAvailable() {

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		Ticket ticket = ticketDAO.getTicket("ABCEF");
		ParkingSpot parkingSpot = new ParkingSpot(8, ParkingType.CAR, false);
		ticketDAO.saveTicket(ticket);
		parkingService.processIncomingVehicle();

		ticketDAO.updateTicket(ticket);

		parkingService.processExitingVehicle();
		assertFalse(parkingSpotDAO.updateParking(parkingSpot));

	}

	@DisplayName("Testing if count value is null")
	@Test
	public void testGetNbTicketNotFound() throws Exception {

		int count = ticketDAO.getNbTicket(null);
		assertThat(count).isEqualTo(0);

	}
}
