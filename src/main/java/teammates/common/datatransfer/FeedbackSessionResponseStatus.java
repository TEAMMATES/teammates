package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	private Set<String> getSetOfStudentsWhoHaveResponded() {
		Set<String> studentsWhoResponded = new HashSet<String>();
		for (Map.Entry<String, List<String>> hasResponseEach : hasResponse.entrySet()) {
			for (String student : hasResponseEach.getValue()) {
				studentsWhoResponded.add(student);
			}
		}
		return studentsWhoResponded;
	}
	
	public List<String> getStudentsWhoDidNotRespondToAnyQuestion() {
		Set<String> studentsWhoResponded = getSetOfStudentsWhoHaveResponded();
		Set<String> studentsWhoHaveNotResponded = new HashSet<String>();
		
		// Compute who has not responded to any question by
		// checking against those who responded to at least one question.
		for (Map.Entry<String, List<String>> noResponseEach : noResponse.entrySet()) {
			for (String student : noResponseEach.getValue()) {
				if (!studentsWhoResponded.contains(student)) {
					studentsWhoHaveNotResponded.add(student);
				}
			}
		}
		
		// Sort the result for nicer display
		List<String> sortedStudents = new ArrayList<String>(studentsWhoHaveNotResponded);
		Collections.sort(sortedStudents);
		return sortedStudents;
	}
}
