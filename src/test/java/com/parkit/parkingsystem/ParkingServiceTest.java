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

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    private ParkingSpot parkingSpot;
    private Ticket ticket;

    private String vehicleRegNumber;


    @BeforeEach
    void setUpPerTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");

            vehicleRegNumber = "ABCDEF";

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicle(){
        when(ticketDAO.getnumberOfTickets(anyString())).thenReturn(1);
        when(ticketDAO.getTicket(vehicleRegNumber)).thenReturn(ticket);

        parkingService.processExitingVehicle();

        verify(ticketDAO, Mockito.times(1)).getnumberOfTickets(anyString());
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void processIncomingVehicleTest(){
        try {
            parkingSpot.setId(1);
            when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

            when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
            when(inputReaderUtil.readSelection()).thenReturn(1);

            // Calling the method to test
            parkingService.processIncomingVehicle();

            // Verifying the interactions
            verify(parkingSpotDAO, times(1)).updateParking(parkingSpot);
            verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));

            // Additional assertions based on the business logic in the method
            assertEquals(false, parkingSpot.isAvailable());
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

}
