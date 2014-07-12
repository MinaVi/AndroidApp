package com.sw.minavi.activity.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCManager {


	public Connection getConnection() throws SQLException {

		Connection conn = DriverManager.getConnection("mysql://localhost:3306/worker_db", "root", "root");
		return conn;
	}
}
