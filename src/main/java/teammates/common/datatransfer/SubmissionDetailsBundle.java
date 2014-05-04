package teammates.common.datatransfer;

import teammates.common.util.Const;

/** 
 * Represents details of students in a team.
 * <br> Contains: 
 * <br> * reviewer name.
 * <br> * reviewee name.
 * <br> * contribution rating given by the reviewer to the reviewee (normalized for the student view).
 * <br> * contribution rating given by the reviewer to the reviewee (normalized for the instructor view).
 */
public class SubmissionDetailsBundle {
    public transient String reviewerName = null;
    public transient String revieweeName = null;
    public transient int normalizedToStudent = Const.INT_UNINITIALIZED;
    public transient int normalizedToInstructor = Const.INT_UNINITIALIZED;
}