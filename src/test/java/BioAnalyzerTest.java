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
        StringReader reader = new StringReader("autoanemiasomal dominant sideroblastic is testvvvvv");
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