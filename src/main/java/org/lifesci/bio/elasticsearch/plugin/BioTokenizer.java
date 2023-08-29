package org.lifesci.bio.elasticsearch.plugin;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.elasticsearch.SpecialPermission;
import org.lifesci.bio.elasticsearch.beanFactory.BioBeanFactory;
import org.lifesci.bio.elasticsearch.service.DictionaryService;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

public class BioTokenizer extends Tokenizer {

    private final StringBuilder buffer = new StringBuilder();
    private int suffixOffset;
    private int tokenStart = 0, tokenEnd = 0;
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);

    private DictionaryService dictionaryService = BioBeanFactory.getDictionaryBean();

    private final static String PUNCTION = " -()/";

    @Override
    public final boolean incrementToken() throws IOException {
        SpecialPermission.check();
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
            else if (PUNCTION.indexOf(ch) != -1 &&
                    AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> {
                        return dictionaryService.isLike(buffer.toString());
                    })
            ) {
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

    private String getNexSplit(String s) {
        StringBuilder buffer = new StringBuilder();

        return "";
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
