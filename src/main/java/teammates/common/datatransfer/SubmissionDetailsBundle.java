package teammates.common.datatransfer;

import teammates.common.util.Config;

public class SubmissionDetailsBundle {
	public transient String reviewerName = null;
	public transient String revieweeName = null;
	public transient int normalizedToStudent = Config.UNINITIALIZED_INT;
	public transient int normalizedToInstructor = Config.UNINITIALIZED_INT;
}