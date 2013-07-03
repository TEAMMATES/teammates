package teammates.common.datatransfer;

import teammates.common.util.Constants;

/**
 * Represents the course statistics for a course.
 * <br> Contains:
 * <br> * The total number of teams in the course.
 * <br> * The total number of students in the course.
 * <br> * The total number of unregistered students in the course.
 */
public class CourseStats {
	public int teamsTotal = Constants.INT_UNINITIALIZED;
	public int studentsTotal = Constants.INT_UNINITIALIZED;
	public int unregisteredTotal = Constants.INT_UNINITIALIZED;
}
