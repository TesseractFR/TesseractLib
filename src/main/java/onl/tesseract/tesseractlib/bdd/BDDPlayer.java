package onl.tesseract.tesseractlib.bdd;

import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.player.TPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BDDPlayer {
    static private final String bddtable = "t_player";
    BDDConnection bddconnection ;
    TPlayer tPlayer ;

    public BDDPlayer(TPlayer tPlayer) {
        this.bddconnection = TesseractLib.getBddManager().getBddConnection();
        this.tPlayer = tPlayer;
    }
    public void registerAll(){
        try {
            final Connection  connection = bddconnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO "+bddtable+" (uuid,genre) VALUE (?,?)");
            preparedStatement.setString(1,tPlayer.getBukkitPlayer().getUniqueId().toString());
            preparedStatement.setString(2,""+tPlayer.getGender().toBdd());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void setGender(TPlayer.Gender gender){
        try {
            final Connection  connection = bddconnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE "+bddtable+" SET genre = ? WHERE uuid = ?");
            preparedStatement.setString(1,""+gender.toBdd());
            preparedStatement.setString(2,tPlayer.getBukkitPlayer().getUniqueId().toString());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public TPlayer.Gender getGender(){
        try {
            final Connection connection = bddconnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT genre FROM "+bddtable + " WHERE uuid = "+tPlayer.getBukkitPlayer().getUniqueId().toString());
            return TPlayer.Gender.valueOf(preparedStatement.executeQuery().getString("genre"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return TPlayer.Gender.OTHER;
    }
}
