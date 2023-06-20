package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
    private static final double FREE_PARKING_DURATION_IN_MILLIS = 30 * 60 * 1000; // 30 minutes en millisecondes

    public void calculateFare(Ticket ticket) {
        calculateFare(ticket, false);
    }

    public void calculateFare(Ticket ticket, boolean discount) {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket cannot be null");
        }

        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect: " + ticket.getOutTime().toString());
        }

        double durationInMilliseconds = ticket.getOutTime().getTime() - ticket.getInTime().getTime();

        if (durationInMilliseconds <= FREE_PARKING_DURATION_IN_MILLIS) {
            ticket.setPrice(0);
        } else {
            double durationInHours = durationInMilliseconds / (60 * 60 * 1000);
            double ratePerHour;

            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ratePerHour = Fare.CAR_RATE_PER_HOUR;
                    break;
                }
                case BIKE: {
                    ratePerHour = Fare.BIKE_RATE_PER_HOUR;
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown Parking Type");
            }

            double totalPrice = durationInHours * ratePerHour;
            if (discount) {
                totalPrice *= 0.95; // 5% de remise pour les utilisateurs récurrents
            }
            ticket.setPrice(totalPrice);
        }
    }
}
