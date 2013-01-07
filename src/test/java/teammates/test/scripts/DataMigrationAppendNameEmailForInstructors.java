package teammates.test.scripts;

import teammates.common.Common;
import teammates.common.Assumption;
import teammates.test.driver.BackDoor;

public class DataMigrationAppendNameEmailForInstructors {

	public static void main(String[] args) {	
		String status = BackDoor.appendNameEmailForInstructors();
		Assumption.assertEquals(Common.BACKEND_STATUS_SUCCESS, status);	
	}
}
