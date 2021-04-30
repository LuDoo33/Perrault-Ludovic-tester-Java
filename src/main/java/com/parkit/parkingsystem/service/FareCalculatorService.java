package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean vehicleExist){


        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        long in = ticket.getInTime().getTime();
        long out = ticket.getOutTime().getTime();
        double test = out - in;
        double duration = ((test / 1000) / 60) / 60 ;


        //free 30 minutes
        if (duration <= 0.5) {
            ticket.setPrice(0);
        }else{
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    //5% reduction if exist
                    if (vehicleExist){
                        ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR * 0.95) ;
                        System.out.println("You get 5% réduction");
                    }else{
                        ticket.setPrice(duration*Fare.CAR_RATE_PER_HOUR);
                    }
                    break;
                }
                case BIKE: {
                    //5% reduction if exist
                    if (vehicleExist){
                        ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR * 0.95) ;
                        System.out.println("You get 5% réduction");
                    }else{
                        ticket.setPrice(duration*Fare.BIKE_RATE_PER_HOUR);
                    }
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
        }
    }
}