import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by nuning on 4/1/21.
 */
public class PageRankCalculation {
    private static final String MAP_FILE_PATH = "./URLtoHTML_nytimes_news.csv";
    private static final String HTML_DIRECTORY = "/Users/nuning/Documents/Study/CS572 - IR/HW/HW4/NYTIMES/nytimes/";
    private static final String EDGE_LIST_FILE = "./externalfile/edgelist.txt";
    private static final String COMMA_DELIMITER = ",";

    public static void main(String[] args) {
        Map<String, String> urlToFileMap = getURLToFileNameMap();
        Map<String, String> fileToUrlMap = getFileToURLNameMap();
        File directory = new File(HTML_DIRECTORY);
        File[] htmlFiles = directory.listFiles();

        if (htmlFiles.length == 0) {
            System.out.println("No HTML files found");
        } else {
            File edgeListFile = new File(EDGE_LIST_FILE);
            if (edgeListFile.exists()) {
                edgeListFile.delete();
            }
            for(File file: htmlFiles) {
                Set<String> edgeList = getEdgeList(file, urlToFileMap, fileToUrlMap);
                writeEdgeListFile(edgeList);
            }
        }
    }

    public static void writeEdgeListFile(Set<String> edgeList) {
        File file = new File(EDGE_LIST_FILE);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        FileWriter fr = null;
        try {
            fr = new FileWriter(file, true);
            BufferedWriter br = new BufferedWriter(fr);
            for(String e: edgeList) {
                br.write(e);
                br.write("\n");
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getEdgeList(File file, Map<String, String> urlToFileMap, Map<String, String> fileToUrlMap) {
        Set<String> edgeList = new HashSet<String>();
        String url = fileToUrlMap.get(file.getName());
        if (url == null) {
            System.out.println("getEdgeList | No URL found for " + file.getName());
        } else {
            try {
                Document doc = Jsoup.parse(file, "UTF-8", url);
                Elements links = doc.select("a[href]");
                for(Element link: links) {
                    String outlinkUrl = link.attr("abs:href").trim();
                    if (urlToFileMap.containsKey(outlinkUrl)) {
                        String outlinkFilename = urlToFileMap.get(outlinkUrl);
                        edgeList.add(file.getName() + " " + outlinkFilename);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("getEdgeList | edge list size: " + edgeList.size());
        return edgeList;
    }

    public static Map<String, String> getURLToFileNameMap() {
        Map<String, String> map = new HashMap<String, String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(MAP_FILE_PATH));
            String line;
            while((line = br.readLine()) != null) {
                line = line.trim();
                String[] values = line.split(COMMA_DELIMITER);
                map.put(values[1], values[0]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }

    public static Map<String, String> getFileToURLNameMap() {
        Map<String, String> map = new HashMap<String, String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(MAP_FILE_PATH));
            String line;
            while((line = br.readLine()) != null) {
                line = line.trim();
                String[] values = line.split(COMMA_DELIMITER);
                map.put(values[0], values[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }
}
