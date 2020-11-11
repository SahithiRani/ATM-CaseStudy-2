package cs19b011;

import java.sql.*;

public class connection {
	
	private static Connection c = null;

	public static Connection connection(){
		
		try {
			if(c == null) {
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:accounts.db");
				
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		return c;
	}

}
