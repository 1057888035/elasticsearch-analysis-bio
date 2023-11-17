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
                "The J wave is a deflection that appears on the surface ECG as a late delta wave following the QRS complex. Also known as an Osborn wave, the J wave has been observed in various conditions and diseases. Our case highlights the typical electrocardiographic manifestations of hypothermia including the J deflection and ST segment elevation during atrial fibrillation. Thorough knowledge of these findings is important for prompt diagnosis and treatment of hypothermic states." +
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