package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ParkingSpotDaoTest {

    private DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private ParkingSpotDAO parkingSpotDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        dataBaseTestConfig = new DataBaseTestConfig();
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;

        // Supprimer les enregistrements des emplacements 3 et 6 de la table des tickets
        try (Connection con = dataBaseTestConfig.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM ticket WHERE PARKING_NUMBER IN (3, 6)")) {
            ps.executeUpdate();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Marquer les emplacements 3 et 6 comme disponibles
        ParkingSpot spot3 = new ParkingSpot(3, ParkingType.CAR, true);
        ParkingSpot spot6 = new ParkingSpot(6, ParkingType.CAR, true);
        parkingSpotDAO.updateParking(spot3);
        parkingSpotDAO.updateParking(spot6);
    }

    @Test
    @DisplayName("Test de récupération du prochain emplacement disponible pour une voiture")
    public void testGetNextAvailableSlotForCar() {
        int nextSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertEquals(3, nextSlot, "Le prochain emplacement disponible pour une voiture doit être 1");
    }

    @Test
    @DisplayName("Test de récupération du prochain emplacement disponible pour une moto")
    public void testGetNextAvailableSlotForBike() {
        assertEquals(4, parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE),
                "Le prochain emplacement disponible pour une moto doit être 4");
    }

    @Test
    @DisplayName("Test de mise à jour de la disponibilité d'un emplacement de parking")
    public void testUpdateParking() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        assertTrue(parkingSpotDAO.updateParking(parkingSpot),
                "La mise à jour de la disponibilité de l'emplacement de parking doit réussir");
    }

    @Test
    @DisplayName("Test de mise à jour réussie de la disponibilité d'un emplacement de parking")
    public void testSuccessfulUpdateParking() {
        ParkingSpot parkingSpot = new ParkingSpot(3, ParkingType.CAR, true);
        boolean result = parkingSpotDAO.updateParking(parkingSpot);
        assertTrue(result, "La mise à jour de la disponibilité de l'emplacement de parking doit réussir");
    }

    @Test
    @DisplayName("Test de mise à jour échouée de la disponibilité d'un emplacement de parking")
    public void testFailedUpdateParking() {
        ParkingSpot parkingSpot = new ParkingSpot(999, ParkingType.CAR, true);
        boolean result = parkingSpotDAO.updateParking(parkingSpot);
        assertFalse(result, "La mise à jour de la disponibilité de l'emplacement de parking doit échouer");
    }

}
