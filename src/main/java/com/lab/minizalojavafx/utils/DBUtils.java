package com.lab.minizalojavafx.utils;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class DBUtils {
    private Connection conn;

    public Connection connectToDB() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mini_zalo", "root", "123456");
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean checkUsernameExist(String username) {
        try (Connection conn = connectToDB();
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM client WHERE username = ?")) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkEmailExist(String email) {
        try (Connection conn = connectToDB();
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM client WHERE email = ?")) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registerUser(String username, String email, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        try (Connection conn = connectToDB();
             PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO client (username, email, password) VALUES (?, ?, ?)")) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, hashedPassword);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loginUser(String username, String password) {
        try (Connection conn = connectToDB();
             PreparedStatement ps = conn.prepareStatement("SELECT password FROM client WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    return BCrypt.checkpw(password, hashedPassword);
                }
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
