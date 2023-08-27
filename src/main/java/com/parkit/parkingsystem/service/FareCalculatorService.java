package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

/**
 * This class is used to calculate the fare for a given ticket.
 */
public class FareCalculatorService {

    /**
     * Calculate the fare for a given ticket.
     * 
     * @param ticket
     * @throws Exception
     */
    public void calculateFare(Ticket ticket) {
        try {
            validate(ticket);

            double durationInHours = getDurationInHours(ticket);
            double rate = getRate(ticket);

            ticket.setPrice(durationInHours * rate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validate(Ticket ticket) throws Exception {
        if (ticket == null || ticket.getParkingSpot() == null) {
            throw new IllegalArgumentException("Invalid ticket or parking spot");
        }

        if (ticket.getOutTime() == null) {
            throw new IllegalArgumentException("Out time not provided");
        }

        if (ticket.getOutTime().before(ticket.getInTime())) {
            throw new IllegalArgumentException("Out time is before in time");
        }
    }

    private double getRate(Ticket ticket) throws Exception {
        double rate;
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR:
                rate = Fare.CAR_RATE_PER_HOUR;
                break;
            case BIKE:
                rate = Fare.BIKE_RATE_PER_HOUR;
                break;
            default:
                throw new IllegalArgumentException("Unknown Parking Type");
        }
        return rate;
    }

    private double getDurationInHours(Ticket ticket) throws Exception {
        long durationInMillis = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
        double durationInHours = durationInMillis / (1000.0 * 60.0 * 60.0);

        if (durationInHours <= 0) {
            throw new IllegalArgumentException("Duration cannot be zero or negative");
        }
        return durationInHours;
    }
}
