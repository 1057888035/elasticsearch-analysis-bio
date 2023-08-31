package org.lifesci.bio.elasticsearch.beanFactory;

import org.lifesci.bio.elasticsearch.service.DictionaryService;
import org.lifesci.bio.elasticsearch.service.impl.MongoDictionary;
import org.lifesci.bio.elasticsearch.service.impl.MysqlDictionary;

import java.util.Locale;

public class BioBeanFactory {

    private final static String DICTIONARY_TYPE = "BIO_ANALYZER_TYPE";
    private final static String DICTIONARY_URL = "BIO_ANALYZER_URL";
    private final static String DICTIONARY_USERNAME = "BIO_ANALYZER_USERNAME";
    private final static String DICTIONARY_PASSWORD = "BIO_ANALYZER_PASSWORD";

    public enum Type {
        MYSQL {
            @Override
            public String reverseMul() {
                return "mysql";
            }

            @Override
            public DictionaryService getDictonary(String url, String username, String password) {
                //return new MysqlDictionary(url, username, password);
                return MysqlDictionary.getInstance("jdbc:mysql://192.168.26.60:13326/smartherb?autoReconnect=true&useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai", "root","Help7777");
            }

        },
        MONGO {
            @Override
            public String reverseMul() {
                return "mongo";
            }

            @Override
            public DictionaryService getDictonary(String url, String username, String password) {
                return new MongoDictionary(url);
            }
        };
        Type() {}

        public abstract String reverseMul();

        public abstract DictionaryService getDictonary(String url, String username, String password);

        public static Type fromString(String op) {
            if (null == op) {
                throw new RuntimeException("Please set \"" + DICTIONARY_TYPE + "\" in your env path");
            }
            return valueOf(op.toUpperCase(Locale.ROOT));
        }
    }


    public final static DictionaryService getDictionaryBean() {
        String type = System.getenv(DICTIONARY_TYPE);
        String url = System.getenv(DICTIONARY_URL);
        String username = System.getenv(DICTIONARY_USERNAME);
        String password = System.getenv(DICTIONARY_PASSWORD);
        return Type.fromString(type).getDictonary(url, username, password);
    }
}
