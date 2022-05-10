package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        Date inHour = ticket.getInTime();
        Date outHour = ticket.getOutTime();

        long duration = outHour.getTime() - inHour.getTime();
        long durationMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);

        double ratio = 1;
        double fare = 0;
        if (durationMinutes <= 30) {
            ratio = 0;
        } else if (ticket.isAlreadyCame()) {
            ratio = 0.95;
        }

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                fare = Fare.CAR_RATE_PER_HOUR;
                break;
            }
            case BIKE: {
                fare = Fare.BIKE_RATE_PER_HOUR;
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }

        ticket.setPrice(durationMinutes * fare / 60);
        BigDecimal result = BigDecimal.valueOf(ticket.getPrice()).multiply(BigDecimal.valueOf(ratio));
        ticket.setPrice(result.doubleValue());
    }
}