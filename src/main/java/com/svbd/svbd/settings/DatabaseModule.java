package com.svbd.svbd.settings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseModule {

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

    private void createTables(Connection connection) throws URISyntaxException {
        var res = getClass().getClassLoader().getResource("sql");
        var file = Paths.get(res.toURI()).toFile();
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