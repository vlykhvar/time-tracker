package com.svbd.svbd.settings;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.*;

public class H2Embedded {

    private static final String URL = "jdbc:h2:~/svbd";

    public void connection() {
        try {
            Connection connection = DriverManager.getConnection(URL);
            System.out.println("connected");
            createTables(connection);
            connection.close();
        } catch (SQLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertQuery(String query) throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        System.out.println("connected");
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(query);
        connection.close();
    }

    public ResultSet getQuery(String query) throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        Statement stmt = connection.createStatement();
        var result = stmt.executeQuery(query);
        connection.close();
        return result;
    }

    public void createTables(Connection connection) throws URISyntaxException {
        URL res = getClass().getClassLoader().getResource("sql");
        File file = Paths.get(res.toURI()).toFile();
        String absolutePath = file.getAbsolutePath();

        try (BufferedReader reader = new BufferedReader(new FileReader(absolutePath))) {
            String line;
            String delimiter = ";";
            StringBuilder buffer = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (buffer.length() == 0 && line.startsWith("DELIMITER")) {
                    delimiter = line.split(" ")[1];
                    continue;
                }
                if (line.startsWith("--") || line.isEmpty()) {
                    continue;
                }
                buffer.append(line.replaceFirst("\\h+$", "")).append("\n");
                if (line.endsWith(delimiter)) {
                    String statement = buffer.toString().replace(delimiter, "");
                    System.out.println("{\n" + statement + "}"); //print the statement to console for debugging purposes
                    Statement stmt = connection.createStatement();
                    stmt.executeUpdate(statement);
                    buffer.setLength(0);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}