package com.hvslab.trendu;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jsoup.Jsoup;

import com.google.common.base.CharMatcher;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class TrendFetcher {

	private SyndFeedInput input = null;
	private URL feedSource = null;

	public TrendFetcher() throws MalformedURLException {
		super();
		this.input = new SyndFeedInput();
		feedSource = new URL(Constants.GOOGLE_TRENDS_URL);
	}

	public List<String> getGoogleTrends() throws IllegalArgumentException, FeedException, IOException {
		List<String> list = new ArrayList<String>();
		SyndFeed feed = input.build(new XmlReader(feedSource));

		for (SyndEntryImpl item : (List<SyndEntryImpl>) feed.getEntries()) {
			List<Element> foreignMarkupList = (List<Element>) item.getForeignMarkup();
			for (Element element : foreignMarkupList) {
				if (element.getName().equalsIgnoreCase("news_item")) {
					String htmlContent = ((Element) element.getChildren().get(1)).getValue();
					String content = html2text(htmlContent);
					if (CharMatcher.ASCII.matchesAllOf(content)) {
						StringBuilder sb = new StringBuilder(item.getTitle());
						sb.append(" - ");
						if (!item.getDescription().getValue().isEmpty()) {
							sb.append(item.getDescription().getValue() + " - ");
						}
						sb.append(content.replace("..", "").trim());

						list.add(sb.toString());
					}
				}
			}
		}
		return list;
	}

	private String html2text(String html) {
		return Jsoup.parse(html).text();
	}
}