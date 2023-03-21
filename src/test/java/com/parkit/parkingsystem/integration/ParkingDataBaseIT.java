package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

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
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        int slotAvant = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR); // pour enregistrer le slot avant
        parkingService.processIncomingVehicle();
        //(done) check that a ticket is actualy saved in DB and Parking table is updated with availability
        
        int slotApres = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertNotEquals(slotAvant, slotApres);
        assertNotNull( ticketDAO.getTicket("ABCDEF")); //check that a ticket is actualy saved in DB 
    }
    
    @Test
    public void testParkingACarTwice(){

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        int slotAvant = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR); // pour enregistrer le slot avant
        parkingService.processIncomingVehicle();
        int slotApres = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        parkingService.processIncomingVehicle();
        int slotApres2 = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        assertNotEquals(slotAvant, slotApres);
        assertEquals(slotApres, slotApres2);

    }

    @Test
    public void testParkingLotExit(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //testParkingACar();
        parkingService.processIncomingVehicle();
        try {//pause ajoutee entre le incoming/exiting sinon base renvoit ticket null parfois
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        parkingService.processExitingVehicle();
        
        //(done) check that the fare generated and out time are populated correctly in the database

        Ticket ticket = ticketDAO.getTicketWithOutTime("ABCDEF");
        assertNotNull(ticket.getOutTime());
        assertEquals(0, ticket.getPrice());  
    }

    
    @Test
    public void testParking5Percent(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        parkingService.processExitingVehicle();
        parkingService.processIncomingVehicle();

        int count = ticketDAO.getCountPreviousOccurence("ABCDEF");
        assertTrue(count>0);
    }
}
