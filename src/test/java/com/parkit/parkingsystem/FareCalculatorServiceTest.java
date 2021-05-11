package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    @DisplayName("Calcule du prix tu ticket CAR avec la réduction fidélité ")
    public void calculateFareCar(){
        Date inTime = new Date();
        // Initialisation de la date d'entré à maintenant - une heure.
        inTime.setTime( System.currentTimeMillis() - (  3600 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //Calcule du prix en indiquant que le client existe déjà
        fareCalculatorService.calculateFare(ticket,true);
        // Vérification du prix du ticket en incluant la réduction.
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR*0.95);
    }

    @Test
    @DisplayName("Calcule du prix tu ticket CAR sans la réduction fidélité ")
    public void calculateFareCarNoReduction(){
        Date inTime = new Date();
        // Initialisation de la date d'entré à maintenant - une heure.
        inTime.setTime( System.currentTimeMillis() - (  3600 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //Calcule du prix en indiquant que le client est nouveau
        fareCalculatorService.calculateFare(ticket,false);
        // Vérification du prix du ticket.
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    @DisplayName("Calcule du prix tu ticket CAR avec la réduction fidélité")
    public void calculateFareBike(){
        Date inTime = new Date();
        // Initialisation de la date d'entré à maintenant - une heure.
        inTime.setTime( System.currentTimeMillis() - (  3600 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //Calcule du prix en indiquant que le client existe déjà
        fareCalculatorService.calculateFare(ticket,true);
        // Vérification du prix du ticket incluant la réduction
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR*0.95);
    }

    @Test
    @DisplayName("Calcule du prix tu ticket CAR sans la réduction fidélité")
    public void calculateFareBikeNoReduction(){
        Date inTime = new Date();
        // Initialisation de la date d'entré à maintenant - une heure.
        inTime.setTime( System.currentTimeMillis() - (  3600 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,false);
        // Vérification du prix du ticket.
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    @DisplayName("Test du parking type non reconnu")
    public void calculateFareUnkownType(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  3600 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //Vérification si le type de parking n'est pas connu.
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket,false));
    }

    @Test
    @DisplayName("Test avec une date d'entrée ultérieur à la date de sortie")
    public void calculateFareBikeWithFutureInTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  3600 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        // Vérification que la date d'entrée n'est pas superieur à la date de sortie
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket,false));
    }

    @Test
    @DisplayName("Test parking BIKE sur 45 minutes")
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,false);
        // Vériciation du prix pour 45 minutes en moto
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @Test
    @DisplayName("Test parking CAR sur 45 minutes")
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,false);
        // Vériciation du prix pour 45 minutes en voiture
        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    @DisplayName("Test du prix CAR sur 24h")
    public void calculateFareCarWithMoreThanADayParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,false);
        // Vérification du prix voiture sur 24h
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    @DisplayName("Si le type de véhicule n'est pas identifié")
    public void  calculateFareUnknowParking(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.TRUCK,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        try{
            fareCalculatorService.calculateFare(ticket,false);
        }catch(Exception e){
            final String expected = "Unkown Parking Type";
            assertEquals(expected,e.getMessage());
        }
    }

    @Test
    @DisplayName("Test des 30 premières minutes")
    public void calculateFare30FreeTest(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - ( 01 * 30 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,false);
        fareCalculatorService.calculateFare(ticket,false);
        // Vérification des 30 premiers minutes gratuites
        assertEquals( 0 , ticket.getPrice());
    }

}

