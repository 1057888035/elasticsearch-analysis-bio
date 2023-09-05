package org.lifesci.bio.elasticsearch.service;

import java.sql.SQLException;

public interface DictionaryService {

    boolean isLike(char[] segmentBuff, int englishStart, int i);

    String isMe(char[] segmentBuff, int englishStart, int i);
}
