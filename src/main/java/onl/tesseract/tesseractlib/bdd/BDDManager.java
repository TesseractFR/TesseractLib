package onl.tesseract.tesseractlib.bdd;

import java.sql.SQLException;

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
            throwables.printStackTrace();
        }
    }
}
