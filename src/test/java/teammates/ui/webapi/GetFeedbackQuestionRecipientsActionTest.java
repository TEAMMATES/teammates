package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackQuestionRecipientsData;
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetFeedbackQuestionRecipientsAction}.
 */
public class GetFeedbackQuestionRecipientsActionTest extends BaseActionTest<GetFeedbackQuestionRecipientsAction> {

    private FeedbackSessionAttributes firstSessionInCourse1;
    private FeedbackSessionAttributes secondSessionInCourse1;
    private FeedbackSessionAttributes firstSessionInCourse2;
    private StudentAttributes student1InCourse1;
    private StudentAttributes student3InCourse1;
    private InstructorAttributes instructor1OfCourse1;
    private InstructorAttributes instructor1OfCourse2;

    @Override
    public void prepareTestData() {
        DataBundle testData = loadDataBundle("/GetFeedbackQuestionRecipientsActionTest.json");
        removeAndRestoreDataBundle(testData);
        firstSessionInCourse1 = testData.feedbackSessions.get("session1InCourse1");
        secondSessionInCourse1 = testData.feedbackSessions.get("session2InCourse1");
        firstSessionInCourse2 = testData.feedbackSessions.get("session1InCourse2");
        student1InCourse1 = testData.students.get("student1InCourse1");
        student3InCourse1 = testData.students.get("student3InCourse1");
        instructor1OfCourse1 = testData.instructors.get("instructor1OfCourse1");
        instructor1OfCourse2 = testData.instructors.get("instructor1OfCourse2");
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.QUESTION_RECIPIENTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void testExecute() {
        // See independent test cases
    }

    @Test
    public void testExecute_invalidIntend_shouldFail() {
        ______TS("Invalid intend, should fail");
        String[] invalidIntendParams = generateParameters(firstSessionInCourse1, 1, Intent.FULL_DETAIL, "", "", "");
        verifyHttpParameterFailure(invalidIntendParams);
    }

    @Test
    public void testExecute_moderatedAndPreviewPersonToGetRecipients_shouldReturnSameRecipientsGotFromGiver() {

        loginAsStudent(student1InCourse1.getGoogleId());
        String[] questionParams = generateParameters(firstSessionInCourse1, 2, Intent.STUDENT_SUBMISSION, "", "", "");
        FeedbackQuestionRecipientsData expectedRecipients = getRecipients(questionParams);

        ______TS("Test moderated person get recipient, should be same as recipients got from giver");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] moderateQuestionParams = generateParameters(
                firstSessionInCourse1, 2, Intent.STUDENT_SUBMISSION, "", student1InCourse1.getEmail(), "");
        FeedbackQuestionRecipientsData moderatedRecipients = getRecipients(moderateQuestionParams);
        verifyFeedbackQuestionRecipientsDataEquals(expectedRecipients, moderatedRecipients);

        ______TS("Test preview person get recipient, should be same as recipients got from giver");
        String[] previewQuestionParams = generateParameters(
                firstSessionInCourse1, 2, Intent.STUDENT_SUBMISSION, "", "", student1InCourse1.getEmail());
        FeedbackQuestionRecipientsData previewRecipients = getRecipients(previewQuestionParams);
        verifyFeedbackQuestionRecipientsDataEquals(expectedRecipients, previewRecipients);
    }

    @Test
    public void testExecute_differentRecipientTypes_shouldReturnRecipientsCorrectly() {

        ______TS("Test typical recipient type: Self");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] selfQuestionParams =
                generateParameters(firstSessionInCourse1, 1, Intent.STUDENT_SUBMISSION, "", "", "");
        FeedbackQuestionRecipientsData selfRecipients = getRecipients(selfQuestionParams);
        assertEquals(1, selfRecipients.getRecipients().size());
        assertEquals("student1InCourse1@gmail.tmt", selfRecipients.getRecipients().get(0).getIdentifier());

        ______TS("Test typical recipient type: Student");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] studentQuestionParams =
                generateParameters(firstSessionInCourse1, 2, Intent.STUDENT_SUBMISSION, "", "", "");
        FeedbackQuestionRecipientsData studentRecipients = getRecipients(studentQuestionParams);
        assertEquals(4, studentRecipients.getRecipients().size());
        assertEquals("student2InCourse1@gmail.tmt", studentRecipients.getRecipients().get(0).getIdentifier());
        assertEquals("student3InCourse1@gmail.tmt", studentRecipients.getRecipients().get(1).getIdentifier());
        assertEquals("student4InCourse1@gmail.tmt", studentRecipients.getRecipients().get(2).getIdentifier());
        assertEquals("student5InCourse1@gmail.tmt", studentRecipients.getRecipients().get(3).getIdentifier());

        ______TS("Test typical recipient type: Instructor");
        loginAsInstructor(instructor1OfCourse2.getGoogleId());
        String[] instructorQuestionParams =
                generateParameters(firstSessionInCourse2, 2, Intent.INSTRUCTOR_SUBMISSION, "", "", "");
        FeedbackQuestionRecipientsData instructorRecipients = getRecipients(instructorQuestionParams);
        assertEquals(2, instructorRecipients.getRecipients().size());
        assertEquals("instructor2@course2.tmt", instructorRecipients.getRecipients().get(0).getIdentifier());
        assertEquals("instructor3@course2.tmt", instructorRecipients.getRecipients().get(1).getIdentifier());

        ______TS("Test typical recipient type: Team");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] teamQuestionParams =
                generateParameters(secondSessionInCourse1, 1, Intent.STUDENT_SUBMISSION, "", "", "");
        FeedbackQuestionRecipientsData teamRecipients = getRecipients(teamQuestionParams);
        assertEquals(1, teamRecipients.getRecipients().size());
        assertEquals("Team 1.2", teamRecipients.getRecipients().get(0).getIdentifier());

        ______TS("Test typical recipient type: Own team");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] ownTeamQuestionParams =
                generateParameters(secondSessionInCourse1, 2, Intent.STUDENT_SUBMISSION, "", "", "");
        FeedbackQuestionRecipientsData ownTeamRecipients = getRecipients(ownTeamQuestionParams);
        assertEquals(1, ownTeamRecipients.getRecipients().size());
        assertEquals("Team 1.1", ownTeamRecipients.getRecipients().get(0).getIdentifier());

        ______TS("Test typical recipient type: Own team member");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] memberQuestionParams =
                generateParameters(secondSessionInCourse1, 3, Intent.STUDENT_SUBMISSION, "", "", "");
        FeedbackQuestionRecipientsData memberRecipients = getRecipients(memberQuestionParams);
        assertEquals(3, memberRecipients.getRecipients().size());
        assertEquals("student2InCourse1@gmail.tmt", memberRecipients.getRecipients().get(0).getIdentifier());
        assertEquals("student3InCourse1@gmail.tmt", memberRecipients.getRecipients().get(1).getIdentifier());
        assertEquals("student4InCourse1@gmail.tmt", memberRecipients.getRecipients().get(2).getIdentifier());

        ______TS("Test typical recipient type: Own team member including self");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] memberWithSelfQuestionParams =
                generateParameters(secondSessionInCourse1, 4, Intent.STUDENT_SUBMISSION, "", "", "");
        FeedbackQuestionRecipientsData memberWithSelfRecipients = getRecipients(memberWithSelfQuestionParams);
        assertEquals(4, memberWithSelfRecipients.getRecipients().size());
        assertEquals("student1InCourse1@gmail.tmt", memberWithSelfRecipients.getRecipients().get(0).getIdentifier());
        assertEquals("student2InCourse1@gmail.tmt", memberWithSelfRecipients.getRecipients().get(1).getIdentifier());
        assertEquals("student3InCourse1@gmail.tmt", memberWithSelfRecipients.getRecipients().get(2).getIdentifier());
        assertEquals("student4InCourse1@gmail.tmt", memberWithSelfRecipients.getRecipients().get(3).getIdentifier());

        ______TS("Test typical recipient type: None");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] noneQuestionParams =
                generateParameters(firstSessionInCourse1, 3, Intent.INSTRUCTOR_SUBMISSION, "", "", "");
        FeedbackQuestionRecipientsData noneRecipients = getRecipients(noneQuestionParams);
        assertEquals(1, noneRecipients.getRecipients().size());
        assertEquals(Const.GENERAL_QUESTION, noneRecipients.getRecipients().get(0).getIdentifier());

    }

    @Test
    @Override
    protected void testAccessControl() {
        //see independent test cases
    }

    @Test
    protected void testAccessControl_studentSubmission() throws Exception {
        // Use typical bundle for testing access control because we want to make the login account consistent
        // with "high-level" and "mid-level" access control tests, although accounts are same in two bundles
        useTypicalDataBundle();
        Intent intent = Intent.STUDENT_SUBMISSION;
        String[] params =
                generateParameters(firstSessionInCourse1, 2, Intent.STUDENT_SUBMISSION, "", "", "");

        ______TS("Typical unauthorized cases");

        verifyInaccessibleWithoutLogin(params);
        String unregUserId = "unreg.user";
        loginAsUnregistered(unregUserId);
        verifyCannotAccess(params);

        ______TS("Student access student's question, should be accessible");

        verifyAccessibleForStudentsOfTheSameCourse(params);

        ______TS("student cannot access other course question");
        params = generateParameters(firstSessionInCourse2, 1, intent, "", "", "");
        verifyCannotAccess(params);

        ______TS("Student intends to access instructor's question, should not be accessible");
        String[] studentAccessInstructorQuestionParams =
                generateParameters(firstSessionInCourse1, 3, Intent.STUDENT_SUBMISSION, "", "", "");
        loginAsStudent(student1InCourse1.getGoogleId());
        verifyCannotAccess(studentAccessInstructorQuestionParams);

        ______TS("Instructor intends to access student's question, should not be accessible");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] instructorAccessStudentQuestionParams =
                generateParameters(firstSessionInCourse1, 2, Intent.INSTRUCTOR_SUBMISSION, "", "", "");
        verifyCannotAccess(instructorAccessStudentQuestionParams);

        ______TS("Instructor access instructor's question, should be accessible");
        String[] instructorSubmissionParams =
                generateParameters(firstSessionInCourse1, 3, Intent.INSTRUCTOR_SUBMISSION, "", "", "");
        verifyOnlyInstructorsOfTheSameCourseCanAccess(instructorSubmissionParams);

        ______TS("Student access student's question, should be accessible");
        String[] studentSubmissionParams =
                generateParameters(firstSessionInCourse1, 2, Intent.STUDENT_SUBMISSION, "", "", "");
        verifyAccessibleForStudentsOfTheSameCourse(studentSubmissionParams);

        ______TS("Not logged in user access with correct unused regKey, should be accessible");
        logic.resetStudentGoogleId(student3InCourse1.getEmail(), student3InCourse1.getCourse());
        StudentAttributes unregisteredStudent =
                logic.getStudentForEmail(student3InCourse1.getCourse(), student3InCourse1.getEmail());
        String[] unregisteredStudentSubmissionParams =
                generateParameters(firstSessionInCourse1, 2, Intent.STUDENT_SUBMISSION,
                        unregisteredStudent.getKey(), "", "");
        verifyAccessibleWithoutLogin(unregisteredStudentSubmissionParams);

        ______TS("Access with correct but used regKey, should not be accessible by anyone");
        StudentAttributes registeredStudent =
                logic.getStudentForEmail(student1InCourse1.getCourse(), student1InCourse1.getEmail());
        String[] registeredStudentSubmissionParams =
                generateParameters(firstSessionInCourse1, 2, Intent.STUDENT_SUBMISSION,
                        registeredStudent.getKey(), "", "");
        verifyCannotAccess(registeredStudentSubmissionParams);

        logoutUser();
        verifyCannotAccess(registeredStudentSubmissionParams);

        ______TS("Question not intended shown to instructor, moderated instructor should not be accessible");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] invalidModeratedInstructorSubmissionParams =
                generateParameters(secondSessionInCourse1, 1, Intent.INSTRUCTOR_SUBMISSION,
                        "", instructor1OfCourse1.getEmail(), "");
        verifyCannotAccess(invalidModeratedInstructorSubmissionParams);

        ______TS("Instructor moderates student's question, should be accessible if he has privilege");
        String[] moderatedStudentSubmissionParams =
                generateParameters(firstSessionInCourse1, 2, Intent.STUDENT_SUBMISSION,
                        "", student1InCourse1.getEmail(), "");

        verifyInaccessibleForInstructorsOfOtherCourses(moderatedStudentSubmissionParams);

        InstructorAttributes helperOfCourse1 = typicalBundle.instructors.get("helperOfCourse1");
        loginAsInstructor(helperOfCourse1.getGoogleId());
        verifyCannotAccess(moderatedStudentSubmissionParams);

        grantInstructorWithSectionPrivilege(helperOfCourse1,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section 1"});
        verifyCanAccess(moderatedStudentSubmissionParams);

        ______TS("Instructor previews student's question, should be accessible if he has privilege");
        String[] previewStudentSubmissionParams =
                generateParameters(firstSessionInCourse1, 2, Intent.STUDENT_SUBMISSION,
                        "", "", student1InCourse1.getEmail());

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, previewStudentSubmissionParams);

    }

    @Test
    protected void testAccessControl_instructorSubmission() throws Exception {
        ______TS("Instructor access instructor's question, should be accessible");
        String[] instructorSubmissionParams =
                generateParameters(firstSessionInCourse1, 3, Intent.INSTRUCTOR_SUBMISSION, "", "", "");
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, instructorSubmissionParams);

        ______TS("Instructor intends to access student's question, should not be accessible");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] instructorAccessStudentQuestionParams =
                generateParameters(firstSessionInCourse1, 2, Intent.INSTRUCTOR_SUBMISSION, "", "", "");
        verifyCannotAccess(instructorAccessStudentQuestionParams);

        ______TS("Instructor moderates another instructor's question, "
                + "should be accessible if he has privilege");
        String[] moderatedInstructorSubmissionParams =
                generateParameters(firstSessionInCourse1, 3, Intent.INSTRUCTOR_SUBMISSION,
                        "", instructor1OfCourse1.getEmail(), "");
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                moderatedInstructorSubmissionParams);

        ______TS("Instructor previews another instructor's question,"
                + " should be accessible if he has privilege");
        String[] previewInstructorSubmissionParams =
                generateParameters(firstSessionInCourse1, 3, Intent.INSTRUCTOR_SUBMISSION,
                        "", "", instructor1OfCourse1.getEmail());
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, previewInstructorSubmissionParams);

        ______TS("Question not intended shown to instructor, moderated instructor should not be accessible");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] invalidModeratedInstructorSubmissionParams =
                generateParameters(secondSessionInCourse1, 1, Intent.INSTRUCTOR_SUBMISSION,
                        "", instructor1OfCourse1.getEmail(), "");
        verifyCannotAccess(invalidModeratedInstructorSubmissionParams);
    }

    private void useTypicalDataBundle() {
        removeAndRestoreTypicalDataBundle();
        firstSessionInCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        secondSessionInCourse1 = typicalBundle.feedbackSessions.get("session2InCourse1");
        firstSessionInCourse2 = typicalBundle.feedbackSessions.get("session1InCourse2");
        student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        student3InCourse1 = typicalBundle.students.get("student3InCourse1");
        instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");
    }

    private String[] generateParameters(FeedbackSessionAttributes session, int questionNumber, Intent intent,
                                        String regKey, String moderatedPerson, String previewPerson) {
        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(session.getFeedbackSessionName(),
                session.getCourseId(), questionNumber);
        return new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getFeedbackQuestionId(),
                Const.ParamsNames.INTENT, intent.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedPerson,
                Const.ParamsNames.PREVIEWAS, previewPerson,
                Const.ParamsNames.REGKEY, regKey,
        };
    }

    private FeedbackQuestionRecipientsData getRecipients(String[] params) {
        GetFeedbackQuestionRecipientsAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        return (FeedbackQuestionRecipientsData) result.getOutput();
    }

    private void verifyFeedbackQuestionRecipientsDataEquals(FeedbackQuestionRecipientsData expected,
                                                            FeedbackQuestionRecipientsData actual) {
        assertEquals(expected.getRecipients().size(), actual.getRecipients().size());
        for (int i = 0; i < expected.getRecipients().size(); i++) {
            assertEquals(expected.getRecipients().get(i).getIdentifier(),
                    actual.getRecipients().get(i).getIdentifier());
            assertEquals(expected.getRecipients().get(i).getName(), actual.getRecipients().get(i).getName());
        }
    }
}
