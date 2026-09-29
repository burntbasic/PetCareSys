package com.petcare.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Activity;

public class ActivityLogDAO {
	public List<Activity> getRecentActivity() throws SQLException, DatabaseConfigException {
		
		List<Activity> activities = new ArrayList<>();
		
		String sql = """
				SELECT 
				CONCAT(u.first_name, ' ', u.last_name) AS user,
				a.activity,
				a.activity_date
				FROM ActivityLog a
				JOIN Users u ON a.user_id = u.user_id
				ORDER BY a.activity_date DESC
				LIMIT 10;
				""";
		
		try(Connection connection = DBConnection.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)) {
			
			ResultSet result = statement.executeQuery();
			
			while(result.next()) {
				Activity activity = new Activity(
						result.getString("user"),
						result.getString("activity"),
						result.getTimestamp("activity_date"));
				
				activities.add(activity);
			}
			return activities;
		}
	}
	
	public boolean addActivity(int userId, String activity) throws SQLException, DatabaseConfigException {

		String sql = "INSERT INTO ActivityLog (user_id, activity) VALUES (?, ?)";

		try(Connection connection = DBConnection.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, userId);
			statement.setString(2, activity);

			return statement.executeUpdate() > 0;
		}
	}

}
