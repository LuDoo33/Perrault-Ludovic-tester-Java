package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

import java.util.concurrent.TimeUnit;

public class FareCalculatorService {

    // added here to check if user is recurrent.
    private TicketDAO ticketDAO;


    public FareCalculatorService(TicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;
    }


    /**
     * Calculate fare for a specific ticket.
     * @param ticket the ticket.
     */
    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + (ticket.getOutTime() != null ? ticket.getOutTime().toString() : null));
        }


        //TODO: Some tests are failing here. Need to check if this logic is correct
        // We use Minute instead of hour, because it's more precise
        long durationMin = TimeUnit.MINUTES.convert(ticket.getOutTime().getTime() - ticket.getInTime().getTime(), TimeUnit.MILLISECONDS);
        // By default the price set to 0
        if (durationMin > Fare.FREE_PARK_DURATION_PER_MINUTE) {
            boolean isRecurrentUser= IsRecurrentUser(ticket.getVehicleRegNumber());
            // apply free 30 min
            durationMin = durationMin - Fare.FREE_PARK_DURATION_PER_MINUTE;
            // apply 5% discount for recurrent users
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice(getPrice(durationMin, Fare.CAR_RATE_PER_MINUTE, isRecurrentUser));
                    break;
                }
                case BIKE: {
                    ticket.setPrice(getPrice(durationMin, Fare.BIKE_RATE_PER_MINUTE, isRecurrentUser));
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
        }
    }

    /**
     * This allow to calculate price from duration, amount depending if users is recurrent or not.
     *
     * @param duration    the duration.
     * @param amount      the amount by minutes.
     * @param isRecurrentUser if user is recurrent.
     * @return the calculated price.
     */
    private double getPrice(long duration, double amount, boolean isRecurrentUser) {
        if (isRecurrentUser) {
            return duration * amount * Fare.DISCOUNT_FIVE_PER_CENT;
        }
        return duration * amount;
    }

    /**
     * check if the user is recurrent.
     * @param vehicleRegNumber vehicle registration number.
     * @return boolean indicating if recurrent user.
     */
    private boolean IsRecurrentUser(String vehicleRegNumber){
        return ticketDAO.isRecurrentUser(vehicleRegNumber);
    }

}