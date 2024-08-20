package com.parkit.parkingsystem;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private ParkingService parkingService;

    @Mock
    private InputReaderUtil inputReaderUtil;
    @Mock
    private ParkingSpotDAO parkingSpotDAO;
    @Mock
    private TicketDAO ticketDAO;
    @Mock
    private FareCalculatorService fareCalculatorService;

    @BeforeEach
    public void setUpPerTest() throws Exception {
        // Utilisation de lenient pour éviter les erreurs d'unnecessary stubbing
        lenient().when(inputReaderUtil.readSelection()).thenReturn(1); // Simulate car selection
        lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF"); // Utilisé dans les tests
        lenient().when(ticketDAO.getTicket(anyString())).thenReturn(new Ticket()); // Retourne un nouveau ticket par défaut
        lenient().when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false); // Par défaut, l'update échoue dans processExitingVehicleTestUnableUpdate

        // Initialisation de ParkingService
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, fareCalculatorService);
    }

    @Test
    public void processExitingVehicleTest() throws Exception {
        // Arrange
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000))); // 1 heure avant
        ticket.setOutTime(new Date());
        ticket.setVehicleRegNumber("ABCDEF");

        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(ticketDAO.getNbTicket(anyString())).thenReturn(2); // Utilisateur régulier

        // Act
        parkingService.processExitingVehicle();

        // Assert
        verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
        verify(fareCalculatorService, times(1)).calculateFare(any(Ticket.class), eq(true));
    }

    /* LuDo - Etape 5 - Parking service test >90% */
    @Test
    public void processExitingVehicleTestUnableUpdate() throws Exception {
        // Arrange
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000))); // 1 heure
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");

        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.getNbTicket(anyString())).thenReturn(2); // Indicate si utilisateur régulier
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false); // Simuler une erreur d'update

        // Act
        parkingService.processExitingVehicle();

        // Assert
        verify(parkingSpotDAO, never()).updateParking(any(ParkingSpot.class)); // Vérifier que updateParking n'est pas appelé
        verify(ticketDAO, times(1)).updateTicket(any(Ticket.class)); // Vérifier que updateTicket est bien appelé
    }

    @Test
    public void testGetNextParkingNumberIfAvailable() throws Exception {
        // Arrange
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

        // Act
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        // Assert
        assertNotNull(parkingSpot);
        assertEquals(1, parkingSpot.getId());
        assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
        assertTrue(parkingSpot.isAvailable());
        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));
    }

    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
        // Arrange
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0); // Simule un numéro de parking invalide ou indisponible

        // Act
        ParkingSpot result = parkingService.getNextParkingNumberIfAvailable(); // Utilise le type de véhicule par défaut ou valide

        // Assert
        assertNull(result); // Vérifie que le résultat est nul, indiquant qu'aucun espace de stationnement n'est disponible
    }

    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
        // Arrange
        // Simule que aucune place n'est disponible
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0); 

        // Act
        ParkingSpot result = parkingService.getNextParkingNumberIfAvailable(); // Pas d'argument, car la méthode ne prend pas de paramètre

        // Assert
        assertNull(result); // Vérifie que le résultat est nul, car aucune place n'est disponible
    }

    @Test
    public void testProcessIncomingVehicle() throws Exception {
        // Arrange
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(new Date());
        
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
        when(ticketDAO.getNbTicket(anyString())).thenReturn(1); // Utilisateur regulier

        // Act
        parkingService.processIncomingVehicle();

        // Assert
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class)); // Verifier parking spot 
        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class)); // Verifier ticket
        
    }

    /* LuDo Parking Service >90% */
    @Test
    public void testProcessIncomingVehicleWhenNoAvailableParkingSpot() throws Exception {
        // Arrange
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0); // Simule aucune place disponible

        // Act
        parkingService.processIncomingVehicle();

        // Assert
        verify(parkingSpotDAO, never()).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, never()).saveTicket(any(Ticket.class));
    }
    @Test
public void testGetNextParkingNumberIfAvailableWithInvalidType() {
    // Arrange
    when(inputReaderUtil.readSelection()).thenReturn(3); // Valeur invalide
    
    // Act & Assert
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
        parkingService.getNextParkingNumberIfAvailable();
    });

    // Optionally check the message of the thrown exception
    assertEquals("L'entrée saisie est invalide", thrown.getMessage());
}
@Test
public void testProcessExitingVehicleWhenTicketNotFound() throws Exception {
    // Arrange
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABC123");
    when(ticketDAO.getTicket("ABC123")).thenReturn(null);

    // Act
    parkingService.processExitingVehicle();

    // Assert
    verify(parkingSpotDAO, never()).updateParking(any(ParkingSpot.class));
    verify(ticketDAO, never()).updateTicket(any(Ticket.class));
}
@Test
public void processIncomingVehicleTestWithNoAvailableParkingSpot() {
    // Arrange
    when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0); // Simule aucune place disponible

    // Act
    parkingService.processIncomingVehicle();

    // Assert
    verify(parkingSpotDAO, never()).updateParking(any(ParkingSpot.class));
    verify(ticketDAO, never()).saveTicket(any(Ticket.class));
}

}