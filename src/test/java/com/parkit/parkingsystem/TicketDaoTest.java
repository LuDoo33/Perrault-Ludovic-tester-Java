package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.*;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.assertj.core.api.Assertions.*;

public class TicketDaoTest {

    public static TicketDAO underTest;
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static DataBasePrepareService dataBasePrepareService;

    @BeforeAll
    public static void setUp() {
        underTest = new TicketDAO();
        underTest.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    public void setUpPerTest(){
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    public static void tearDown(){
    }

    @Test
    void itShouldSaveTicketForCar() {
        //GIVEN
        Ticket ticket = new Ticket();
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setId(5);
        ticket.setVehicleRegNumber("IAMACAR");
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN
        underTest.saveTicket(ticket);

        //THEN
        Ticket expected = underTest.getTicket("IAMACAR");
        assertEquals(expected.getVehicleRegNumber(), "IAMACAR");
    }

    @Test
    void itShouldSaveTicketForBike() {
        //GIVEN
        Ticket ticket = new Ticket();
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setId(5);
        ticket.setVehicleRegNumber("IAMABIKE");
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN
        underTest.saveTicket(ticket);

        //THEN
        Ticket expected = underTest.getTicket("IAMABIKE");
        assertThat(expected.getVehicleRegNumber()).isEqualTo(ticket.getVehicleRegNumber());
    }

    @Test
    void itShouldUpdateTicket() {
        //GIVEN
        Ticket ticket = new Ticket();
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setVehicleRegNumber("TESTA");
        ticket.setInTime(inTime);
        ticket.setParkingSpot(parkingSpot);
        underTest.saveTicket(ticket);

        //WHEN
        Date outTime = new Date();
        ticket.setOutTime(outTime);
        Boolean isUpdated = underTest.updateTicket(ticket);

        //THEN
        Ticket expected = underTest.getTicket("TESTA");
        assertThat(isUpdated).isTrue();
        assertThat(expected.getVehicleRegNumber()).isEqualTo(ticket.getVehicleRegNumber());
    }

    @Test
    public void updateTicketWithNoVehicleRegNumber() {
        //GIVEN
        Ticket ticket = underTest.getTicket(null);

        //WHEN
        boolean result = underTest.updateTicket(ticket);

        //THEN
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void saveTicketWithNoVehicleRegNumber() {
        //GIVEN
        Ticket ticket = underTest.getTicket(null);

        //WHEN
        boolean result = underTest.saveTicket(ticket);

        //THEN
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void itShouldReturnFalseForNonRecurrentUser(){
        //GIVEN > WHEN
        Boolean isRecurrent = underTest.isMultipleTicket("FIRSTTIME");
        //THEN
        assertThat(isRecurrent).isFalse();
    }

    @Test
    public void itShouldReturnTrueForRecurrentUser(){
        //GIVEN
        Ticket ticket = new Ticket();
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setId(5);
        ticket.setVehicleRegNumber("IAMACAR");
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //WHEN
        underTest.saveTicket(ticket);
        underTest.saveTicket(ticket);
        Boolean isRecurrent = underTest.isMultipleTicket("IAMACAR");
        //THEN
        assertThat(isRecurrent).isTrue();
    }

}
