package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintStream;
import java.util.Calendar;
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
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final Date IN_TIME = new Date(2023, Calendar.OCTOBER, 20, 01, 00);
    private final Date OUT_TIME = new Date(2023, Calendar.OCTOBER, 20, 02, 00);


    @BeforeEach
    void setUpPerTest() {
        try {
            lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            lenient().when(inputReaderUtil.readSelection()).thenReturn(1);

            parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            ticket = new Ticket();
            ticket.setInTime(IN_TIME);
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");

            vehicleRegNumber = "ABCDEF";

            System.setOut(new PrintStream(outputStreamCaptor));

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleOk(){
        when(ticketDAO.getnumberOfTickets(anyString())).thenReturn(1);
        when(ticketDAO.getTicket(vehicleRegNumber, false)).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

        parkingService.processExitingVehicle(OUT_TIME);

        verify(ticketDAO, Mockito.times(1)).getnumberOfTickets(anyString());
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void processIncomingCarTest(){
        try {
            parkingSpot.setId(1);
            when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

            when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
            when(inputReaderUtil.readSelection()).thenReturn(1);

            // Calling the method to test
            parkingService.processIncomingVehicle(IN_TIME);

            // Verifying the interactions
            verify(parkingSpotDAO, times(1)).updateParking(parkingSpot);
            verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));

            // Additional assertions based on the business logic in the method
            assertFalse(parkingSpot.isAvailable());
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void processIncomingBikeTest(){
        try {
            parkingSpot.setId(1);
            parkingSpot.setParkingType(ParkingType.BIKE);
            when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

            when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
            when(inputReaderUtil.readSelection()).thenReturn(2);

            // Calling the method to test
            parkingService.processIncomingVehicle(IN_TIME);

            // Verifying the interactions
            verify(parkingSpotDAO, times(1)).updateParking(parkingSpot);
            verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));

            // Additional assertions based on the business logic in the method
            assertFalse(parkingSpot.isAvailable());
        } catch (Exception e) {
            fail("Exception not expected: " + e.getMessage());
        }
    }

    @Test
    public void processIncomingVehicleRecurrentCustomerMessageOK() throws Exception{
        parkingSpot.setId(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);

            when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

            when(ticketDAO.getnumberOfTickets(anyString())).thenReturn(3);

            // Calling the method to test
            parkingService.processIncomingVehicle(IN_TIME);

            // Verifying the interactions
            verify(inputReaderUtil, times(1)).readSelection();
            verify(parkingSpotDAO, times(1)).updateParking(parkingSpot);
            verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
            assertTrue(outputStreamCaptor.toString()
                .trim().contains("Happy to see you again ! As a regular user of our parking you will have a 5% discount"));
    }

    @Test
    public void processIncomingVehicleWrongTypeKo() throws Exception{
        parkingSpot.setId(1);
        when(inputReaderUtil.readSelection()).thenReturn(5);

        try{
            parkingService.processIncomingVehicle(IN_TIME);
        } catch (Exception e){
            assertTrue(outputStreamCaptor.toString()
                    .trim().contains("Error parsing user input for type of vehicle"));
        }

        // Verifying the interactions
        verify(parkingSpotDAO, times(0)).updateParking(parkingSpot);
        verify(ticketDAO, times(0)).saveTicket(any(Ticket.class));

    }

    @Test
    public void getNextSpotNoPlaceAvailableKo(){
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);

        try {
            parkingService.getNextParkingNumberIfAvailable();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Error fetching parking number from DB. Parking slots might be full"));
        }
    }

}
