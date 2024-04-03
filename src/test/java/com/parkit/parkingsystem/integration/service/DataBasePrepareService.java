package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;

import java.sql.Connection;

public class DataBasePrepareService {

	DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

	public void initDataBase() {
		Connection connection = null;
		try{
			connection = dataBaseTestConfig.getConnection();

			connection.prepareStatement("insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) values(3,\"ABCDEF\",0,\"2022-01-01 12:30:00\", null)").execute();
			System.out.println("statement executed.");
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			dataBaseTestConfig.closeConnection(connection);
		}
	}
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

}
