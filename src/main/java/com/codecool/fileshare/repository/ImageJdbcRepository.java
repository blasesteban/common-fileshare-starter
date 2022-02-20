package com.codecool.fileshare.repository;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.stereotype.Component;

import java.sql.*;


@Component("jdbc")
public class ImageJdbcRepository implements ImageRepository {

    /*
    *   // implement store image in database here
        // content is base64 coded image file
        // generate and return uuid of stored image
        // https://www.base64-image.de/
        // https://codebeautify.org/base64-to-image-converter
    *
    * */
    @Override
    public String storeImage(String category, String content) {
        String uuid = null;

        if (insertNewImage(category,content)){
            uuid = getLastUUID(content);
        }
        return uuid;
    }

    @Override
    public String readImage(String uuid) {
        // implement readImage from database here
        // return base64 encoded image
        return null;
    }


    private static Connection getConnection() {
        var ds = new PGSimpleDataSource();
        ds.setURL(System.getenv("DB_URL"));
        ds.setUser(System.getenv("DB_USER"));
        ds.setPassword(System.getenv("DB_PASSWORD"));
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static boolean insertNewImage(String category, String content){
        String extension = content.split(";")[0].split("/")[1];
        System.out.println("The extension is"+extension);
        try (PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO image(category,content,extension) VALUES(?,?,?)")) {
            stmt.setString(1,category);
            stmt.setBytes(2,content.getBytes());
            stmt.setString(3,extension);

            int numberOfRowsChanged = stmt.executeUpdate();
            if (numberOfRowsChanged==0){
                return false;
            }else {
                return true;
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return false;
    }
    private static String getLastUUID(String content){
            try (PreparedStatement stmt = getConnection().prepareStatement("select id from image WHERE content=?")) {
                stmt.setBytes(1,content.getBytes());
                ResultSet rs = stmt.executeQuery();
                if(rs.next()){
                    return rs.getString("id");
                }else{
                    System.out.println("No such a content");
                }

            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        return null;
    }
}
