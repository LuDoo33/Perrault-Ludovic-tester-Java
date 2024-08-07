package com.parkit.parkingsystem.service;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

public class ParkingService {

    private static final Logger logger = LogManager.getLogger("ParkingService");

    private InputReaderUtil inputReaderUtil;
    private ParkingSpotDAO parkingSpotDAO;
    private TicketDAO ticketDAO;
    private FareCalculatorService fareCalculatorService;

    // Constructeur avec FareCalculatorService
    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO, FareCalculatorService fareCalculatorService) {
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
        this.fareCalculatorService = fareCalculatorService;
    }

    // Constructeur sans FareCalculatorService pour les tests
    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
        this(inputReaderUtil, parkingSpotDAO, ticketDAO, new FareCalculatorService());
    }

    public void processIncomingVehicle() {
        try {
            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            if (parkingSpot != null && parkingSpot.getId() > 0) {
                String vehicleRegNumber = getVehicleRegNumber();
                parkingSpot.setAvailable(false);
                parkingSpotDAO.updateParking(parkingSpot); // Allouer cet espace de stationnement et marquer sa disponibilité comme fausse

                Date inTime = new Date();
                Ticket ticket = new Ticket();
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(0);
                ticket.setInTime(inTime);
                ticket.setOutTime(null);
                ticketDAO.saveTicket(ticket);

                // Vérifier si le véhicule est un utilisateur régulier
                int numTickets = ticketDAO.getNbTicket(vehicleRegNumber);
                if (numTickets > 1) {
                    System.out.println("Heureux de vous revoir ! En tant qu’utilisateur régulier de notre parking, vous allez obtenir une remise de 5%");
                }

                System.out.println("Ticket généré et sauvegardé dans la base de données");
                System.out.println("Veuillez garer votre véhicule au numéro d'emplacement : " + parkingSpot.getId());
                System.out.println("Heure d'arrivée enregistrée pour le numéro de véhicule : " + vehicleRegNumber + " est : " + inTime);
            }
        } catch (Exception e) {
            logger.error("Impossible de traiter le véhicule entrant", e);
        }
    }

    private String getVehicleRegNumber() throws Exception {
        System.out.println("Veuillez taper le numéro d'immatriculation du véhicule et appuyer sur la touche Entrée");
        return inputReaderUtil.readVehicleRegistrationNumber();
    }

    public ParkingSpot getNextParkingNumberIfAvailable() {
        int parkingNumber = 0;
        ParkingSpot parkingSpot = null;
        try {
            ParkingType parkingType = getVehicleType();
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
            System.out.println(parkingNumber);
            if (parkingNumber > 0) {
                parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
            } else {
                throw new Exception("Erreur lors de la récupération du numéro de parking depuis la base de données. Les places de parking pourraient être complètes");
            }
        } catch (IllegalArgumentException ie) {
            logger.error("Erreur lors de l'analyse de l'entrée de l'utilisateur pour le type de véhicule", ie);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du prochain emplacement de parking disponible", e);
        }
        return parkingSpot;
    }

    private ParkingType getVehicleType() {
        System.out.println("Veuillez sélectionner le type de véhicule dans le menu");
        System.out.println("1 VOITURE");
        System.out.println("2 MOTO");
        int input = inputReaderUtil.readSelection();
        switch (input) {
            case 1:
                return ParkingType.CAR;
            case 2:
                return ParkingType.BIKE;
            default:
                System.out.println("Entrée incorrecte fournie");
                throw new IllegalArgumentException("L'entrée saisie est invalide");
        }
    }

    public void processExitingVehicle() {
        try {
            String vehicleRegNumber = getVehicleRegNumber();
            Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
            Date outTime = new Date();
            ticket.setOutTime(outTime);

            // Vérifier si le véhicule est un utilisateur régulier
            int numTickets = ticketDAO.getNbTicket(vehicleRegNumber);
            boolean discount = numTickets > 1;

            // Calculer le tarif avec ou sans réduction en fonction du statut de l'utilisateur
            fareCalculatorService.calculateFare(ticket, discount);

            if (ticketDAO.updateTicket(ticket)) {
                ParkingSpot parkingSpot = ticket.getParkingSpot();
                parkingSpot.setAvailable(true);
                parkingSpotDAO.updateParking(parkingSpot);
                System.out.println("Veuillez payer le tarif de stationnement : " + ticket.getPrice());
                System.out.println("Heure de sortie enregistrée pour le numéro de véhicule : " + ticket.getVehicleRegNumber() + " est : " + outTime);
            } else {
                System.out.println("Impossible de mettre à jour les informations du ticket. Une erreur est survenue");
            }
        } catch (Exception e) {
            logger.error("Impossible de traiter le véhicule sortant", e);
        }
    }
}
