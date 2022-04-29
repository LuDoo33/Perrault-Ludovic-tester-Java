package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.util.Date;

public class FareCalculatorService {

    private final static double THIRTY_MINUTES=0.5;

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double duration = getDuration(ticket);

        if (duration<THIRTY_MINUTES)
            ticket.setPrice(0);
        else switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

    private double getDuration(Ticket ticket) {
        Date inHour = ticket.getInTime();
        Date outHour = ticket.getOutTime();

        long durationInMilliseconds = outHour.getTime() - inHour.getTime();
        return (durationInMilliseconds/ (1000 * 60)) / 60.0;
    }
}