package org.lifesci.bio.elasticsearch.service.impl;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.elasticsearch.SpecialPermission;
import org.lifesci.bio.elasticsearch.service.DictionaryService;

import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class MysqlDictionary implements DictionaryService {

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

    private BloomFilter<String> filter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), 1000000, 0.01);

    private Map<String, String> list = new HashMap<>();

    public MysqlDictionary(String url, String username, String password) {
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
                    list.put(name.toLowerCase(Locale.ROOT), type);
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

    @Override
    public String isLike(String token) {
        if (list.containsKey(token)) {
            return list.get(token);
        }
        if (filter.mightContain(token)) {
            return "false";
        } else {
            return "word";
        }
    }
}
