package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class TicketDAOTest {

    private TicketDAO ticketDAO = new TicketDAO();
    private static Ticket ticket;
    private static final String VEHICLE_REG_NUMBER="XX-111-YY";
    private static final String VEHICLE_REG_NUMBER_NOT_RECURRENT="ZZ-000-ZZ";

    @BeforeEach
    private void setUpPerTest() {
        DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        ticket = new Ticket();
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR,true));
        ticket.setInTime(new Date());
        ticket.setPrice(1);
        ticket.setVehicleRegNumber(VEHICLE_REG_NUMBER);
    }

    @Test
    public void saveTicketTest() {
        assertTrue(ticketDAO.saveTicket(ticket));
    }

    @Test
    public void saveTicketWithoutOutTimeTest() {
        ticket.setOutTime(null);
        assertTrue(ticketDAO.saveTicket(ticket));
    }

    @Test
    public void getTicketTest() {
        saveTicketTest();
        Ticket ticket = ticketDAO.getTicket(VEHICLE_REG_NUMBER);
        assertNotNull(ticket);
    }

    @Test
    public void updateTicketTest() {
        ticket.setOutTime(new Date());
        assertTrue(ticketDAO.updateTicket(ticket));
    }


    @Test
    public void IsRecurrentUserTest() {
        saveTicketTest();
        assertTrue(ticketDAO.isRecurrentUser(VEHICLE_REG_NUMBER));
    }

    @Test
    public void IsNotRecurrentUserTest() {
        assertFalse(ticketDAO.isRecurrentUser(VEHICLE_REG_NUMBER_NOT_RECURRENT));
    }


}
