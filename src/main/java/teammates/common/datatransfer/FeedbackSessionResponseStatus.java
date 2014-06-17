package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackSessionResponseStatus {
    public List<String> hasResponse; 
    public List<String> noResponse;   
    public Map<String, String> emailNameTable;
    
    public FeedbackSessionResponseStatus() {        
        hasResponse = new ArrayList<String>();
        noResponse = new ArrayList<String>();
        emailNameTable = new HashMap<String, String>();
    }
    
    public List<String> getStudentsWhoDidNotRespondToAnyQuestion() {
        Collections.sort(noResponse);
        return noResponse;
    }
}

