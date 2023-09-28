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
        if (lowerCase.isEmpty()) {
            return false;
        }
        boolean b = filter.mightContain(lowerCase) || filter.mightContain(lowerCase.replaceAll("'s", ""));
        if (!b) {
            // 特殊情况处理
            // 最后一个字符是特殊字符
            byte[] bytes = lowerCase.getBytes();
            byte aByte = bytes[bytes.length - 1];
            switch (String.valueOf((char)aByte)) {
                case "s":
                case ".":
                case ",":
                    StringBuilder sb = new StringBuilder();
                    for (int i1 = 0; i1 < bytes.length - 1; i1++) {
                        sb.append((char) bytes[i1]);
                    }
                    b = filter.mightContain((sb.toString()));
                    break;
                default:
                    break;
            }

        }
        return b;
    }

    @Override
    public String isMe(char[] segmentBuff, int englishStart, int i) {
        StringBuilder builder = new StringBuilder();
        for (int j = englishStart; j < englishStart + i; j++) {
            builder.append(segmentBuff[j]);
        }
        String lowerCase = builder.toString().toLowerCase();
        if (lowerCase.isEmpty()) {
            return null;
        }
        String s = list.get(lowerCase);
        if (StringUtils.isNullOrEmpty(s)) {
            // 特殊情况处理
            // 包含带有‘s的数据分词
            s = list.get(lowerCase.replaceAll("'s", ""));
        }
        if (StringUtils.isNullOrEmpty(s)) {
            // 特殊情况处理
            // 最后一个字符是特殊字符
            byte[] bytes = lowerCase.getBytes();
            byte aByte = bytes[bytes.length - 1];
            switch (String.valueOf((char)aByte)) {
                case "s":
                case ".":
                case ",":
                    StringBuilder sb = new StringBuilder();
                    for (int i1 = 0; i1 < bytes.length - 1; i1++) {
                        sb.append((char) bytes[i1]);
                    }
                    s = list.get(sb.toString());
                    break;
                default:
                    break;
            }

        }
        return s;
    }
}
