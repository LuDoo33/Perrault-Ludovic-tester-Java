package com.parkit.parkingsystem.integration;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

public class ParkingDataBaseIT {

    private ParkingSpotDAO parkingSpotDAO;
    private TicketDAO ticketDAO;
    private InputReaderUtil inputReaderUtil;
    private FareCalculatorService fareCalculatorService;
    private ParkingService parkingService;

    @BeforeEach
    public void setUp() throws Exception {
        // Initialisation des mocks
        parkingSpotDAO = mock(ParkingSpotDAO.class);
        ticketDAO = mock(TicketDAO.class);
        inputReaderUtil = mock(InputReaderUtil.class);
        fareCalculatorService = mock(FareCalculatorService.class);

        // Création du service avec les mocks
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, fareCalculatorService);

        // Configuration des mocks pour les tests
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABC123");
        when(inputReaderUtil.readSelection()).thenReturn(1); // Simule la sélection du type de véhicule
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(1); // Simule la disponibilité de la place de parking
        when(ticketDAO.getTicket("ABC123")).thenReturn(createSampleTicket()); // Simule un ticket retourné
        when(ticketDAO.getNbTicket("ABC123")).thenReturn(2); // Simule le nombre de tickets pour utilisateur récurrent
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true); // Simule la mise à jour réussie du ticket
    }

    @Test
    public void testParkingACar() throws Exception {
        // Simule l'entrée d'un véhicule
        parkingService.processIncomingVehicle();

        // Vérifiez que la place de parking est mise à jour
        verify(parkingSpotDAO).updateParking(argThat(parkingSpot ->
            parkingSpot.getId() == 1 && !parkingSpot.isAvailable()
        ));

        // Vérifiez que le ticket est sauvegardé
        verify(ticketDAO).saveTicket(any(Ticket.class));
    }

    @Test
    public void testParkingLotExit() throws Exception {
        // Simule la sortie d'un véhicule
        parkingService.processExitingVehicle();

        // Vérifiez que la place de parking est mise à jour
        verify(parkingSpotDAO).updateParking(argThat(parkingSpot ->
            parkingSpot.getId() == 1 && parkingSpot.isAvailable()
        ));

        // Vérifiez que le ticket est mis à jour
        Ticket ticket = ticketDAO.getTicket("ABC123");
        assertNotNull(ticket, "Ticket should not be null");
        assertNotNull(ticket.getOutTime(), "Out time should not be null");

        // Assurez-vous que le ticket a été mis à jour dans la base de données
        verify(ticketDAO).updateTicket(any(Ticket.class));
    }

    @Test
    public void testParkingLotExitRecurringUser() throws Exception {
        // Simule le nombre de tickets pour l'utilisateur récurrent
        when(ticketDAO.getNbTicket("ABC123")).thenReturn(2); // Plus de 1 ticket pour l'utilisateur

        // Simule la sortie d'un véhicule
        parkingService.processExitingVehicle();

        // Vérifiez que la place de parking est mise à jour
        verify(parkingSpotDAO).updateParking(argThat(parkingSpot ->
            parkingSpot.getId() == 1 && parkingSpot.isAvailable()
        ));

        // Vérifiez que le ticket est mis à jour
        Ticket ticket = ticketDAO.getTicket("ABC123");
        assertNotNull(ticket, "Ticket should not be null");
        assertNotNull(ticket.getOutTime(), "Out time should not be null");

        // Assurez-vous que le ticket a été mis à jour dans la base de données
        verify(ticketDAO).updateTicket(any(Ticket.class));
    }

    private Ticket createSampleTicket() {
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, true)); // Assurez-vous d'utiliser ParkingType.CAR
        ticket.setVehicleRegNumber("ABC123");
        ticket.setPrice(0);
        ticket.setInTime(new Date());
        ticket.setOutTime(null);
        return ticket;
    }
}
