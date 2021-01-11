package onl.tesseract.tesseractlib.bdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BDDConnection {
    private BDD database;
    private Connection connection;


    public BDDConnection(BDD database) {
        this.database = database;
        this.connect();
    }


    private void connect(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(database.getUrl(),database.getUser(), database.getPassword());

            System.out.println("[BDD] : connection done");
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
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
