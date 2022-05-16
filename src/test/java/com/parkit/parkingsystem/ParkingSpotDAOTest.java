package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.parkit.parkingsystem.constants.ParkingType.CAR;
import static junit.framework.Assert.assertEquals;

public class ParkingSpotDAOTest {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    public void getNextAvailableSlotTest() {
        // GIVEN
        ParkingType parkingType = CAR;

        //WHEN
        int actual = parkingSpotDAO.getNextAvailableSlot(parkingType);

        //THEN
        assertEquals(1, actual);
    }

    @Test
    public void updateParkingTest() {
        // GIVEN
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        //WHEN
        parkingSpotDAO.updateParking(parkingSpot);

        //THEN
        assertEquals(false, parkingSpot.isAvailable());
        assertEquals(1, parkingSpot.getId());
    }
}
