package cs19b011;

import java.sql.*;

public class InsertAccIntoDB {
	
	private Connection connect() {
		// SQLite connection string
		String url = "jdbc:sqlite:accounts.db";
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	

}
