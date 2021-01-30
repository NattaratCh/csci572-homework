package main;

import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by nuning on 1/27/21.
 */
public class CrawlerData {
    private List<String> allVisitUrls;
    private Map<String, Integer> fetchData;
    private Map<String, URLData> visitData;
    private List<Pair<String, PointerStatus>> urlData;
    private Map<String, Integer> contentTypeMap;
    private Map<Integer, Integer> statusCodeMap;
    private Map<Integer, Integer> sizeMap;
    private int totalProcessPages;
    private int numberOfFailFetch;

    public CrawlerData() {
        allVisitUrls = new ArrayList<>();
        fetchData = new HashMap<>();
        visitData = new HashMap<>();
        urlData = new ArrayList<>();
        contentTypeMap = new HashMap<>();
        statusCodeMap = new HashMap<>();
        sizeMap = new HashMap<>();
    }

    public List<String> getAllVisitUrls() {
        return allVisitUrls;
    }

    public void setAllVisitUrls(List<String> allVisitUrls) {
        this.allVisitUrls = allVisitUrls;
    }

    public Map<String, Integer> getFetchData() {
        return fetchData;
    }

    public void setFetchData(Map<String, Integer> fetchData) {
        this.fetchData = fetchData;
    }

    public Map<String, URLData> getVisitData() {
        return visitData;
    }

    public void setVisitData(Map<String, URLData> visitData) {
        this.visitData = visitData;
    }

    public List<Pair<String, PointerStatus>> getUrlData() {
        return urlData;
    }

    public void setUrlData(List<Pair<String, PointerStatus>> urlData) {
        this.urlData = urlData;
    }

    public Map<String, Integer> getContentTypeMap() {
        return contentTypeMap;
    }

    public Map<Integer, Integer> getStatusCodeMap() {
        return statusCodeMap;
    }

    public Map<Integer, Integer> getSizeMap() {
        return sizeMap;
    }

    public int getNumberOfFailFetch() {
        return numberOfFailFetch;
    }

    public void addAllVisitUrls(String url) {
        allVisitUrls.add(url);
    }

    public int getTotalProcessPages() {
        return totalProcessPages;
    }

    public void addTotalProcessPages() {
        totalProcessPages++;
    }

    public void addVisitData(URLData urlData) {
        visitData.putIfAbsent(urlData.getUrl(), urlData);
    }

    public void addFetchData(String url, int statusCode) {
        fetchData.putIfAbsent(url, statusCode);
    }

    public void addUrlData(String url, PointerStatus pointerStatus) {
        urlData.add(new Pair(url, pointerStatus));
    }

    public void addContentType(String contentType) {
        int count = contentTypeMap.getOrDefault(contentType.toLowerCase(), 0);
        contentTypeMap.put(contentType.toLowerCase(), count + 1);
    }

    public void addStatusCode(Integer statusCode) {
        statusCode = statusCode >= 200 && statusCode < 300 ? 200 : statusCode;
        int count = statusCodeMap.getOrDefault(statusCode, 0);
        statusCodeMap.put(statusCode, count + 1);

        if (statusCode > 300) numberOfFailFetch++;
    }

    public void addSize(long size) {
        // Possible value
        // < 1KB:
        // 1KB ~ <10KB:
        // 10KB ~ <100KB:
        // 100KB ~ <1MB:
        // >= 1MB:
        int kb = 1024;
        size = size / kb;
        int key = 0;
        if (size < 1) {
            key = 0;
        } else if (size >= 1 && size < 10) {
            key = 1;
        } else if (size >= 10 && size < 100) {
            key = 2;
        } else if (size >= 100 && size < 1024) {
            key = 3;
        } else if (size >= 1024) {
            key = 4;
        } else {
            System.out.println("Error content size" + size);
            return;
        }

        int count = sizeMap.getOrDefault(key, 0);
        sizeMap.put(key, count + 1);
    }

    public Set<String> getUniqueVisitedUrl() {
        return new HashSet<>(allVisitUrls);
    }

    public Set<String> getUniqueInsideUrl() {
        List<String> insideUrl = urlData.stream()
                .filter(p -> p.getValue() == PointerStatus.OK)
                .map(e -> e.getKey())
                .collect(Collectors.toList());
        return new HashSet<>(insideUrl);
    }

    public Set<String> getUniqueOutsideUrl() {
        List<String> outsideUrl = urlData.stream()
                .filter(p -> p.getValue() == PointerStatus.N_OK)
                .map(e -> e.getKey())
                .collect(Collectors.toList());
        return new HashSet<>(outsideUrl);
    }
}

enum PointerStatus {
    OK,
    N_OK
}