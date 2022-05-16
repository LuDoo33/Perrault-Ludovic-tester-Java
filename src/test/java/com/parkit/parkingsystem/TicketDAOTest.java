package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class TicketDAOTest {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @BeforeAll
    private static void setUp() throws Exception {
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    public void saveTicketTest() {
        // GIVEN : Un ticket CAR sur la place numéro 1 avec la plaque ABCDEF
        Ticket t = new Ticket();
        t.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        t.setVehicleRegNumber("ABCDEF");
        t.setPrice(15);
        t.setInTime(new Date());
        t.setOutTime(new Date());

        // WHEN : sauvegarde d'un ticket dans la bdd
        boolean result = ticketDAO.saveTicket(t);

        // THEN : Quand on récupère les tickets dans la bdd, on retrouve le ticket inséré
        assertFalse(result);
        Ticket bddTicket = ticketDAO.getTicket("ABCDEF");
        assertEquals(ParkingType.CAR, bddTicket.getParkingSpot().getParkingType());
        assertEquals(1, bddTicket.getParkingSpot().getId());
        assertEquals("ABCDEF", bddTicket.getVehicleRegNumber());
        assertEquals(15, bddTicket.getPrice());
        assertNotNull(bddTicket.getInTime());
        assertNotNull(bddTicket.getOutTime());
    }

    @Test
    public void saveTicketNoOutTimeTest() {
        // GIVEN : Un ticket CAR sur la place numéro 1 avec la plaque ABCDEF
        Ticket t = new Ticket();
        t.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        t.setVehicleRegNumber("ABCDEF");
        t.setPrice(15);
        t.setInTime(new Date());
        t.setOutTime(null);

        // WHEN : sauvegarde d'un ticket dans la bdd
        boolean result = ticketDAO.saveTicket(t);

        // THEN : Quand on récupère les tickets dans la bdd, on retrouve le ticket inséré
        assertFalse(result);
        Ticket bddTicket = ticketDAO.getTicket("ABCDEF");
        assertEquals(ParkingType.CAR, bddTicket.getParkingSpot().getParkingType());
        assertEquals(1, bddTicket.getParkingSpot().getId());
        assertEquals("ABCDEF", bddTicket.getVehicleRegNumber());
        assertEquals(15, bddTicket.getPrice());
        assertNotNull(bddTicket.getInTime());
        assertNull(bddTicket.getOutTime());
    }

    @Test
    public void getTicketTest() {
        // GIVEN
        Ticket t = new Ticket();
        t.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        t.setVehicleRegNumber("ABCDEF");
        t.setPrice(15);
        t.setInTime(new Date());
        t.setOutTime(new Date());
        ticketDAO.saveTicket(t);

        String vehReg = "ABCDEF";
        int ticketNumberOccurence = 0;

        // WHEN
        Ticket actual = ticketDAO.getTicket(vehReg);

        // THEN
        assertEquals(t.getVehicleRegNumber(), actual.getVehicleRegNumber());
        assertEquals(t.getPrice(), actual.getPrice());
        assertNotNull(actual.getOutTime());
        assertNotNull(actual.getInTime());
    }

    @Test
    public void getNumberOccurrenceTest() {
        // GIVEN
        String vehReg = "ABCDEF";
        int expected = 0;

        // WHEN
        int actual = ticketDAO.getTicketNumberOccurrence(vehReg);

        // THEN
        assertEquals(expected, actual);
    }
}
