import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCrawler extends WebCrawler {
	private static final Logger LOGGER = LoggerFactory.getLogger(MyCrawler.class);

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|jpg"
			 + "|png|mp3|mp3|zip|gz))$");
	private final static Pattern WHITE_LIST = Pattern.compile(".*(\\.(html|HTML|doc|pdf|jpg|jpeg|png|gif))$");
	
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		return WHITE_LIST.matcher(href).matches()
				&& href.startsWith("https://www.nytimes.com");
	}
	
	@Override
	public void visit(Page page) {
		 String url = page.getWebURL().getURL();
		 LOGGER.info("URL: " + url);
		 if (page.getParseData() instanceof HtmlParseData) {
			 HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			 String text = htmlParseData.getText();
			 String html = htmlParseData.getHtml();
			 Set<WebURL> links = htmlParseData.getOutgoingUrls();
			 LOGGER.info("Text length: " + text.length());
			 LOGGER.info("Html length: " + html.length());
			 LOGGER.info("Number of outgoing links: " + links.size());
		 }
	}

	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		LOGGER.info(webUrl + " - " + statusCode);
	}
}
