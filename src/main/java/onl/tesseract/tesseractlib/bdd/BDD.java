package onl.tesseract.tesseractlib.bdd;

import onl.tesseract.tesseractlib.TesseractLib;

public class BDD {
    private final String host;
    private final String user;
    private final String password;
    private final int port;
    private final String dbName;

    public BDD(String host, String user, String password, String dbName, int port) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
        this.dbName = dbName;
    }

    public String getUrl(){
        TesseractLib.logger().info("jdbc:mysql://"+host+":"+port+"/"+dbName);
        return "jdbc:mysql://"+host+":"+port+"/"+dbName;

    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
