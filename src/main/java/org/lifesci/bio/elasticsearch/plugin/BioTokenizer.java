package org.lifesci.bio.elasticsearch.plugin;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BioTokenizer extends Tokenizer {

    private final StringBuilder buffer = new StringBuilder();
    private int suffixOffset;
    private int tokenStart = 0, tokenEnd = 0;
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);

    private final static List<String> LKIKEDB = Arrays.stream("gene,my".split(",", -1)).toList();

    private final static String PUNCTION = " -()/";

    @Override
    public final boolean incrementToken() throws IOException {
        clearAttributes();
        buffer.setLength(0);
        int ci;
        char ch;
        tokenStart = tokenEnd;

        ci = input.read();
        if(ci>64&&ci<91){
            ci=ci+32;
        }
        ch = (char) ci;
        while (true) {
            if (ci == -1){
                if (buffer.length() == 0)
                    return false;
                else {
                    termAtt.setEmpty().append(buffer);
                    offsetAtt.setOffset(correctOffset(tokenStart),
                            correctOffset(tokenEnd));
                    return true;
                }
            }
            else if (PUNCTION.indexOf(ch) != -1 && likeDb(buffer.toString())) {
                //buffer.append(ch);
                tokenEnd++;
                if(buffer.length()>0){
                    termAtt.setEmpty().append(buffer);
                    offsetAtt.setOffset(correctOffset(tokenStart), correctOffset(tokenEnd));
                    return true;
                }else {
                    ci = input.read();
                    if(ci>64&&ci<91){
                        ci=ci+32;
                    }
                    ch = (char) ci;
                }
            } else {
                buffer.append(ch);
                tokenEnd++;
                ci = input.read();
                if(ci>64&&ci<91){
                    ci=ci+32;
                }
                ch = (char) ci;
            }
        }
    }

    /**
     * 判断空格分词后是否存在db中，是则返回false，不是则返回true
     * @param token
     * @return
     */
    private boolean likeDb(String token) {
        return !LKIKEDB.contains(token);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        tokenStart = tokenEnd = 0;
    }

    @Override
    public void end() throws IOException {
        final int finalOffset = correctOffset(suffixOffset);
        this.offsetAtt.setOffset(finalOffset, finalOffset);
    }
}
