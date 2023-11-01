package org.lifesci.bio.elasticsearch.service.impl;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.mysql.cj.util.StringUtils;
import org.lifesci.bio.elasticsearch.service.DictionaryService;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    protected Map<String, Boolean> noCauseList = new HashMap<>();


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
        Boolean isAllUp = false;
        StringBuilder builder = new StringBuilder();
        for (int j = englishStart; j < englishStart + i; j++) {
            builder.append(segmentBuff[j]);
        }
        // 字节构建成小写字母
        String lowerCase = builder.toString().toLowerCase();
        if (lowerCase.isEmpty()) {
            return null;
        }
        String s = list.get(lowerCase);
        if (!StringUtils.isNullOrEmpty(s)) {
            isAllUp = noCauseList.get(lowerCase);
            s += ":::0";
        }
        if (StringUtils.isNullOrEmpty(s)) {
            // 特殊情况处理
            // 包含带有‘s的数据分词
            s = list.get(lowerCase.replaceAll("'s", ""));
            isAllUp = noCauseList.get(lowerCase.replaceAll("'s", ""));
            if (!StringUtils.isNullOrEmpty(s)) {
                s = s + ":::2";
                builder.deleteCharAt(builder.length()-1).deleteCharAt(builder.length()-1);
            }
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
                    builder.deleteCharAt(builder.length() - 1);
                    StringBuilder sb = new StringBuilder();
                    for (int i1 = 0; i1 < bytes.length - 1; i1++) {
                        sb.append((char) bytes[i1]);
                    }
                    isAllUp = noCauseList.get(sb.toString());
                    s = list.get(sb.toString());
                    if (!StringUtils.isNullOrEmpty(s)) {
                        s = s + ":::1";
                    }
                    break;
                default:
                    break;
            }

        }
        // 判断缩写
        if (!StringUtils.isNullOrEmpty(s)) {
            if (null != isAllUp && isAllUp && !stringIsUp(builder.toString())) {
                return null;
            }
        }
        return s;
    }

    /**
     * 根据名称获取别名
     * @param name
     * @return
     */
    @Override
    public List<String> getAlias(String name) {
        // TODO
        List<String> objects = new ArrayList<>();
        if (name.equals("tetanus")) {
            objects.add("tetanus2");
        }
        if (name.equals("tetanus2")) {
            objects.add("tetanus");
        }
        return objects;
    }

    /**
     * 判断字符串是纯大写
     * @param text
     * @return
     */
    protected boolean stringIsUp(String text) {
        for (byte aByte : text.getBytes()) {
            if (!Character.isUpperCase(aByte)) {
                return false;
            }
        }
        return true;
    }
}
