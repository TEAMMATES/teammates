package teammates.common.datatransfer;

import teammates.common.Common;

public class SubmissionDetailsBundle {
	public transient String reviewerName = null;
	public transient String revieweeName = null;
	public transient int normalizedToStudent = Common.UNINITIALIZED_INT;
	public transient int normalizedToInstructor = Common.UNINITIALIZED_INT;
}