package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ParkingServiceTest {

    private static ParkingService underTest;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    public void setUpPerTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            //one hour ago
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            underTest = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processIncomingVehicleTestForCar () {
        //GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(2);

        //WHEN
        underTest.processIncomingVehicle();

        //THEN
        verify(ticketDAO, Mockito.times(1)).saveTicket(any());
    }

    @Test
    public void processIncomingVehicleTestForBike () {
        //GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(4);

        //WHEN
        underTest.processIncomingVehicle();

        //THEN
        verify(ticketDAO, Mockito.times(1)).saveTicket(any());
    }
    @Test
    public void processExitingVehicleTest(){
        //GIVEN > WHEN
        underTest.processExitingVehicle();

        //THEN
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void processNonRecurrentUserTest(){
        //GIVEN
        when(ticketDAO.isMultipleTicket(any())).thenReturn(false);

        //WHEN
        underTest.processExitingVehicle();

        //THEN
        ArgumentCaptor<Ticket> ticketArgumentCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketDAO).updateTicket(ticketArgumentCaptor.capture()); //capture the ticket that was passed to ticketDAO inside parkingService.processExitingVehicle
        Ticket capturedTicket = ticketArgumentCaptor.getValue();
        assertThat(capturedTicket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR);
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void processDiscountForRecurrentUsersTest () {
        //GIVEN
        when(ticketDAO.isMultipleTicket(any())).thenReturn(true);

        //WHEN
        underTest.processExitingVehicle();

        //THEN
        ArgumentCaptor<Ticket> ticketArgumentCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketDAO).updateTicket(ticketArgumentCaptor.capture()); //capture the ticket that was passed to ticketDAO inside parkingService.processExitingVehicle
        Ticket capturedTicket = ticketArgumentCaptor.getValue();
        assertThat(capturedTicket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR * 95 / 100);
    }

}
