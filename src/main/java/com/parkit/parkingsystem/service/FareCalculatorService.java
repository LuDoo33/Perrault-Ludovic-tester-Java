package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.dao.TicketDAO;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class FareCalculatorService {
    private static final double FREE_PARKING_DURATION_IN_MILLIS = 30 * 60 * 1000; // 30 minutes en millisecondes
    private final TicketDAO ticketDAO;
    public FareCalculatorService(TicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;
    }
    public void calculateFare(Ticket ticket) {
        System.out.println("Calcul du tarif pour le ticket: " + ticket);
        // Ajoute des logs de débogage pour afficher les valeurs des dates
        System.out.println("InTime: " + ticket.getInTime());
        System.out.println("OutTime: " + ticket.getOutTime());

        // Si c'est un véhicule récurrent, le booléen sera true, sinon false
        boolean isRecurrentUser = ticketDAO.getNbTicket(ticket.getVehicleRegNumber()) > 1;
        calculateFare(ticket, isRecurrentUser);
    }

    public void calculateFare(Ticket ticket, boolean isRecurrentUser) {
        System.out.println("Calcul du tarif pour le ticket: " + ticket);
        if (ticket == null) {
            throw new IllegalArgumentException("Le ticket ne peut pas être null");
        }
        System.out.println("Heure d'entrée: " + ticket.getInTime());
        System.out.println("Heure de sortie: " + ticket.getOutTime());

        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) || ticket.getOutTime().equals(ticket.getInTime())) {
            throw new IllegalArgumentException("La date de sortie indiquée est incorrecte: " + ticket.getOutTime().toString());
        }

        double durationInMilliseconds = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
        System.out.println("Durée en millisecondes: " + durationInMilliseconds);

        if (durationInMilliseconds <= FREE_PARKING_DURATION_IN_MILLIS) {
            ticket.setPrice(0);
            System.out.println("Moins de 30 minutes de stationnement. Tarif: 0");
        } else {
            double durationInHours = durationInMilliseconds / (60 * 60 * 1000);
            double ratePerHour;
            System.out.println("Durée en heures: " + durationInHours);

            ParkingType parkingType = (ticket.getParkingSpot() != null && ticket.getParkingSpot().getParkingType() != null) ? ticket.getParkingSpot().getParkingType() : null;

            if(parkingType == null) {
                throw new IllegalArgumentException("Type de parking inconnu");
            }

            switch (parkingType) {
                case CAR: {
                    ratePerHour = Fare.CAR_RATE_PER_HOUR;
                    break;
                }
                case BIKE: {
                    ratePerHour = Fare.BIKE_RATE_PER_HOUR;
                    break;
                }
                default:
                    throw new IllegalArgumentException("Type de parking inconnu");
            }

            double totalPrice = durationInHours * ratePerHour;
            System.out.println("Prix total avant remise: " + totalPrice);
            if (isRecurrentUser) {
                totalPrice *= 0.95; // 5% de remise pour les utilisateurs récurrents
                System.out.println("Prix avec remise: " + totalPrice);
            }
            ticket.setPrice(totalPrice);
            System.out.println("Prix final: " + ticket.getPrice());
        }
    }
}

