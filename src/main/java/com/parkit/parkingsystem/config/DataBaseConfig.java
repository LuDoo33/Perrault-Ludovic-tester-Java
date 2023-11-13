package com.parkit.parkingsystem.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class DataBaseConfig {

//Logger classe qui permet d'envoyer des messages dans le système de log
	// getLogger() pour obtenir une instance du logger à utiliser
	private static final Logger logger = LogManager.getLogger("DataBaseConfig");

	// Pour se connecter à une BDD il faut instancier un objet de la classe
	// Connection JDBC
	public Connection getConnection() throws ClassNotFoundException, SQLException {

		// émet un message avec le niveau de gravité INFO, correspond à des messages
		// d'informations

		logger.info("Create DB connection");
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connection = null;
		// DriverManager classe qui agit comme une interface entre utilisateur et
		// pilotes.
		// Il gère l'établissement d'une connexion entre une bdd et le pilote approprié.
		try {
			connection = DriverManager.getConnection(
					// préciser sous forme d'URL la base à accéder , nom user + password
					"jdbc:mysql://127.0.0.1:3306/prod", "root", "rootroot");

		} catch (SQLException e) {
			logger.error("Connection denied");
			//throw new Exception("");
		}
		return connection;
	}

	public void closeConnection(Connection con) {
		if (con != null) {
			try {
				// méthode close() ferme connexion et libère immédiatement une ressource JDBC
				con.close();
				logger.info("Closing DB connection");
			} catch (SQLException e) {
				logger.error("Error while closing connection", e);
			}
		}
	}

	public void closePreparedStatement(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
				logger.info("Closing Prepared Statement");
			} catch (SQLException e) {
				logger.error("Error while closing prepared statement", e);
			}
		}
	}

	public void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
				logger.info("Closing Result Set");
			} catch (SQLException e) {
				logger.error("Error while closing result set", e);
			}
		}
	}
}
