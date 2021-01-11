package onl.tesseract.tesseractlib.bdd;

public class BDD {
    private String host;
    private String user;
    private String password;
    private int port;
    private String dbName;

    public BDD(String host, String user, String password, String dbName, int port) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
        this.dbName = dbName;
    }

    public String getUrl(){
        return "jdbc:mysql://"+host+":"+port+"/"+dbName;

    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
