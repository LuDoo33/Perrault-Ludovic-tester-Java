package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
	    lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

	    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
	    Ticket ticket = new Ticket();
	    ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
	    ticket.setParkingSpot(parkingSpot);
	    ticket.setVehicleRegNumber("ABCDEF");
	    when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
	    lenient().when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

	    lenient().when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

	    parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException("Failed to set up test mock objects");
	}
    }

    @Test
    public void testOutTimeWhenProcessExitingVehicle() {
	// GIVEN - ARRANGE --- Already done in BeforeEeach

	// WHEN - ACT
	parkingService.processExitingVehicle(new Date());
	Ticket ticketAfterExitingProcess = ticketDAO.getTicket("ABCDEF");

	// THEN - ASSERT
	verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
	assertThat(ticketAfterExitingProcess.getOutTime()).isNotNull();
    }

    @Test
    @DisplayName("on teste un utilisateur regulier lors de la sortie")
    public void testRecurringUserExiting() {
	// GIVEN - ARRANGE --- Already done in BeforeEeach
	when(ticketDAO.getCountForVehicleRegNumber("ABCDEF")).thenReturn(2);

	// WHEN - ACT
	parkingService.processExitingVehicle(new Date());
	Ticket ticketAfterExitingProcess = ticketDAO.getTicket("ABCDEF");

	// THEN - ASSERT
	assertThat(ticketAfterExitingProcess.getPrice()).isLessThan(1.5);
    }

    @Test
    @DisplayName("on teste avec un get ticket null")
    public void testProcessExitingVehicleWithANullTicket() {
	// GIVEN - ARRANGE --- Already done in BeforeEeach
	when(ticketDAO.getTicket(anyString())).thenReturn(null);
	// WHEN - ACT
	parkingService.processExitingVehicle(new Date());
	Ticket ticketAfterIncomingProcess = ticketDAO.getTicket("ABCDEF");

	// THEN - ASSERT
	assertThat(ticketAfterIncomingProcess).isNull();
    }

    @Test
    @DisplayName("on teste avec un update ticket null")
    public void testProcessExitingVehicleWithUpdatingANullTicket() {
	// GIVEN - ARRANGE --- Already done in BeforeEeach
	when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
	// WHEN - ACT
	parkingService.processExitingVehicle(new Date());
	Ticket ticketAfterExitingProcess = ticketDAO.getTicket("ABCDEF");

	// THEN - ASSERT
	assertThat(ticketAfterExitingProcess.getOutTime()).isNotNull();
    }

    @Test
    @DisplayName("on teste que l'heure d'entrée n'est pas nulle")
    public void testInTimeWhenProcessIncomingVehicle() {
	// GIVEN - ARRANGE --- Already done in BeforeEeach
	when(inputReaderUtil.readSelection()).thenReturn(1);
	when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
	// WHEN - ACT
	parkingService.processIncomingVehicle();
	Ticket ticketAfterIncomingProcess = ticketDAO.getTicket("ABCDEF");

	// THEN - ASSERT
	assertThat(ticketAfterIncomingProcess.getInTime()).isNotNull();
    }

    @Test
    @DisplayName("on teste un utilisateur regulier lors de l'entrée")
    public void testRecurringUserIncoming() {
	// GIVEN - ARRANGE --- Already done in BeforeEeach
	when(inputReaderUtil.readSelection()).thenReturn(1);
	when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
	when(ticketDAO.getCountForVehicleRegNumber("ABCDEF")).thenReturn(1);
	// WHEN - ACT
	parkingService.processIncomingVehicle();

	Ticket ticketAfterIncomingProcess = ticketDAO.getTicket("ABCDEF");

	// THEN - ASSERT
	assertThat(ticketAfterIncomingProcess.getPrice()).isEqualTo(0);
    }

    @Test
    @DisplayName("on teste avec un parkingSpot null")
    public void testWithANullParkingSpotInProcessIncomingVehicle() {
	// GIVEN - ARRANGE --- Already done in BeforeEeach

	// WHEN - ACT
	parkingService.processIncomingVehicle();
	Ticket ticketAfterIncomingProcess = ticketDAO.getTicket("ABCDEF");

	// THEN - ASSERT
	assertThat(ticketAfterIncomingProcess.getInTime()).isNotNull();
    }

    @Test
    @DisplayName("on teste avec un parkingSpot id = 0")
    public void testWithAZeroIdParkingSpotInProcessIncomingVehicle() {
	// GIVEN - ARRANGE --- Already done in BeforeEeach
	when(inputReaderUtil.readSelection()).thenReturn(1);
	when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);

	// WHEN - ACT
	parkingService.processIncomingVehicle();

	Ticket ticketAfterIncomingProcess = ticketDAO.getTicket("ABCDEF");

	// THEN - ASSERT
	assertThat(ticketAfterIncomingProcess.getPrice()).isEqualTo(0);
    }
}
