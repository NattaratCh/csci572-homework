package main;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.TextParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCrawler extends WebCrawler {
	private static final Logger LOGGER = LoggerFactory.getLogger(MyCrawler.class);

	private static final Pattern FILTERS = Pattern.compile(
			".*(\\.(css|js|bmp|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v" +
					"|rm|smil|wmv|swf|wma|zip|rar|gz|jnlp|woff|ttf|xls|xlsx|ppt|pptx|json|xml))$");
	private static final Pattern WHITE_LIST = Pattern.compile(".*(\\.(html|HTML|doc|pdf|jpg|jpeg|png|gif))$");
	private static final Set<String> FILTERS_CONTENT_TYPE = new HashSet(Arrays.asList(
			"text/css",
			"text/javascript",
			"application/json",
			"application/json+oembed",
			"application/javascript",
			"text/xml",
			"application/xml"
	));
	private static final String[] insideUrls = {
			"https://www.nytimes.com",
			"http://www.nytimes.com",
			"https://nytimes.com",
			"http://nytimes.com"
	};

	CrawlerData crawlerData;

	public MyCrawler() {
		crawlerData = new CrawlerData();
	}

	@Override
	public Object getMyLocalData() {
		return crawlerData;
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();

		return !FILTERS.matcher(href).matches() &&
				( href.startsWith(insideUrls[0])
				|| href.startsWith(insideUrls[1])
				|| href.startsWith(insideUrls[2])
				|| href.startsWith(insideUrls[3]) );
	}
	
	@Override
	public void visit(Page page) {
		 String url = page.getWebURL().getURL();
		 long size = page.getContentData().length;
		 int statusCode = page.getStatusCode();
		 String contentType = page.getContentType().split(";")[0];
		 if (FILTERS_CONTENT_TYPE.contains(contentType)) return;

		 LOGGER.info("URL: " + url);
		 LOGGER.info("Content-type: " + contentType);
		 crawlerData.addTotalProcessPages();
		 Set<WebURL> links = new HashSet<>();
		 if (page.getParseData() instanceof HtmlParseData) {
			 HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			 links = htmlParseData.getOutgoingUrls();
		 } else if (page.getParseData() instanceof BinaryParseData) {
		 	BinaryParseData binaryParseData = (BinaryParseData) page.getParseData();
		 	links = binaryParseData.getOutgoingUrls();
		 } else if (page.getParseData() instanceof TextParseData) {
		 	TextParseData textParseData = (TextParseData) page.getParseData();
		 	links = textParseData.getOutgoingUrls();
		 }

		 int outlinks = links.size();
		 crawlerData.addVisitData(new URLData(url, size, outlinks, contentType, statusCode));
		 crawlerData.addContentType(contentType);
		 crawlerData.addSize(size);

		 // Process outlinks
		 for(WebURL webURL: links) {
		 	String link = webURL.getURL().toLowerCase();
		 	crawlerData.addUrlData(link, getPointerStatus(link));
		 	crawlerData.addAllVisitUrls(link);
		 }

		 if (crawlerData.getTotalProcessPages() % 50 == 0) {
		 	LOGGER.info(String.format("Total process pages: (%d)", crawlerData.getTotalProcessPages()));
		 }
	}

	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		LOGGER.info("handlePageStatusCode: " + webUrl + " - " + statusCode);
		crawlerData.addFetchData(webUrl.getURL(), statusCode);
		crawlerData.addStatusCode(statusCode);
	}

	private PointerStatus getPointerStatus(String url) {
		url = url.toLowerCase();
		if (!FILTERS.matcher(url).matches() && (
				url.startsWith(insideUrls[0])
				|| url.startsWith(insideUrls[1])
				|| url.startsWith(insideUrls[2])
				|| url.startsWith(insideUrls[3])))
			return PointerStatus.OK;
		else
			return PointerStatus.N_OK;
	}
}
