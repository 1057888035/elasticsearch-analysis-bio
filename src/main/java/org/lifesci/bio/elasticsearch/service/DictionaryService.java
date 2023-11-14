package org.lifesci.bio.elasticsearch.service;

import org.lifesci.bio.elasticsearch.dic.TypeDto;

import java.sql.SQLException;
import java.util.List;

public interface DictionaryService {

    boolean isLike(char[] segmentBuff, int englishStart, int i);

    TypeDto isMe(char[] segmentBuff, int englishStart, int i);

    List<String> getAlias(String name);
}
