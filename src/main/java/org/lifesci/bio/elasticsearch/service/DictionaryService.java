package org.lifesci.bio.elasticsearch.service;

import java.sql.SQLException;
import java.util.List;

public interface DictionaryService {

    boolean isLike(char[] segmentBuff, int englishStart, int i);

    String isMe(char[] segmentBuff, int englishStart, int i);

    List<String> getAlias(String name);
}
