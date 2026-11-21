package com.mbogo.sufeeds;



import java.sql.Connection;
import java.sql.SQLException;

public class contest {

    public static void main(String[] args) {

        // try-with-resources: the connection is closed automatically when the block ends
        try (Connection conn = Connections.getConnection()) {

            // This line only runs if getConnection() succeeded
            System.out.println("Connected to db_morgan_mbogo_227060!");

        } catch (SQLException e) {
            // This runs if anything goes wrong, instead of crashing the program
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}

