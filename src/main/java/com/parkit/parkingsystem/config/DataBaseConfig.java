package com.parkit.parkingsystem.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sonatype.plexus.components.sec.dispatcher.model.ConfigProperty;

import java.io.*;
import java.sql.*;
import java.util.Properties;

public class DataBaseConfig {

    private static final Logger logger = LogManager.getLogger("DataBaseConfig");

    public Connection getConnection() throws SQLException {
        logger.info("Create DB connection");
        String user = null;
        String pass = null;
        Properties properties = new Properties();
        String path = "./src/main/resources/Config.properties";

        try{
            FileInputStream test = new FileInputStream(new File(path));
            properties.load(test);
             user = properties.getProperty("username");
             pass = properties.getProperty("password");
            Class.forName("com.mysql.cj.jdbc.Driver");
            test.close();

        }catch( IOException | ClassNotFoundException e ){
            logger.error(e);
        }
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/prod?useLegacyDatetimeCode=false&serverTimezone=UTC",user,pass);

    }

    public void closeConnection(Connection con){
        if(con!=null){
            try {
                con.close();
                logger.info("Closing DB connection");
            } catch (SQLException e) {
                logger.error("Error while closing connection",e);
            }
        }
    }

    public void closePreparedStatement(PreparedStatement ps) {
        if(ps!=null){
            try {
                ps.close();
                logger.info("Closing Prepared Statement");
            } catch (SQLException e) {
                logger.error("Error while closing prepared statement",e);
            }
        }
    }

    public void closeResultSet(ResultSet rs) {
        if(rs!=null){
            try {
                rs.close();
                logger.info("Closing Result Set");
            } catch (SQLException e) {
                logger.error("Error while closing result set",e);
            }
        }
    }
}
