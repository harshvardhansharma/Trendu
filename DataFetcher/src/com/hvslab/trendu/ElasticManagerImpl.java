package com.hvslab.trendu;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.google.common.io.Files;

public class ElasticManagerImpl implements ElasticManager {

    private static Client client;

    private static String clusterName = "elasticsearch";

    public static void initialize() throws UnknownHostException {
        // Settings settings = Settings.settingsBuilder().put("cluster.name",
        // "elasticsearch").build();
        client = TransportClient.builder().build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
    }

    public static void main(String[] args) throws IOException {
        initialize();
        //		Map<List<String>, List<String>> data = readCSVFile(args[0]);
        //		System.err.println(data);

        List<String> keywords = new ArrayList<String>();
        keywords.add("Tamil Nadu");
        keywords.add("Tamil");
        keywords.add("India");
        keywords.add("Tamil cinema");
        keywords.add("South India");
        keywords.add("Tamil-language media");
        keywords.add("Tamil-language media");
        keywords.add("States and territories of India");
        keywords.add("Films");

        // Map<String, List<String>> response = categorizeKeywordWise(keywords);
        // List<String> finalCategories = categorize(response);
        // System.out.println(finalCategories);

        // SearchResponse response = search("keywords", "cricket");
        // SearchResponse response = printAll();
        // put(data);

    }

    private static List<String> categorize(Map<String, List<String>> response) {
        int totalSize = 0;

        Map<String, Integer> resultCategoryToCountMap = new HashMap<String, Integer>();

        for (Entry<String, List<String>> entry : response.entrySet()) {
            totalSize += entry.getValue().size();

            for (String item : entry.getValue()) {
                Integer count = resultCategoryToCountMap.get(item);
                if (count == null) {
                    resultCategoryToCountMap.put(item, 1);
                } else {
                    resultCategoryToCountMap.put(item, count + 1);
                }
            }
        }

        List<String> result = new ArrayList<String>();
        for (Entry<String, Integer> entry : resultCategoryToCountMap.entrySet()) {
            double percentageScore = (double) entry.getValue() / totalSize;
            if (percentageScore >= 0.3) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    private static Map<String, List<String>> categorizeKeywordWise(List<String> newKeywords) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (String keyword : newKeywords) {
            SearchResponse response = search("keywords", keyword);
            if (response.getHits() != null && response.getHits().getHits().length != 0) {
                for (SearchHit searchHit : response.getHits().getHits()) {
                    if (map.get(keyword) == null) {
                        map.put(keyword, getCategoriesFromSearchHit(searchHit));
                    } else {
                        map.get(keyword).addAll(getCategoriesFromSearchHit(searchHit));
                    }
                }
            }
        }
        return map;
    }

    private static List<String> getCategoriesFromSearchHit(SearchHit searchHit) {
        return (List<String>) searchHit.getSource().get("categories");
    }

    private static void put(Map<List<String>, List<String>> map) throws IOException {
        for (Entry<List<String>, List<String>> item : map.entrySet()) {
            XContentBuilder builder = jsonBuilder().startObject().field("keywords", item.getKey()).field("categories", item.getValue()).endObject();
            IndexResponse response = client.prepareIndex("categories", "category").setSource(builder).get();
        }
    }

    private static SearchResponse search(String field, String value) {
        Map<String, String> query = new HashMap<String, String>();
        query.put(field, value);
        SearchResponse response = client.prepareSearch().setIndices("categories").setTypes("category").setMinScore(0.55f).setSearchType(SearchType.QUERY_AND_FETCH).setQuery(
                QueryBuilders.fuzzyQuery(field, value)).execute().actionGet();
        return response;
    }

    public static List<String> getData(String string) {
        List<String> list = new ArrayList<String>();
        StringTokenizer stk = new StringTokenizer(string.trim(), ",");
        while (stk.hasMoreTokens()) {
            String item = stk.nextToken().trim();
            if (item != null && !item.isEmpty()) {
                list.add(item);
            }
        }
        return list;
    }

    public static void searchDocument(Client client, String index, String type, String field, String value) {
        // SearchResponse response =
        // client.prepareSearch(index).setTypes(type).setSearchType(SearchType.QUERY_AND_FETCH)
        // .setQuery(QueryBuilders.fieldQuery(field,
        // value)).setExplain(true).execute().actionGet();
        // System.out.println(response.getHits().getHits()[0]);
        // System.out.println(">>>>>>>>>>>");
    }

    public static SearchRequestBuilder fetchSearchRequest() {
        return client.prepareSearch("bank").setTypes("account");
    }

    public static Map<String, Object> putJsonDocument(String category, List<String> keywords) {
        Map<String, Object> jsonDocument = new HashMap<String, Object>();
        jsonDocument.put("category", category);
        jsonDocument.put("keywords", keywords);
        return jsonDocument;
    }

    private static Map<List<String>, List<String>> readCSVFile(String filePath) throws IOException {

        File file = new File(filePath);
        List<String> lines = Files.readLines(file, Charset.defaultCharset());
        Map<List<String>, List<String>> map = new HashMap<List<String>, List<String>>();
        for (String line : lines) {
            StringTokenizer stk = new StringTokenizer(line, ":");
            List<String> keywords = getData(stk.nextToken());
            List<String> categories = getData(stk.nextToken());

            map.put(keywords, categories);
        }
        return map;
    }

    public void addData() {
        // TODO Auto-generated method stub

    }

}
