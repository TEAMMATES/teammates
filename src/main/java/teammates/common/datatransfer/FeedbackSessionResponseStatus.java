package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedbackSessionResponseStatus {
    public List<String> expected;
    public List<String> hasResponse; 
    public List<String> noResponse;   
    
    public FeedbackSessionResponseStatus() {        
        expected = new ArrayList<String>();
        hasResponse = new ArrayList<String>();
        noResponse = new ArrayList<String>();
    }
    
    public void addUserWithResponses(String userName){
        hasResponse.add(userName);
    }

    public void addExpected(String userName){
        expected.add(userName);
    }

    public void addUserWithNoResponses(String userName){
        noResponse.add(userName);
    }
    
    public List<String> getStudentsWhoDidNotRespondToAnyQuestion() {
        Collections.sort(noResponse);
        return noResponse;
    }
}
