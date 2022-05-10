package com.parkit.parkingsystem.integration.util;



import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@ExtendWith(MockitoExtension.class)
public class InputReaderUtilTest {

    private InputReaderUtil inputReaderUtil;

    @BeforeEach
    private void setUp() {
        inputReaderUtil = new InputReaderUtil();
    }

    @Test
    public void readSelectionTest(){
        // GIVEN : quand on lit l'entrée standard, on lit le chiffre 1
        InputStream inputStream = new ByteArrayInputStream("1".getBytes());
        System.setIn(inputStream);

        // WHEN : appel readSelection
        int input = inputReaderUtil.readSelection();

        // THEN : la méthode a renvoyé le chiffre 1
        assertEquals(1, input);
    }

    @Test
    public void readSelectionTestWithBadInput(){
        // GIVEN : quand on lit l'entrée standard, on lit 'abc'
        InputStream inputStream = new ByteArrayInputStream("abc".getBytes());
        System.setIn(inputStream);

        // WHEN : appel readSelection
        int input = inputReaderUtil.readSelection();

        // THEN : la méthode a renvoyé le chiffre -1
        assertEquals(-1, input);
    }

   @Test
    public void readVehicleRegistrationNumberTest() {
        // GIVEN : quand on lit l'entrée standard, on lit le numéro d'immatriculation ABCDEF
        InputStream inputStream = new ByteArrayInputStream("ABCDEF".getBytes());
        System.setIn(inputStream);

        // WHEN : appel readVehicleRegistrationNumber
        String vehiculeRegNumber = inputReaderUtil.readVehicleRegistrationNumber();

        // THEN : la méthode a renvoyé ABCDEF
        assertEquals("ABCDEF", vehiculeRegNumber);
    }

    @Test
    public void readVehicleRegistrationNumberThrowsIllegalArgumentExceptionTest() {
        // GIVEN : quand on lit l'entrée standard, on lit le numéro d'immatriculation
        InputStream inputStream = new ByteArrayInputStream(" ".getBytes());
        System.setIn(inputStream);

        assertThrows(IllegalArgumentException.class, () -> inputReaderUtil.readVehicleRegistrationNumber());
    }

}
