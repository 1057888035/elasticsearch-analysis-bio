package org.lifesci.bio.elasticsearch.plugin;

import org.apache.lucene.analysis.Analyzer;

public class BioAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String s) {
        return new TokenStreamComponents(new BioTokenizer());
    }
}
