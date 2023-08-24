package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

  public void calculateFare(Ticket ticket){
    if (ticket == null || ticket.getParkingSpot() == null) {
        throw new IllegalArgumentException("Invalid ticket or parking spot");
    }

    if (ticket.getOutTime() == null) {
        throw new IllegalArgumentException("Out time not provided");
    }

    if (ticket.getOutTime().before(ticket.getInTime())) {
        throw new IllegalArgumentException("Out time is before in time");
    }

    long durationInMillis = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
double durationInHours = durationInMillis / (1000.0 * 60.0 * 60.0); // Not rounded down

if (durationInHours <= 0) {
    throw new IllegalArgumentException("Duration cannot be zero or negative");
}

// If duration is less than 1 hour but greater than zero, it will just charge based on actual duration


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

    ticket.setPrice(durationInHours * rate);
}
}