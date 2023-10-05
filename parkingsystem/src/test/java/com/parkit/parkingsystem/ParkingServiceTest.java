package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock(lenient = true)
    private static InputReaderUtil inputReaderUtil;
    @Mock(lenient = true)
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock(lenient = true)
    private static TicketDAO ticketDAO;

    // ...

    @BeforeEach
    private void setUpPerTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000))); // 1 hour ago
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            // when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            parkingService.processExitingVehicle();

            verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void processIncomingVehicleTest() {
        try {
            // Arrange
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
            when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(inputReaderUtil.readSelection()).thenReturn(1); 

            // Act
            parkingService.processIncomingVehicle();

            // Assert
            verify(parkingSpotDAO, times(1)).updateParking(argThat(argument -> argument.getId() == 1 &&
                    argument.getParkingType() == ParkingType.CAR &&
                    !argument.isAvailable()));
            verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void processExitingVehicleTestUnableUpdate() {
        try {
            // Arrange
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000))); // 1 hour ago
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

            // Act
            parkingService.processExitingVehicle();

            // Assert
            verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void testGetNextParkingNumberIfAvailable() {
        try {
            // Arrange
            ParkingType parkingType = ParkingType.CAR;
            int parkingNumber = 1;
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(parkingSpotDAO.getNextAvailableSlot(parkingType)).thenReturn(parkingNumber);

            // Act
            ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

            // Assert
            assertNotNull(parkingSpot, "Parking spot should not be null");
            assertEquals(parkingNumber, parkingSpot.getId(), "Parking number should be 1");
            assertEquals(parkingType, parkingSpot.getParkingType(), "Parking type should be CAR");
            assertTrue(parkingSpot.isAvailable(), "Parking spot should be available");

        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
        try {
            // Arrange
            when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);
            when(inputReaderUtil.readSelection()).thenReturn(1);

            // Act
            ParkingSpot result = parkingService.getNextParkingNumberIfAvailable();

            // Assert
            assertNull(result, "Le résultat doit être null car aucun spot n'est disponible");
        } catch (Exception e) {
            fail("Aucune exception ne doit être lancée");
        }
    }

    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
        try {
            // Arrange
            when(inputReaderUtil.readSelection()).thenReturn(3);

            // Act
            ParkingSpot result = parkingService.getNextParkingNumberIfAvailable();

            // Assert
            assertNull(result, "Le résultat doit être null en raison d'une entrée utilisateur incorrecte");
        } catch (Exception e) {
            fail("Aucune exception ne doit être lancée");
        }
    }

}
