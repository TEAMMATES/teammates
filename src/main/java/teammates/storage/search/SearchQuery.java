package teammates.storage.search;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Sanitizer;

import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;

public abstract class SearchQuery {
    protected static final String AND = " AND ";
    protected static final String OR = " OR ";
    protected static final String NOT = " NOT ";
    
    //to be defined by the inherited class
    protected String visibilityQueryString;
    
    private QueryOptions options;
    private List<String> textQueryStrings = new ArrayList<String>();
    private List<String> dateQueryStrings = new ArrayList<String>();
    
    protected void setOptions(QueryOptions options){
        this.options = options;
    }
    
    protected SearchQuery setTextFilter(String textField, String queryString){
        this.textQueryStrings.add(textField + ":" + Sanitizer.sanitizeForSearch(queryString).toLowerCase().trim());
        return this;
    }
    
    protected SearchQuery setDateFilter(String dateField, String startTime, String endTime){
        this.dateQueryStrings.add(startTime + " <= " + dateField + AND + dateField + " <= " + endTime);
        return this;
    }
    
    public Query toQuery(){
        String queryString = buildQueryString();
        return Query.newBuilder()
                .setOptions(options)
                .build(queryString);
    }
    
    @Override
    public String toString(){
        return buildQueryString();
    }
    
    private String buildQueryString(){
        StringBuilder queryStringBuilder = new StringBuilder(visibilityQueryString);
        for(String textQuery : textQueryStrings){
            queryStringBuilder.append(AND).append(textQuery);
        }
        for(String dateQuery : dateQueryStrings){
            queryStringBuilder.append(AND).append(dateQuery);
        }
        return queryStringBuilder.toString();
    }
}
