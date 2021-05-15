package com.parkit.parkingsystem.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class InputReaderUtilTest {

    private static final Scanner scan = new Scanner(System.in);
    InputReaderUtil inputReaderUtil = new InputReaderUtil();

    @Test
    @DisplayName("Test saisi utilisateur valide")
    public void readSelectionWithGoodValue() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("1".getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(byteArrayInputStream);
        inputReaderUtil.setScanner(scanner);
        assertEquals(inputReaderUtil.readSelection(), 1);
    }
    @Test
    @DisplayName("Test saisi utilisateur invalide")
    public void readSelectionWithBadValue() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("abcd\n".getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(byteArrayInputStream);
        inputReaderUtil.setScanner(scanner);
        assertEquals(inputReaderUtil.readSelection(), -1);
    }

    @Test
    @DisplayName("Test saisi utilisateur string valide")
    public void readhSelectionWithGoodString() throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("abcd\n".getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(byteArrayInputStream);
        inputReaderUtil.setScanner(scanner);
        assertEquals(inputReaderUtil.readVehicleRegistrationNumber(), "abcd");
    }

    @Test
    @DisplayName("Test saisi utilisateur invalide")
    public void readSelectionWithError() throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(byteArrayInputStream);
        inputReaderUtil.setScanner(scanner);
        try {
            inputReaderUtil.readVehicleRegistrationNumber();
        } catch (Exception e) {
            assertEquals("No line found", e.getMessage());
        }
    }

}
