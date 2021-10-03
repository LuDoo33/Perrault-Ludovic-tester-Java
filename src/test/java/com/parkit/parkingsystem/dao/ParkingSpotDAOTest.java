package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;

public class ParkingSpotDAOTest {

	private static ParkingSpotDAO parkingSpotDAO;
	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static final Logger logger = LogManager.getLogger("PArkingSpotDAOTest");
	Connection con = null;

	@Mock
	private static ParkingSpot parkingSpot;

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
	 * Testing of id number parking type CAR should return 2 because 1 is not
	 * available in DB test
	 */
	@Test
	public void getNextAvailableSlotTest_Car() {
		// GIVEN
		parkingSpot = mock(ParkingSpot.class);
		when(parkingSpot.getParkingType()).thenReturn(ParkingType.CAR);

		// WHEN
		int parkingId = parkingSpotDAO.getNextAvailableSlot(parkingSpot.getParkingType());

		// THEN
		verify(parkingSpot, times(1)).getParkingType();
		assertEquals(2, parkingId);
	}

	/*
	 * Testing of id number parking type BIKE return 4 the first id parking
	 * available for BIKE in the DB test
	 */
	@Test
	public void getNextAvailableSlotTest_BIKE() {
		// GIVEN
		parkingSpot = mock(ParkingSpot.class);
		when(parkingSpot.getParkingType()).thenReturn(ParkingType.BIKE);

		// WHEN
		int parkingId = parkingSpotDAO.getNextAvailableSlot(parkingSpot.getParkingType());

		// THEN
		verify(parkingSpot, times(1)).getParkingType();
		assertEquals(4, parkingId);
	}

	/*
	 * Testing the updating of any type of the parking
	 */
	@Test
	public void updateParkingTest() {
		// GIVEN
		parkingSpot = mock(ParkingSpot.class);
		when(parkingSpot.isAvailable()).thenReturn(true);
		when(parkingSpot.getParkingType()).thenReturn(any(ParkingType.class));
		parkingSpot.setId(1);

		// WHEN
		parkingSpotDAO.updateParking(parkingSpot);

		// THEN
		verify(parkingSpot, times(1)).isAvailable();
		assertTrue(parkingSpot.isAvailable());
	}

	/*
	 * Testing the failure updating
	 */
	@Test
	public void updateParkingTestFailour() {
		ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
		ParkingSpot parkingSpot = mock(ParkingSpot.class);
		assertFalse(parkingSpotDAO.updateParking(parkingSpot));

	}
}
