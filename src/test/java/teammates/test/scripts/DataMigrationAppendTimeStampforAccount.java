package teammates.test.scripts;

import teammates.test.driver.BackDoor;

public class DataMigrationAppendTimeStampforAccount {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int count;
		do {
			String response = BackDoor.appendTimestampForAccount();
			count = Integer.parseInt(response);
			System.out.println("Handled " + count + " entities");
		} while (count != 0);
		System.out.println("Migration complete");
	}

}
