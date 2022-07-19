package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
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
    private static InputReaderUtil inputReaderUtil; // ON SIMULE LA CLASSE InputReaderUtil

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
	// TODO: check that a ticket is actually saved in DB and Parking table is
	// updated with availability

	// GIVEN - ARRANGE : Already done in @BeforeEach

	// WHEN - ACT
	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
	Ticket ticketBeforeProcess = ticketDAO.getTicket("ABCDEF");
	assertThat(ticketBeforeProcess).isNull();

	parkingService.processIncomingVehicle();

	// THEN - ASSERT
	Ticket ticket = ticketDAO.getTicket("ABCDEF");

	assertThat(ticket).isNotNull(); // checking that a ticket is actually saved in DB
	assertThat(ticket.getVehicleRegNumber()).isEqualTo("ABCDEF");

	assertThat(ticket.getParkingSpot().isAvailable()).isFalse(); // checking that Parking table is updated with
								     // availability
    }

    @Test
    public void testParkingLotExit() {
	testParkingACar(); // UNE PARTIE D'UN TEST NE DOIT PAS ETRE UTILISE DANS UN AUTRE
	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
	parkingService.processExitingVehicle();
	// TODO: check that the fare generated and out time are populated correctly in
	// the database

    }

}
