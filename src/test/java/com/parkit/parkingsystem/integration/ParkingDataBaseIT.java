package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static String registrationNumber = "ABCDEF";

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
        // Simulation de la saisie utilisateur : CAR
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(registrationNumber);
        //Nettoyage de la base de donnée
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    @DisplayName("Test de sortie du véhicule")
    public void testParkingLotExitIt() throws Exception {
        //Execution du testParkingACarIt pour simuler un nouveau véhicule
        testParkingACarIt();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        // Pause du thread pour obtenir une date differente entre  l'arrivé du véhicule et la sortie de celui-ci
       // Thread.sleep(1000);
        // Processus de sortie du véhicule.
        parkingService.processExitingVehicle();
        ticketDAO.getTicket(registrationNumber);

        int availableSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        // Vérication que le prix du ticket est à 0
        assertEquals(0,ticketDAO.getTicket(registrationNumber).getPrice());
        // Vérification que la place de parking est disponible.
        assertEquals(1, availableSlot);
        // Vérification qu'une date de sortie existe'
        assertNotNull(ticketDAO.getTicket(registrationNumber).getOutTime());
    }


    @Test
    @DisplayName("Test de d'entrée du véhicule")
    public void testParkingACarIt() throws Exception {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        // Processus d"entrée du véhicule.
        parkingService.processIncomingVehicle();
        String vehiculeNumber = inputReaderUtil.readVehicleRegistrationNumber();
        // Vérification entre le numéro du ticket fournis et celui présent en base de donnée
        assertEquals(vehiculeNumber, ticketDAO.getTicket(registrationNumber).getVehicleRegNumber());
    }


}
