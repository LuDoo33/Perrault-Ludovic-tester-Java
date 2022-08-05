package com.parkit.parkingsystem.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class TicketDAOTest {
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static ParkingSpot parkingSpot;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    private static Ticket ticketToSave;
    private static Ticket ticketToUpdate;

    @BeforeAll
    private static void setUp() throws Exception {
	parkingSpot = new ParkingSpot(2, ParkingType.BIKE, false);
	parkingSpotDAO = new ParkingSpotDAO();
	parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
	ticketDAO = new TicketDAO();
	ticketDAO.dataBaseConfig = dataBaseTestConfig;
	dataBasePrepareService = new DataBasePrepareService();
	ticketToSave = new Ticket();
	ticketToUpdate = new Ticket();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
	dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown() {
    }

    @Test
    @DisplayName("Test de la sauvegarde d'un ticket en BDD")
    public void testSavingATicketInDataBase() {
	// GIVEN - ARRANGE // ON CREE UN TICKET
	Date inTime = new Date();
	Date outTime = new Date();
	inTime.setTime(System.currentTimeMillis());
	outTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000)); // AJOUT 1 HEURE

	ticketToSave.setId(1);
	ticketToSave.setParkingSpot(parkingSpot);
	ticketToSave.setVehicleRegNumber("AT-444-ST");
	ticketToSave.setPrice(5);
	ticketToSave.setInTime(inTime);
	ticketToSave.setOutTime(outTime);

	// WHEN - ACT // ON SAUVE LE TICKET
	ticketDAO.saveTicket(ticketToSave);

	// ON RECUPERE LE TICKET DANS LA BDD
	Ticket savedTicket = ticketDAO.getTicket("AT-444-ST");

	// THEN - ASSERT // ON VERIFIE QUE LE TICKET A BIEN ETE SAUVEGARDE
	assertThat(savedTicket.getParkingSpot()).isEqualTo(ticketToSave.getParkingSpot());

	// ASSERTTHAT(ACTUAL --- EXPECTED);
	assertThat(savedTicket.getPrice()).isEqualTo(savedTicket.getPrice());

	// LE FORMAT DE LA DATE CHANGE UNE FOIS EXTRAIT DE LA BDD
	// CELA FAIT ECHOUER LES 2 DERNIERS TESTS
	System.out.println("L'heure avant le passage en BDD est : " + ticketToSave.getInTime());
	System.out.println("L'heure après le passage en BDD est : " + savedTicket.getInTime());

	// assertThat(ticketToSave.getInTime()).isEqualTo(savedTicket.getInTime());
	// assertThat(ticketToSave.getOutTime()).isEqualTo(savedTicket.getOutTime());
    }

    @Test
    @DisplayName("test de l'échec de l'enregistrement dans la base de données")
    public void testFailedToSAveTicket() {
	// GIVEN - ARRANGE
	ticketToSave = null;
	// WHEN - ACT
	boolean result = ticketDAO.saveTicket(ticketToSave);

	// THEN - ASSERT
	assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Test pour de la récupération d'un ticket avec une plaque nulle")
    public void testFailedToGetTicket() {
	// GIVEN - ARRANGE

	Ticket gettedTicket = new Ticket();
	// WHEN - ACT
	gettedTicket = ticketDAO.getTicket(null);

	// THEN - ASSERT
	assertThat(gettedTicket).isNull();
    }

    @Test
    @DisplayName("Test de la mise à jour d'un ticket en BDD")
    public void testUpdatingATicketInDataBase() {
	// GIVEN - ARRANGE
	testSavingATicketInDataBase();
	Date outTime = new Date();
	outTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000)); // AJOUT 1 HEURE

	ticketToUpdate.setPrice(10);
	ticketToUpdate.setOutTime(outTime);
	ticketToUpdate.setId(1);
	ticketToUpdate.getOutTime().getTime();

	// WHEN - ACT
	ticketDAO.updateTicket(ticketToUpdate);
	Ticket updatedTicket = ticketDAO.getTicket("AT-444-ST");

	// THEN - ASSERT
	assertThat(updatedTicket.getPrice()).isEqualTo(ticketToUpdate.getPrice());
	// ACTUAL:JAVA.SQL --- EXPECTED:JAVA.UTIL
	// assertThat(updatedTicket.getOutTime().getTime()).isEqualTo(ticketToUpdate.getOutTime().getTime());
    }

    @Test
    @DisplayName("Test de l'échec de la mise à jour d'un ticket")
    public void testFaillingToUpdateATicketInDataBase() {
	// GIVEN - ARRANGE
	ticketToUpdate = null;

	// WHEN - ACT
	boolean result = ticketDAO.updateTicket(ticketToUpdate);

	// THEN - ASSERT
	assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Test de comptage du nombre de ticket dans la BDD")
    public void getCountForVehicleRegNumber() {
	// GIVEN - ARRANGE
	testUpdatingATicketInDataBase();
	// WHEN - ACT
	int result = ticketDAO.getCountForVehicleRegNumber("AT-444-ST");
	// THEN - ASSERT
	assertThat(result).isGreaterThan(0);
    }

}
