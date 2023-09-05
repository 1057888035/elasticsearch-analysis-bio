package org.lifesci.bio.elasticsearch.plugin;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl;
import org.lifesci.bio.elasticsearch.cfg.Configuration;
import org.lifesci.bio.elasticsearch.core.IKSegmenter;
import org.lifesci.bio.elasticsearch.core.Lexeme;

import java.io.IOException;

public class BioTokenizer extends Tokenizer {

    private int suffixOffset;
    private int tokenStart = 0, tokenEnd = 0;
    private final CharTermAttribute termAtt;
    private final OffsetAttribute offsetAtt;

    private IKSegmenter bioSegmenter;

    public BioTokenizer(Configuration configuration) {
        super();
        termAtt = addAttribute(CharTermAttribute.class);
        offsetAtt = addAttribute(OffsetAttribute.class);
        this.bioSegmenter = new IKSegmenter(input, configuration);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        clearAttributes();
        Lexeme nextLexeme = bioSegmenter.next();
        if (nextLexeme != null) {
            termAtt.append(nextLexeme.getLexemeText());
            termAtt.setLength(nextLexeme.getLength());
            offsetAtt.setOffset(correctOffset(nextLexeme.getBeginPosition()), correctOffset(nextLexeme.getEndPosition()));
            tokenEnd = nextLexeme.getEndPosition();
            ((PackedTokenAttributeImpl) termAtt).setType(nextLexeme.getTypeString());
            return true;
        }
        return false;
    }



    @Override
    public void reset() throws IOException {
        super.reset();
        bioSegmenter.reset(input);
        tokenStart = tokenEnd = 0;
    }

    @Override
    public void end() throws IOException {
        super.end();
        final int finalOffset = correctOffset(suffixOffset);
        this.offsetAtt.setOffset(finalOffset, finalOffset);

    }
}
