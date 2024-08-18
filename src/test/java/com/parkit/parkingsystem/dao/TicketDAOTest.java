package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class TicketDAOTest {

    private TicketDAO ticketDAO;
    private DataBaseConfig dataBaseConfig;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        dataBaseConfig = Mockito.mock(DataBaseConfig.class);
        connection = Mockito.mock(Connection.class);
        preparedStatement = Mockito.mock(PreparedStatement.class);
        resultSet = Mockito.mock(ResultSet.class);

        Mockito.when(dataBaseConfig.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);

        ticketDAO = new TicketDAO();
        ticketDAO.setDataBaseConfig(dataBaseConfig);
    }

    @Test
    public void testSaveTicket() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, true)); // Utilisation correcte de ParkingType.CAR
        ticket.setVehicleRegNumber("ABC123");
        ticket.setPrice(10.0);
        ticket.setInTime(new Date());

        Mockito.when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = ticketDAO.saveTicket(ticket);

        assertTrue(result);
    }

    @Test
    public void testGetTicket() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, true)); // Utilisation correcte de ParkingType.CAR
        ticket.setVehicleRegNumber("ABC123");
        ticket.setPrice(10.0);
        ticket.setInTime(new Date());
        ticket.setOutTime(new Date());

        Mockito.when(resultSet.next()).thenReturn(true);
        Mockito.when(resultSet.getInt(1)).thenReturn(1);
        Mockito.when(resultSet.getInt(2)).thenReturn(1);
        Mockito.when(resultSet.getDouble(3)).thenReturn(10.0);
        Mockito.when(resultSet.getTimestamp(4)).thenReturn(new Timestamp(new Date().getTime()));
        Mockito.when(resultSet.getTimestamp(5)).thenReturn(new Timestamp(new Date().getTime()));
        Mockito.when(resultSet.getString(6)).thenReturn("CAR"); // Adapté au type ParkingType.CAR

        Ticket resultTicket = ticketDAO.getTicket("ABC123");

        assertNotNull(resultTicket);
        assertEquals("ABC123", resultTicket.getVehicleRegNumber());
    }

    @Test
    public void testUpdateTicket() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setPrice(20.0);
        ticket.setOutTime(new Date());

        Mockito.when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = ticketDAO.updateTicket(ticket);

        assertTrue(result);
    }

    @Test
    public void testGetNbTicket() throws Exception {
        Mockito.when(resultSet.next()).thenReturn(true);
        Mockito.when(resultSet.getInt(1)).thenReturn(1);

        int count = ticketDAO.getNbTicket("ABC123");

        assertEquals(1, count);
    }
}
