package org.lifesci.bio.elasticsearch.plugin;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.lifesci.bio.elasticsearch.cfg.Configuration;

public class BioTokenizerFactory extends AbstractTokenizerFactory {

    private Configuration configuration;

    public BioTokenizerFactory(IndexSettings indexSettings, Environment environment, String s, Settings settings) {
        super(indexSettings, settings, s);
        this.configuration=new Configuration(environment,settings).setUseSmart(true);
    }

    public BioTokenizerFactory setBioOnly() {
        this.configuration.setBioOnly(true);
        return this;
    }

    public static   BioTokenizerFactory getBioOnlyTokenizerFactory(IndexSettings indexSettings, Environment environment, String s, Settings settings) {
        return new BioTokenizerFactory(indexSettings, environment, s, settings).setBioOnly();
    }

    @Override
    public Tokenizer create() {
        return new BioTokenizer(configuration);
    }
}
