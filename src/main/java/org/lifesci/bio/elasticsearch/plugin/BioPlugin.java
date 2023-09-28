package org.lifesci.bio.elasticsearch.plugin;

import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class BioPlugin extends Plugin implements AnalysisPlugin {

    public static String PLUGIN_NAME = "analysis-bio";

    public BioPlugin() {
        super();
    }

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> getTokenizers() {
        Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> extra = new HashMap<>();
        extra.put("bio_word", BioTokenizerFactory::new);
        extra.put("bio_only_word", BioTokenizerFactory::getBioOnlyTokenizerFactory);
        return extra;
    }


    @Override
    public Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
        return Collections.singletonMap("bio_smart", BioAnalyzerProvider::new);
    }
}
