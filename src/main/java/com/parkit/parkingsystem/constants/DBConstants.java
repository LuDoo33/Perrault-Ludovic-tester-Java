package com.parkit.parkingsystem.constants;

public class DBConstants {

    // Requête pour obtenir le prochain numéro de parking disponible
    public static final String GET_NEXT_PARKING_SPOT = 
        "SELECT MIN(PARKING_NUMBER) FROM parking WHERE AVAILABLE = TRUE AND TYPE = ?";
        
    // Requête pour mettre à jour la disponibilité d'un espace de parking
    public static final String UPDATE_PARKING_SPOT = 
        "UPDATE parking SET available = ? WHERE PARKING_NUMBER = ?";
        
    // Requête pour enregistrer un nouveau ticket
    public static final String SAVE_TICKET = 
        "INSERT INTO ticket (PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) VALUES (?, ?, ?, ?, ?)";
        
    // Requête pour mettre à jour le ticket (prix et heure de sortie)
    public static final String UPDATE_TICKET = 
        "UPDATE ticket SET PRICE = ?, OUT_TIME = ? WHERE ID = ?";
        
    // Requête pour récupérer le ticket le plus récent pour un véhicule
    public static final String GET_TICKET = 
        "SELECT t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE " +
        "FROM ticket t JOIN parking p ON p.parking_number = t.parking_number " +
        "WHERE t.VEHICLE_REG_NUMBER = ? ORDER BY t.IN_TIME DESC LIMIT 1";
        
    // Requête pour compter le nombre de tickets pour un véhicule
    public static final String GET_TICKET_COUNT = 
        "SELECT COUNT(*) FROM ticket WHERE VEHICLE_REG_NUMBER = ?";
}
