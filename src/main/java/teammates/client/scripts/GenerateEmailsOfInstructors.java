package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import teammates.client.remoteapi.RemoteApiClient;
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
		for (int i = 0; i < instructorAccounts.size() - 1; i++) {
			String email = instructorAccounts.get(i).getEmail();
			if (email != null) {
				System.out.print(email + ",");
			}
		}
		
		// Last one
		System.out.println(instructorAccounts.get(instructorAccounts.size()-1).getEmail());
	}
	
}
