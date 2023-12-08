package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class ParkingServiceTest {
	@MockitoSettings(strictness = Strictness.LENIENT)
	static class JUnit5MockitoTest {
		@Mock
		private static ParkingService parkingService;

		@Mock
		private static InputReaderUtil inputReaderUtil;
		@Mock
		private static ParkingSpotDAO parkingSpotDAO;
		@Mock
		private static TicketDAO ticketDAO;
		@Mock
		private static ParkingSpot parkingSpot;

		@BeforeEach
		private void setUpPerTest() {
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

				ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
				Ticket ticket = new Ticket();
				ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
				ticket.setParkingSpot(parkingSpot);
				ticket.setVehicleRegNumber("ABCDEF");
				when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
				when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

				when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

				parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to set up test mock objects");
			}
		}

		@Test
		public void processExitingReccurentVehicleTest() throws Exception {
			when(ticketDAO.getNbTicket(eq("ABCDEF"))).thenReturn(2);

			parkingService.processExitingVehicle();
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
			verify(ticketDAO, Mockito.times(1)).getNbTicket(eq("ABCDEF"));
		}

		@Test
		public void processExitingNewVehicleTest() throws Exception {
			when(ticketDAO.getNbTicket(eq("ABCDEF"))).thenReturn(1); 

			parkingService.processExitingVehicle();
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
			verify(ticketDAO, Mockito.times(1)).getNbTicket(eq("ABCDEF"));
			verify(ticketDAO, Mockito.times(1)).getTicket(eq("ABCDEF"));
			verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
		}

		@Test
		public void testProcessIncomingReccurentVehicle() throws Exception {
			when(ticketDAO.getNbTicket(eq("ABCDEF"))).thenReturn(2);
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
			when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(1);

			parkingService.processIncomingVehicle();

			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
			verify(ticketDAO, Mockito.times(1)).getNbTicket(eq("ABCDEF"));
			verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
		}

		@Test
		public void testProcessIncomingNewVehicle() throws Exception {
			when(ticketDAO.getNbTicket(eq("ABCDF"))).thenReturn(0);
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDF");
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
			when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(1);

			parkingService.processIncomingVehicle();

			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
			verify(ticketDAO, Mockito.times(1)).getNbTicket(eq("ABCDF"));
		}

		@Test
		public void processExitingVehicleTestUnableUpdate() {
			when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
			parkingService.processExitingVehicle();
			verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
		}

		@Test
		public void testGetNextParkingNumberIfAvailable() {
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
			parkingService.getNextParkingNumberIfAvailable();

			verify(inputReaderUtil, Mockito.times(1)).readSelection();
			verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any(ParkingType.class));
			assertThat(parkingSpot.isAvailable()).isFalse();
			assertThat(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).isEqualTo(1);
			
				
		}

		@Test
		public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {

			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);

			parkingService.getNextParkingNumberIfAvailable();

			verify(inputReaderUtil, Mockito.times(1)).readSelection();
			verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any(ParkingType.class));
		}
		
		@Test
		public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
			when(inputReaderUtil.readSelection()).thenReturn(-1);
			
			parkingService.getNextParkingNumberIfAvailable();
			
			verify(inputReaderUtil, Mockito.times(1)).readSelection();
			
		}
		
		@Test
		public void testGetNextParkingNumberIfAvailableUnableToFetchDB() {

			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(false);
			when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);

			parkingService.getNextParkingNumberIfAvailable();
			
		}

		@Test
		public void testInputReaderUtilReadExpectedSelection() {
			when(inputReaderUtil.readSelection()).thenReturn(2);
			when(parkingSpotDAO.updateParking(null)).thenReturn(false);
			when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

			parkingService.processIncomingVehicle();

			verify(inputReaderUtil, Mockito.times(1)).readSelection();
			

		}
		
		@Test
		public void testIncomingVehicleParkingSpotIdIsEqualToZero() {
			when(parkingSpot.getId()).thenReturn(0);
			parkingService.processIncomingVehicle();
		}
 
	}
}
