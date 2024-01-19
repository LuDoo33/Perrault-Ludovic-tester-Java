package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean getDiscount){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();
        double durationInMilliseconds = outHour - inHour;

        // Convert milliseconds to hour
        double duration = durationInMilliseconds/3600000;
        double price =0;

        // Calculate the price if the duration exceed 30 minutes else the price is still 0
        if(duration > 0.5) {
            // Remove the first free half hour
            duration = duration - 0.5;
            switch (ticket.getParkingSpot().getParkingType()){
                case CAR: {
                    price = duration * Fare.CAR_RATE_PER_HOUR;
                    break;
                }
                case BIKE: {
                    price = duration * Fare.BIKE_RATE_PER_HOUR;
                    break;
                }
                default: throw new IllegalArgumentException("Unkown Parking Type");
            }
            if(getDiscount) {
                price = 0.95*price;
            }
        }
        ticket.setPrice(price);
    }
}