package teammates.common.datatransfer;

import teammates.common.util.Const;

public class SubmissionDetailsBundle {
	public transient String reviewerName = null;
	public transient String revieweeName = null;
	public transient int normalizedToStudent = Const.INT_UNINITIALIZED;
	public transient int normalizedToInstructor = Const.INT_UNINITIALIZED;
}