package onl.tesseract.tesseractlib.bdd;

import onl.tesseract.tesseractlib.TesseractLib;

import java.sql.SQLException;
import java.util.logging.Level;

public class BDDManager {

    private BDDConnection bddConnection;


    public BDDManager(String host, int port, String username, String password, String database) {
        this.bddConnection = new BDDConnection(new BDD(host,username,password,database,port));
    }

    public BDDConnection getBddConnection() {
        return bddConnection;
    }

    public void close() {
        try {
            bddConnection.close();
        } catch (SQLException throwables) {
            TesseractLib.logger().log(Level.SEVERE, "Failed to close database connection", throwables);
        }
    }
}
