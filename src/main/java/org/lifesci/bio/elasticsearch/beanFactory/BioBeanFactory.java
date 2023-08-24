package org.lifesci.bio.elasticsearch.beanFactory;

import org.lifesci.bio.elasticsearch.service.DictionaryService;
import org.lifesci.bio.elasticsearch.service.impl.MongoDictionary;
import org.lifesci.bio.elasticsearch.service.impl.MysqlDictionary;

import java.util.Locale;

public class BioBeanFactory {

    private final static String DICTIONARY_TYPE = "BIO_ANALYZER_TYPE";

    public enum Type {
        MYSQL {
            @Override
            public String reverseMul() {
                return "mysql";
            }

            @Override
            public DictionaryService getDictonary() {
                return new MysqlDictionary();
            }
        },
        MONGO {
            @Override
            public String reverseMul() {
                return "mongo";
            }

            @Override
            public DictionaryService getDictonary() {
                return new MongoDictionary();
            }
        };
        Type() {}

        public abstract String reverseMul();

        public abstract DictionaryService getDictonary();

        public static Type fromString(String op) {
            if (null == op) {
                throw new RuntimeException("Please set \"" + DICTIONARY_TYPE + "\" in your env path");
            }
            return valueOf(op.toUpperCase(Locale.ROOT));
        }
    }


    public final static DictionaryService getDictionaryBean() {
        String type = System.getenv(DICTIONARY_TYPE);
        System.getenv();
        return Type.fromString(type).getDictonary();
    }
}
