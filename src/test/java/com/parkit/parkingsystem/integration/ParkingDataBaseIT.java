package com.parkit.parkingsystem.integration;

import static org.mockito.Mockito.when;

import com.parkit.parkingsystem.config.DataBaseTestConfig;
import com.parkit.parkingsystem.service.DataBasePrepareService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    ParkingService parkingService;

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

    @BeforeEach
    private void setUpPerTest() throws Exception {
        dataBasePrepareService.clearDataBaseEntries();
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }

    @AfterAll
    private static void tearDown() throws Exception {
        dataBaseTestConfig.closeConnection(dataBaseTestConfig.getConnection());
    }

    @Test
    public void testParkingACar() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        parkingService.processIncomingVehicle();

        Ticket ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());

        assertNotNull(ticket, "Ticket saved");
        assertEquals(false, ticket.getParkingSpot().isAvailable(), "Parcking is not availability");
    }


    @Test
    public void testParkingABike() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEFG");
        parkingService.processIncomingVehicle();

        Ticket ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());

        assertNotNull(ticket, "Ticket saved");
        assertEquals(false, ticket.getParkingSpot().isAvailable(), "Parcking is not availability");
    }

    @Test
    public void testParkingLotExit() throws Exception {
        testParkingACar();

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();

        Ticket ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
        assertNotEquals(0, ticket.getPrice(), "Tarif généré");
    }
}
