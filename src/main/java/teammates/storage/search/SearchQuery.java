package teammates.storage.search;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.util.Sanitizer;
import teammates.common.util.Utils;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;

/**
 * The SearchQuery object that defines how we query {@link Document}
 */
public abstract class SearchQuery {

    protected static Logger log = Utils.getLogger();
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
    
    /*
     * Return how many query strings a SearchQuery object has
     */
    public int getFilterSize(){
        return textQueryStrings.size() + dateQueryStrings.size();
    }
    
    protected SearchQuery setTextFilter(String textField, String queryString){
        String sanitizedQueryString = Sanitizer.sanitizeForSearch(queryString).toLowerCase().trim();
        if(!sanitizedQueryString.isEmpty()){
            String preparedOrQueryString = prepareOrQueryString(sanitizedQueryString);
            this.textQueryStrings.add(textField + ":" + preparedOrQueryString);
        }
        return this;
    }
    
    private String prepareOrQueryString(String queryString){
        queryString = queryString.replaceAll("\"", " \" ");
        String[] splitStrings = queryString.trim().split("\\s+");

        List<String> keywords = new ArrayList<String>();
        String key = "";
        boolean isStartQuote = false;
        for(int i = 0; i < splitStrings.length; i++){
            if(!splitStrings[i].equals("\"")){
                if(isStartQuote){
                    key += " " + splitStrings[i];
                } else {
                    keywords.add(splitStrings[i]);
                }
            } else {
                if(isStartQuote){
                    isStartQuote = false;
                    if(!key.trim().equals("")){
                        keywords.add(key.trim());
                    }
                    key = "";
                } else {
                    isStartQuote = true;
                }
            }
        }
        
        if(isStartQuote && !key.trim().equals("")){
            keywords.add(key.trim());
        }

        if(keywords.size() < 1) return "";
        
        StringBuilder preparedQueryString = new StringBuilder("("+ keywords.get(0));
        
        for(int i = 1; i < keywords.size(); i++){
            preparedQueryString.append(OR).append(keywords.get(i));
        }
        return preparedQueryString.toString() + ")";
    }
    
    protected SearchQuery setDateFilter(String dateField, String startTime, String endTime){
        this.dateQueryStrings.add(startTime + " <= " + dateField + AND + dateField + " <= " + endTime);
        return this;
    }
    
    /*
     * Build the {@link Query} object
     */
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
        
        boolean isfirstElement = visibilityQueryString.isEmpty() ? true : false;
        
        for(String textQuery : textQueryStrings){
            if(isfirstElement){
                queryStringBuilder.append(textQuery);
                isfirstElement = false;
            } else {
                queryStringBuilder.append(AND).append(textQuery);
            }
        }
        for(String dateQuery : dateQueryStrings){
            if(isfirstElement){
                queryStringBuilder.append(dateQuery);
                isfirstElement = false;
            } else {
                queryStringBuilder.append(AND).append(dateQuery);
            }
        }
        log.info("Query: " + queryStringBuilder.toString());
        return queryStringBuilder.toString();
    }
}
