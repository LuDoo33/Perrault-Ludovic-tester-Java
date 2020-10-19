package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Interactive Shell with options.
 */
public class InteractiveShell {

    /**
     * @see Logger
     */
    private static final Logger logger =
            LogManager.getLogger("InteractiveShell");

    /**
     * Load Interface depending on option.
     */
    public static void loadInterface() throws Exception {
        logger.info("App initialized!!!");
        logger.info("Welcome to Parking System!");

        boolean continueApp = true;
        InputReaderUtil inputReaderUtil = new InputReaderUtil();
        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
        TicketDAO ticketDAO = new TicketDAO();
        ParkingService parkingService =
                new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        while (continueApp) {
            loadMenu();
            int option = inputReaderUtil.readSelection();
            switch (option) {
                case 1: {
                    parkingService.processIncomingVehicle();
                    break;
                }
                case 2: {
                    parkingService.processExitingVehicle();
                    break;
                }
                case 3: {
                    logger.info("Exiting from the system!");
                    continueApp = false;
                    break;
                }
                default: logger.info("Unsupported option. Please "
                        + "enter a number corresponding to the provided menu");
            }
        }
    }

    /**
     * Load Menu.
     */
    private static void loadMenu() {
        logger.info("Please select an option."
                + "Simply enter the number to choose an action");
        logger.info("1 New Vehicle Entering - Allocate Parking Space");
        logger.info("2 Vehicle Exiting - Generate Ticket Price");
        logger.info("3 Shutdown System");
    }

}
