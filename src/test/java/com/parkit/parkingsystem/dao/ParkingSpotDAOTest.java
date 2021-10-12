package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;

public class ParkingSpotDAOTest {

	private static ParkingSpotDAO parkingSpotDAO;
	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static final Logger logger = LogManager.getLogger("ParkingSpotDAOTest");
	Connection con = null;

	private ParkingSpot parkingSpot;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;

	}

	@BeforeEach
	public void setUpPerTest() {
		try {
			con = dataBaseTestConfig.getConnection();
		} catch (Exception ex) {
			logger.error("Error connecting to data base", ex);

		}
	}

	@AfterEach
	private void tearDownPerTest() {
		dataBaseTestConfig.closeConnection(con);
	}

	@AfterAll
	private static void tearDown() {

	}

	/*
	 * Testing of id number parking type CAR should return 1
	 */
	@Test
	public void getNextAvailableSlotTest_Car() {
		// GIVEN
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		// WHEN
		int parkingId = parkingSpotDAO.getNextAvailableSlot(parkingSpot.getParkingType());

		// THEN
		assertEquals(1, parkingId);
	}

	/*
	 * Testing of id number parking type BIKE return 4 the first id parking
	 * available for BIKE in the DB test
	 */
	@Test
	public void getNextAvailableSlotTest_BIKE() {
		// GIVEN
		parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		// WHEN
		int parkingId = parkingSpotDAO.getNextAvailableSlot(parkingSpot.getParkingType());

		// THEN
		assertEquals(4, parkingId);
	}

	/*
	 * Testing the update car parking of the parking
	 */
	@Test
	public void updateParkingTest_forCAR() {
		// GIVEN
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

		// WHEN
		parkingSpotDAO.updateParking(parkingSpot);

		// THEN
		assertTrue(parkingSpot.isAvailable());
	}

	/*
	 * Testing the update BIKE parking of the parking
	 */
	@Test
	public void updateParkingTest_forBIKE() {
		// GIVEN
		parkingSpot = new ParkingSpot(1, ParkingType.BIKE, true);

		// WHEN
		parkingSpotDAO.updateParking(parkingSpot);

		// THEN
		assertTrue(parkingSpot.isAvailable());
	}

	/*
	 * Testing the failure updating
	 */
	@Test
	public void updateParkingTestFailour() {
		parkingSpot = new ParkingSpot(0, null, false);
		assertFalse(parkingSpotDAO.updateParking(parkingSpot));

	}
}
