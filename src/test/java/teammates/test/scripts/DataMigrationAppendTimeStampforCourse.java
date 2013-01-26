package teammates.test.scripts;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.test.driver.BackDoor;

public class DataMigrationAppendTimeStampforCourse {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String result = BackDoor.appendTimestampForCourse();
		Assumption.assertEquals(Common.BACKEND_STATUS_SUCCESS, result);
	}

}
