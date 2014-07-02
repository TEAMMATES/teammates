package teammates.storage.search;

import teammates.common.util.Sanitizer;

import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;

public class SearchQuery {
    private static final String AND = " AND ";
    
    private QueryOptions options;
    private String textQueryString = "";
    private String dateQueryString = "";
    private String googleId;

    public SearchQuery(QueryOptions options, String googleId){
        this.options = options;
        this.googleId = googleId;
    }
    
    public SearchQuery setTextFilter(String queryString){
        this.textQueryString = "searchableText:" + Sanitizer.sanitizeForHtml(queryString).toLowerCase().trim();
        return this;
    }
    
    public SearchQuery setDateFilter(String dateField, String startTime, String endTime){
        this.dateQueryString = startTime + " <= " + dateField + " AND " + dateField + " <= " + endTime;
        return this;
    }
    
    public Query toQuery(){
        String queryString = buildQueryString();
        return Query.newBuilder()
                .setOptions(options)
                .build(queryString);
    }
    
    private String buildQueryString(){
        StringBuilder queryStringBuilder = new StringBuilder("whoCanSee:" + googleId);
        if(!textQueryString.isEmpty()){
            queryStringBuilder.append(AND).append(textQueryString);
        }
        if(!dateQueryString.isEmpty()){
            queryStringBuilder.append(AND).append(dateQueryString);
        }
        return queryStringBuilder.toString();
    }
}
