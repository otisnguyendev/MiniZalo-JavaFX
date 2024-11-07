package com.lab.minizalojavafx.util;

import com.lab.minizalojavafx.model.Attachment;
import com.lab.minizalojavafx.model.Message;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public void saveMessageToDatabase(String sender, String recipient, String content) {
        String query = "INSERT INTO message (sender_id, receiver_id, content) VALUES (?, ?, ?)";

        try (Connection conn = connectToDB();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            int senderId = getClientIdByUsername(sender);
            int recipientId = getClientIdByUsername(recipient);

            if (senderId != -1 && recipientId != -1) {
                stmt.setInt(1, senderId);
                stmt.setInt(2, recipientId);
                stmt.setString(3, content);

                stmt.executeUpdate();
                System.out.println("Tin nhắn đã được lưu vào cơ sở dữ liệu.");
            } else {
                System.err.println("Không thể tìm thấy ID cho người gửi hoặc người nhận.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getClientIdByUsername(String username) {
        String query = "SELECT id FROM client WHERE username = ?";
        try (Connection conn = connectToDB();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Message> getMessagesBetweenUsers(String sender, String recipient) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT m.id, s.username AS sender, r.username AS recipient, m.content, m.timestamp " +
                "FROM message m " +
                "JOIN client s ON m.sender_id = s.id " +
                "JOIN client r ON m.receiver_id = r.id " +
                "WHERE (s.username = ? AND r.username = ?) " +
                "   OR (s.username = ? AND r.username = ?) " +
                "ORDER BY m.timestamp";

        try (Connection conn = connectToDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, sender);
            pstmt.setString(2, recipient);
            pstmt.setString(3, recipient);
            pstmt.setString(4, sender);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String senderName = rs.getString("sender");
                    String recipientName = rs.getString("recipient");
                    String content = rs.getString("content");
                    Timestamp timestamp = rs.getTimestamp("timestamp");

                    messages.add(new Message(id, senderName, recipientName, content, timestamp.toLocalDateTime()));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }


    public List<String> searchClients(String query) {
        List<String> clients = new ArrayList<>();
        String sql = "SELECT username, email FROM client WHERE username LIKE ? OR email LIKE ?";

        try (Connection conn = connectToDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + query + "%");
            pstmt.setString(2, "%" + query + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    clients.add(rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    public void saveAttachment(int messageId, String filePath, String fileType) {
        String query = "INSERT INTO attachments (message_id, file_path, file_type) VALUES (?, ?, ?)";
        try (Connection conn = connectToDB();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, messageId);
            stmt.setString(2, filePath);
            stmt.setString(3, fileType);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Attachment> getAttachmentsByMessageId(int messageId) {
        List<Attachment> attachments = new ArrayList<>();
        String query = "SELECT * FROM attachment WHERE message_id = ?";
        try (Connection conn = connectToDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, messageId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String filePath = rs.getString("file_path");
                    String fileType = rs.getString("file_type");
                    attachments.add(new Attachment(id, messageId, filePath, fileType));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attachments;
    }

    public int getLastInsertedMessageId(String sender, String recipient) {
        String query = "SELECT id FROM message WHERE sender_id = ? AND receiver_id = ? ORDE;R BY timestamp DESC LIMIT 1";
        try (Connection conn = connectToDB();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            int senderId = getClientIdByUsername(sender);
            int recipientId = getClientIdByUsername(recipient);

            stmt.setInt(1, senderId);
            stmt.setInt(2, recipientId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public boolean emailExists(String email) {
        String query = "SELECT * FROM client WHERE email = ?";
        try (Connection conn = connectToDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean resetPassword(String email, String newPassword) {
        if (!emailExists(email)) {
            System.out.println("Email không tồn tại trong hệ thống.");
            return false;
        }
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        String query = "UPDATE client SET password = ? WHERE email = ?";

        try (Connection conn = connectToDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, email);
            int rowsUpdated = pstmt.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


}
