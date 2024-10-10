package com.lab.minizalojavafx.utils;

import java.sql.*;

public class DBUtils {
    private Connection conn;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private Statement statement;

    public Connection connectToDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/minizalo", "root", "123456");
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean checkUsernameExist(String username) {
        try {
            preparedStatement = conn.prepareStatement("SELECT * FROM user WHERE username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkEmailExist(String email) {
        try {
            preparedStatement = conn.prepareStatement("SELECT * FROM user WHERE email = ?");
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void registerUser (String username, String email, String password) {
        try {
            preparedStatement = conn.prepareStatement("INSERT INTO user (username, email, password) VALUES (?, ?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}