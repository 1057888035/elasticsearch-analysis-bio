package org.lifesci.bio.elasticsearch.service.impl;

import org.lifesci.bio.elasticsearch.service.DictionaryService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MysqlDictionary implements DictionaryService {

    private String url;

    private String username;

    private String password;


    private Connection connection;

    public MysqlDictionary(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException("DB create error");
        }
    }

    @Override
    public boolean isLike(String token) {
        try {
            String sql = "select `name` from bio_dictionary where `name` = ?" ;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            List<String> names = new ArrayList<>();
            while (resultSet.next()) {
                names.add(resultSet.getString(1));
            }
            if (names.isEmpty() || (names.size() == 1 && names.get(0).equals(token))) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException("");
        }
    }

    @Override
    public void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException("connection error");
        }
    }
}
