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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;
    private static ParkingSpot parkingSpot;
    private static Ticket ticket;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUp() {
        try {
            
            parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }
    
    @Test
    public void testProcessIncomingVehicle() throws Exception {
    	//GIVEN
    	//To entry in the if(parkingSpot !=null && parkingSpot.getId() > 0){}, need to go inside getNextParkingNumberIfAvailable();
    	//and to have parkingSpot !=null, we need the type and parkingNumber > 0 here spot 1 :
    	when(inputReaderUtil.readSelection()).thenReturn(1); //Return ParkingType.CAR
    	when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1); //Return spot 1 for 
    	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    	when(ticketDAO.getNbTicket("ABCDEF")).thenReturn(0); //simulating new user
    	when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
    	
    	// WHEN
        parkingService.processIncomingVehicle();
        
        // THEN
        verify(ticketDAO, times(1)).getNbTicket("ABCDEF");
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
        
        assertFalse(parkingSpot.isAvailable());
        assertNotNull(ticket.getParkingSpot());
        assertEquals("ABCDEF",ticket.getVehicleRegNumber());
        assertEquals(0,ticket.getPrice());
        assertNull(ticket.getOutTime());

    }

    @Test
    public void processExitingVehicleTest() throws Exception{
    	// GIVEN
    	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    	when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
    	when(ticketDAO.getNbTicket("ABCDEF")).thenReturn(1); //simulating new user
    	when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
    	when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
    	
    	// WHEN
        parkingService.processExitingVehicle();
        
        // THEN
        verify(ticketDAO, times(1)).getTicket(anyString());
        verify(ticketDAO, times(1)).getNbTicket("ABCDEF");
        verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
                
        assertTrue(parkingSpot.isAvailable());
    }
    
    @Test
    public void processExitingVehicleTestUnableUpdate() throws Exception {
    	// GIVEN
    	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    	when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
    	when(ticketDAO.getNbTicket("ABCDEF")).thenReturn(3); //simulating recurrent user
    	when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
    	
    	// WHEN
        parkingService.processExitingVehicle();
        
        // THEN
        verify(ticketDAO, times(1)).getTicket(anyString());
        verify(ticketDAO, times(1)).getNbTicket("ABCDEF");
        verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, never()).updateParking(any(ParkingSpot.class));
                
        assertFalse(parkingSpot.isAvailable());
    }
    
    @Test
    public void testGetNextParkingNumberIfAvailable() {
    	// GIVEN
    	when(inputReaderUtil.readSelection()).thenReturn(1); //Return ParkingType.CAR
    	when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1); //Return spot 1
    	
    	// WHEN
    	ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        
        // THEN
        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));
                
        assertNotNull(parkingSpot);
        assertEquals(1,parkingSpot.getId());
        assertTrue(parkingSpot.isAvailable());
    }
    
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
    	// GIVEN
    	when(inputReaderUtil.readSelection()).thenReturn(2); //Return ParkingType.BIKE
    	when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0); //Return no spot available
    	
    	// WHEN
    	ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        
        // THEN
        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));
                
        assertNull(parkingSpot);
    }
    
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
    	// GIVEN
    	when(inputReaderUtil.readSelection()).thenReturn(3); //Return Wrong ParkingType
       	
    	// WHEN
    	ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        
        // THEN
                        
        assertNull(parkingSpot);
        //assertThrows(IllegalArgumentException.class, () -> parkingService.getNextParkingNumberIfAvailable());
    }

}
