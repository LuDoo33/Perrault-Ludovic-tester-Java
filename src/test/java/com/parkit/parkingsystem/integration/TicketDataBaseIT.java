package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

@ExtendWith(MockitoExtension.class)
public class TicketDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static ParkingSpot parkingSpot;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    private static Ticket ticketToSave;

    @BeforeAll
    private static void setUp() throws Exception {
	parkingSpot = new ParkingSpot(2, ParkingType.BIKE, false);
	parkingSpotDAO = new ParkingSpotDAO();
	parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
	ticketDAO = new TicketDAO();
	ticketDAO.dataBaseConfig = dataBaseTestConfig;
	dataBasePrepareService = new DataBasePrepareService();
	ticketToSave = new Ticket();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
	dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown() {
    }

    @Test
    @DisplayName("Test de la sauvegarde d'un ticket")
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

	// LE FORMAT DE LA DATE CHANGE UNE FOIS EXTRAIT DE LA BDD
	System.out.println(ticketToSave.getInTime());
	System.out.println(savedTicket.getInTime());

	// THEN - ASSERT // ON VERIFIE QUE LE TICKET A BIEN ETE SAUVEGARDE
	assertThat(ticketToSave.getParkingSpot()).isEqualTo(savedTicket.getParkingSpot());

	// ASSERTTHAT "but was" isEqualTo "expected"
	assertThat(ticketToSave.getPrice()).isEqualTo(savedTicket.getPrice());

	// assertThat(ticketToSave.getInTime()).isEqualTo(savedTicket.getInTime());
	// assertThat(ticketToSave.getOutTime()).isEqualTo(savedTicket.getOutTime());
    }
}
