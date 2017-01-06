package teammates.ui.automated;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.logic.api.EmailGenerator;

/**
 * Task queue worker action: sends registration email for an instructor of a course.
 */
public class InstructorCourseJoinEmailWorkerAction extends AutomatedAction {
    
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
        String instructorEmail = getRequestParamValue(ParamsNames.INSTRUCTOR_EMAIL);
        Assumption.assertNotNull(instructorEmail);
        
        CourseAttributes course = logic.getCourse(courseId);
        Assumption.assertNotNull(course);
        InstructorAttributes instructor = logic.getInstructorForEmail(courseId, instructorEmail);
        Assumption.assertNotNull(instructor);
        
        EmailWrapper email = new EmailGenerator().generateInstructorCourseJoinEmail(course, instructor);
        try {
            emailSender.sendEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while sending email", e);
        }
    }
    
}
