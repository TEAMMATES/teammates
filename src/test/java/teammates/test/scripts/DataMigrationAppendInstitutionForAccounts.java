package teammates.test.scripts;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;

import teammates.test.driver.RemoteApiClient;

public class DataMigrationAppendInstitutionForAccounts extends RemoteApiClient {
	
	private static final boolean isTrial = false;
	
	public static void main(String[] args) throws IOException {
		DataMigrationAppendInstitutionForAccounts migrator = new DataMigrationAppendInstitutionForAccounts();
		migrator.doOperationRemotely();
	}
	
	protected void doOperation() {
		//appendInstitutionForAccounts();
		setInstructorStatusForInstructorAccounts();
	}
	
	private void setInstructorStatusForInstructorAccounts() {
		String query = "SELECT FROM " + Instructor.class.getName();
		
		@SuppressWarnings("unchecked")
		List<Instructor> instructors = (List<Instructor>) pm.newQuery(query).execute();
		
		int count = 0;
		HashMap<String, Boolean> hasBeenModified = new HashMap<String, Boolean>();
		for (Instructor i : instructors) {
			if (!hasBeenModified.containsKey(i.getGoogleId())) {
				query = "SELECT FROM " + Account.class.getName() +
						" WHERE googleId == '" + i.getGoogleId() + "'";
				
				@SuppressWarnings("unchecked")
				List<Account> accounts = (List<Account>) pm.newQuery(query).execute();
				Account instructorAccount = accounts.get(0);
				instructorAccount.setIsInstructor(true);
				hasBeenModified.put(i.getGoogleId(), true);
				count++;
			}
		}
		
		pm.flush();
		pm.close();
		
		System.out.println("Reapplied instructor status for " + count + " accounts");
	}

	private static void appendInstitutionForAccounts() {
		// Instructor Accounts get Institute for an Instructor
		String query = "select from " + Account.class.getName()
				+ " where isInstructor == true";
		
		@SuppressWarnings("unchecked")
		List<Account> instructorAccounts = (List<Account>) pm.newQuery(query).execute();
		
		HashMap<String, String> instructorInstitutions = new HashMap<String, String>();
		
		for (Account a : instructorAccounts) {
			if (a.getInstitute() == null || a.getInstitute().isEmpty()) {
				a.setInstitute("National University of Singapore");
			}
			instructorInstitutions.put(a.getGoogleId(), a.getInstitute());
		}
		
		System.out.println("Finished mapping instructor-institutions: " + instructorInstitutions.size());
		
		//======================================================================
		// Given Institute for Instructor create Course-Institute pair
		query = "select from " + Instructor.class.getName();
		
		@SuppressWarnings("unchecked")
		List<Instructor> instructors = (List<Instructor>) pm.newQuery(query).execute();
		
		HashMap<String, String> courseInstitutions = new HashMap<String, String>();
		
		for (Instructor i : instructors) {
			courseInstitutions.put(i.getCourseId(), instructorInstitutions.get(i.getGoogleId()));
		}
		
		System.out.println("Finished mapping course-institutions: " + courseInstitutions.size());
		
		//======================================================================
		// Given Course-Institute Pair create Student-Institute Pair
		query = "select from " + Student.class.getName()
				+ " where ID != null";
		
		@SuppressWarnings("unchecked")
		List<Student> students = (List<Student>) pm.newQuery(query).execute();
		
		HashMap<String, String> studentInstitutions = new HashMap<String, String>();
		
		for (Student s : students) {
			studentInstitutions.put(s.getID(), courseInstitutions.get(s.getCourseID()));
		}
		
		System.out.println("Finished mapping student-institutions: " + studentInstitutions.size());
		
		//======================================================================
		// *******************************************************************
		// Can test the functionality up to this point to check correctness of mapping
		// Only the following loop does the appending
		// *******************************************************************
		//======================================================================
		// Student Accounts append Institute from Student-Institute pair		
		int count = 0;
		for (String id : studentInstitutions.keySet()) {
			query = "select from " + Account.class.getName()
					+ " where googleId == \"" + id + "\"";
			
			@SuppressWarnings("unchecked")
			List<Account> studentAccounts = (List<Account>) pm.newQuery(query).execute();
			if (studentAccounts.size() > 0) {
				Account a = studentAccounts.get(0);
				if (a.getInstitute() == null || a.getInstitute().equals("")) {
					System.out.println("Assigning '" + studentInstitutions.get(a.getGoogleId()) + "' to '" + a.getGoogleId() + "'");
					if (!isTrial) {
						a.setInstitute(studentInstitutions.get(a.getGoogleId()));
					}
					count++;
				}
			}
		}
		
		System.out.println("Appended for " + count + " entities");
		pm.flush();
		pm.close();
	}
	
	private static void undoAppendInstitutionForAccounts() {
		String query = "SELECT FROM " + Account.class.getName();
		@SuppressWarnings("unchecked")
		List<Account> accountsList = (List<Account>) pm.newQuery(query).execute();
		
		int count = 0;
		for (Account a : accountsList) {
			if ((a.getInstitute() != null) && (!a.getInstitute().equals(""))) {
				a.setInstitute("");
				pm.deletePersistent(a);
				pm.makePersistent(a);
				count++;
			}
		}
		System.out.println("Undid " + count + " entitites from " + accountsList.size() + " entities");
	}
}
