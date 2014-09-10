package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackSessionResponseStatus {
    public List<String> hasResponse; 
    public List<String> noResponse;   
    public Map<String, String> idNameTable;
    
    public FeedbackSessionResponseStatus() {        
        hasResponse = new ArrayList<String>();
        noResponse = new ArrayList<String>();
        idNameTable = new HashMap<String, String>();
    }
    
    public List<String> getStudentsWhoDidNotRespondToAnyQuestion() {
        for(int  i  = 0; i < noResponse.size(); i++){
            String googleId = noResponse.get(i);
            noResponse.set(i, idNameTable.get(googleId));
        }
        Collections.sort(noResponse);
        return noResponse;
    }
}

