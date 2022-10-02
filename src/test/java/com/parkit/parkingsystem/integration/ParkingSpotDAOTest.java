package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.model.ParkingSpot;

class ParkingSpotDAOTest {

    private static ParkingSpotDAO parkingSpotDAO;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
    }

  //  @Test
    //void getNextAvaiableSlotCar() {
      //  assertNotEquals(1, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
    //}

    @Test
    void getNextAvaiableSlotBike() {
        assertNotEquals(1, parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE));
    }

    @Test
    void updateParkingSpot() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        assertEquals(true, parkingSpotDAO.updateParking(parkingSpot));
    }
}
