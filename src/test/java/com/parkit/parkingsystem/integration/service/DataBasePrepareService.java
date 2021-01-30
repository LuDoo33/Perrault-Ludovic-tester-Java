package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;

import java.lang.ref.PhantomReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataBasePrepareService {

  DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

  public void clearDataBaseEntries() {
    PreparedStatement ps1 = null;
    PreparedStatement ps2 = null;
    Connection connection = null;
    try {
      connection = dataBaseTestConfig.getConnection();

      //set parking entries to available
      ps1 = connection.prepareStatement("update parking set available = true");
      ps1.execute();

      //clear ticket entries;
      ps2 = connection.prepareStatement("truncate table ticket");
      ps2.execute();

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      dataBaseTestConfig.closePreparedStatement(ps1);
      dataBaseTestConfig.closePreparedStatement(ps2);
      dataBaseTestConfig.closeConnection(connection);
    }
  }


}
