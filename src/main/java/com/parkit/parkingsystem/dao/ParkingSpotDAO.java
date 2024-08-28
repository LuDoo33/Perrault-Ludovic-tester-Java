package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

public class ParkingSpotDAO {
    private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    public int getNextAvailableSlot(ParkingType parkingType){
        Connection con = null;
        int result = -1;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
            ps.setString(1, parkingType.toString());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getInt(1);
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        }catch (Exception ex){
            logger.error("Error fetching next available slot", ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
        return result;
    }

    public boolean updateParking(ParkingSpot parkingSpot){
        //mettre à jour la disponibilité de cette place de parking
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
            ps.setBoolean(1, parkingSpot.isAvailable());
            ps.setInt(2, parkingSpot.getId());
            int updateRowCount = ps.executeUpdate();
            dataBaseConfig.closePreparedStatement(ps);
            return (updateRowCount == 1);
        }catch (Exception ex){
            logger.error("Error updating parking info", ex);
            return false;
        }finally {
            dataBaseConfig.closeConnection(con);
        }
    }

    // Nouvelle méthode pour obtenir une place de parking par ID
    public ParkingSpot getParkingSpot(int id) {
        Connection con = null;
        ParkingSpot parkingSpot = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_PARKING_SPOT);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ParkingType parkingType = ParkingType.valueOf(rs.getString("TYPE"));
                boolean isAvailable = rs.getBoolean("AVAILABLE");
                parkingSpot = new ParkingSpot(id, parkingType, isAvailable);
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        } catch (Exception ex) {
            logger.error("Error fetching parking spot", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
        }
        return parkingSpot;
    }

    // Nouvelle méthode pour mettre à jour une place de parking
    public boolean updateParkingSpot(ParkingSpot parkingSpot) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
            ps.setBoolean(1, parkingSpot.isAvailable());
            ps.setInt(2, parkingSpot.getId());
            int updateRowCount = ps.executeUpdate();
            dataBaseConfig.closePreparedStatement(ps);
            return (updateRowCount == 1);
        } catch (Exception ex) {
            logger.error("Error updating parking spot", ex);
            return false;
        } finally {
            dataBaseConfig.closeConnection(con);
        }
    }
}
