package com.parkit.parkingsystem.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

public class ParkingSpotDAOTest {

    @Test
    @DisplayName("test si une place est disponible")
    public void testGettingNextAvailableSlot() {

	// GIVEN - ARRANGE
	// WHEN - ACT
	// THEN - ASSERT
    }

    @Test
    @DisplayName("test que la mise à jour d'une place de parking est ok")
    public void testUpdatingParkingSpot() {
	// GIVEN - ARRANGE
	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

	// WHEN - ACT
	ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
	boolean result = parkingSpotDAO.updateParking(parkingSpot);

	// THEN - ASSERT
	assertThat(result).isTrue();
    }

    @Disabled
    @Test
    @DisplayName("test qu'une erreur se produit quand un mauvais parametre est passé à updateParking")
    public void testUpdateParkingOccurError() {
	// GIVEN - ARRANGE
	ParkingSpot parkingSpot = new ParkingSpot(8, ParkingType.BIKE, false);

	// WHEN - ACT
	ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
	boolean result = parkingSpotDAO.updateParking(parkingSpot);

	// THEN - ASSERT
	// LE RETOUR EST FALSE QUAND UNE ERREUR DE MISE A JOUR SE PRODUIT
	// assertThat(result).isFalse();
    }
}
