package teammates.common.datatransfer;

import teammates.common.Common;

/**
 * Represents the course statistics for a course.
 * <br> Contains:
 * <br> * The total number of teams in the course.
 * <br> * The total number of students in the course.
 * <br> * The total number of unregistered students in the course.
 */
public class CourseStats {
	public int teamsTotal = Common.UNINITIALIZED_INT;
	public int studentsTotal = Common.UNINITIALIZED_INT;
	public int unregisteredTotal = Common.UNINITIALIZED_INT;
}
