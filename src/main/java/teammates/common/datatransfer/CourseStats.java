package teammates.common.datatransfer;

import teammates.common.util.Const;

/**
 * Represents the course statistics for a course.
 * <br> Contains:
 * <br> * The total number of teams in the course.
 * <br> * The total number of students in the course.
 * <br> * The total number of unregistered students in the course.
 */
public class CourseStats {
	public int teamsTotal = Const.INT_UNINITIALIZED;
	public int studentsTotal = Const.INT_UNINITIALIZED;
	public int unregisteredTotal = Const.INT_UNINITIALIZED;
}
