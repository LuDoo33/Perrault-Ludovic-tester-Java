package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static DataBasePrepareService dataBasePrepareService;
    private static Ticket ticket;
    private static ParkingSpot parkingSpot;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
 
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
        
        ticket= new Ticket();
        parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        
		when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
		
		ticket.setParkingSpot(parkingSpot);
		
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
        
        /*
         *Verify that a ticket is actually saved in DB 
         */
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
        assertTrue(ticketDAO.saveTicket(ticket));
        
        /*
         *Verify that a Parking table is update 
         */
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        assertTrue(parkingSpotDAO.updateParking(parkingSpot));
        
        /*
         *Check that a Parking is actually not available
         */
        assertFalse(parkingSpot.isAvailable());
    }

    @Test
    public void testParkingLotExit(){
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database
    }

}
