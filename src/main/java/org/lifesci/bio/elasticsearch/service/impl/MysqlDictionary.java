package org.lifesci.bio.elasticsearch.service.impl;

import org.lifesci.bio.elasticsearch.service.DictionaryService;
import org.lifesci.bio.elasticsearch.service.impl.bean.BioDictionary;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class MysqlDictionary implements DictionaryService {

    private String url;
    private String username;
    private String password;

    private List<BioDictionary> dictionaryMap = new ArrayList<>();




    public MysqlDictionary(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        init();
    }

    private void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "select `id`,`name`,`term`,`type` from bio_dictionary";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                String name = resultSet.getString(2);
                String term = resultSet.getString(3);
                String type = resultSet.getString(4);
                dictionaryMap.add(new BioDictionary(id, name, term, type));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isLike(String token) {
        List<BioDictionary> list = dictionaryMap.stream().filter(dictionary -> {
            return dictionary.containsName(token);
        }).collect(Collectors.toList());
        if (list.isEmpty()) {
            return true;
        }
        if (list.size() == 1 && list.get(0).getName().equals(token)) {
            return true;
        } else {
            return false;
        }
    }
}
