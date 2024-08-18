package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

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
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
public void testParkingACar() {
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    
    // Simuler l'entrée d'un véhicule
    parkingService.processIncomingVehicle();
    
    // Vérifier que le ticket a été correctement sauvegardé dans la base de données
    Ticket ticket = ticketDAO.getTicket("ABCDEF");
    assertNotNull(ticket, "Ticket should not be null");
    assertEquals("ABCDEF", ticket.getVehicleRegNumber(), "Vehicle registration number should match");
    
    // Vérifier que la disponibilité du parking a été mise à jour
    ParkingSpot parkingSpot = parkingSpotDAO.getParkingSpot(ticket.getParkingSpot().getId());
    assertNotNull(parkingSpot, "Parking spot should not be null");
    assertFalse(parkingSpot.isAvailable(), "Parking spot should be marked as unavailable");
}


    @Test
public void testParkingLotExit() {
    // Simuler l'entrée d'un véhicule
    testParkingACar();
    
    // Simuler la sortie du véhicule
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    parkingService.processExitingVehicle();
    
    // Vérifier que le tarif généré est correct et que le temps de sortie est correctement enregistré
    Ticket ticket = ticketDAO.getTicket("ABCDEF");
    assertNotNull(ticket, "Ticket should not be null");
    assertNotNull(ticket.getOutTime(), "Exit time should be set");
    
    // Assumer que vous avez une méthode pour calculer le tarif attendu
    double expectedFare = calculateExpectedFare(ticket);
    assertEquals(expectedFare, ticket.getPrice(), "Fare should match the expected amount");
    
    // Vérifier que la place de parking est marquée comme disponible
    ParkingSpot parkingSpot = parkingSpotDAO.getParkingSpot(ticket.getParkingSpot().getId());
    assertNotNull(parkingSpot, "Parking spot should not be null");
    assertTrue(parkingSpot.isAvailable(), "Parking spot should be marked as available");
}
@Test
public void testParkingLotExitRecurringUser() {
    // Simuler l'entrée du véhicule pour un utilisateur récurrent
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("RECUR123");
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    parkingService.processIncomingVehicle();
    
    // Simuler la sortie du véhicule pour un utilisateur récurrent
    ParkingSpot parkingSpot = parkingSpotDAO.getParkingSpot(1); // Utiliser l'ID approprié
    parkingSpot.setAvailable(false);
    parkingSpotDAO.updateParkingSpot(parkingSpot);
    
    parkingService.processExitingVehicle();
    
    // Vérifier que la remise de 5% a été appliquée correctement
    Ticket ticket = ticketDAO.getTicket("RECUR123");
    assertNotNull(ticket, "Ticket should not be null");
    assertNotNull(ticket.getOutTime(), "Exit time should be set");
    
    double expectedFare = calculateExpectedFareWithDiscount(ticket);
    assertEquals(expectedFare, ticket.getPrice(), "Fare should match the expected discounted amount");
    
    // Vérifier que la place de parking est marquée comme disponible
    parkingSpot = parkingSpotDAO.getParkingSpot(ticket.getParkingSpot().getId());
    assertNotNull(parkingSpot, "Parking spot should not be null");
    assertTrue(parkingSpot.isAvailable(), "Parking spot should be marked as available");
}
}
