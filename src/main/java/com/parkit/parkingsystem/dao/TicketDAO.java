package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    // Ajout de la méthode setDataBaseConfig pour faciliter l'injection de dépendances
    public void setDataBaseConfig(DataBaseConfig dataBaseConfig) {
        this.dataBaseConfig = dataBaseConfig;
    }

    public boolean saveTicket(Ticket ticket) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.SAVE_TICKET);
            ps.setInt(1, ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
            ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : new Timestamp(ticket.getOutTime().getTime()));
            int result = ps.executeUpdate();
            return (result > 0);
        } catch (ClassNotFoundException e) {
            logger.error("Erreur de classe non trouvée lors de l'enregistrement du ticket", e);
        } catch (SQLException e) {
            logger.error("Erreur SQL lors de l'enregistrement du ticket", e);
        } catch (Exception ex) {
            logger.error("Erreur inattendue lors de l'enregistrement du ticket", ex);
        } finally {
            // Fermeture des ressources
            dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }

    public Ticket getTicket(String vehicleRegNumber) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Ticket ticket = null;
        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.GET_TICKET);
            ps.setString(1, vehicleRegNumber);
            rs = ps.executeQuery();
            if (rs.next()) {
                ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(2));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(3));
                ticket.setInTime(rs.getTimestamp(4));
                ticket.setOutTime(rs.getTimestamp(5));
            }
        } catch (ClassNotFoundException e) {
            logger.error("Erreur de classe non trouvée lors de la récupération du ticket", e);
        } catch (SQLException e) {
            logger.error("Erreur SQL lors de la récupération du ticket", e);
        } catch (Exception ex) {
            logger.error("Erreur inattendue lors de la récupération du ticket", ex);
        } finally {
            // Fermeture des ressources
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        }
        return ticket;
    }

    public boolean updateTicket(Ticket ticket) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
            ps.setInt(3, ticket.getId());
            int result = ps.executeUpdate();
            return (result > 0);
        } catch (ClassNotFoundException e) {
            logger.error("Erreur de classe non trouvée lors de la mise à jour du ticket", e);
        } catch (SQLException e) {
            logger.error("Erreur SQL lors de la mise à jour du ticket", e);
        } catch (Exception ex) {
            logger.error("Erreur inattendue lors de la mise à jour du ticket", ex);
        } finally {
            // Fermeture des ressources
            dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }

    public int getNbTicket(String vehicleRegNumber) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.GET_TICKET_COUNT);
            ps.setString(1, vehicleRegNumber);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (ClassNotFoundException e) {
            logger.error("Erreur de classe non trouvée lors du comptage des tickets", e);
        } catch (SQLException e) {
            logger.error("Erreur SQL lors du comptage des tickets", e);
        } catch (Exception ex) {
            logger.error("Erreur inattendue lors du comptage des tickets", ex);
        } finally {
            // Fermeture des ressources
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        }
        return count;
    }
}
