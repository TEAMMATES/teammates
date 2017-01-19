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
 * Task queue worker action: sends feedback session reminder email to a course.
 */
public class FeedbackSessionRemindEmailWorkerAction extends AutomatedAction {
    
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
        
        try {
            FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
            List<StudentAttributes> studentList = logic.getStudentsForCourse(courseId);
            List<InstructorAttributes> instructorList = logic.getInstructorsForCourse(courseId);
            
            List<StudentAttributes> studentsToRemindList = new ArrayList<StudentAttributes>();
            for (StudentAttributes student : studentList) {
                if (!logic.isFeedbackSessionCompletedByStudent(session, student.email)) {
                    studentsToRemindList.add(student);
                }
            }
            
            // Filter out instructors who have submitted the feedback session
            List<InstructorAttributes> instructorsToRemindList = new ArrayList<InstructorAttributes>();
            for (InstructorAttributes instructor : instructorList) {
                if (!logic.isFeedbackSessionCompletedByInstructor(session, instructor.email)) {
                    instructorsToRemindList.add(instructor);
                }
            }
            
            List<EmailWrapper> emails = new EmailGenerator().generateFeedbackSessionReminderEmails(
                    session, studentsToRemindList, instructorsToRemindList, instructorList);
            taskQueuer.scheduleEmailsForSending(emails);
        } catch (Exception e) {
            log.severe("Unexpected error while sending emails: " + TeammatesException.toStringWithStackTrace(e));
        }
    }
    
}
