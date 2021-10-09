package onl.tesseract.tesseractlib.bdd;

import onl.tesseract.tesseractlib.TesseractLib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class BDDConnection {
    private final BDD database;
    private Connection connection;


    public BDDConnection(BDD database) {
        this.database = database;
        this.connect();
    }


    private void connect(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(database.getUrl(),database.getUser(), database.getPassword());

            TesseractLib.logger().info("[BDD] : connection done");
        } catch (SQLException | ClassNotFoundException throwables) {
            TesseractLib.logger().log(Level.SEVERE, "Could not connect to database", throwables);
        }
    }

    public void close() throws SQLException {
        if(this.connection != null){
            if(!this.connection.isClosed()){
                this.connection.close();
            }
        }
    }

    public Connection getConnection() throws SQLException {
        if(connection != null){
            if(!connection.isClosed()){
                return connection;
            }
        }
        connect();
        return connection;
    }
}
