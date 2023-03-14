package com.parkit.parkingsystem.service;

import java.text.DecimalFormat;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

//        int inHour = ticket.getInTime().getHours();
//        int outHour = ticket.getOutTime().getHours();
        
        double inHour = ticket.getInTime().getTime();  // temps ecoulé en millisecondes
        double outHour = ticket.getOutTime().getTime();// temps ecoulé  en millisecondes
        
        

        //TODO: Some tests are failing here. Need to check if this logic is correct
        double duration = outHour - inHour;  // difference en millisecondes
        DecimalFormat df = new DecimalFormat("###.##");

        duration = duration / 1000.0 / 60.0 / 60.0; // difference en fraction d 'heure
    	System.out.println("Durée de stationnement réelle : " + df.format(duration) + "h");

        duration = duration - 0.50; // On enlève les premières 30 minutes    
        if (duration <0 ) {
        	duration = 0;
        }
    	System.out.println("Le stationnement est gratuit pour les 30 premières minutes.");
    	System.out.println("Durée de stationnement prise encompte : " + df.format(duration) + "h ");

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                TicketDAO ticketDAO = new TicketDAO();
                int nbPreviousOccurence = ticketDAO.getCountPreviousOccurence(ticket.getVehicleRegNumber());
                if (nbPreviousOccurence>1) {
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR * 0.95);
                    System.out.println("Nous avons également appliqué les 5% de remise!");
                } else {
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                    System.out.println("Tarif normal.");
                }
                break;
        }
            case BIKE: {
                TicketDAO ticketDAO = new TicketDAO();
                int nbPreviousOccurence = ticketDAO.getCountPreviousOccurence(ticket.getVehicleRegNumber());
                if (nbPreviousOccurence>1) {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR * 0.95);
                    System.out.println("Nous avons également appliqué les 5% de remise!");
                } else {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                    System.out.println("Tarif normal.");
                }
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}