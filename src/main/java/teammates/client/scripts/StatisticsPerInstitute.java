package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.storage.entity.Account;

/**
 * Generate list of institutes and number of users per institute.
 */
public class StatisticsPerInstitute extends RemoteApiClient {
	
	private static final int INSTRUCTOR_INDEX = 0;
	private static final int STUDENT_INDEX = 1;
	
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
				institutes.get(a.getInstitute()).put(INSTRUCTOR_INDEX, 0);
				institutes.get(a.getInstitute()).put(STUDENT_INDEX, 0);
			}

			// Increase the appropriate slot
			if (a.isInstructor()) {
				institutes.get(a.getInstitute()).put(INSTRUCTOR_INDEX,
						institutes.get(a.getInstitute()).get(INSTRUCTOR_INDEX) + 1);
			} else {
				institutes.get(a.getInstitute()).put(STUDENT_INDEX,
						institutes.get(a.getInstitute()).get(STUDENT_INDEX) + 1);
			}
		}
		
		List<InstituteStats> statList = convertToList(institutes);
		sortByTotalStudentsDescending(statList);
		print(statList);
	}
	
	private void print(List<InstituteStats> statList) {
		System.out.println("===================================================");
		System.out.println("Format=> Instructors + Students = Total [Institute]");
		System.out.println("===================================================");
		int i = 0;
		int runningTotal = 0;
		for (InstituteStats stats : statList) {
			i++;
			int numInstructors = stats.instructorTotal;
			int numStudents = stats.studentTotal;
			int total = numInstructors + numStudents;
			runningTotal += total; 
			System.out.println(
					"["+i+"]" + numInstructors + "+" + numStudents + "=" 
							+ total	+ "{" + runningTotal + "}\t[" + stats.name + "]");
		}
		
	}

	private List<InstituteStats> convertToList(
			HashMap<String, HashMap<Integer, Integer>> institutes) {
		List<InstituteStats> list = new ArrayList<InstituteStats>();
		for (String insName : institutes.keySet()) {
			InstituteStats insStat = new InstituteStats();
			insStat.name = insName;
			insStat.studentTotal = institutes.get(insName).get(STUDENT_INDEX);
			insStat.instructorTotal = institutes.get(insName).get(INSTRUCTOR_INDEX);
			list.add(insStat);
		}
		return list;
	}
	
	private void sortByTotalStudentsDescending(List<InstituteStats> list){
		Collections.sort(list, new Comparator<InstituteStats>() {
			public int compare(InstituteStats inst1, InstituteStats inst2) {
				//the two objects are swapped, to sort in descending order
				return (new Integer(inst2.studentTotal).compareTo(new Integer(inst1.studentTotal)));
			}
		});
	}

	class InstituteStats{
		String name;
		int studentTotal;
		int instructorTotal;
		
		
	}
	
}
