package com.parkit.parkingsystem;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    public static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    public void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // 1 heure
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice(), 0.01);
    }

    @Test
    public void calculateFareBike() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // 1 heure
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(Fare.BIKE_RATE_PER_HOUR, ticket.getPrice(), 0.01);
    }

    @Test
    public void calculateFareUnknownType() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // 1 heure
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000)); // Temps futur
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000)); // 45 minutes de stationnement, devrait donner 3/4 du tarif
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0.75 * Fare.BIKE_RATE_PER_HOUR, ticket.getPrice(), 0.01);
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000)); // 45 minutes de stationnement, devrait donner 3/4 du tarif
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0.75 * Fare.CAR_RATE_PER_HOUR, ticket.getPrice(), 0.01);
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000)); // 24 heures de stationnement, devrait donner 24 * tarif horaire
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(24 * Fare.CAR_RATE_PER_HOUR, ticket.getPrice(), 0.01);
    }

    /* LuDo = Véhicule <30min = gratuit */

    @Test
    public void calculateFareCarWithLessThan30minutesParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (20 * 60 * 1000)); // 20 minutes de stationnement
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        System.out.println("Le Prix est donc de " + ticket.getPrice() + "€");
        assertEquals(0, ticket.getPrice(), 0.01);
    }

    @Test
    public void calculateFareBikeWithLessThan30minutesParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (20 * 60 * 1000)); // 20 minutes de stationnement
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        System.out.println("Le Prix est donc de " + ticket.getPrice() + "€");
        assertEquals(0, ticket.getPrice(), 0.01);
    }

   
/* LuDo Client régulier = 5% de remise */
//#region Etape 3
@Test
    public void calculateFareCarWithDiscount() {
        // Créer un objet Date pour l'heure actuelle
        Date inTime = new Date();
        // Définir l'heure de sortie comme l'heure actuelle plus 31 minutes pour garantir plus de 30 minutes
        Date outTime = new Date(System.currentTimeMillis() + (31 * 60 * 1000)); // 31 minutes de stationnement
    
        // Créer un objet ParkingSpot pour une voiture
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    
        // Configurer les détails du ticket
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
    
        // Appliquer la réduction en simulant que le véhicule est d'un utilisateur régulier
        fareCalculatorService.calculateFare(ticket, true); // Appliquer la réduction
    
        // Calculer le tarif normal pour la durée de stationnement
        double normalFare = (31 / 60.0) * Fare.CAR_RATE_PER_HOUR; // Tarif pour 31 minutes
    
        // Calculer le tarif attendu avec une réduction de 5%
        double expectedFare = normalFare * 0.95; // 5% de réduction
    
        // Afficher des informations pour le débogage
        System.out.println("Test de la voiture avec réduction.");
        System.out.println("Heure d'arrivée : " + ticket.getInTime());
        System.out.println("Heure de départ : " + ticket.getOutTime());
        System.out.println("Prix calculé : " + ticket.getPrice() + "€" );
    
        // Vérifier que le prix calculé est correct avec la réduction
        assertEquals(expectedFare, ticket.getPrice(), 0.01, "Le tarif doit être 95% du tarif normal pour une voiture avec réduction.");
    }

    @Test
    public void calculateFareBikeWithDiscount() {
        // Créer un objet Date pour l'heure actuelle
        Date inTime = new Date();
        // Définir l'heure de sortie comme l'heure actuelle plus 31 minutes pour garantir réduction après 30 minutes
        Date outTime = new Date(System.currentTimeMillis() + (31 * 60 * 1000)); // 31 minutes de stationnement
    
        // Créer un objet ParkingSpot pour une moto
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
    
        // Configurer les détails du ticket
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
    
        // Appliquer la réduction en simulant que le véhicule est d'un utilisateur régulier
        fareCalculatorService.calculateFare(ticket, true); // Appliquer la réduction
    
        // Calculer le tarif normal pour la durée de stationnement
        double normalFare = (31 / 60.0) * Fare.BIKE_RATE_PER_HOUR; // Tarif pour 31 minutes
    
        // Calculer le tarif attendu avec une réduction de 5%
        double expectedFare = normalFare * 0.95; // 5% de réduction
    
        // Afficher des informations pour le débogage
        System.out.println("Test de la moto avec réduction.");
        System.out.println("Heure d'arrivée : " + ticket.getInTime());
        System.out.println("Heure de départ : " + ticket.getOutTime());
        System.out.println("Prix calculé : " + ticket.getPrice() + "€" );
    
        // Vérifier que le prix calculé est correct avec la réduction
        assertEquals(expectedFare, ticket.getPrice(), 0.01, "Le tarif doit être 95% du tarif normal pour une moto avec réduction.");
    }




    
}
