package teammates.test.scripts;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.test.driver.BackDoor;

public class DataMigrationAppendInstitutionforAccount {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String result = BackDoor.appendInstitutionForAccount();
		Assumption.assertEquals(Common.BACKEND_STATUS_SUCCESS, result);
	}

}
