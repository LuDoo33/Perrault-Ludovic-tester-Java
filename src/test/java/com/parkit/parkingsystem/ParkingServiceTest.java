package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	@BeforeEach
	private void setUpPerTest() {
		try {
			// when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			// ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			// Ticket ticket = new Ticket();
			// Integer nbTicket = 0;
			// ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			// ticket.setParkingSpot(parkingSpot);
			// ticket.setVehicleRegNumber("ABCDEF");

			// when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
			// when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
			// when(ticketDAO.getNbTicket(anyString())).thenReturn(nbTicket);

			// when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(false);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	@Test
	public void testProcessExitingVehicle_withGetTicket() {
		// Given
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");

		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		when(ticketDAO.getTicket(eq("ABCDEF"))).thenReturn(ticket);

		// When
		parkingService.processExitingVehicle();

		// Then
		verify(ticketDAO, Mockito.times(1)).getTicket(eq("ABCDEF"));
	}

	@Test
	public void testProcessExitingVehicle_withGetNbTicket() {
		// Given
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		int nbTicket = 2;
		ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");

		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		when(ticketDAO.getTicket(eq("ABCDEF"))).thenReturn(ticket);
		when(ticketDAO.getNbTicket(eq("ABCDEF"))).thenReturn(nbTicket);

		// When
		parkingService.processExitingVehicle();

		// Then
		verify(ticketDAO, times(1)).getNbTicket(eq("ABCDEF"));
	}

	@Test
	public void testProcessExitingVehicle_withUpdateParking() {
		// Given
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");

		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

		// When
		parkingService.processExitingVehicle();

		// Then
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
	}

	@Test
	public void testProcessIncomingVehicle() {
		// Given
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		parkingSpot.setId(0);

		Ticket ticket = new Ticket();
		ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setPrice(0);
		ticket.setOutTime(null);

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(false);

		// When
		parkingService.processIncomingVehicle();

		// Then
		verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
	}

	@Test
	public void testProcessExitingVehicleUnableUpdate() {
		// Given
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();

		ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");

		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

		// When
		parkingService.processExitingVehicle();

		// Then
		verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
	}

	@Test
	public void testGetNextParkingNumberIfAvailable() {
		// Given
		// ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

		// When
		parkingService.getNextParkingNumberIfAvailable();

		// Then
		verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
	}

	@Test
	public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
		// Given

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(-1);

		// When
		ParkingSpot parkingSpotNull = parkingService.getNextParkingNumberIfAvailable();

		// Then
		verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
		assertThat(parkingSpotNull);
	}

	@Test
	public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
		// Given
		when(inputReaderUtil.readSelection()).thenReturn(3);

		// When
		parkingService.getNextParkingNumberIfAvailable();

		// Then
		verify(inputReaderUtil, times(1)).readSelection();

	}

	private void initTest() {
		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Ticket ticket = new Ticket();
			Integer nbTicket = 0;
			ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");

			when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
			when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
			when(ticketDAO.getNbTicket(anyString())).thenReturn(nbTicket);

			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(false);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}
}
