package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.parkit.parkingsystem.App.logger;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.junit.jupiter.api.AfterEach;

import java.util.Calendar;
import java.util.Date;


import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

public class ParkingServiceTest {

    @InjectMocks
    private ParkingService parkingService;

    @Mock
    private TicketDAO ticketDAO;
    @Mock
    private InputReaderUtil inputReaderUtil;
    @Mock
    private ParkingSpotDAO parkingSpotDAO;
    @Mock
    private FareCalculatorService fareCalculatorService;
    @AfterEach
    public void tearDown() {
        // Nettoyage après chaque test
        System.out.println("Nettoyage après le test...");
    }

    @DisplayName("Doit tester le traitement d'un véhicule sortant avec un succès de mise à jour")
    @Test
    public void processExitingVehicleTest() throws Exception {
        // ARRANGE
        Ticket ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(new Date());
        ticket.setOutTime(null);

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
        when(ticketDAO.getNbTicket("ABCDEF")).thenReturn(2);  // Pour simuler un utilisateur récurrent
        when(ticketDAO.updateTicket(ticket)).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);  // Vous pouvez ajouter des conditions plus spécifiques si nécessaire

        doNothing().when(fareCalculatorService).calculateFare(any(Ticket.class), eq(true));  // Simulez le calcul du tarif

        // ACT
        parkingService.processExitingVehicle();

        // ASSERT
        verify(ticketDAO, times(2)).getTicket("ABCDEF");
        verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
        verify(fareCalculatorService, times(0)).calculateFare(any(Ticket.class), eq(true));
    }


    @DisplayName("Doit tester le traitement d'un véhicule entrant")
    @Test
    public void processIncomingVehicleTest() throws Exception {

        // Arrange
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(parkingSpot.getId());

// Act
        parkingService.processIncomingVehicle();

// Assert
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    }


    @DisplayName("Doit tester le traitement d'un véhicule sortant avec un numéro d'immatriculation inexistant")
    @Test
    public void processExitingVehicleWithNonExistentRegNumberTest() {

        try {
            // Arrange
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("NONEXISTENT");
            when(ticketDAO.getTicket(anyString())).thenReturn(null);

            // Act
            parkingService.processExitingVehicle();

            // Assert
            verify(ticketDAO, times(1)).getTicket(anyString());
            verify(ticketDAO, times(0)).updateTicket(any(Ticket.class));
            verify(parkingSpotDAO, times(0)).updateParking(any(ParkingSpot.class));
        } catch (Exception e) {
            fail("Test a échoué à cause de : " + e.getMessage());
        }
    }

    @DisplayName("Doit tester le traitement d'un véhicule sortant avec un numéro d'immatriculation inexistant")
    @Test
    public void processExitingVehicleWithNullRegNumberTest() throws Exception {

            // Arrange
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
            when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(parkingSpot.getId());

        try {
            parkingService.processIncomingVehicle();
        } catch (Exception e) {
            // Assert
            assertTrue(e instanceof IllegalArgumentException);
            assertTrue(e.getMessage().contains("Le numéro d'immatriculation du véhicule ne peut pas être nul ou vide"));
            verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
            verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
        }
    }

    @DisplayName("Doit tester le traitement d'un véhicule sortant avec une mise à jour de ticket échouée")
    @Test
    public void processExitingVehicleWithFailedTicketUpdateTest() throws Exception {
        // Arrange
        String vehicleRegNumber = "ABCDEF";
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setInTime(new Date(System.currentTimeMillis() - 3600 * 1000)); // pour simuler un stationnement d'une heure

        ParkingSpot mockParkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(mockParkingSpot);

        // Simule la récupération du ticket par le numéro d'enregistrement du véhicule
        when(ticketDAO.getTicket(vehicleRegNumber)).thenReturn(ticket);

        // Simule l'échec de la mise à jour du ticket pour n'importe quel objet Ticket
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

        // Simule la lecture du numéro d'enregistrement du véhicule
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);

        // Act
        parkingService.processExitingVehicle();

        // Assert
        verify(ticketDAO, times(1)).getTicket(vehicleRegNumber);
        verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, times(0)).updateParking(mockParkingSpot);

    }

    @DisplayName("Doit tester le traitement d'un véhicule entrant avec un numéro d'immatriculation existant")
    @Test
    public void processIncomingVehicleWithExistingRegNumberTest() throws Exception {
        // Arrange
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setOutTime(null);  // Le véhicule n'est pas encore sorti du parking

        // Act
        parkingService.processIncomingVehicle();

        // Assert
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    }

    @DisplayName("Doit tester le traitement d'un véhicule sortant avec une erreur de mise à jour de la place de parking")
    @Test
    public void TraitementVehiculeSortantAvecErreurMiseAJourPlaceParkingTest() throws Exception {
        // Arrange
        Ticket ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(new Date());
        ticket.setOutTime(new Date());
        ticket.setParkingSpot(parkingSpot);

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
        when(ticketDAO.updateTicket(ticket)).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenThrow(new RuntimeException("Erreur de mise à jour de la place de parking"));

        // Act
        try {
            parkingService.processExitingVehicle();
        } catch (Exception e) {
            // Assert
            assertTrue(e.getMessage().contains("Erreur de mise à jour de la place de parking"));
        }
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    @DisplayName("Doit tester le traitement d'un véhicule sortant avec une erreur de mise à jour de la place de parking")
    @Test
    public void TraitementVehiculeSortanBiketAvecErreurMiseAJourPlaceParkingTest() throws Exception {
        // Arrange
        Ticket ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(new Date());
        ticket.setOutTime(new Date());
        ticket.setParkingSpot(parkingSpot);

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
        when(ticketDAO.updateTicket(ticket)).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenThrow(new RuntimeException("Erreur de mise à jour de la place de parking"));

        // Act
        try {
            parkingService.processExitingVehicle();
        } catch (Exception e) {
            // Assert
            assertTrue(e.getMessage().contains("Erreur de mise à jour de la place de parking"));
        }
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }


    @DisplayName("Doit tester le tarif gratuit pour moins de 30 minutes")
    @Test
    public void freeRateForLessThan30MinutesTest() throws Exception {
        // ARRANGE
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(new Date(System.currentTimeMillis() - (15 * 60 * 1000))); // 15 minutes avant
        ticket.setOutTime(new Date()); // maintenant

        when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);

        // ACT
        parkingService.processExitingVehicle(); // Appelle calculateFare dans la méthode processExitingVehicle()

        // ASSERT
        assertEquals(0, ticket.getPrice());
    }

    @DisplayName("Doit tester le traitement d'un véhicule entrant avec un parking plein")
    @Test
    public void processIncomingVehicleWithFullParkingTest() {
        // Arrange
        when(inputReaderUtil.readSelection()).thenReturn(1);

        // Simule un parking plein
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1); // -1 indique un parking plein

        // Act
        parkingService.processIncomingVehicle();

        // Assert
        verify(parkingSpotDAO, times(0)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, times(0)).saveTicket(any(Ticket.class));
        verify(inputReaderUtil, times(1)).readSelection();
    }

    @DisplayName("Doit tester le tarif gratuit pour exactement 30 minutes")
    @Test
    public void freeRateForExactly30MinutesTest() throws Exception {
        // ARRANGE
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setInTime(new Date(System.currentTimeMillis() - (30 * 60 * 1000))); // 30 minutes avant
        ticket.setOutTime(new Date()); // maintenant

        when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);

        // ACT
        parkingService.processExitingVehicle(); // Appelle calculateFare dans la méthode processExitingVehicle()

        // ASSERT
        assertEquals(0, ticket.getPrice());
    }

    @Test
    @DisplayName("Doit tester le tarif pour un utilisateur récurrent avec exactement une heure (remise de 5%)")
    public void recurrentUserDiscountExactOneHourTest() throws Exception {

        logger.info("Début de ma méthode");

        // ARRANGE
        String vehicleRegNumber = "ABCDEF";
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber(vehicleRegNumber);
        Calendar date = Calendar.getInstance();
        long timeInSecs = date.getTimeInMillis();
        Date inTime = new Date(timeInSecs - (60 * 60 * 1000));
        ticket.setInTime(inTime);

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        ticket.setParkingSpot(parkingSpot);

        // l'utilisateur est un utilisateur récurrent (a déjà des tickets enregistrés)
        when(ticketDAO.getNbTicket(vehicleRegNumber)).thenReturn(2);
        when(ticketDAO.getTicket(vehicleRegNumber)).thenReturn(ticket);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

        // ACT
        parkingService.processExitingVehicle();

        // ASSERT
        double expectedPrice = (Fare.CAR_RATE_PER_HOUR * 0.95);  // Applique la remise de 5%
        assertEquals(expectedPrice, ticket.getPrice(), 0.001);
        verify(ticketDAO, times(2)).getTicket(vehicleRegNumber);
        verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();
        verify(ticketDAO, times(1)).updateTicket(ticket);
        verify(fareCalculatorService, times(0)).calculateFare(any(Ticket.class), eq(true));
        verify(ticketDAO, times(1)).getNbTicket(vehicleRegNumber);

        logger.info("Fin de ma méthode");
    }

    @DisplayName("Test du traitement d'un véhicule entrant avec un type d'entrée valide")
    @Test
    public void processIncomingVehicleValidTypeTest() throws Exception {
        // Arrange
        when(inputReaderUtil.readSelection()).thenReturn(1);  // 1 est pour ParkingType.CAR
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("CAR123");

        ParkingSpot mockParkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

        // Act
        parkingService.processIncomingVehicle();

        // Assert
        verify(inputReaderUtil, times(1)).readSelection(); // Cet appel est effectué à l'intérieur de getVehichleType()
        verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
    }

    @DisplayName("Test du traitement d'un véhicule entrant avec un numéro d'immatriculation null")
    @Test
    public void processIncomingVehicleNullRegNumberTest() throws Exception {
        // Arrange
        ParkingSpot mockParkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

        when(inputReaderUtil.readSelection()).thenReturn(1);  // Sélectionne ParkingType.CAR
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(""); // Numéro d'immatriculation vide
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        when(parkingSpotDAO.updateParking(mockParkingSpot)).thenReturn(true);
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

        // Act
        parkingService.processIncomingVehicle();

        // Assert
        verify(inputReaderUtil, times(1)).readSelection();
        verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();
        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
        verify(parkingSpotDAO, times(1)).updateParking(mockParkingSpot);
    }
}

