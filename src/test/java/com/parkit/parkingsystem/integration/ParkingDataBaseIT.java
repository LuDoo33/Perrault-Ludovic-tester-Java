package com.parkit.parkingsystem.integration;

import static org.mockito.Mockito.when;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import junit.framework.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @AfterAll
    private static void tearDown() {
        dataBasePrepareService.clearDataBaseEntries();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    public void testParkingACar() {
        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        parkingService.processIncomingVehicle();
        Ticket testTicket = ticketDAO.getTicket("ABCDEF");

        //THEN
        Assert.assertNotNull(testTicket);
        Assert.assertFalse(testTicket.getParkingSpot().isAvailable());
        Assert.assertEquals(1, testTicket.getParkingSpot().getId());
        Assert.assertEquals("ABCDEF", testTicket.getVehicleRegNumber());
    }

    @Test
    public void testParkingLotExit() {
        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        parkingService.processIncomingVehicle();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        parkingService.processExitingVehicle();
        Ticket testTicket = ticketDAO.getTicket("ABCDEF");

        //THEN
        Assert.assertNotNull(testTicket.getPrice());
        Assert.assertNotNull(testTicket.getOutTime());
    }

    @Test
    public void getNextAvailableSpotCarTest() {
        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        parkingService.processIncomingVehicle();

        //THEN
        Assert.assertEquals(2, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
    }

    @Test
    public void getNextAvailableSpotBikeTest() {
        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        parkingService.processIncomingVehicle();

        //THEN
        Assert.assertEquals(4, parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE));
    }

    @Test
    public void updateParkingSpotCarTest() {
        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ParkingSpot parkingSpot2 = new ParkingSpot(2, ParkingType.CAR, false);

        //WHEN
        parkingService.processIncomingVehicle();

        //THEN
        Assert.assertEquals(true, parkingSpotDAO.updateParking(parkingSpot));
        Assert.assertEquals(true, parkingSpotDAO.updateParking(parkingSpot2));
        Assert.assertEquals(3, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
    }
}

