package org.lifesci.bio.elasticsearch.service.impl;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.mysql.cj.util.StringUtils;
import org.lifesci.bio.elasticsearch.service.DictionaryService;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class BaseDictionary implements DictionaryService {

    /**
     * 布隆过滤器，将词语切割后放入其中
     */

    protected BloomFilter<String> filter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), 1000000, 0.01);

    /**
     * 完全匹配，获取类型
     */
    protected Map<String, String> list = new HashMap<>();


    @Override
    public boolean isLike(char[] segmentBuff, int englishStart, int i) {
        StringBuilder builder = new StringBuilder();
        for (int j = englishStart; j < englishStart + i; j++) {
            builder.append(segmentBuff[j]);
        }
        String lowerCase = builder.toString().toLowerCase();
        return filter.mightContain(builder.toString().toLowerCase()) || filter.mightContain(lowerCase.replaceAll("'s",""));
    }

    @Override
    public String isMe(char[] segmentBuff, int englishStart, int i) {
        StringBuilder builder = new StringBuilder();
        for (int j = englishStart; j < englishStart + i; j++) {
            builder.append(segmentBuff[j]);
        }
        String lowerCase = builder.toString().toLowerCase();
        String s = list.get(lowerCase);
        if (StringUtils.isNullOrEmpty(s)) {
            // 特殊情况处理
            // 包含带有‘s的数据分词
            return list.get(lowerCase.replaceAll("'s",""));
        }
        return s;
    }
}
