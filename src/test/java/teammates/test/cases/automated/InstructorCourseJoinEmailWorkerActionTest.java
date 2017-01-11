package teammates.test.cases.automated;

import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.ui.automated.InstructorCourseJoinEmailWorkerAction;

/**
 * SUT: {@link InstructorCourseJoinEmailWorkerAction}.
 */
public class InstructorCourseJoinEmailWorkerActionTest extends BaseAutomatedActionTest {
    
    private static final DataBundle dataBundle = getTypicalDataBundle();
    
    @Override
    protected String getActionUri() {
        return Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_WORKER_URL;
    }
    
    @Test
    public void allTests() {
        
        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");
        InstructorAttributes instr1InCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        
        String[] submissionParams = new String[] {
                ParamsNames.COURSE_ID, course1.getId(),
                ParamsNames.INSTRUCTOR_EMAIL, instr1InCourse1.email
        };
        
        InstructorCourseJoinEmailWorkerAction action = getAction(submissionParams);
        action.execute();
        
        verifyNumberOfEmailsSent(action, 1);
        
        EmailWrapper email = action.getEmailSender().getEmailsSent().get(0);
        assertEquals(String.format(EmailType.INSTRUCTOR_COURSE_JOIN.getSubject(), course1.getName(),
                                   course1.getId()),
                     email.getSubject());
        assertEquals(instr1InCourse1.email, email.getRecipient());
        
    }
    
    @Override
    protected InstructorCourseJoinEmailWorkerAction getAction(String... submissionParams) {
        return (InstructorCourseJoinEmailWorkerAction)
                gaeSimulation.getAutomatedActionObject(getActionUri(), submissionParams);
    }
    
}
