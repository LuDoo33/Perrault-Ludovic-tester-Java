package com.parkit.parkingsystem.service;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
	private static final Logger logger = LogManager.getLogger("FareCalculator");
	
    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null)){
        	logger.info("getInTime: " + ticket.getInTime());
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        
        double inHour = dateToHour(ticket.getInTime());
        double outHour = dateToHour(ticket.getOutTime());
        
        if( inHour > outHour ){
        	logger.info("getInTime: " + ticket.getInTime());
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        
        double duration = outHour - inHour;
        
        logger.info("inHour : " + inHour + " | outHour : " + outHour + " | duration : " + duration);

        double price = 0;
        if(duration > 0.5) {
        	price = duration;
        	switch (ticket.getParkingSpot().getParkingType()){
	            case CAR: {
	            	price *= Fare.CAR_RATE_PER_HOUR;
	                break;
	            }
	            case BIKE: {
	            	price *= Fare.BIKE_RATE_PER_HOUR;
	                break;
	            }
	            default: throw new IllegalArgumentException("Unkown Parking Type");
	        }
        	if(ticket.isRecuringMember())price *= 0.95;
        }
        ticket.setPrice(price);
    }
    
    /**@author Mo-BiuS
     * @param d Date
     * @return double representing the number of hour elapsed from the 1st January 2000.
     */
    protected double dateToHour(Date d) {
    	
    	
    	double year = d.getYear()-101;
    	if(year % 4 == 0) year *= 8784;
    	else year *= 8760;
    	
    	double month = 0;
    	for(int m = 1; m < d.getMonth(); m++) {
        	if(m == 2) {
            	if(d.getYear() % 4 == 0) month += 696;
            	else month += 672;
        	}
        	else if(m % 2 == 0) month += 720;
        	else month += 744;
    	}
    	
    	double day = (d.getDate()-1)*24;
    	
    	double hour = d.getHours();
    	
    	double minute = ((double)d.getMinutes())/60;
    	
    	return year + month + day + hour + minute;
    }
}