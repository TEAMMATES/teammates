package teammates.ui.automated;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.core.StudentsLogic;

/**
 * Task queue worker action: sends registration email for a student of a course.
 */
public class CourseJoinRemindEmailWorkerAction extends AutomatedAction {
    
    @Override
    protected String getActionDescription() {
        return null;
    }
    
    @Override
    protected String getActionMessage() {
        return null;
    }
    
    @Override
    public void execute() {
        String courseId = getRequestParamValue(ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String studentEmail = getRequestParamValue(ParamsNames.STUDENT_EMAIL);
        Assumption.assertNotNull(studentEmail);
        
        try {
            StudentsLogic.inst().sendRegistrationInviteToStudent(courseId, studentEmail);
        } catch (EntityDoesNotExistException e) {
            log.severe("Unexpected error while sending emails: " + TeammatesException.toStringWithStackTrace(e));
        }
    }
    
}
