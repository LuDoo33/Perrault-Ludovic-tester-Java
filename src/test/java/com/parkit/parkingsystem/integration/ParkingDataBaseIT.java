package com.parkit.parkingsystem.integration;

import static org.mockito.Mockito.when;

import java.util.Date;

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
	// TODO: check that a ticket is actually saved in DB and Parking table is
	// updated with availability

	// ARRANGE
	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
	String plaque = "";
	try {
	    plaque = inputReaderUtil.readVehicleRegistrationNumber();
	} catch (Exception e) {
	}

	Date inTime = new Date();
	Ticket ticket = new Ticket();
	// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
	// ticket.setId(ticketID);
	/*
	 * ticket.setParkingSpot(parkingSpot);
	 * ticket.setVehicleRegNumber(vehicleRegNumber); ticket.setPrice(0);
	 * ticket.setInTime(inTime); ticket.setOutTime(null);
	 */
	// ACT
	parkingService.processIncomingVehicle();
	Ticket t = ticketDAO.getTicket(plaque);
	if (t != null) {
	    t.getId();
	}

	// ASSERT
	// COMMENCER PAR LE ASSERT : VERIFIER QU'UN TICKET EST BIEN ENREGISTRE DANS LA
	// BASE DE DONNEE
	// assertEquals(Exception e, NULL);
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
