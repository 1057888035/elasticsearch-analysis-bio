package org.lifesci.bio.elasticsearch.service.impl;

import org.lifesci.bio.elasticsearch.service.DictionaryService;

public class MysqlDictionary implements DictionaryService {

    @Override
    public boolean isLike(String token) {
        return false;
    }
}
