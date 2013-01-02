package teammates.test.scripts;

import teammates.common.Common;
import teammates.common.Assumption;
import teammates.test.driver.BackDoor;

public class DataMigrationCreateAccountsForInstructors {

	public static void main(String[] args) {	
		String status = BackDoor.createAccountsForInstructors();
		Assumption.assertEquals(Common.BACKEND_STATUS_SUCCESS, status);	
	}
}
