package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

public class ParkingSpotDAOTest {

    private ParkingSpotDAO parkingSpotDAO;

    @Mock
    private DataBaseConfig dataBaseConfig;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseConfig;
    }

    @Test
    public void testGetNextAvailableSlot() throws Exception {
        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertEquals(1, result);

        when(resultSet.next()).thenReturn(false);
        result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertEquals(-1, result);
    }

    @Test
    public void testUpdateParking() throws Exception {
        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        boolean result = parkingSpotDAO.updateParking(parkingSpot);
        assertTrue(result);

        when(preparedStatement.executeUpdate()).thenReturn(0);
        result = parkingSpotDAO.updateParking(parkingSpot);
        assertFalse(result);
    }

    @Test
    public void testGetParkingSpot() throws Exception {
        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(DBConstants.GET_PARKING_SPOT)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("TYPE")).thenReturn(ParkingType.CAR.toString());
        when(resultSet.getBoolean("AVAILABLE")).thenReturn(true);

        ParkingSpot parkingSpot = parkingSpotDAO.getParkingSpot(1);
        assertNotNull(parkingSpot);
        assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
        assertTrue(parkingSpot.isAvailable());

        when(resultSet.next()).thenReturn(false);
        parkingSpot = parkingSpotDAO.getParkingSpot(1);
        assertNull(parkingSpot);
    }

    @Test
    public void testUpdateParkingSpot() throws Exception {
        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        boolean result = parkingSpotDAO.updateParkingSpot(parkingSpot);
        assertTrue(result);

        when(preparedStatement.executeUpdate()).thenReturn(0);
        result = parkingSpotDAO.updateParkingSpot(parkingSpot);
        assertFalse(result);
    }
}
