package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParkingSpotDAOTest {

    private ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
    ParkingSpot parkingSpotNotAvailable = new ParkingSpot(1, ParkingType.CAR, false);

    ParkingSpot parkingSpotAvailable = new ParkingSpot(1, ParkingType.CAR, true);
    ParkingSpot parkingSpotNotFound = new ParkingSpot(1000000000, ParkingType.CAR, false);

    @BeforeEach
    private void setUpPerTest() {
        DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
    }

    @Test
    public void getNextAvailableSlotTest() {
        assertTrue(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)>0);
    }

    @Test
    public void getNextAvailableSlotNullPointerExceptionTest() {
        parkingSpotDAO.dataBaseConfig = null;
        assertThrows(NullPointerException.class,() -> parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
    }

    @Test
    public void updateParkingTest() {
        assertTrue(parkingSpotDAO.updateParking(parkingSpotNotAvailable));
        // revert update into database from not available to available
        parkingSpotDAO.updateParking(parkingSpotAvailable);
    }

    @Test
    public void updateParkingNullPointerExceptionTest() {
        parkingSpotDAO.dataBaseConfig = null;
        assertThrows(NullPointerException.class,() -> parkingSpotDAO.updateParking(parkingSpotNotAvailable));

    }

    @Test
    public void updateParkingNotFoundTest() {
        assertFalse(parkingSpotDAO.updateParking(parkingSpotNotFound));

    }

}
