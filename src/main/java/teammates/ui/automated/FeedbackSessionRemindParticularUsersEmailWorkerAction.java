package teammates.ui.automated;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.logic.api.EmailGenerator;

/**
 * Task queue worker action: sends feedback session reminder email to particular students of a course.
 */
public class FeedbackSessionRemindParticularUsersEmailWorkerAction extends AutomatedAction {
    
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
        String feedbackSessionName = getRequestParamValue(ParamsNames.SUBMISSION_FEEDBACK);
        Assumption.assertNotNull(feedbackSessionName);
        
        String courseId = getRequestParamValue(ParamsNames.SUBMISSION_COURSE);
        Assumption.assertNotNull(courseId);
        
        String[] usersToRemind = getRequestParamValues(ParamsNames.SUBMISSION_REMIND_USERLIST);
        
        try {
            FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
            List<StudentAttributes> studentsToRemindList = new ArrayList<StudentAttributes>();
            List<InstructorAttributes> instructorsToRemindList = new ArrayList<InstructorAttributes>();
            
            for (String userEmail : usersToRemind) {
                StudentAttributes student = logic.getStudentForEmail(courseId, userEmail);
                if (student != null) {
                    studentsToRemindList.add(student);
                }
                
                InstructorAttributes instructor = logic.getInstructorForEmail(courseId, userEmail);
                if (instructor != null) {
                    instructorsToRemindList.add(instructor);
                }
            }
            
            List<EmailWrapper> emails = new EmailGenerator().generateFeedbackSessionReminderEmails(
                    session, studentsToRemindList, instructorsToRemindList, new ArrayList<InstructorAttributes>());
            taskQueuer.scheduleEmailsForSending(emails);
        } catch (Exception e) {
            log.severe("Unexpected error while sending emails: " + TeammatesException.toStringWithStackTrace(e));
        }
    }
    
}
