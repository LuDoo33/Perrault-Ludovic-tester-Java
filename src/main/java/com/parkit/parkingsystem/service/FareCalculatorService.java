package com.parkit.parkingsystem.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
    private static final double DISCOUNT_RATE = 0.95; // Taux de réduction de 5%
    private static final int DISCOUNT_THRESHOLD_MINUTES = 30; // Seuil pour la réduction en minutes

    public void calculateFare(Ticket ticket) {
        // Appel de la méthode surchargée sans remise
        calculateFare(ticket, false);
    }

    public void calculateFare(Ticket ticket, boolean discount) {
        if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
            throw new IllegalArgumentException("L'heure de sortie fournie est incorrecte : " + ticket.getOutTime());
        }

        // Calculer la durée en millisecondes
        long durationInMillis = ticket.getOutTime().getTime() - ticket.getInTime().getTime();

        // Convertir la durée en minutes
        double durationInMinutes = durationInMillis / (60.0 * 1000.0);

        // Si le temps de stationnement est inférieur à 30 minutes, les frais sont gratuits
        if (durationInMinutes < DISCOUNT_THRESHOLD_MINUTES) {
            ticket.setPrice(0);
            System.out.println("Le temps de stationnement est inférieur à 30 minutes, C'est gratuit!!!!!!!!.");
            return;
        }

        // Convertir la durée en heures
        double durationInHours = durationInMillis / (60.0 * 60.0 * 1000.0);

        // Vérifier le type de stationnement et calculer le tarif
        if (ticket.getParkingSpot().getParkingType() == null) {
            throw new IllegalArgumentException("Type de stationnement inconnu");
        }

        double price;
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR:
                price = durationInHours * Fare.CAR_RATE_PER_HOUR;
                break;
            case BIKE:
                price = durationInHours * Fare.BIKE_RATE_PER_HOUR;
                break;
            default:
                throw new IllegalArgumentException("Type de stationnement inconnu");
        }

        // Appliquer la remise si le paramètre discount est true
        if (discount) {
            price *= DISCOUNT_RATE; // Appliquer la réduction de 5%
            System.out.println("En tant qu’utilisateur régulier de notre parking, vous allez obtenir une remise de 5%");
        }

        // Arrondir le prix à 2 décimales
        BigDecimal roundedPrice = new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);
        ticket.setPrice(roundedPrice.doubleValue());
    }
}
