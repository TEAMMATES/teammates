package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.Account;

/**
 * SUT: {@link InstructorCourseJoinEmailWorkerAction}.
 */
public class InstructorCourseJoinEmailWorkerActionTest
        extends BaseActionTest<InstructorCourseJoinEmailWorkerAction> {

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_WORKER_URL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

    @Override
    @Test(enabled = false) // failing due to sql accountsdb being mocked somehow
    public void testExecute() throws InvalidParametersException, EntityAlreadyExistsException {

        CourseAttributes course1 = typicalBundle.courses.get("typicalCourse1");
        InstructorAttributes instr1InCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        // account is migrated to sql, so we will just create the account for use here
        sqlLogic.createAccount(new Account("idOfInstructor2OfCourse1", "Instructor 2 of Course 1", "instr2@course1.tmt"));
        Account inviter = sqlLogic.getAccountForGoogleId("idOfInstructor2OfCourse1");

        ______TS("typical case: new instructor joining");

        String[] submissionParams = new String[] {
                ParamsNames.COURSE_ID, course1.getId(),
                ParamsNames.INSTRUCTOR_EMAIL, instr1InCourse1.getEmail(),
                ParamsNames.INVITER_ID, inviter.getGoogleId(),
                ParamsNames.IS_INSTRUCTOR_REJOINING, "false",
        };

        InstructorCourseJoinEmailWorkerAction action = getAction(submissionParams);
        action.execute();

        verifyNumberOfEmailsSent(1);

        EmailWrapper email = mockEmailSender.getEmailsSent().get(0);

        assertEquals(String.format(EmailType.INSTRUCTOR_COURSE_JOIN.getSubject(), course1.getName(),
                                   course1.getId()),
                     email.getSubject());
        assertEquals(instr1InCourse1.getEmail(), email.getRecipient());

        ______TS("typical case: old instructor rejoining (after google id reset)");

        submissionParams = new String[] {
                ParamsNames.COURSE_ID, course1.getId(),
                ParamsNames.INSTRUCTOR_EMAIL, instr1InCourse1.getEmail(),
                ParamsNames.IS_INSTRUCTOR_REJOINING, "true",
        };

        action = getAction(submissionParams);
        action.execute();

        verifyNumberOfEmailsSent(1);

        email = mockEmailSender.getEmailsSent().get(0);

        assertEquals(String.format(EmailType.INSTRUCTOR_COURSE_REJOIN_AFTER_GOOGLE_ID_RESET.getSubject(), course1.getName(),
                course1.getId()),
                email.getSubject());
        assertEquals(instr1InCourse1.getEmail(), email.getRecipient());

    }

}
