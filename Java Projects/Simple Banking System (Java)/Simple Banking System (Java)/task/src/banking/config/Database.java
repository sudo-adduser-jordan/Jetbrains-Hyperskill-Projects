package banking.config;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {

    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS card
            (id INTEGER PRIMARY KEY AUTOINCREMENT,
            number TEXT NOT NULL,
            pin TEXT NOT NULL,
            balance INTEGER DEFAULT 0)""";

    private final String dbName;
    private String url;
    private final SQLiteDataSource dataSource;

    public Database(String dbName) {
        dataSource = new SQLiteDataSource();
        this.dbName = dbName;
        createDatabase();
        createTable();
    }


    private void createDatabase() {
        if (dbName.contains(".s3db")) {
            url = "jdbc:sqlite:" + dbName;
            dataSource.setUrl(url);
        } else {
            throw new IllegalArgumentException("Wrong file extension: " + dbName);
        }
    }


    private void createTable() {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_TABLE)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }
}