package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class ParkingService {

	private static final Logger logger = LogManager.getLogger("ParkingService");

	private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

	private InputReaderUtil inputReaderUtil;
	private ParkingSpotDAO parkingSpotDAO;
	private TicketDAO ticketDAO;

	public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
		this.inputReaderUtil = inputReaderUtil;
		this.parkingSpotDAO = parkingSpotDAO;
		this.ticketDAO = ticketDAO;
	}

	// méthode qui sert à attribuer une place aux nouvelles voitures entrantes,
	// génère un ticket et une heure d'entrée, récupère plaque immatriculation
	// enregistre ticket généré dans bdd
	public void processIncomingVehicle() {
		logger.info("Je rentre dans la méthode processIncomingVehicle()");

		try {
			ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
			if (parkingSpot != null && parkingSpot.getId() > 0) {
				String vehicleRegNumber = getVehichleRegNumber();
				parkingSpot.setAvailable(false);
				parkingSpotDAO.updateParking(parkingSpot);// allot this parking space and mark it's availability as
															// false

				Date inTime = new Date();
				Ticket ticket = new Ticket();
				// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
				// ticket.setId(ticketID);
				ticket.setParkingSpot(parkingSpot);
				ticket.setVehicleRegNumber(vehicleRegNumber);
				ticket.setPrice(0);
				ticket.setInTime(inTime);
				ticket.setOutTime(null);
				ticketDAO.saveTicket(ticket);
				logger.debug("Generated Ticket and saved in DB");
				logger.debug("Please park your vehicle in spot number:" + parkingSpot.getId());
				logger.debug("Recorded in-time for vehicle number:" + vehicleRegNumber + " is:" + inTime);
			}
		} catch (Exception e) {
			logger.error("Unable to process incoming vehicle", e);
		}
	}

	// méthode qui permet de lire les données entrées par le clavier depuis la
	// classe inputReaderUtil
	private String getVehichleRegNumber() throws Exception {
		System.out.println("Please type the vehicle registration number and press enter key");
		return inputReaderUtil.readVehicleRegistrationNumber();
	}

//retourne un objet de type ParkingSpot avec le numero de la place disponible, le type de vehicule, 
	public ParkingSpot getNextParkingNumberIfAvailable() {
		logger.info("Je rentre dans la méthode getNextParkingNumberIfAvailable()");
		int parkingNumber = 0;
		ParkingSpot parkingSpot = null;
		try {
			ParkingType parkingType = getVehichleType();
			parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
			logger.debug(parkingType);
			logger.debug(parkingNumber);
			if (parkingNumber > 0) {
				parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
				logger.debug(parkingSpot);
				
			} else {
				throw new Exception("Error fetching parking number from DB. Parking slots might be full");
			}
		} catch (IllegalArgumentException ie) {
			logger.error("Error parsing user input for type of vehicle", ie);
		} catch (Exception e) {
			logger.error("Error fetching next available parking slot", e);
		}
		return parkingSpot;
	}

	private ParkingType getVehichleType() {
		System.out.println("Please select vehicle type from menu");
		System.out.println("1 CAR");
		System.out.println("2 BIKE");
		int input = inputReaderUtil.readSelection();
		switch (input) {
		case 1: {
			return ParkingType.CAR;
		}
		case 2: {
			return ParkingType.BIKE;
		}
		default: {
			System.out.println("Incorrect input provided");
			throw new IllegalArgumentException("Entered input is invalid");
		}
		}
	}
//méthode qui permet de calculer le prix du ticket du vehicule sortant 

	public void processExitingVehicle() {
		logger.info("Je rentre dans la méthode processExitingVehicle()");
		try {
			String vehicleRegNumber = getVehichleRegNumber();
			Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);

			Date outTime = new Date();
			ticket.setOutTime(outTime);
			logger.debug(outTime);
			
			int nbOfTickets = ticketDAO.getNbTicket(vehicleRegNumber);
			/*if (nbOfTickets > 1) {
				fareCalculatorService.calculateFare(ticket, true);

			} else {

				fareCalculatorService.calculateFare(ticket, false);
			}*/
			if (ticketDAO.updateTicket(ticket) && (nbOfTickets > 1)) {
				ParkingSpot parkingSpot = ticket.getParkingSpot();
				parkingSpot.setAvailable(true);
				parkingSpotDAO.updateParking(parkingSpot);
				fareCalculatorService.calculateFare(ticket, true);

				logger.debug(ticketDAO.getNbTicket(vehicleRegNumber));

				System.out.println("Please pay the parking fare:" + ticket.getPrice());
				System.out.println(
						"Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime);
			} else {
				System.out.println("Unable to update ticket information. Error occurred");
				fareCalculatorService.calculateFare(ticket, false);

			}
		} catch (Exception e) {
			logger.error("Unable to process exiting vehicle", e);
		}
	}

}
