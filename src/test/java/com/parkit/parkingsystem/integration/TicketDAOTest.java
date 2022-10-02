package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;


import com.parkit.parkingsystem.config.DataBaseTestConfig;
import com.parkit.parkingsystem.service.DataBasePrepareService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

class TicketDAOTest {

    private static DataBasePrepareService dataBasePrepareService;
    private static TicketDAO ticketDAO;
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        // apres le tour de test on vide la base de données
        dataBasePrepareService.clearDataBaseEntries();
    }


    @Test
    void saveTicketGenerated() throws Exception {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("974564");
        assertEquals(true, ticketDAO.saveTicket(ticket));
    }

    @Test
    void getTicketGenerated() throws Exception {
        Ticket ticket = ticketDAO.getTicket("974564");
        assertNotNull(ticket);
    }

    @Test
    void updateTicketGenerated() throws Exception {
        Ticket ticket = ticketDAO.getTicket("974564");

        ticket.setOutTime(new Date());

        assertEquals(true, ticketDAO.updateTicket(ticket));
    }

}
