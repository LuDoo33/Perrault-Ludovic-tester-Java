package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    private double durationTicketBase1(Ticket ticket){
        long inTime = ticket.getInTime().getTime();
        long outTime = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        long duration = outTime - inTime; // Duration millisecond of parking

        long hours = duration / (60 * 60 * 1000); // take integer (nb hours) -> (60*60 = nb seconds in hour -> *1000 = nb milliseconds in hour)
        long remainingMilliseconds = duration % (60 * 60 * 1000); // milliseconds remaining
        long minutes = remainingMilliseconds / (60 * 1000); // (60*1000 = nb milliseconds in minute)

        return hours + ((double) minutes / 60.0); // Casting minutes for return double (decimal) (30(min)/60 = 0.5 + nb hours (hour = 1)

    }

    public void calculateFare(Ticket ticket){
        // OutTime is null or OutTime before InTime
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double durationTicketBase1 = durationTicketBase1(ticket);

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(durationTicketBase1 * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(durationTicketBase1 * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

}