package com.parkit.parkingsystem.constants;

public class Fare {
    public static final double BIKE_RATE_PER_HOUR = 1.0;
    public static final double CAR_RATE_PER_HOUR = 1.5;

    public static double getCarRatePerHour() {
        return CAR_RATE_PER_HOUR;
    }
    public static double getBikeRatePerHour() {
        return BIKE_RATE_PER_HOUR;
    }
}