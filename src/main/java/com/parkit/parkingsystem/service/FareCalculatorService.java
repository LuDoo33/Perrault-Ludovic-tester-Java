package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
    private static final double FREE_PARKING_DURATION_IN_MILLIS = 30 * 60 * 1000;  // 30 minutes en millisecondes

    public void calculateFare(Ticket ticket) {
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
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice(durationInHours * Fare.CAR_RATE_PER_HOUR);
                    break;
                }
                case BIKE: {
                    ticket.setPrice(durationInHours * Fare.BIKE_RATE_PER_HOUR);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown Parking Type");
            }
        }
    }
}
