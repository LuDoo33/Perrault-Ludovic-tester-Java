package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(registrationNumber);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    public void testParkingLotExitIt() throws Exception {
        //testParkingACarIt();
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("Test");

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        String vehiculeNumber = inputReaderUtil.readVehicleRegistrationNumber();

        Ticket ticket = ticketDAO.getTicket(vehiculeNumber);
        Date date = ticket.getInTime();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR,-1);
        Date newDate = c.getTime();

        ticket.setInTime(newDate);
        ticketDAO.updateTicket(ticket);

        parkingService.processExitingVehicle();


        //TimeUnit.SECONDS.sleep(5);
        assertNotNull(ticketDAO.getTicket(vehiculeNumber).getPrice());
        assertNotNull(ticketDAO.getTicket(vehiculeNumber).getOutTime());
    }


    @Test
    public void testParkingACarIt() throws Exception {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        String vehiculeNumber = inputReaderUtil.readVehicleRegistrationNumber();
        assertEquals(vehiculeNumber, ticketDAO.getTicket(registrationNumber).getVehicleRegNumber());
    }


}
