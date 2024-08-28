package com.parkit.parkingsystem.integration.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;

public class DataBaseTestConfig extends DataBaseConfig {

    private static final Logger logger = LogManager.getLogger("DataBaseTestConfig");

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        logger.info("Création de la base de donnée");
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/test","root","rootroot");
    }

    public void closeConnection(Connection con){
        if(con!=null){
            try {
                con.close();
                logger.info("Fermeture de la connexion à la base de données");
            } catch (SQLException e) {
                logger.error("Erreur lors de la fermeture de la connexion",e);
            }
        }
    }

    public void closePreparedStatement(PreparedStatement ps) {
        if(ps!=null){
            try {
                ps.close();
                logger.info("Fermeture Prepared Statement");
            } catch (SQLException e) {
                logger.error("Erreur fermeture prepared statement",e);
            }
        }
    }

    public void closeResultSet(ResultSet rs) {
        if(rs!=null){
            try {
                rs.close();
                logger.info("Fermeture Result Set");
            } catch (SQLException e) {
                logger.error("Erreur lors de la fermeture result set",e);
            }
        }
    }
}
