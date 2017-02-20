package teammates.test.cases.automated;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.ui.automated.InstructorCourseJoinEmailWorkerAction;

/**
 * SUT: {@link InstructorCourseJoinEmailWorkerAction}.
 */
public class InstructorCourseJoinEmailWorkerActionTest extends BaseAutomatedActionTest {
    
    @Override
    protected String getActionUri() {
        return Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_WORKER_URL;
    }
    
    @Test
    public void allTests() {
        
        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");
        InstructorAttributes receiver = dataBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes sender = dataBundle.instructors.get("instructor2OfCourse1");
        
        String[] submissionParams = new String[] {
                ParamsNames.COURSE_ID, course1.getId(),
                ParamsNames.EMAIL_RECEIVER, receiver.email,
                ParamsNames.SENDER_ID, sender.googleId
        };
        
        InstructorCourseJoinEmailWorkerAction action = getAction(submissionParams);
        action.execute();
        
        verifyNumberOfEmailsSent(action, 1);
        
        EmailWrapper email = action.getEmailSender().getEmailsSent().get(0);
        assertEquals(String.format(EmailType.INSTRUCTOR_COURSE_JOIN.getSubject(), course1.getName(),
                                   course1.getId()),
                     email.getSubject());
        assertEquals(receiver.email, email.getRecipient());
        
    }
    
    @Override
    protected InstructorCourseJoinEmailWorkerAction getAction(String... params) {
        return (InstructorCourseJoinEmailWorkerAction) gaeSimulation.getAutomatedActionObject(getActionUri(), params);
    }
    
}
