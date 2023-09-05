package org.lifesci.bio.elasticsearch.plugin;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.lifesci.bio.elasticsearch.cfg.Configuration;

public class BioAnalyzerProvider extends AbstractIndexAnalyzerProvider<BioAnalyzer> {

    private final BioAnalyzer bioAnalyzer;


    public BioAnalyzerProvider(IndexSettings indexSettings, Environment environment, String s, Settings settings) {
        super(s, settings);
        Configuration configuration = new Configuration(environment, settings).setUseSmart(true);
        this.bioAnalyzer = new BioAnalyzer(configuration);
    }


    @Override
    public BioAnalyzer get() {
        return bioAnalyzer;
    }
}
