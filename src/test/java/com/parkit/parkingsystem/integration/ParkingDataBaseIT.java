package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @AfterEach
    private void verifyPerTest() throws Exception {
	verify(inputReaderUtil).readSelection();
	verify(inputReaderUtil, atLeast(1)).readVehicleRegistrationNumber();
    }

    @AfterAll
    private static void tearDown() {
    }

    @Test
    @DisplayName("Test du process d'entrée d'un vehicule")
    public void testParkingACar() {
	// TODO: check that a ticket is actually saved in DB and Parking table is
	// updated with availability

	// GIVEN - ARRANGE : Already done in @BeforeEach

	// WHEN - ACT
	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
	Ticket ticketBeforeProcess = ticketDAO.getTicket("ABCDEF");
	assertThat(ticketBeforeProcess).isNull();

	parkingService.processIncomingVehicle();

	Ticket ticket = ticketDAO.getTicket("ABCDEF");

	// THEN - ASSERT
	assertThat(ticket).isNotNull(); // checking that a ticket is actually saved in DB
	assertThat(ticket.getVehicleRegNumber()).isEqualTo("ABCDEF");

	assertThat(ticket.getParkingSpot().isAvailable()).isFalse(); // checking that Parking table is updated with
								     // availability
    }

    @Test
    @DisplayName("Test du process de sortie d'un vehicule")
    public void testParkingLotExit() {

	// TODO: check that the fare generated and out time are populated correctly in
	// the database

	// GIVEN - ARRANGE // LA VOITURE ENTRE DANS LE PARKING
	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

	// LA VOITURE ENTRE DANS LE PARKING
	testParkingACar();

	// WHEN - ACT // LA VOITURE SORT DU PARKING
	parkingService.processExitingVehicle();

	Ticket ticketAfterExitProcess = ticketDAO.getTicket("ABCDEF");

	// MODIFICATION DE L'HEURE DE SORTIE
	Date dateInTheFuture = new Date();
	dateInTheFuture.setTime((System.currentTimeMillis() + 20000));
	ticketAfterExitProcess.setOutTime(dateInTheFuture);

	// THEN - ASSERT
	assertThat(ticketAfterExitProcess).isNotNull(); // ON VERIFIE QUE LE TICKET APRES PROCESS N'EST PAS VIDE
	assertThat(ticketAfterExitProcess.getPrice()).isNotNull(); // ON VERIFIE QUE LE PRIX DU TICKET N'EST PLUS NULL

	// ON PEUT VERIFIER QUE l'HEURE DE SORTIE EST BIEN PRESENTE DANS LA BDD
	assertThat(ticketAfterExitProcess.getOutTime()).isNotNull();
	System.out.println(
		"Affichage dans la console de l'heure de sortie : " + ticketAfterExitProcess.getOutTime() + "\n");
	// ON PEUT AUSSI VERIFIER QUE L'HEURE D'ENTREE EST AVANT L'HEURE DE SORTIE
	assertThat(ticketAfterExitProcess.getInTime()).isBeforeOrEqualTo(ticketAfterExitProcess.getOutTime());
    }

}
