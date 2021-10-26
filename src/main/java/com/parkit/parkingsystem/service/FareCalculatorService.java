package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

/**
 * @author Philémon Globléhi <philemon.globlehi@gmail.com>
 */
public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (null == ticket.getOutTime()) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        int inHour = ticket.getInTime().getHours();
        int outHour = ticket.getOutTime().getHours();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        int duration = outHour - inHour;

        int durationWithoutBonusTime = this.bonusTime(duration);

        double discount = 5.0;
        double priceDiscount;
        boolean isRecurrent = false;
        double price;

        TicketDAO ticketDAO = new TicketDAO();
        if (2 <= ticketDAO.countTicket(ticket.getVehicleRegNumber())) {
            isRecurrent = true;
        }

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                price = duration * Fare.CAR_RATE_PER_HOUR;
                if (isRecurrent) {
                    ticket.setPrice(price - this.discount(price, discount));
                } else {
                    ticket.setPrice(price);
                }
                break;
            }
            case BIKE: {
                price = duration * Fare.BIKE_RATE_PER_HOUR;
                if (isRecurrent) {
                    ticket.setPrice(price - this.discount(price, discount));
                } else {
                    ticket.setPrice(price);
                }
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

    public int bonusTime(int fareDuration) {
        final int BONUS_TIME_IN_MILLISECONDS = 30 * 60 * 1000;

        if (BONUS_TIME_IN_MILLISECONDS < fareDuration) {
            return fareDuration - BONUS_TIME_IN_MILLISECONDS;
        }

        return 1;
    }

    public double discount(double intialPrice, double discount) {
        if (0 != discount) {
            return intialPrice - (intialPrice * discount)/100;
        }

        return 0;
    }
}