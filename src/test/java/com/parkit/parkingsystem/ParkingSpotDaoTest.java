package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class ParkingSpotDaoTest {
    public static ParkingSpotDAO underTest;
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static DataBasePrepareService dataBasePrepareService;

    @BeforeAll
    public static void setUp() {
        underTest = new ParkingSpotDAO();
        underTest.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }


    @AfterAll
    public static void tearDown(){
    }

    @Test
    public void updateParkingCarTest () {
        //GIVEN
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

        //WHEN
        underTest.updateParking(parkingSpot);
        int result = underTest.getNextAvailableSlot(ParkingType.CAR);

        //THEN
        assertThat(parkingSpot.getId()).isEqualTo(result);
    }

    @Test
    public void updateParkingBikeTest () {
        //GIVEN
        ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, true);

        //WHEN
        underTest.updateParking(parkingSpot);
        int result = underTest.getNextAvailableSlot(ParkingType.BIKE);

        //THEN
        assertThat(parkingSpot.getId()).isEqualTo(result);
    }

    @Test
    public void updateInvalidParkingSpotCarTestShouldReturnFalse()  {
        //GIVEN
        boolean isUpdated = underTest.updateParking(null);
        //WHEN > THEN
        assertThat(isUpdated).isEqualTo(false);
    }

    @Test
    public void getNextAvailableSlotWithInvalidParkingTypeShouldReturnMinus1 () {
        //GIVEN
        int result = underTest.getNextAvailableSlot(null);
        //WHEN > THEN
        assertThat(result).isEqualTo(-1);
    }

    @Test
    public void switchParkingTypeShouldUpdateTest () {
        //GIVEN
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        underTest.updateParking(parkingSpot);

        //WHEN
        int parkingNumber = underTest.getNextAvailableSlot(ParkingType.BIKE);
        parkingSpot.setParkingType(ParkingType.BIKE);
        parkingSpot.setId(parkingNumber);
        boolean result = underTest.updateParking(parkingSpot);

        //THEN
        assertThat(result).isEqualTo(true);
    }


}
