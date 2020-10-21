package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * Start App.
 */
public class App {
    /**
     * @see Logger
     */
    private static final Logger LOGGER = LogManager.getLogger("App");

    /**
     * Main.
     * @param args arg
     */
    public static void main(final String[] args) {
        LOGGER.info("Initializing Parking System");
        try {
            Scanner scan = new Scanner(System.in);
            InteractiveShell.loadInterface(scan);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
