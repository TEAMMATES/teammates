package teammates.common.datatransfer;

import teammates.common.util.Config;

/**
 * Represents the course statistics for a course.
 * <br> Contains:
 * <br> * The total number of teams in the course.
 * <br> * The total number of students in the course.
 * <br> * The total number of unregistered students in the course.
 */
public class CourseStats {
	public int teamsTotal = Config.UNINITIALIZED_INT;
	public int studentsTotal = Config.UNINITIALIZED_INT;
	public int unregisteredTotal = Config.UNINITIALIZED_INT;
}
