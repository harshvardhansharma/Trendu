package com.hvslab.trendu;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.sun.syndication.io.FeedException;
import com.textrazor.AnalysisException;

public class App {

	private static TrendFetcher trendFetcher = null;
	private static Classifier classifier = null;

	public static void main(String[] args)
			throws IOException, AnalysisException, IllegalArgumentException, FeedException {

		trendFetcher = new TrendFetcher();
		List<String> trendingContentList = trendFetcher.getGoogleTrends();

		classifier = new Classifier();
		List<String> categories = classifier.classify(trendingContentList);

		writeToFile(categories);
	}

	private static void writeToFile(List<String> contentToWrite) throws IOException {

		Calendar calendar = Calendar.getInstance();
		Date date = new Date();
		calendar.setTime(date);
		String fileName = "data_" + calendar.get(Calendar.DATE) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_"
				+ calendar.get(Calendar.YEAR) + ".txt";

		File file = new File(fileName);
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fileWriter = new FileWriter(file, true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWriter);
		for (String line : contentToWrite) {
			bufferWritter.write(line + " ::: \n\n");
		}
		bufferWritter.close();
		fileWriter.close();
	}

}
