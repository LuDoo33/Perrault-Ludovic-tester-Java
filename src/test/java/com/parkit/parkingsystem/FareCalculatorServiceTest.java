package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;


import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;


public class FareCalculatorServiceTest {

    private Ticket ticket = new Ticket();
    private TicketDAO ticketDAOMock = mock(TicketDAO.class);
    private FareCalculatorService fareCalculatorService = new FareCalculatorService(ticketDAOMock);

    @Test
    @DisplayName("Doit lancer une exception lorsque le ticket est null")
    public void calculateFareWithNullTicket() {
        assertThrows(IllegalArgumentException.class, () -> {
            fareCalculatorService.calculateFare(null, false);
        });
    }

    @Test
    @DisplayName("Doit calculer le tarif pour une voiture avec une durée de stationnement d'1H")
    public void calculateFareCar() {

        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // 1 heure plus tôt
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        double expectedPrice = 1.5;
        fareCalculatorService.calculateFare(ticket);
        assertEquals(expectedPrice, ticket.getPrice());
    }

    @Test
    @DisplayName("Doit calculer le tarif pour une moto avec une durée de stationnement d'1H")
    public void calculateFareBike() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // 1 heure plus tôt
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        double expectedPrice = 1;
        fareCalculatorService.calculateFare(ticket);

        assertEquals(expectedPrice, ticket.getPrice());
    }

    @Test
    @DisplayName("Doit lancer une IllegalArgumentException pour un type de stationnement inconnu")
    public void calculateFareUnkownType() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    @DisplayName("Doit lancer une IllegalArgumentException pour une durée de stationnement dans le futur")
    public void calculateFareBikeWithFutureInTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    @DisplayName("Doit payer 0 pour une moto avec une durée de stationnement de moins de 30mn")
    public void calculateFareBikeWithLessThan30minutesParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (25 * 60 * 1000)); // 25 minutes de stationnement
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0, ticket.getPrice());
    }

    @Test
    @DisplayName("Doit payer 0 pour une voiture avec une durée de stationnement de moins de 30mn")
    public void calculateFareCarWithLessThan30minutesParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (25 * 60 * 1000)); // 25 minutes de stationnement
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0, ticket.getPrice());
    }

    @Test
    @DisplayName("Doit calculer le tarif avec remise de 5% pour une voiture et une durée de 2H")
    public void calculateFareCarWithDiscount() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (120 * 60 * 1000)); // 2 heures plus tôt
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        double expectedPrice = 2.85;
        fareCalculatorService.calculateFare(ticket, true);

        // Compare les valeurs avec une tolérance de 0.001 (0.001 = 0.1 centime)
        assertEquals(expectedPrice,ticket.getPrice(),0.01);
    }

    @Test
    @DisplayName("Doit calculer le tarif avec remise de 5% pour une moto")
    public void calculateFareBikeWithDiscount() {

        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // 1 heure plus tôt
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        double expectedPrice = 0.95;
        fareCalculatorService.calculateFare(ticket, true);
        assertEquals(expectedPrice, ticket.getPrice());
    }
}
