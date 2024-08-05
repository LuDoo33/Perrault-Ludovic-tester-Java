package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime());
        }

        // Calculer la durée en millisecondes
        long durationInMillis = ticket.getOutTime().getTime() - ticket.getInTime().getTime();

        // Convertir la durée en minutes
        double durationInMinutes = durationInMillis / (60.0 * 1000.0);

        // Si le temps de stationnement est inférieur à 30 minutes, les frais sont gratuits
        if (durationInMinutes < 30) {
            ticket.setPrice(0);
            return;
        }

        // Convertir la durée en heures
        double durationInHours = durationInMillis / (60.0 * 60.0 * 1000.0);

        // Vérifier le type de stationnement et calculer le tarif
        if (ticket.getParkingSpot().getParkingType() == null) {
            throw new IllegalArgumentException("Unknown Parking Type");
        }

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR:
                ticket.setPrice(durationInHours * Fare.CAR_RATE_PER_HOUR);
                break;
            case BIKE:
                ticket.setPrice(durationInHours * Fare.BIKE_RATE_PER_HOUR);
                break;
            default:
                throw new IllegalArgumentException("Unknown Parking Type");
        }
    }
}
