package teammates.test.scripts;

import teammates.common.Common;
import teammates.common.Assumption;
import teammates.test.driver.BackDoor;

public class DataMigrationCreateInstructorPerCourse {

	public static void main(String[] args) {	
		String status = BackDoor.createInstructorsFromCourses();
		Assumption.assertEquals(Common.BACKEND_STATUS_SUCCESS, status);	
	}
}
