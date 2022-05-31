package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import java.util.Date;

/**
 * Class to calculate parking fare.
 */
public class FareCalculatorService {

    private static final double THIRTY_MINUTES = 0.5;

    /**
     * Calculates parking fare based on the time user spent on the parking.
     *
     * @param ticket used for calculating fare
     */
    public void calculateFare(Ticket ticket) {
        if((ticket.getOutTime()==null) || (ticket.getOutTime().before(ticket.getInTime()))){
            throw new IllegalArgumentException(
                "Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double duration = getDuration(ticket);

        if(duration<THIRTY_MINUTES){
            ticket.setPrice(0);
        }else{
            double percentage = ticket.isRecurringClient() ? 0.95 : 1.0;
            switch(ticket.getParkingSpot().getParkingType()){

                case CAR:{
                    ticket.setPrice(duration*Fare.CAR_RATE_PER_HOUR*percentage);
                    break;
                }
                case BIKE:{
                    ticket.setPrice(duration*Fare.BIKE_RATE_PER_HOUR*percentage);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
        }
    }

    private double getDuration(Ticket ticket) {
        Date inHour = ticket.getInTime();
        Date outHour = ticket.getOutTime();

        long durationInMilliseconds = outHour.getTime()-inHour.getTime();
        return (durationInMilliseconds/(1000*60))/60.0;
    }
}