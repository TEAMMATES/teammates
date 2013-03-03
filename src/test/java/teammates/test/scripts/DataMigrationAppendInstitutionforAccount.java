package teammates.test.scripts;

import teammates.test.driver.BackDoor;

public class DataMigrationAppendInstitutionforAccount {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int count;
		do {
			String result = BackDoor.appendInstitutionForAccount();
			count = Integer.parseInt(result);
			System.out.println("Handled " + count + " entities");
		} while (count != 0);
		System.out.println("Migration complete");
	}

}
