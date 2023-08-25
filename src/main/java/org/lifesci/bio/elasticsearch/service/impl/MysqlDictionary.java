package org.lifesci.bio.elasticsearch.service.impl;

import org.lifesci.bio.elasticsearch.service.DictionaryService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
            Statement stmt = connection.createStatement();
        } catch (Exception e) {
            throw new RuntimeException("");
        }

        return false;
    }

    @Override
    public void closeConnection() throws SQLException {
        connection.close();
    }
}
