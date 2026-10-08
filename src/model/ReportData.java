package model;

import java.util.List;

public class ReportData {
    private final String title;
    private final String[] headers;
    private final List<Object[]> rows;

    public ReportData(String title, String[] headers, List<Object[]> rows) {
        this.title = title;
        this.headers = headers;
        this.rows = rows;
    }
    public String getTitle() { return title; }
    public String[] getHeaders() { return headers; }
    public List<Object[]> getRows() { return rows; }
}
