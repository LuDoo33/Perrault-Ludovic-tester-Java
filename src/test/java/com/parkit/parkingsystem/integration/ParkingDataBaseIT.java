package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.parkit.parkingsystem.constants.Fare;

import java.util.Calendar;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static final DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();

    @Mock
    private static InputReaderUtil inputReaderUtil;
    private static FareCalculatorService fareCalculatorService;

    @BeforeAll
    public static void setUp() {
        fareCalculatorService = new FareCalculatorService(ticketDAO);
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
       // dataBasePrepareService.clearDataBaseEntries();
    }
   /* private void insertTestData() {
        Ticket ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(5.0);
        ticket.setInTime(new Date());
        ticket.setOutTime(null);
        ticketDAO.saveTicket(ticket);
    }*/

    @BeforeEach
    public void setUpPerTest() throws Exception {
        lenient().when(inputReaderUtil.readSelection()).thenReturn(1);
        lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        //dataBasePrepareService.clearDataBaseEntries();
       // insertTestData();  // insérez les données de test après avoir nettoyé la base de données
    }

    @AfterAll
    public static void tearDown() {
       // dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    @DisplayName("Vérifie qu'un ticket est bien enregistré dans la base de données et que la table de stationnement est mise à jour avec la disponibilité")
    public void testParkingACar() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        // Simule une durée de stationnement
        Ticket ticketToUpdate = ticketDAO.getTicket("ABCDEF");
        Calendar date = Calendar.getInstance();
        date.add(Calendar.HOUR, -1); // Simule un stationnement d'une heure
        ticketToUpdate.setInTime(date.getTime());
        ticketDAO.updateTicket(ticketToUpdate);

        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        double currentRatePerHour = Fare.getCarRatePerHour();

        // Calcule la durée réelle en heures
        long durationInMillis = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
        double actualDurationInHours = durationInMillis / (1000.0 * 60 * 60);

        // Calcule le tarif attendu
        double expectedFare = currentRatePerHour * actualDurationInHours * 0.95;

        assertNotNull(ticket, "Le ticket ne doit pas être null");
        assertNotNull(ticket.getInTime(), "L'heure d'entrée doit être renseignée");
        assertEquals(expectedFare, ticket.getPrice(), 0.001, "Le tarif du ticket doit correspondre à la valeur attendue");
        assertFalse(ticket.getParkingSpot().isAvailable(), "La place de parking doit être marquée comme non disponible");
    }

    @Test
    @DisplayName("Vérifie que le tarif généré et l'heure de sortie sont correctement renseignés dans la base de données après la sortie du parking")
    public void testParkingLotExit() {
        // Préparation : Simule l'entrée d'une voiture dans le parking
        testParkingACar();

        // Simule une durée de stationnement
        Ticket ticketToUpdate = ticketDAO.getTicket("ABCDEF");
        Calendar date = Calendar.getInstance();
        date.add(Calendar.HOUR, -1); // Simule un stationnement d'une heure
        ticketToUpdate.setInTime(date.getTime());
        ticketDAO.updateTicket(ticketToUpdate);

        // Action : Traite la sortie d'une voiture du parking
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();

        // Vérification : Récupère le ticket et vérifie les détails de la sortie
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticket, "Le ticket ne doit pas être null");
        assertNotNull(ticket.getOutTime(), "L'heure de sortie doit être renseignée");

        // Calcule la durée réelle en heures
        long durationInMillis = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
        double actualDurationInHours = durationInMillis / (1000.0 * 60 * 60);

        // Calcule le tarif attendu
        double expectedFare = Fare.getCarRatePerHour() * actualDurationInHours * 0.95;
        assertEquals(expectedFare, ticket.getPrice(), 0.001, "Le tarif doit correspondre à la valeur attendue après la sortie du parking");
    }

    @Test
    @DisplayName("Teste la sortie d'un utilisateur récurrent du parking, vérifie le calcul du tarif avec la remise et la disponibilité de la place de parking")
    public void testParkingLotExitRecurringUser() throws Exception {
        double currentRatePerHour = Fare.getCarRatePerHour();
        String vehicleRegNumber = "ABCDEF";

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
        when(inputReaderUtil.readSelection()).thenReturn(1);

        // Simulate a previous ticket for the vehicle to mark it as a recurring user
        Ticket previousTicket = new Ticket();
        previousTicket.setInTime(new Date());
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        previousTicket.setParkingSpot(parkingSpot);
        previousTicket.setVehicleRegNumber(vehicleRegNumber);
        ticketDAO.saveTicket(previousTicket);

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        // Simulate vehicle entry
        parkingService.processIncomingVehicle();

        // Simulate a 2-hour parking duration
        Ticket ticketToUpdate = ticketDAO.getTicket(vehicleRegNumber);
        Calendar date = Calendar.getInstance();
        date.add(Calendar.HOUR, -1);
        ticketToUpdate.setInTime(date.getTime());
        ticketDAO.updateTicket(ticketToUpdate);

        // Simulate vehicle exit
        parkingService.processExitingVehicle();

        // Retrieve the updated ticket
        Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);

        // Calculate the actual duration in hours
        long durationInMillis = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
        double actualDurationInHours = durationInMillis / (1000.0 * 60 * 60);

        // Calculate the expected fare
        double expectedFare = currentRatePerHour * actualDurationInHours * 0.95; // 5% discount for recurring users

        // Assertions
        assertNotNull(ticket, "Le ticket ne doit pas être null");
        assertNotNull(ticket.getOutTime(), "L'heure de sortie doit être renseignée");
        assertEquals(expectedFare, ticket.getPrice(), 0.001, "Le tarif doit avoir une remise de 5%");
        assertEquals(ticket.getParkingSpot().isAvailable(), false, "La place de parking doit être marquée comme disponible");
    }
}
