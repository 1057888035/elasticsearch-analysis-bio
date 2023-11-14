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
                "The ovarian surface epithelium (ZETA) is a key tissue in the pathogenesis of ovarian surface epithelial-stromal tumours and ovarian endometriosis, commonly encountered gynaecological diseases. Despite the high incidence of these diseases, experimental in vitro studies of OSE are few and so we used the scraping method with an enzymatic procedure to isolate human OSE and studied its characteristics in vitro. Nineteen normal ovaries were used. After incubation of the ovary for 40 min in collagenase type 1 solution (300 U/ml), the surface cells were removed by gentle scraping with a surgical blade. Cells obtained as a cluster after unit gravity sedimentation with 5% bovine serum albumin in medium 199 were cultured in medium 199 containing 15% fetal bovine serum. The viable cell number in a single ovary was 0.1-2.7 x 10(6). The outgrowth of cells started from a homogeneous population of single cells, and the cell population doubling time was between 7 and 10 days. Confluent monolayers were formed after 13-20 days and subcultured from one to three times. The monolayers mostly had a cobblestone appearance, and fusiform or polygonal cells were also observed. By cytochemistry, immunocytochemistry and scanning and transmission electron microscopy, the cells were shown to have characteristics of mesothelial OSE cells in short-term culture. This experimental approach was efficient in providing cultured human OSE, which can be utilized to investigate pathobiology and carcinogenesis."+
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