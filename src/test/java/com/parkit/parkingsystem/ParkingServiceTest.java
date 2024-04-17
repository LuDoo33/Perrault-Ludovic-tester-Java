package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {
	private static final Logger logger = LogManager.getLogger("ParkingServiceTest");

	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	Ticket ticket;
	String vehicleRegNumber;
	int id;
	int parkingNumber;

	@BeforeEach
	private void setUpPerTest() {
		logger.debug("Je rentre dans la méthode setUpPerTest()");
		try {

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	@Test
	public void testProcessIncomingVehicle() throws Exception {
		//		GIVEN

		vehicleRegNumber = "ABCDEF";
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket(id, parkingSpot, "ABCDEF", 0,
				new Date(System.currentTimeMillis() - (60 * 60 * 1000)), null);
		logger.debug(ticket);

		//		WHEN
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(parkingSpotDAO.updateParking(parkingSpot)).thenReturn(true);

		parkingService.processIncomingVehicle();

		//		THEN
		verify(ticketDAO).saveTicket(any(Ticket.class));

	}
	
	@Test
	public void testProcessExitingVehicleUnableUpdate() throws Exception {
		//		GIVEN
		vehicleRegNumber = "ABCDEF";
		Ticket ticket = new Ticket(id, null, vehicleRegNumber, 0,
				new Date(System.currentTimeMillis() - (60 * 60 * 1000)), new Date());

		//		WHEN
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
		when(ticketDAO.getTicket(vehicleRegNumber)).thenReturn(ticket);
		when(ticketDAO.getNbTicket(vehicleRegNumber)).thenReturn(1);

		parkingService.processExitingVehicle();

		//		THEN
		assertFalse(ticketDAO.updateTicket(ticket));
	}

	@Test
	public void testGetNextParkingNumberForCarIfAvailable() {

		logger.debug("Je rentre dans la méthode testGetNextParkingNumberIfAvailable()");

		//		GIVEN
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);


		//		WHEN

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

		//		THEN
		assertEquals(parkingSpot, parkingService.getNextParkingNumberIfAvailable());
	}

	@Test
	public void testGetNextParkingNumberForBikeIfAvailable() {

		logger.debug("Je rentre dans la méthode testGetNextParkingNumberIfAvailable()");

		//		GIVEN
		ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, true);


		//		WHEN

		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(4);

		//		THEN
		assertEquals(parkingSpot, parkingService.getNextParkingNumberIfAvailable());
	}
	
	@Test
	public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
 
		//WHEN
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(-1);


		//THEN
		assertNull(parkingService.getNextParkingNumberIfAvailable());
	}

	
	@Test
	public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {


		when(inputReaderUtil.readSelection()).thenReturn(3);

		assertThrows(IllegalArgumentException.class, () -> parkingService.getVehichleType(), "Entered input is invalid");
		assertNull(parkingService.getNextParkingNumberIfAvailable());
	}

	
	@Test
	@DisplayName("processExitingVehicleCheckThatUpdateParkingMethodCalledTest()")
	public void processExitingVehicleCheckThatUpdateParkingMethodCalledTest() {

		logger.debug("Je rentre dans la méthode processExitingVehicleTest()");

		try {

			//		GIVEN
			vehicleRegNumber = "ABCDEF";
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
			
			ticket = new Ticket(id, parkingSpot, vehicleRegNumber, 1.25,
					new Date(System.currentTimeMillis() - (60 * 60 * 1000)), new Date());

			//			WHEN
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
			when(ticketDAO.getTicket(vehicleRegNumber)).thenReturn(ticket);
			when(ticketDAO.updateTicket(ticket)).thenReturn(true);
			when(ticketDAO.getNbTicket(vehicleRegNumber)).thenReturn(4);

			parkingService.processExitingVehicle();

			//			THEN
			verify(parkingSpotDAO).updateParking(parkingSpot);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
