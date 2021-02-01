package main;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {
	private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);
	private static final String FETCH_FILE = "./output/fetch_nytimes.csv";
	private static final String VISIT_FILE = "./output/visit_nytimes.csv";
	private static final String URL_FILE = "./output/url_nytimes.csv";
	private static final String OUTPUT = "./output/CrawlReport_nytimes.txt";
	private static final String NEWS_URL = "https://www.nytimes.com";


	public static void main(String[] args) throws Exception {
		 String crawlStorageFolder = "./data/crawl";
		 int numberOfCrawlers = 10;
		 int maxPagesToFetch = 20000;
		 int maxDepth = 16;
		 CrawlConfig config = new CrawlConfig();
		 config.setCrawlStorageFolder(crawlStorageFolder);
		 config.setMaxPagesToFetch(maxPagesToFetch);
		 config.setMaxDepthOfCrawling(maxDepth);
		 config.setPolitenessDelay(100);
		 config.setIncludeHttpsPages(true);
		 config.setIncludeBinaryContentInCrawling(true);
		 config.setRespectNoIndex(false);
		 config.setRespectNoFollow(false);
		 /*
		 * Instantiate the controller for this crawl.
		 */
		 PageFetcher pageFetcher = new PageFetcher(config);
		 RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		 RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		 CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		 /*
		 * For each crawl, you need to add some seed urls. These are the first
		 * URLs that are fetched and then the crawler starts following links
		 * which are found in these pages
		 */
		 controller.addSeed(NEWS_URL);

		 /*
		 * Start the crawl. This is a blocking operation, meaning that your code
		 * will reach the line after this only when crawling is finished.
		 */
		 controller.start(MyCrawler.class, numberOfCrawlers);

		List<Object> crawlerLocalData = controller.getCrawlersLocalData();
		Map<String, Integer> fetchData = new HashMap<>();
		Map<String, URLData> visitData = new HashMap<>();
		List<Pair<String, PointerStatus>> urlData = new ArrayList<>();

		int fetchAttempted = 0;
		int successFetchAttempted = 0;
		int failFetchAttempted = 0;

		int totalUrls = 0;
		int totalUniqueUrls = 0;
		int totalUniqueInUrls = 0;
		int totalUniqueOutUrls = 0;

		Map<Integer, Integer> statusCodeMap = new HashMap<>();
		Map<Integer, Integer> sizeMap = new HashMap<>();
		Map<String, Integer> contentTypeMap = new HashMap<>();

		int totalProcessPages = 0;
		int numberOfSuccessFetch = 0;

		for(Object obj: crawlerLocalData) {
			CrawlerData crawlerData = (CrawlerData) obj;
			List<String> allVisitUrls = crawlerData.getAllVisitUrls();
			Map<String, URLData> visitMap = crawlerData.getVisitData();
			Map<String, Integer> fetchMap = crawlerData.getFetchData();
			urlData.addAll(crawlerData.getUrlData());

			appendMap(fetchData, fetchMap);
			appendVisitMap(visitData, visitMap);

			fetchAttempted += fetchMap.size();
			successFetchAttempted += visitMap.size();
			failFetchAttempted += crawlerData.getNumberOfFailFetch();

			totalUrls += allVisitUrls.size();
			totalUniqueUrls += crawlerData.getUniqueVisitedUrl().size();
			totalUniqueInUrls += crawlerData.getUniqueInsideUrl().size();
			totalUniqueOutUrls += crawlerData.getUniqueOutsideUrl().size();

			appendMap(statusCodeMap, crawlerData.getStatusCodeMap());
			appendMap(sizeMap, crawlerData.getSizeMap());
			appendMap(contentTypeMap, crawlerData.getContentTypeMap());

			totalProcessPages += crawlerData.getTotalProcessPages();
			numberOfSuccessFetch += crawlerData.getNumberOfSuccessFetch();
		}

		LOGGER.info("Start writting CSV files");
		writeCSV(FETCH_FILE, createFetchDataList(fetchData));
		writeCSV(VISIT_FILE, createVisitedDataList(visitData));
		writeCSV(URL_FILE, createUrlDataList(urlData));
		LOGGER.info("Start results files");
		writeResults(fetchAttempted, successFetchAttempted, failFetchAttempted, totalUrls, totalUniqueUrls, totalUniqueInUrls, totalUniqueOutUrls,
				statusCodeMap, sizeMap, contentTypeMap, totalProcessPages, numberOfSuccessFetch);
	}

	private static void writeResults(int fetchAttempted, int successFetchAttempted, int failFetchAttempted,
									 int totalUrls, int totalUniqueUrls, int totalUniqueInUrls, int totalUniqueOutUrls,
									 Map<Integer, Integer> statusCodeMap, Map<Integer, Integer> sizeMap, Map<String, Integer> contentTypeMap,
									 int totalProcessPages, int numberOfSuccessFetch) {
		File outputFile = new File(OUTPUT);
		if (outputFile.exists()) {
			outputFile.delete();
		}

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(OUTPUT, true));
			writer.append("totalProcessPages: " + totalProcessPages + "\n");
			writer.append("numberOfSuccessFetch: " + numberOfSuccessFetch + "\n");
			writer.append("\n");
			writer.append("Name: Nattarat Champreeda\n");
			writer.append("USC ID: 5487597112\n");
			writer.append("News site crawled: nytimes.com\n");
			writer.append("Number of threads: 10\n");

			writer.append("\n");
			writer.append("Fetch Statistics\n");
			writer.append("==============\n");
			writer.append("# fetches attempted: " + fetchAttempted + "\n");
			writer.append("# fetches succeeded: " + successFetchAttempted + "\n");
			writer.append("# fetches failed or aborted: " + failFetchAttempted + "\n");
			writer.append("# fetches disappear: " + (numberOfSuccessFetch - successFetchAttempted) + "\n");

			writer.append("\n");
			writer.append("Outgoing URLs:\n" );
			writer.append("==============\n");
			writer.append("Total URLs extracted: " + totalUrls + "\n");
			writer.append("# unique URLs extracted: " + totalUniqueUrls + "\n");
			writer.append("# unique URLs within News Site: " + totalUniqueInUrls + "\n");
			writer.append("# unique URLs outside News Site: " + totalUniqueOutUrls + "\n");

			writer.append("\n");
			writer.append("Status Codes:\n");
			writer.append("==============\n");
			for(Integer key: statusCodeMap.keySet()) {
				writer.append(key + ": " + statusCodeMap.get(key) + "\n");
			}

			writer.append("\n");
			writer.append("File Sizes:\n");
			writer.append("==============\n");
			TreeMap<Integer, String> sizeDescMap = getSizeMap();
			for(Integer key: sizeDescMap.keySet()) {
				writer.append(sizeDescMap.get(key) + ": " + sizeMap.get(key) + "\n");
			}

			writer.append("\n");
			writer.append("Content Types:\n");
			writer.append("==============\n");
			for(String key: contentTypeMap.keySet()) {
				writer.append(key + ": " + contentTypeMap.get(key) + "\n");
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static TreeMap<Integer, String> getSizeMap() {
		TreeMap<Integer, String> map = new TreeMap<>();
		map.put(0, "< 1KB");
		map.put(1, "1KB ~ <10KB");
		map.put(2, "10KB ~ <100KB");
		map.put(3, "100KB ~ <1MB");
		map.put(4, ">= 1MB");
		return map;
	}

	private static List<String[]> createFetchDataList(Map<String, Integer> fetchData ) {
		List<String[]> lines = new ArrayList<>();
		lines.add(new String[] { "URL", "Status Code" });
		for(String key: fetchData.keySet()) {
			lines.add(new String[] { key, fetchData.get(key).toString() });
		}
		return lines;
	}

	private static List<String[]> createVisitedDataList(Map<String, URLData> visitedData ) {
		List<String[]> lines = new ArrayList<>();
		lines.add(new String[] { "URL", "Size (Bytes)", "Number of Outlinks", "Content type" });
		for(String key: visitedData.keySet()) {
			URLData data = visitedData.get(key);
			lines.add(new String[] { key, String.valueOf(data.getSize()), String.valueOf(data.getOutlinks()), data.getContentType()});
		}
		return lines;
	}

	private static List<String[]> createUrlDataList(List<Pair<String, PointerStatus>> urlData ) {
		List<String[]> lines = new ArrayList<>();
		lines.add(new String[] { "URL", "Indicator" });
		for(Pair<String, PointerStatus> p: urlData) {
			lines.add(new String[] { p.getKey(), p.getValue().toString() });
		}
		return lines;
	}

	private static void writeCSV(String filename, List<String[]> lines) {
		File csvOutputFile = new File(filename);
		if (csvOutputFile.exists()) {
			csvOutputFile.delete();
		}
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			lines.stream()
					.map(Controller::convertToCSV)
					.forEach(pw::println);
			pw.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static String convertToCSV(String[] data) {
		return Stream.of(data)
				.collect(Collectors.joining(","));
	}

	private static void appendIntegerMap(Map<Integer, Integer> toMap, Map<Integer, Integer> fromMap) {
		for(Integer key: fromMap.keySet()) {
			if (toMap.containsKey(key)) {
				int count = toMap.get(key) + fromMap.get(key);
				toMap.put(key, count);
			} else {
				toMap.put(key, fromMap.get(key));
			}
		}
	}

	private static void appendStringMap(Map<String, Integer> toMap, Map<String, Integer> fromMap) {
		for(String key: fromMap.keySet()) {
			if (toMap.containsKey(key)) {
				int count = toMap.get(key) + fromMap.get(key);
				toMap.put(key, count);
			} else {
				toMap.put(key, fromMap.get(key));
			}
		}
	}

	private static<T> void appendMap(Map<T, Integer> toMap, Map<T, Integer> fromMap) {
		for(T key: fromMap.keySet()) {
			if (toMap.containsKey(key)) {
				int count = toMap.get(key) + fromMap.get(key);
				toMap.put(key, count);
			} else {
				toMap.put(key, fromMap.get(key));
			}
		}
	}

	private static void appendVisitMap(Map<String, URLData> toMap, Map<String, URLData> fromMap) {
		for(String key: fromMap.keySet()) {
			if (!toMap.containsKey(key)) {
				toMap.put(key, fromMap.get(key));
			}
		}
	}
}
