package teammates.client.remoteapi;

import java.io.IOException;
import java.util.List;

import teammates.storage.entity.Account;

/**
 * Obtains email of instructors and prints to console
 */
public class GenerateEmailsOfInstructors extends RemoteApiClient {
	
	public static void main(String[] args) throws IOException {
		GenerateEmailsOfInstructors statistics = new GenerateEmailsOfInstructors();
		statistics.doOperationRemotely();
	}
	
	@SuppressWarnings("unchecked")
	protected void doOperation() {
		String q = "SELECT FROM " + Account.class.getName() + " WHERE isInstructor == true";
		
		List<Account> instructorAccounts = (List<Account>) pm.newQuery(q).execute();
		
		// Print
		for (Account a : instructorAccounts) {
			System.out.println(a.getName() + ": " + a.getEmail() + ", (" + a.getInstitute() + ")");
		}
	}
	
}
