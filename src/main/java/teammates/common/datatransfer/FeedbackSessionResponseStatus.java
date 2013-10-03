package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackSessionResponseStatus {
	public Map<String, List<String>> noResponse;
	public Map<String, List<String>> hasResponse;	
	
	public FeedbackSessionResponseStatus() {		
		noResponse = new HashMap<String, List<String>> ();
		hasResponse = new HashMap<String, List<String>> ();
	}
	
	public void add(String questionId, String userName, boolean responded) {
		List<String> users;
		Map<String, List<String>> listToAddTo;		
		if (responded) {
			listToAddTo = hasResponse;
		} else {
			listToAddTo = noResponse;
		}
		users = listToAddTo.get(questionId);
		if (users == null) {
			users = new ArrayList<String>();
		}
		users.add(userName);
		listToAddTo.put(questionId, users);
	}
}
