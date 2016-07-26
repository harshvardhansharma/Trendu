package com.hvslab.trendu;

import java.util.ArrayList;
import java.util.List;

import com.textrazor.AnalysisException;
import com.textrazor.NetworkException;
import com.textrazor.TextRazor;
import com.textrazor.annotations.AnalyzedText;
import com.textrazor.annotations.Topic;

public class Classifier {

	private TextRazor textRazorClient = null;

	public Classifier() {
		super();
		textRazorClient = new TextRazor(Constants.TEXT_RAZOR_API_KEY);
		textRazorClient.addExtractor("topics");
	}

	public List<String> classify(List<String> trendingContentList) throws NetworkException, AnalysisException {

		List<String> categories = new ArrayList<String>();

		int count = 1;
		for (String trendingContent : trendingContentList) {
			System.out.println("Classifying item #" + count++);
			AnalyzedText response = textRazorClient.analyze(trendingContent);
			String row = "";
			int i = 0;
			for (Topic topic : response.getResponse().getTopics()) {
				if (topic.getScore() > 0.78) {
					row += topic.getLabel() + ", ";
					i++;
				}
			}
			if (i > 3) {
				categories.add(row.substring(0, row.lastIndexOf(",")));
			}
		}
		return categories;
	}
}
