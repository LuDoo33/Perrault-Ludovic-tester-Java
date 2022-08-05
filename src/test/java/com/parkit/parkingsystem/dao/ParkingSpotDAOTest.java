package com.parkit.parkingsystem.dao;

import static org.assertj.core.api.Assertions.assertThat;

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
	assertThat(result).isBetween(1, 3);
    }

    @Test
    @DisplayName("test si une place de moto est disponible")
    public void testGettingNextAvailableBikeSlot() {
	// GIVEN - ARRANGE
	ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
	// WHEN - ACT
	int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE);
	// THEN - ASSERT
	assertThat(result).isBetween(4, 5);
    }

    @Test
    @DisplayName("test si un type null est passé alors on passe dans exception")
    public void testWhenParameterOfGetNextAvailableSlotIsNull() {
	// GIVEN - ARRANGE
	ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
	// when(parkingSpotDAO.getNextAvailableSlot(null)).thenThrow(new Exception());

	// WHEN - ACT
	int result = parkingSpotDAO.getNextAvailableSlot(null);

	// THEN - ASSERT
	assertThat(result).isEqualTo(-1);
    }

    // DUXIEME BRANCHE DU IF --- A CHERCHER
    @Disabled
    @Test
    @DisplayName("test si un aucun resultat n'est trouvé")
    public void testWhenResultSetOfGetNextAvailableSlotIsEmpty() {
	// GIVEN - ARRANGE
	ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();

	// WHEN - ACT
	int result = parkingSpotDAO.getNextAvailableSlot(null);

	// THEN - ASSERT
	assertThat(result).isEqualTo(-1);
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
	// LE TEST PASSE AU VERT MAIS PAS DE MODIFICATION CONSTATEE DANS BDD - PEUT ETRE
	// A REVOIR
	assertThat(result).isTrue();
    }

    @Test
    @DisplayName("test que la mise à jour n'est pas faite")
    public void testNoUpdateParkingSpot() {
	// GIVEN - ARRANGE

	ParkingSpot parkingSpot = new ParkingSpot(-1, null, false);

	// WHEN - ACT
	ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
	boolean result = parkingSpotDAO.updateParking(parkingSpot);

	// THEN - ASSERT
	assertThat(result).isFalse();
    }

    // FAIRE ECHOUER ACCES A BDD - CAS OPTIONNEL
    @Disabled
    @Test
    @DisplayName("test pour une erreur dans le process de mise à jour")
    public void testCatchingExceptionWhenUpdateParking() {
	// GIVEN - ARRANGE
	ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

	// WHEN - ACT
	ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
	boolean result = parkingSpotDAO.updateParking(parkingSpot);

	// THEN - ASSERT
	assertThat(result).isFalse();
    }
}
