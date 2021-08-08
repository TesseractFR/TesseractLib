package onl.tesseract.tesseractlib.bddfacade;

import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.equipment.invocable.Elytra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ElytraTrailsFacade {

    static private final String bddtable = "t_player_elytra_trails";

    public static List<Elytra.Trail> getPlayedTrails(UUID playerUUID)
    {

        List<Elytra.Trail> list = new ArrayList<>();
        try
        {
            Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT trails FROM " + bddtable + " WHERE player_uuid=?");
            preparedStatement.setString(1, playerUUID.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                list.add(Elytra.Trail.valueOf(resultSet.getString(1)));
            }
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return list;
    }


    public static void addTrail(UUID playerUUID, Elytra.Trail trail){
        try{
            Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO "+bddtable+" (player_uuid, trails) VALUES (?,?)");
            preparedStatement.setString(1,playerUUID.toString());
            preparedStatement.setString(2,trail.toString());
            preparedStatement.execute();
            return;
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        throw new NullPointerException("Trail can't be added");
    }

    public static void removeTrail(UUID playerUUID, Elytra.Trail trail){
        try{
            Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM "+bddtable+" WHERE player_uuid = ? AND trails= ?");
            preparedStatement.setString(1,playerUUID.toString());
            preparedStatement.setString(2,trail.toString());
            preparedStatement.execute();
            return;
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        throw new NullPointerException("Trail can't be removed");
    }

    public static boolean hasTrail(UUID playerUUID, Elytra.Trail trail){
        try{
            Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM "+bddtable+" WHERE player_uuid = ? AND trails= ?");
            preparedStatement.setString(1,playerUUID.toString());
            preparedStatement.setString(2,trail.toString());
            return (preparedStatement.executeQuery().next());
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        throw new NullPointerException("Trail can't be removed");
    }
}
