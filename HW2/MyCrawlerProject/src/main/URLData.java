package main;

/**
 * Created by nuning on 1/29/21.
 */
public class URLData {
    private String url;
    private long size;
    private int outlinks;
    private String contentType;
    private int statusCode;

    public URLData(String url, long size, int outlinks, String contentType, int statusCode) {
        this.url = url;
        this.size = size;
        this.outlinks = outlinks;
        this.contentType = contentType;
        this.statusCode = statusCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getOutlinks() {
        return outlinks;
    }

    public void setOutlinks(int outlinks) {
        this.outlinks = outlinks;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
