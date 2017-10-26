package lucene.text;

import lucene.text.Field.VecTextField;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class FileIndexing {
    /**
     * 创建索引
     *
     * @param analyzer
     * @throws Exception
     */
    public static void createIndex(Analyzer analyzer, final Path input, final Path output) throws Exception {
        Directory dire = FSDirectory.open(output);
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter iw = new IndexWriter(dire, iwc);
        FileIndexing.addDoc(iw, input);
        iw.close();
    }

    /**
     * 获取文本内容
     *
     * @param file
     * @return
     * @throws Exception
     */
    @SuppressWarnings("resource")
    public static List<String> getContent(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);
        List<String> lines = new ArrayList<String>();
        BufferedReader br = new BufferedReader(isr);
        String o = null;
        while ((o = br.readLine()) != null) {
            lines.add(o);
        }
        return lines;
    }

    /**
     * 动态添加Document
     *
     * @param iw
     * @throws Exception
     */
    public static void addDoc(IndexWriter iw, final Path input) throws Exception {
        File[] files = new File(input.toString()).listFiles();
        for (File file : files) {
            List<String> content = FileIndexing.getContent(file);
            String name = file.getName();
            String path = file.getAbsolutePath();
            for (int i = 0; i < content.size(); i++) {
                String o = content.get(i);
                String[] columns = o.split("\t");
                if (columns.length != 2) {
                    throw new Exception("Wrong length in text file: " + path);
                }
                Document doc = new Document();
                doc.add(new VecTextField("id", columns[0], Store.YES));
                doc.add(new VecTextField("post", columns[1], Store.YES));
                iw.addDocument(doc);
                iw.commit();
            }
        }
    }

    /**
     * 搜索
     *
     * @param query
     * @throws Exception
     */
    private static void search(Query query, final Path output) throws Exception {
        Directory dire = FSDirectory.open(output);
        IndexReader ir = DirectoryReader.open(dire);
        IndexSearcher is = new IndexSearcher(ir);
        TopDocs td = is.search(query, 1000);
        System.out.println("共为您查找到" + td.totalHits + "条结果");
        ScoreDoc[] sds = td.scoreDocs;
        for (ScoreDoc sd : sds) {
            Document d = is.doc(sd.doc);
            System.out.println("[" + d.get("id") + "]\t" + d.get("post"));
        }
    }


    public static void main(String[] args) throws Exception {
        if(args.length != 2){
            System.out.println("help: java -cp JAR "+ FileIndexing.class.getName() + " INPUT_ABS_DIR OUTPUT_ABS_DIR");
            System.out.println(" INPUT_ABS_DIR: absolute path that contains text files, each line in every file is in 'TEXT_ID TAB TEXT' format.");
            System.out.println(" OUTPUT_ABS_DIR: absolute path that generates lucene index files.");
        }

        String input = args[0];
        String output = args[1];
        System.out.println("read text from " + input + " ...");
        System.out.println("index is piped out to " + output + " ...");
        Analyzer analyzer = new WhitespaceAnalyzer();
        FileIndexing.createIndex(analyzer, Paths.get(input), Paths.get(output));
        System.out.println("make a query as test ...");
        QueryParser parser = new QueryParser("post", analyzer);
        Query query = parser.parse("新能源");
        FileIndexing.search(query, Paths.get(output));
    }
}
