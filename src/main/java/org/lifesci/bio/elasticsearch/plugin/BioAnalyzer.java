package org.lifesci.bio.elasticsearch.plugin;

import org.apache.lucene.analysis.Analyzer;
import org.lifesci.bio.elasticsearch.cfg.Configuration;

public class BioAnalyzer extends Analyzer {

    private Configuration configuration;

    public BioAnalyzer() {
        this.configuration = new Configuration(true, false, false);
        this.configuration.setBioOnly(true);
    }

    public BioAnalyzer(Configuration configuration) {
        super();
        this.configuration = configuration;
    }

    @Override
    protected TokenStreamComponents createComponents(String s) {
        return new TokenStreamComponents(new BioTokenizer(configuration));
    }
}
