package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe App point d'entree de l'application
 * systeme de paiement de parking automatise * 
 */	
public class App {
    private static final Logger logger = LogManager.getLogger("App");
    public static void main(String args[]){
        logger.info("Initializing Parking System");
        InteractiveShell.loadInterface();
    }
}
