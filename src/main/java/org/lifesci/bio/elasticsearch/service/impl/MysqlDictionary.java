package org.lifesci.bio.elasticsearch.service.impl;

import org.elasticsearch.SpecialPermission;
import org.lifesci.bio.elasticsearch.dic.FilterEntity;
import org.lifesci.bio.elasticsearch.service.DictionaryService;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.*;
import java.util.*;

public class MysqlDictionary extends BaseDictionary implements DictionaryService {

    private static MysqlDictionary mysqlDictionary;
    public static DictionaryService getInstance(String s, String root, String help7777) {
        if (mysqlDictionary == null) {
            mysqlDictionary = new MysqlDictionary(s, root, help7777);
        }
        return mysqlDictionary;
    }

    private String url;
    private String username;
    private String password;

    private MysqlDictionary(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        SpecialPermission.check();
        Boolean aBoolean = AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);
                String sql = "select `name`,`type`from bio_dictionary";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String name = resultSet.getString(1);
                    String type = resultSet.getString(2);
                    FilterEntity filterEntity = new FilterEntity();
                    filterEntity.setType(type);
                    filterEntity.setNoCause(stringIsUp(name));
                    list.put(name.toLowerCase(Locale.ROOT), filterEntity);
                    String[] split = name.toLowerCase(Locale.ROOT).split(" ", -1);
                    for (int i = 0; i < split.length; i++) {
                        StringBuilder builder = new StringBuilder();
                        for (int j = 0; j <= i; j++) {
                            builder.append(" ");
                            builder.append(split[j]);
                        }
                        filter.put(builder.toString().replaceFirst(" ",""));
                    }
                }
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        });
    }

    public Map getList() {
        return list;
    }
}
