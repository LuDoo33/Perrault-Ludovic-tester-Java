package com.parkit.parkingsystem.constants;

public class DBConstants {

    public static final String GET_NEXT_PARKING_SPOT = 
        "SELECT MIN(PARKING_NUMBER) FROM parking WHERE AVAILABLE = TRUE AND TYPE = ?";
        
    public static final String UPDATE_PARKING_SPOT = 
        "UPDATE parking SET available = ? WHERE PARKING_NUMBER = ?";
        
    public static final String SAVE_TICKET = 
        "INSERT INTO ticket (PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) VALUES (?, ?, ?, ?, ?)";
        
    public static final String UPDATE_TICKET = 
        "UPDATE ticket SET PRICE = ?, OUT_TIME = ? WHERE ID = ?";
        
    public static final String GET_TICKET = 
        "SELECT t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE " +
        "FROM ticket t JOIN parking p ON p.parking_number = t.parking_number " +
        "WHERE t.VEHICLE_REG_NUMBER = ? ORDER BY t.IN_TIME LIMIT 1";
        
    // Nouvelle constante pour compter les tickets d'un véhicule
    public static final String GET_TICKET_COUNT = 
        "SELECT COUNT(*) FROM ticket WHERE VEHICLE_REG_NUMBER = ?";
}
