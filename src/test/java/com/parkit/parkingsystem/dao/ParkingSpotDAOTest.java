package com.parkit.parkingsystem.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotDAOTest {

    @Mock
    DataBaseConfig dataBaseConfig;

    @Test
    @DisplayName("test si une place de voiture est disponible")
    public void testGettingNextAvailableCarSlot() {

	// GIVEN - ARRANGE
	ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
	// WHEN - ACT
	int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
	// THEN - ASSERT
	assertThat(result).isBetween(1, 5);
    }

    @Test
    @DisplayName("test si une place de moto est disponible")
    public void testGettingNextAvailableBikeSlot() {

	// GIVEN - ARRANGE
	ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
	// WHEN - ACT
	int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE);
	// THEN - ASSERT
	assertThat(result).isBetween(1, 5);
    }

    @Disabled
    @Test
    @DisplayName("test pour une exception levée")
    public void testExceptionThrowsWhenGettingNextAvailableSlot() {

	// GIVEN - ARRANGE
	ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
	// when(dataBaseConfig.getConnection()).thenThrow(new ClassNotFoundException(),
	// new SQLException());

	// WHEN - ACT
	// int result = parkingSpotDAO.getNextAvailableSlot(null);

	// THEN - ASSERT
	// ASSERTTROWS A UTILISER
	// assertThrows(Exception.class, () ->
	// parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR),
	// "une erreur de connexion lève une exception");
	assertThrows(NullPointerException.class, () -> parkingSpotDAO.getNextAvailableSlot(null));
	// assertThat(result).isEqualTo(-1);
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
	// LE TEST PASSE AU VERT MAIS PAS DE MODIFICATION CONSTATEE DANS BDD - A REVOIR
	assertThat(result).isTrue();
    }

    @Disabled
    @Test
    @DisplayName("test qu'une erreur se produit quand un mauvais parametre est passé à updateParking")
    public void testExceptionThrowsWhenUpdateParking() {
	// GIVEN - ARRANGE
	// A CONTINUER...
	// when(dataBaseConfig.getConnection()).thenThrow(new ClassNotFoundException(),
	// new SQLException());
	ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

	// WHEN - ACT
	// assertThrows(ClassNotFoundException.class, SQLException.class, () ->
	// ParkingSpotDAO.updateParking(new ));
	ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
	boolean result = parkingSpotDAO.updateParking(parkingSpot);

	// THEN - ASSERT
	// LE RETOUR DOIT ETRE FALSE QUAND UNE ERREUR DE MISE A JOUR SE PRODUIT
	assertThrows(NullPointerException.class, () -> parkingSpotDAO.updateParking(parkingSpot));
	assertThat(result).isFalse();
    }
}
