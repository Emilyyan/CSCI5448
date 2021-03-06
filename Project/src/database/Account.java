package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.howtodoinjava.hashing.password.demo.bcrypt.BCrypt;

public class Account {

	private Connection conn;

	public Account(Connection conn) {
		this.conn = conn;
	}

	public boolean login(String email, String password) throws SQLException {

		String sql = "select password_hash from Authentication where emailAddress=?";

		PreparedStatement stmt = conn.prepareStatement(sql);

		stmt.setString(1, email);

		ResultSet rs = stmt.executeQuery();
		
		String password_hash = "";
		boolean matched = false;
		while (rs.next()){
			password_hash = rs.getString("password_hash");
		}
		stmt.close();
		
		matched = BCrypt.checkpw(password, password_hash);
		return matched;

	}
	
	public void create(String email, String password) throws SQLException {
		
		String generatedSecuredPasswordHash = BCrypt.hashpw(password, BCrypt.gensalt(12));
         
		String sql1 = "insert into User (emailAddress) values (?)";
		PreparedStatement stmt1 = conn.prepareStatement(sql1);
		stmt1.setString(1, email);
		stmt1.executeUpdate();
		stmt1.close();
		
		
		String sql2 = "insert into Authentication (emailAddress,password_hash) values (?,?)";
		PreparedStatement stmt2 = conn.prepareStatement(sql2);
		stmt2.setString(1, email);
		stmt2.setString(2, generatedSecuredPasswordHash);
		stmt2.executeUpdate();
		stmt2.close();
		
	}
	
	public boolean exists(String email) throws SQLException {

		String sql = "select count(*) as count from User where emailAddress=?";

		PreparedStatement stmt = conn.prepareStatement(sql);

		stmt.setString(1, email);

		ResultSet rs = stmt.executeQuery();

		int count = 0;

		if (rs.next()) {
			count = rs.getInt("count");
		}
		
		rs.close();

		if (count == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	
	
	public void post(String email, String title, String type, String description, String contactEmail, String contactName, String status) throws SQLException {

		
		String sql = "INSERT INTO Posting(User_Email, title, type, description,contactEmail, contactName, status) VALUES(?,?,?,?,?,?,?)";

		PreparedStatement stmt = conn.prepareStatement(sql);

		stmt.setString(1, email);
		stmt.setString(2, title);
		stmt.setString(3, type);
		stmt.setString(4, description);
		stmt.setString(5, contactEmail);
		stmt.setString(6, contactName);
		stmt.setString(7, status);
		

		stmt.executeUpdate();
		stmt.close();
	}
	
	public ArrayList<String> selectPosting() throws SQLException{
		
		PreparedStatement stmt = null;
		ArrayList result = new ArrayList<String>();
		String sql = "SELECT status, title, type FROM Posting WHERE User_Email = ?";
		try {
			stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
		
			while (rs.next()) {
				
				String status = rs.getString("status");
				String title = rs.getString("title");
				String type = rs.getString("type");
				result.add(status);
				result.add(title);
				result.add(type);


			}

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		} finally {

			if (stmt != null) {
				stmt.close();
			}

		}
		return result;

	}
}
