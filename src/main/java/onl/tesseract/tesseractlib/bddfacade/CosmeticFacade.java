package onl.tesseract.tesseractlib.bddfacade;

import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.cosmetics.Cosmetic;
import onl.tesseract.tesseractlib.cosmetics.CosmeticManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CosmeticFacade {
    public static Map<String, Set<Cosmetic>> getAll(UUID uuid)
    {

        Map<String, Set<Cosmetic>> out = new HashMap<>();
        try
        {
            Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT cosmetic_type,cosmetic FROM t_player_cosmetics WHERE player_uuid = ?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                String type = resultSet.getString(1);
                Cosmetic cosmetic;
                try{
                    cosmetic = CosmeticManager.stringToCosmetic(type, resultSet.getString(2));
                }catch (IllegalArgumentException e){
                    continue;
                }
                if (!out.containsKey(type))
                    out.put(type, new HashSet<>());
                out.get(type).add(cosmetic);
            }
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return out;
    }

    public static void add(UUID uuid, String type, Cosmetic cosmetic)
    {
        try
        {
            Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO t_player_cosmetics (player_uuid, cosmetic_type, cosmetic) VALUES (?,?,?)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, type);
            preparedStatement.setString(3, cosmetic.toString());
            preparedStatement.execute();
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }

    public static void remove(UUID uuid, String type, Cosmetic cosmetic)
    {
        try
        {
            Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM t_player_cosmetics WHERE player_uuid= ? AND cosmetic_type = ? AND cosmetic=?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, type);
            preparedStatement.setString(3, cosmetic.toString());
            preparedStatement.executeUpdate();
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

    }
}
