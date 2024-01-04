package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;
    private final Date IN_TIME = new Date(2023, Calendar.OCTOBER, 20, 1, 0);
    private final Date OUT_TIME = new Date(2023, Calendar.OCTOBER, 20, 2, 0);

    @BeforeAll
    static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar(){
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(IN_TIME);
        ticket.setOutTime(OUT_TIME);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,false);
        assertEquals(0.5*Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }

    @Test
    public void calculateFareBike(){

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(IN_TIME);
        ticket.setOutTime(OUT_TIME);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,false);
        assertEquals(ticket.getPrice(), 0.5*Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType(){

        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(IN_TIME);
        ticket.setOutTime(OUT_TIME);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket, false));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        Date outTime = new Date(2022, Calendar.OCTOBER, 20, 1, 0); // outTime one year before inTime
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(IN_TIME);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket,false ));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        Date outTime = new Date(2023, Calendar.OCTOBER, 20, 1, 45); // 45 minutes should result 25% of the rate per hour
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(IN_TIME);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,false );
        assertEquals((0.25 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){

        Date outTime = new Date(2023, Calendar.OCTOBER, 20, 1, 45); // 45 minutes should result 25% of the rate per hour
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(IN_TIME);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,false );
        assertEquals( (0.25*Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        Date inTime = new Date(2023,10, 19, 0, 0);
        inTime.setTime(inTime.getTime());//24 hours parking time should give 24 (- 0.5 free) * parking fare per hour
        Date outTime = new Date(2023, Calendar.OCTOBER, 20, 0, 0);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,false );
        assertEquals( (23.5*Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithLessThan30minutesParkingTime(){
        Date outTime = new Date(2023, Calendar.OCTOBER, 20, 1, 30);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(IN_TIME);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,false );
        assertEquals( (0) , ticket.getPrice());
    }

    @Test
    public void calculateFareBikeWithLessThan30minutesParkingTime(){
        Date outTime = new Date(2023, Calendar.OCTOBER, 20, 1, 30);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(IN_TIME);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, false);
        assertEquals( (0) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithDiscountParkingTime(){

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(IN_TIME);
        ticket.setOutTime(OUT_TIME);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,true );
        assertEquals( (0.95 * 0.5 *Fare.CAR_RATE_PER_HOUR ) , ticket.getPrice());
    }

    @Test
    public void calculateFareBikeWithDiscountParkingTime(){
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(IN_TIME);
        ticket.setOutTime(OUT_TIME);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, true );
        assertEquals( (0.95*0.5*Fare.BIKE_RATE_PER_HOUR ) , ticket.getPrice());
    }

}
