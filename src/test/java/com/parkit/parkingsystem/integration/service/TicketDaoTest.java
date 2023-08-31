package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.sql.Time;
import java.util.Date;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class TicketDaoTest {

    private final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private final TicketDAO ticketDAO = new TicketDAO();

    private Ticket ticket;
    @Mock
    private Logger mockLogger;

    @BeforeEach
    public void setUp() {
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(new Date());
        ticket.setOutTime(null);
    }

    @Test
    public void testSaveTicket() {
        assertNotNull(ticket.getParkingSpot(), "ParkingSpot ne doit pas être null");
        int nbTicketsBefore = ticketDAO.getNbTicket("ABCDEF");
        ticketDAO.saveTicket(ticket);
        int nbTicketsAfter = ticketDAO.getNbTicket("ABCDEF");
        assertEquals(nbTicketsBefore + 1, nbTicketsAfter, "Un nouveau ticket doit être ajouté");
    }

    @Test
    public void testSaveTicketFail() {
        // Configurer le mock pour remplacer le logger original
        LogManager.getLogger(TicketDAO.class).removeAllAppenders();
        LogManager.getLogger(TicketDAO.class).addAppender((Appender) mockLogger);

        ticket.setParkingSpot(null);
        assertFalse(ticketDAO.saveTicket(ticket), "Le ticket ne doit pas être sauvegardé si ParkingSpot est null");
    }

    @Test
    public void testGetTicket() {
        ticketDAO.saveTicket(ticket);
        Ticket retrievedTicket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(retrievedTicket, "Le ticket récupéré ne doit pas être null");
        assertEquals("ABCDEF", retrievedTicket.getVehicleRegNumber(), "Le numéro d'immatriculation doit être ABCDEF");
    }

    @Test
    public void testGetTicketNotFound() {
        Ticket retrievedTicket = ticketDAO.getTicket("NOTFOUND");
        assertNull(retrievedTicket, "Le ticket récupéré doit être null si non trouvé");
    }

    @Test
    public void testUpdateTicket() {
        ticketDAO.saveTicket(ticket);
        Ticket retrievedTicket = ticketDAO.getTicket("ABCDEF");
        retrievedTicket.setPrice(10.0);
        retrievedTicket.setOutTime(new Date());
        assertTrue(ticketDAO.updateTicket(retrievedTicket), "Le ticket doit être mis à jour");
    }

    @Test
    public void testUpdateTicketFail() {
        ticketDAO.saveTicket(ticket);
        Ticket retrievedTicket = ticketDAO.getTicket("ABCDEF");
        retrievedTicket.setOutTime(null);
        assertFalse(ticketDAO.updateTicket(retrievedTicket), "Le ticket ne doit pas être mis à jour si OutTime est null");
    }

    @Test
    public void testGetNbTicket() {
        int initialNbTickets = ticketDAO.getNbTicket("ABCDEF");

        ticketDAO.saveTicket(ticket);

        int finalNbTickets = ticketDAO.getNbTicket("ABCDEF");

        assertEquals(initialNbTickets + 1, finalNbTickets, "Le nombre de tickets devrait augmenter de 1");
    }


    @Test
    public void testGetNbTicketNotFound() {
        int nbTickets = ticketDAO.getNbTicket("NOTFOUND");
        assertEquals(0, nbTickets, "Le nombre de tickets doit être 0 si non trouvé");
    }
}
