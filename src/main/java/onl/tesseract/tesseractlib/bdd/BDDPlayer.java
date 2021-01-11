package onl.tesseract.tesseractlib.bdd;

import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.player.TPlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BDDPlayer {
    static private final String bddtable = "t_player";
    BDDConnection bddconnection;
    TPlayer tPlayer;

    public BDDPlayer(TPlayer tPlayer) {
        this.bddconnection = TesseractLib.getBddManager().getBddConnection();
        this.tPlayer = tPlayer;
    }
    public void registerAll(){
        try {
            final Connection  connection = bddconnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO "+bddtable+" (uuid,genre) VALUE (?,?)");
            preparedStatement.setString(1,tPlayer.getBukkitPlayer().getUniqueId().toString());
            preparedStatement.setString(2,""+tPlayer.getGender().toBdd());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void saveGender(){
        try {
            final Connection  connection = bddconnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE "+bddtable+" SET genre = ? WHERE uuid = ?");
            preparedStatement.setString(1,""+tPlayer.getGender().toBdd());
            preparedStatement.setString(2,tPlayer.getBukkitPlayer().getUniqueId().toString());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


}
