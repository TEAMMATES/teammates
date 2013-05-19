package teammates.client.scripts;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.storage.entity.Account;

/**
 * Generate list of institutes and number of users per institute.
 */
public class StatisticsPerInstitute extends RemoteApiClient {
	
	private static final int INSTRUCTOR = 0;
	private static final int STUDENT = 1;
	
	public static void main(String[] args) throws IOException {
		StatisticsPerInstitute statistics = new StatisticsPerInstitute();
		statistics.doOperationRemotely();
	}
	
	@SuppressWarnings("unchecked")
	protected void doOperation() {
		HashMap<String, HashMap<Integer, Integer>> institutes = new HashMap<String, HashMap<Integer, Integer>>();
		String q = "SELECT FROM " + Account.class.getName();
		
		List<Account> allAccounts = (List<Account>) pm.newQuery(q).execute();
		
		for (Account a : allAccounts) {
			
			if (a.getInstitute() == null) {
				System.out.println("Account without institute "
						+ a.getGoogleId());
				continue;
			}
			
			// Create an entry in the HashMap if new
			if (!institutes.containsKey(a.getInstitute())) {
				institutes.put(a.getInstitute(),
						new HashMap<Integer, Integer>());
				institutes.get(a.getInstitute()).put(INSTRUCTOR, 0);
				institutes.get(a.getInstitute()).put(STUDENT, 0);
			}

			// Increase the appropriate slot
			if (a.isInstructor()) {
				institutes.get(a.getInstitute()).put(INSTRUCTOR,
						institutes.get(a.getInstitute()).get(INSTRUCTOR) + 1);
			} else {
				institutes.get(a.getInstitute()).put(STUDENT,
						institutes.get(a.getInstitute()).get(STUDENT) + 1);
			}
		}
		
		System.out.println("===================================================");
		System.out.println("Format=> Instructors + Students = Total [Institute]");
		System.out.println("===================================================");
		// Generate Statistics
		for (String institute : institutes.keySet()) {
			int numInstructors = institutes.get(institute).get(INSTRUCTOR);
			int numStudents = institutes.get(institute).get(STUDENT);
			int total = numInstructors + numStudents;
			System.out.println(numInstructors + "+" + numStudents + "=" + total
					+ "\t[" + institute + "]");
		}
	}
	
}
