package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        
        int slotApres = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertNotEquals(slotAvant, slotApres);//check that parking table is updated with availability
        assertNotNull( ticketDAO.getTicket("ABCDEF")); //check that a ticket is actualy saved in DB 
    }
    
    @Test
    public void testParkingACarTwice(){

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        int slotAvant = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR); // on enregistre la valeur du prochain slot disponible
        parkingService.processIncomingVehicle();   // on fait rentrer le véhicule
        int slotApres = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR); // on enregistre la valeur du prochain slot disponible
        assertNotEquals(slotAvant, slotApres);  // on verifie que les 2 valeurs de slot sont bien différents (donc le vehicule a été garé)
        parkingService.processIncomingVehicle(); // on essaie à nouveau de faire rentrer le véhicule
        int slotApres2 = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);  //on enregistre la valeur du prochain slot disponible

        assertEquals(slotApres, slotApres2);  // on s'assure cette fois que les valeurs sont les memes (donc que le véhicule n'a pas pu etre garé 2 fois de suite sans sortir)

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
        
        Ticket ticket = ticketDAO.getTicketWithOutTime("ABCDEF");
        assertNotNull(ticket.getOutTime()); //vérifier que le temps de sortie est renseigné correctement dans la base de données
        assertEquals(0, ticket.getPrice()); // vérifier que la tarif générée est renseigné dans la base de données
      
        }
	}