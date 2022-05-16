package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    @InjectMocks
    private ParkingService parkingService;

    @Mock
    private InputReaderUtil inputReaderUtil;
    @Mock
    private ParkingSpotDAO parkingSpotDAO;
    @Mock
    private TicketDAO ticketDAO;

    private void setUpPerTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleTest(){
        setUpPerTest();
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void processExitingVehicleTestNotUpdateTicketTest() throws Exception {

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        parkingService.processExitingVehicle();

        verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
    }


    @Test
    public void processExitingVehicleTestThrowsExceptionTest(){
        //GIVEN
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenThrow(IllegalArgumentException.class);
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        parkingService.processExitingVehicle();

        //THEN
        verify(ticketDAO, Mockito.times(0)).getTicket(anyString());
        verify(ticketDAO, Mockito.times(0)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void getNextParkingNumberIfAvailableForCarTest(){
        //GIVEN
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        ParkingSpot expectedCarParkingSpot = new ParkingSpot(2,ParkingType.CAR,true);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(2);

        //WHEN
        ParkingSpot actualCarParkingSpot = parkingService.getNextParkingNumberIfAvailable();

        //THEN
        verify(inputReaderUtil).readSelection();
        verify(parkingSpotDAO).getNextAvailableSlot(any());
        assertEquals(expectedCarParkingSpot, actualCarParkingSpot);
    }

    @Test
    public void getNextParkingNumberIfAvailableForBikeTest(){
        //GIVEN
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        ParkingSpot expectedBikeParkingSpot = new ParkingSpot(5,ParkingType.BIKE,true);
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(5);

        //WHEN
        ParkingSpot actualBikeParkingSpot = parkingService.getNextParkingNumberIfAvailable();

        //THEN
        verify(inputReaderUtil).readSelection();
        verify(parkingSpotDAO).getNextAvailableSlot(any());
        assertEquals(expectedBikeParkingSpot, actualBikeParkingSpot);
    }

    @Test
    public void getNextParkingNumberIfAvailableThrowsExceptionTest() {
        //GIVEN
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(0);

        //WHEN
        ParkingSpot actualParkingSpot = parkingService.getNextParkingNumberIfAvailable();

        //THEN
        verify(inputReaderUtil).readSelection();
        verify(parkingSpotDAO).getNextAvailableSlot(any());
        assertNull(actualParkingSpot);
    }

    @Test
    public void getNextParkingNumberIfAvailableThrowsIllegalArgumentExceptionTest() {
        //GIVEN
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        when(inputReaderUtil.readSelection()).thenReturn(4);

        //WHEN
        ParkingSpot actualParkingSpot = parkingService.getNextParkingNumberIfAvailable();

        //THEN
        verify(inputReaderUtil).readSelection();
        assertNull(actualParkingSpot);
    }

    @Test
    public void processIncomingVehicleTest(){
        //GIVEN
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(1);
        when(inputReaderUtil.readSelection()).thenReturn(1);

        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        parkingService.processIncomingVehicle();

        //THEN
        verify(inputReaderUtil,Mockito.times(1)).readVehicleRegistrationNumber();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    }

    @Test
    public void processIncomingVehicleThrowsExceptionTest() {
        //GIVEN
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(1);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenThrow(IllegalArgumentException.class);

        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        parkingService.processIncomingVehicle();

        //THEN
        verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
    }
}
