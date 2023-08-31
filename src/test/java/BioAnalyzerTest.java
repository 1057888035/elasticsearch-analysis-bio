import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;
import org.lifesci.bio.elasticsearch.plugin.BioAnalyzer;

public class BioAnalyzerTest {
    @Test
    public void testAnalyzer() throws Exception {
        BioAnalyzer analyzer = new BioAnalyzer();
        TokenStream ts = analyzer.tokenStream("text", "Disease of capillaries is a test string,and metabolic diseases equals this dis");
        CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        while (ts.incrementToken()) {
            System.out.println(term.toString());
        }
        ts.end();
        ts.close();
    }
}
