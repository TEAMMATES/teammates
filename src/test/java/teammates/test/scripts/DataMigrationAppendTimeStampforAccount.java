package teammates.test.scripts;

import teammates.common.Assumption;
import teammates.common.Common;
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
		} while (count != 0);
	}

}
