import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
	private static final String FETCH_FILE = "fetch_nytimes.csv";
	private static final String VISIT_FILE = "visit_nytimes.csv";
	private static final String URL_FILE = "url_nytimes.csv";
	private static final String NEWS_URL = "https://www.nytimes.com";

	public static void main(String[] args) throws Exception {
		 String crawlStorageFolder = "/Users/nuning/MyCrawlerProject/data/crawl";
		 int numberOfCrawlers = 7;
		 int maxPagesToFetch = 20000;
		 int maxDepth = 16;
		 CrawlConfig config = new CrawlConfig();
		 config.setCrawlStorageFolder(crawlStorageFolder);
		 config.setMaxPagesToFetch(maxPagesToFetch);
		 config.setMaxDepthOfCrawling(maxDepth);
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
	}
}
