package onl.tesseract.tesseractlib.bddfacade;


import onl.tesseract.tesseractlib.TesseractLib;
import onl.tesseract.tesseractlib.cosmetics.familier.Pet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PetFacade {
    final static String bddtable = "t_player_pet";


    public static List<Pet> getPets(UUID playerUUID){
        List<Pet> out = new ArrayList<>();

        try{
            Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT pet FROM "+bddtable+" WHERE playerUUID = ?");
            preparedStatement.setString(1,playerUUID.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                out.add(Pet.valueOf(resultSet.getString(1)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return out;
    }

    public static void addPet(UUID player, Pet pet){
        try
        {
            Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO " + bddtable + " (playerUUID, pet) VALUES (?,?)");
            preparedStatement.setString(1, player.toString());
            preparedStatement.setString(2, pet.toString());
            preparedStatement.execute();
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }

    public static void removePet(UUID uuid, Pet pet)
    {
        try
        {
            Connection connection = TesseractLib.getBddManager().getBddConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM " + bddtable + " WHERE playerUUID=? AND pet =?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, pet.toString());
            preparedStatement.execute();
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }
}
