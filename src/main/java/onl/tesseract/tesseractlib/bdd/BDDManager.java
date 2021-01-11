package onl.tesseract.tesseractlib.bdd;

import java.sql.SQLException;

public class BDDManager {

    private BDDConnection bddConnection;

    public BDDManager(){
       this.bddConnection = new BDDConnection(new BDD("localhost","user","","TesseractBase",3306));
    }

    public void close() throws SQLException {
        bddConnection.close();
    }
}
