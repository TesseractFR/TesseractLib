package onl.tesseract.tesseractlib.bddfacade;

import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.cosmetics.Cosmetic;
import onl.tesseract.tesseractlib.cosmetics.CosmeticType;
import onl.tesseract.tesseractlib.familier.Pet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CosmeticFacade {
    public static Map<CosmeticType, Set<Cosmetic>> getAll(UUID uuid)
    {

        Map<CosmeticType, Set<Cosmetic>> out = new HashMap<>();
        try
        {Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT cosmetic_type,cosmetic FROM t_player_cosmetics WHERE player_uuid = ?");
            preparedStatement.setString(1,uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                CosmeticType type = CosmeticType.valueOf(resultSet.getString(1));
                Cosmetic cosmetic = null;
                switch (type){

                    case PET -> {
                        cosmetic = Pet.valueOf(resultSet.getString(2));
                    }
                    case ELYTRA_TRAIL -> {
                    }
                    default -> {
                        throw new IllegalArgumentException();
                    }
                }
                if(!out.containsKey(type))
                    out.put(type,new HashSet<>());
                out.get(type).add(cosmetic);
            }
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return out;
    }
}
