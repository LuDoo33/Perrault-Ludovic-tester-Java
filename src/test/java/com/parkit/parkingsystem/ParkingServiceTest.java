package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
	
//    FareCalculatorService fareCalculatorService = mock(FareCalculatorService.class);

	Ticket ticket;
	String vehicleRegNumber;
	int id;
	int parkingNumber;

	@BeforeEach
	private void setUpPerTest() {
		logger.debug("Je rentre dans la méthode setUpPerTest()");
		try {

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
//			parkingService = new ParkingService(fareCalculatorService);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	// test de l’appel de la méthode ProcessIncomingVehicle() où tout se déroule
	// comme attendu.
	@Test
	public void testProcessIncomingVehicle() throws Exception {
		//		GIVEN

		vehicleRegNumber = "ABCDEF";
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		logger.debug(ticket);
		Ticket ticket = new Ticket(id, parkingSpot, vehicleRegNumber, 0,
				new Date(System.currentTimeMillis() - (60 * 60 * 1000)), null);
		logger.debug(ticket);

		//		WHEN
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
		when(parkingSpotDAO.updateParking(parkingSpot)).thenReturn(false);

		parkingService.processIncomingVehicle();

		//		THEN

		verify(ticketDAO).saveTicket(ticket);

	}

	// exécution du test dans le cas ou la méthode updateTicket() de ticketDAO
	// renvoie false lors de l'appel de processExitingVehicle
	@Test
	public void testProcessExitingVehicleUnableUpdate() throws Exception {
		//		GIVEN

		vehicleRegNumber = "ABCDEF";
		logger.debug(ticket);
		Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
		ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStreamCaptor));


		//		WHEN
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
		when(ticketDAO.getTicket(vehicleRegNumber)).thenReturn(ticket);
		when(ticketDAO.getNbTicket(vehicleRegNumber)).thenReturn(1);
		when(ticketDAO.updateTicket(ticket)).thenReturn(false);


		parkingService.processExitingVehicle();

		//		THEN
        verify(System.out).println("Unable to update ticket information. Error occurred");

	}

	// test de l’appel de la méthode getNextParkingNumberIfAvailable() avec pour
	// résultat
	// l’obtention d’un spot dont l’ID est 1 et qui est disponible.
	@Test
	public void testGetNextParkingNumberIfAvailable() {

		logger.debug("Je rentre dans la méthode testGetNextParkingNumberIfAvailable()");

		//		GIVEN
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

		//		WHEN

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

		parkingService.getNextParkingNumberIfAvailable();

		//		THEN

//		assertEquals(parkingSpotDAO.updateParking(parkingSpot), true);
		assertEquals(ParkingSpot.class, parkingSpot);
	}

	// test de l’appel de la méthode getNextParkingNumberIfAvailable()
	// avec pour résultat aucun spot disponible (la méthode renvoie null).
	@Test
	public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {

		//WHEN
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(-1);

		parkingService.getNextParkingNumberIfAvailable();
		
		//THEN
//		assertNull();
	}

	/*
	 * test de l’appel de la méthode getNextParkingNumberIfAvailable() avec pour
	 * résultat aucun spot (la méthode renvoie null) car l’argument saisi par
	 * l’utilisateur concernant le type de véhicule est erroné (par exemple,
	 * l’utilisateur a saisi 3 .
	 */
	@Test
	public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {

		//		GIVEN

		//		WHEN
		when(inputReaderUtil.readSelection()).thenReturn(3);
		when(parkingSpotDAO.getNextAvailableSlot(null)).thenReturn(-1);
//        when(parkingService.getVehichleType()).thenThrow(new CustomException("Message d'erreur"));
		when(inputReaderUtil.readSelection()).thenThrow(new IllegalArgumentException("Entered input is invalid"));

		parkingService.getNextParkingNumberIfAvailable();

		//		THEN
//        assertThrows(IllegalArgumentException.class, () -> parkingService.getVehichleType(), "Entered input is invalid");
		assertThrows(IllegalArgumentException.class, () -> parkingService.getNextParkingNumberIfAvailable(), "Error parsing user input for type of vehicle");
        assertThrows(Exception.class, () -> parkingService.getNextParkingNumberIfAvailable(), "Error fetching next available parking slot");

	}

	/*
	 * Complétez le test existant : processExitingVehicleTest ○ Ce test doit
	 * également mocker l’appel à la méthode getNbTicket() implémentée lors de
	 * l’étape précédente.
	 */
	@Test
	@DisplayName("processExitingVehicleCheckThatUpdateParkingMethodCalledTest()")
	public void processExitingVehicleCheckThatUpdateParkingMethodCalledTest() {

		logger.debug("Je rentre dans la méthode processExitingVehicleTest()");

		try {

			//		GIVEN
			FareCalculatorService fareCalculatorService = new FareCalculatorService();
			vehicleRegNumber = "ABCDEF";
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
			ticket = new Ticket(id, parkingSpot, vehicleRegNumber, 1.25,
					new Date(System.currentTimeMillis() - (60 * 60 * 1000)), new Date());
			
			//			WHEN
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
			when(ticketDAO.getTicket(vehicleRegNumber)).thenReturn(ticket);
			when(ticketDAO.updateTicket(ticket)).thenReturn(true);
			when(parkingSpotDAO.updateParking(parkingSpot)).thenReturn(true);
			when(ticketDAO.getNbTicket(vehicleRegNumber)).thenReturn(4);

			parkingService.processExitingVehicle();

			//			THEN
			verify(fareCalculatorService).calculateFare(ticket, true);
//	        verify(fareCalculatorService, times(1)).calculateFare(ticket, true);
//	        Argument passed to verify() is of type FareCalculatorService and is not a mock!


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
