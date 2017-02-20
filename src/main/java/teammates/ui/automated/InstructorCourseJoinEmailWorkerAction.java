package teammates.ui.automated;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
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
        String senderId = getRequestParamValue(ParamsNames.SENDER_ID);
        Assumption.assertNotNull(senderId);
        String courseId = getRequestParamValue(ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String receiverInstructorEmail = getRequestParamValue(ParamsNames.EMAIL_RECEIVER);
        Assumption.assertNotNull(receiverInstructorEmail);

        AccountAttributes sender = logic.getAccount(senderId);
        Assumption.assertNotNull(sender);
        
        CourseAttributes course = logic.getCourse(courseId);
        Assumption.assertNotNull(course);
        
        InstructorAttributes receiver = logic.getInstructorForEmail(courseId, receiverInstructorEmail);
        Assumption.assertNotNull(receiver);
        
        EmailWrapper email = new EmailGenerator()
                .generateInstructorCourseJoinEmail(sender, receiver, course);
        
        try {
            emailSender.sendEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while sending email", e);
        }
    }
    
}
