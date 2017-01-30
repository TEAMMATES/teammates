package teammates.storage.search;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Logger;
import teammates.common.util.Sanitizer;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;

/**
 * The SearchQuery object that defines how we query {@link Document}
 */
public abstract class SearchQuery {

    protected static final String AND = " AND ";
    protected static final String OR = " OR ";
    protected static final String NOT = " NOT ";
    
    private static final Logger log = Logger.getLogger();
    
    private String visibilityQueryString;
    
    private QueryOptions options;
    private List<String> textQueryStrings = new ArrayList<String>();
    
    protected SearchQuery(List<InstructorAttributes> instructors, String queryString) {
        options = QueryOptions.newBuilder()
                .setLimit(20)
                .build();
        visibilityQueryString = instructors == null ? "" : prepareVisibilityQueryString(instructors);
        setTextFilter(Const.SearchDocumentField.SEARCHABLE_TEXT, queryString);
    }
    
    protected SearchQuery(String queryString) {
        this(null, queryString);
    }
    
    protected abstract String prepareVisibilityQueryString(List<InstructorAttributes> instructors);
    
    /**
     * Returns how many query strings a SearchQuery object has.
     */
    public int getFilterSize() {
        return textQueryStrings.size();
    }
    
    private void setTextFilter(String textField, String queryString) {
        
        // The sanitize process considers the '.' (dot) as a space and this
        // returns unnecessary search results in the case if someone searches
        // using an email. To avoid this, we check whether the input text is an
        // email, and if yes, we skip the sanitize process.
        String sanitizedQueryString =
                FieldValidator.isValidEmailAddress(queryString)
                ? queryString.toLowerCase().trim()
                : Sanitizer.sanitizeForSearch(queryString).toLowerCase().trim();
        
        if (!sanitizedQueryString.isEmpty()) {
            String preparedOrQueryString = prepareOrQueryString(sanitizedQueryString);
            textQueryStrings.add(textField + ":" + preparedOrQueryString);
        }
    }
    
    private String prepareOrQueryString(String queryString) {
        String[] splitStrings = queryString.replaceAll("\"", " \" ").trim().split("\\s+");

        List<String> keywords = new ArrayList<String>();
        StringBuilder key = new StringBuilder();
        boolean isStartQuote = false;
        for (String splitString : splitStrings) {
            if ("\"".equals(splitString)) {
                if (isStartQuote) {
                    String trimmedKey = key.toString().trim();
                    isStartQuote = false;
                    if (!trimmedKey.isEmpty()) {
                        keywords.add(trimmedKey);
                    }
                    key.setLength(0);
                } else {
                    isStartQuote = true;
                }
            } else {
                if (isStartQuote) {
                    key.append(' ').append(splitString);
                } else {
                    keywords.add(splitString);
                }
            }
        }
        
        String trimmedKey = key.toString().trim();
        if (isStartQuote && !trimmedKey.isEmpty()) {
            keywords.add(trimmedKey);
        }

        if (keywords.isEmpty()) {
            return "";
        }
        
        StringBuilder preparedQueryString = new StringBuilder("(\"" + keywords.get(0) + "\"");
        
        for (int i = 1; i < keywords.size(); i++) {
            preparedQueryString.append(OR).append("\"" + keywords.get(i) + "\"");
        }
        return preparedQueryString.toString() + ")";
    }
    
    /**
     * Builds the {@link Query} object.
     */
    public Query toQuery() {
        return Query.newBuilder().setOptions(options).build(toString());
    }
    
    @Override
    public String toString() {
        StringBuilder queryStringBuilder = new StringBuilder(visibilityQueryString);
        
        boolean isfirstElement = visibilityQueryString.isEmpty();
        
        for (String textQuery : textQueryStrings) {
            if (isfirstElement) {
                queryStringBuilder.append(textQuery);
                isfirstElement = false;
            } else {
                queryStringBuilder.append(AND).append(textQuery);
            }
        }
        log.info("Query: " + queryStringBuilder.toString());
        return queryStringBuilder.toString();
    }
}
