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
        String inviterId = getRequestParamValue(ParamsNames.INVITER_ID);
        Assumption.assertNotNull(inviterId);
        String courseId = getRequestParamValue(ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String inviteReceiverEmail = getRequestParamValue(ParamsNames.INVITE_RECEIVER_EMAIL);
        Assumption.assertNotNull(inviteReceiverEmail);

        AccountAttributes inviter = logic.getAccount(inviterId);
        Assumption.assertNotNull(inviter);
        
        CourseAttributes course = logic.getCourse(courseId);
        Assumption.assertNotNull(course);
        
        InstructorAttributes inviteReceiver = logic.getInstructorForEmail(courseId, inviteReceiverEmail);
        Assumption.assertNotNull(inviteReceiver);
        
        EmailWrapper email = new EmailGenerator()
                .generateInstructorCourseJoinEmail(inviter, inviteReceiver, course);
        
        try {
            emailSender.sendEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while sending email", e);
        }
    }
    
}
