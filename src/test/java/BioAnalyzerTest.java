import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;
import org.lifesci.bio.elasticsearch.beanFactory.BioBeanFactory;
import org.lifesci.bio.elasticsearch.plugin.BioAnalyzer;
import org.lifesci.bio.elasticsearch.service.impl.MysqlDictionary;

import java.io.StringReader;
import java.util.Map;
import java.util.Set;

public class BioAnalyzerTest {
    @Test
    public void testAnalyzer() throws Exception {
        BioAnalyzer analyzer = new BioAnalyzer();
        StringReader reader = new StringReader("" +
                "Large interindividual variation is observed in both the response and toxicity associated with anticancer therapy. The etiology of this variation is multifactorial, but is due in part to host genetic variations. Pharmacogenetic and pharmacogenomic studies have successfully identified genetic variants that contribute to this variation in susceptibility to chemotherapy. This review provides an overview of the progress made in the field of pharmacogenetics and pharmacogenomics using a five-stage architecture, which includes 1) determining the role of genetics in drug response; 2) screening and identifying genetic markers; 3) validating genetic markers; 4) clinical utility assessment; and 5) pharmacoeconomic impact. Examples are provided to illustrate the identification, validation, utility, and challenges of these pharmacogenetic and pharmacogenomic markers, with the focus on the current application of this knowledge in cancer therapy. With the advance of technology, it becomes feasible to evaluate the human genome in a relatively inexpensive and efficient manner; however, extensive pharmacogenetic research and education are urgently needed to improve the translation of pharmacogenetic concepts from bench to bedside." +
                "");
        TokenStream ts = analyzer.tokenStream("", reader);
        CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        while (ts.incrementToken()) {
            System.out.println(term.toString());
        }
        ts.end();
        ts.close();
    }

    @Test
    public void testAnalyzer2() throws Exception {
        BioAnalyzer analyzer = new BioAnalyzer();
        MysqlDictionary dictionaryBean = (MysqlDictionary) BioBeanFactory.getDictionaryBean();
        Map<String, String> list = dictionaryBean.getList();
        Set<String> strings = list.keySet();
        for (String name : strings) {
            System.out.println(name + "==============================================");
            StringReader reader = new StringReader(name);
            TokenStream ts = analyzer.tokenStream("", reader);
            CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                System.out.println(term.toString());
            }
            ts.end();
            ts.close();

        }
    }
}