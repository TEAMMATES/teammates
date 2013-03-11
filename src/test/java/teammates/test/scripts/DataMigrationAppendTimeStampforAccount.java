package teammates.test.scripts;

import teammates.test.driver.BackDoor;

public class DataMigrationAppendTimeStampforAccount {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int count;
		int entityCountStart = 0;
		do {
			String response = BackDoor.appendTimestampForAccount(entityCountStart);
			count = Integer.parseInt(response);
			System.out.println("Starting at: " + entityCountStart + ", handled " + count + " entities");
			entityCountStart += count;
		} while (count != 0);
		System.out.println("Migration complete");
	}

}
