package teammates.common.datatransfer;

/**
 * The search result bundle object. 
 */
public abstract class SearchResultBundle {
    protected String extractContentFromQuotedString(String quotedString){
        if(quotedString.matches("^\".*\"$")){
            return quotedString.substring(1, quotedString.length() - 1);
        } else {
            return quotedString;
        }
    }
    
    public abstract int getResultSize();
}
