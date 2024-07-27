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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static ParkingService parkingService;

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
    	when(inputReaderUtil.readSelection()).thenReturn(1); //Return ParkingType.CAR
    	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
           
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
    	//GIVEN
    	parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    	
    	// WHEN
        parkingService.processIncomingVehicle();
        
        // THEN
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        //Check that a ticket is actually saved in DB :
        assertNotNull(ticket);
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());
        //and Parking table is updated with availability :
        ParkingSpot parkingSpot = ticket.getParkingSpot();
        assertNotNull(parkingSpot);
        assertFalse(parkingSpot.isAvailable());
    }

    @Test
    public void testParkingLotExit(){
      	testParkingACar(); //Entry
      	parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();//Exit
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        //Check that the fare generated and out time are populated correctly in the database
        assertNotNull(ticket.getOutTime());
        assertTrue(ticket.getPrice()==0); //Because stayed in the parking less than 30 minutes so free
    }
    
    @Test
    public void testParkingLotExitRecurringUser(){
    	testParkingLotExit();//Was once in the parking
    	testParkingACar(); //Enter the second time
        parkingService.processExitingVehicle();//exit the second time
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        //Check the ticket price for a recurring user :
        assertTrue(ticket.getPrice()==0); //Because stayed in the parking less than 30 minutes so free       
    }

}
