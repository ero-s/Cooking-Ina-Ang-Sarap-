// DatabaseManager.java
package com.example.cookingina.database;

import com.example.cookingina.model.LevelData;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dbcookingina";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // Initialize database schema (call once at startup)
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Create tbllevel
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS tbllevel (
                levelid INT PRIMARY KEY AUTO_INCREMENT,
                targetincome DECIMAL(10,2) NOT NULL,
                maxcustomers INT NOT NULL,
                timelimit INT NOT NULL,
                patiencelevel INT NOT NULL
            ) ENGINE=InnoDB""");

            // 2. Create tblplayer with strict defaults
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS tblplayer (
                playerid INT PRIMARY KEY AUTO_INCREMENT,
                username VARCHAR(255) NOT NULL UNIQUE,
                password VARCHAR(255) NOT NULL,
                displayname VARCHAR(255) NOT NULL DEFAULT '',
                joindate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                levelid INT NOT NULL DEFAULT 1,
                FOREIGN KEY (levelid)
                    REFERENCES tbllevel(levelid)
                    ON DELETE SET DEFAULT
            ) ENGINE=InnoDB""");

            // 3. Insert default level (id=1) if missing
            stmt.executeUpdate("""
            INSERT IGNORE INTO tbllevel
            (levelid, targetincome, maxcustomers, timelimit, patiencelevel)
            VALUES (1, 100.00, 5, 300, 60)
        """);

            // 4. Fix historical NULL values (if any)
            stmt.executeUpdate("""
            UPDATE tblplayer 
            SET levelid = 1 
            WHERE levelid IS NULL
        """);

            // 5. Enforce NOT NULL constraint
            stmt.executeUpdate("""
            ALTER TABLE tblplayer 
            MODIFY levelid INT NOT NULL DEFAULT 1
        """);

        } catch (SQLException e) {
            e.printStackTrace();
            // Return 1 on error (if converting to int return type)
        }
    }
    // Get player's current level
    public static int getPlayerLevel(String username) {
        String sql = "SELECT levelid FROM tblplayer WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt("levelid") : 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }
    }

    // Update player's progress
    public static void updatePlayerLevel(String username, int newLevel) {
        String sql = "UPDATE tblplayer SET levelid = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newLevel);
            pstmt.setString(2, username);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static LocalDateTime getJoinDate(String username) {
        String sql = "SELECT joindate FROM tblplayer WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("joindate");
                return timestamp != null ? timestamp.toLocalDateTime() : null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDisplayName(String username) {
        String sql = "SELECT displayname FROM tblplayer WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("displayname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username; // fallback to username
    }

    public static int registerUser(String username, String password) {
        String sql = "INSERT INTO tblplayer (username, password, displayname) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, username);
            pstmt.executeUpdate();
            return 0; // Success

        } catch (SQLException e) {
            // Check for duplicate username error code (MySQL error 1062)
            if (e.getErrorCode() == 1062) {
                return 1; // Username exists
            } else {
                e.printStackTrace();
                return 2; // Other errors
            }
        }
    }

    public static boolean validateLogin(String username, String password) {
        String sql = "SELECT * FROM tblplayer WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            return pstmt.executeQuery().next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getPlayerId(String username) {
        String sql = "SELECT playerid FROM tblplayer WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("playerid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // not found
    }

    public static List<LevelData> getAllLevelsWithPlayerLevel(String username) {
        List<LevelData> levels = new ArrayList<>();
        int playerLevel = getPlayerLevel(username);

        String sql = "SELECT levelid, targetincome, maxcustomers, timelimit, patiencelevel FROM tbllevel ORDER BY levelid ASC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                LevelData level = new LevelData();
                level.levelId = rs.getInt("levelid");
                level.targetIncome = rs.getDouble("targetincome");
                level.maxCustomers = rs.getInt("maxcustomers");
                level.timeLimit = rs.getInt("timelimit");
                level.patienceLevel = rs.getInt("patiencelevel");
                level.unlocked = level.levelId <= playerLevel;
                levels.add(level);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return levels;
    }

    public static List<LevelData> getAllLevels(int currentLevel) {
        List<LevelData> levels = new ArrayList<>();

        String sql = "SELECT * FROM tbllevel ORDER BY levelid";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                LevelData data = new LevelData();
                data.levelId = rs.getInt("levelid");
                data.targetIncome = rs.getDouble("targetincome");
                data.maxCustomers = rs.getInt("maxcustomers");
                data.timeLimit = rs.getInt("timelimit");
                data.patienceLevel = rs.getInt("patiencelevel");
                data.unlocked = data.levelId <= currentLevel;  // determine unlock status

                levels.add(data);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return levels;
    }

}