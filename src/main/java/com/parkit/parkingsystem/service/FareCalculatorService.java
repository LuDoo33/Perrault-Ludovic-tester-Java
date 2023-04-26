package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import java.time.Duration;
import java.util.Date;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, Boolean isRecurrent){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double duration;
        double price;
        Date inHour = ticket.getInTime();
        Date outHour = ticket.getOutTime();
        Duration difference = Duration.between(inHour.toInstant(), outHour.toInstant());

        //free if less than 30 minutes
        if(difference.toMinutes() <= 30) {
            duration = 0;
        }
        //if more than 30 but less than 45 minutes
        else if (difference.toMinutes() <= 45) {
            duration = 0.75;
        }
        else
        {
            duration = difference.toHours();
        }

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                price = duration * Fare.CAR_RATE_PER_HOUR;
                break;
            }
            case BIKE: {
                price = duration * Fare.BIKE_RATE_PER_HOUR;
                break;
            }
            default: throw new IllegalArgumentException("Unknown Parking Type");
        }
        ticket.setPrice(isRecurrent? price * 95 / 100 : price);
    }
}
