package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.NoSuchElementException;

public class DataBasePrepareService {

    DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    public void clearDataBaseEntries(){
        Connection connection = null;
        try{
            connection = dataBaseTestConfig.getConnection();

            //set parking entries to available
            connection.prepareStatement("update parking set available = true").execute();

            //clear ticket entries;
            connection.prepareStatement("truncate table ticket").execute();

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }

    /**
     * Connect to DB and send SQL request to find out if the vehicle plate exists
     * @param vehicleRegNumber
     * @return true or false
     */
    public boolean ticketExistsForVehicleRegNumber(final String vehicleRegNumber) {
        try (Connection connection = dataBaseTestConfig.getConnection()) {
            final PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) as quantity FROM ticket WHERE ticket.VEHICLE_REG_NUMBER = ?");
            ps.setString(1, vehicleRegNumber);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("quantity") > 0;
            } else {
                throw new NoSuchElementException("Empty ResultSet");
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Connect to DB and send SQL request to find out if the slot is available or not
     * @param parkingNumber
     * @return true or false
     */
    public boolean slotAvailable(final int parkingNumber) {
        try (Connection connection = dataBaseTestConfig.getConnection()) {
            final PreparedStatement ps = connection.prepareStatement("SELECT * FROM parking WHERE PARKING_NUMBER = ?;");
            ps.setInt(1, parkingNumber);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("available") > 0;
            } else {
                throw new NoSuchElementException("Empty ResultSet");
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Connect to DB and send SQL request to find out if the column price and out time are well informed for the vehicle plate
     * @param vehicleRegNumber
     * @return true or false
     */
    public boolean checkPriceAndOutTimeNotNull(String vehicleRegNumber) {
        try (Connection connection = dataBaseTestConfig.getConnection()) {
            final PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) as quantity FROM ticket WHERE VEHICLE_REG_NUMBER = ? and price is not null and out_time is not null;");
            ps.setString(1, vehicleRegNumber);
            final ResultSet rs = ps.executeQuery();     
            if (rs.next()) {
                return rs.getInt("quantity") > 0;
            } else {
                throw new NoSuchElementException("Empty ResultSet");
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}