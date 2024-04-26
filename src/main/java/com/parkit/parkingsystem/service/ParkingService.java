package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
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


	public void processIncomingVehicle() {
		logger.info("Je rentre dans la méthode processIncomingVehicle()");
		int id = 0;
		try {
			ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
			if (parkingSpot != null && parkingSpot.getId() > 0) {
				String vehicleRegNumber = getVehichleRegNumber();
				parkingSpot.setAvailable(false);
				parkingSpotDAO.updateParking(parkingSpot);

				Date inTime = new Date();
				Ticket ticket = new Ticket(id, parkingSpot, vehicleRegNumber, 0, inTime, null);
				logger.debug(ticket);
				ticketDAO.saveTicket(ticket);
				logger.debug("Generated Ticket and saved in DB");
				logger.debug("Please park your vehicle in spot number:" + parkingSpot.getId());
				logger.debug("Recorded in-time for vehicle number:" + vehicleRegNumber + " is:" + inTime);
			}else {
				logger.debug("Generated Ticket and saved in DB");
			}
		} catch (Exception e) {
			logger.error("parkingSpot == null || parkingSpot.getId() <= 0");
		}
	}

	private String getVehichleRegNumber() throws Exception {
		System.out.println("Please type the vehicle registration number and press enter key");
		return inputReaderUtil.readVehicleRegistrationNumber();
	}

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

			}
		} catch (IllegalArgumentException ie) {
			logger.error("Error parsing user input for type of vehicle", ie);
		} catch (Exception e) {
			logger.error("Error fetching next available parking slot", e);
		}
		return parkingSpot;
	}

	public ParkingType getVehichleType() {
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

	public void processExitingVehicle() {
		logger.info("Je rentre dans la méthode processExitingVehicle()");
		try {
			String vehicleRegNumber = getVehichleRegNumber();
			logger.debug(vehicleRegNumber);
			Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
			logger.debug(ticket);
			logger.debug(ticket.getOutTime());
			if (ticket != null) {
				Date outTime = new Date();
				ticket.setOutTime(outTime);
				logger.debug(outTime);

			} else {
				logger.debug("Ticket or outTime is null");
			}

			int nbOfTickets = ticketDAO.getNbTicket(vehicleRegNumber);
			logger.debug(nbOfTickets);

			if(nbOfTickets > 1) {
				fareCalculatorService.calculateFareWithDiscount(ticket, true);
			} else {
				fareCalculatorService.calculateFareDiscountToFalse(ticket);
			}
			if (ticketDAO.updateTicket(ticket)) {
				ParkingSpot parkingSpot = ticket.getParkingSpot();
				parkingSpot.setAvailable(true);
				parkingSpotDAO.updateParking(parkingSpot);

				logger.debug(ticketDAO.getNbTicket(vehicleRegNumber));

				System.out.println("Please pay the parking fare:" + ticket.getPrice());
			} else {
				System.out.println("Unable to update ticket information. Error occurred");

			}
		} catch (Exception e) {
			logger.error("Unable to process exiting vehicle", e);
		}
	}

}
