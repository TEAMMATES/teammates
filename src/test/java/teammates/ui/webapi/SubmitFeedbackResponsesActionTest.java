package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;
import teammates.ui.output.FeedbackResponseData;
import teammates.ui.output.FeedbackResponsesData;
import teammates.ui.request.FeedbackResponsesRequest;
import teammates.ui.request.Intent;

/**
 * SUT: {@link SubmitFeedbackResponsesAction}.
 */
public class SubmitFeedbackResponsesActionTest extends BaseActionTest<SubmitFeedbackResponsesAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSES;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    private FeedbackSessionAttributes getSession(String sessionId) {
        return typicalBundle.feedbackSessions.get(sessionId);
    }

    private InstructorAttributes getInstructor(String instructorId) {
        return typicalBundle.instructors.get(instructorId);
    }

    private InstructorAttributes loginInstructor(String instructorId) {
        InstructorAttributes instructor = getInstructor(instructorId);
        loginAsInstructor(instructor.getGoogleId());

        return instructor;
    }

    private StudentAttributes getStudent(String studentId) {
        return typicalBundle.students.get(studentId);
    }

    private List<StudentAttributes> getStudents(String... studentIds) {
        List<StudentAttributes> students = new ArrayList<>();
        for (String studentId : studentIds) {
            StudentAttributes student = getStudent(studentId);
            students.add(student);
        }

        return students;
    }

    private StudentAttributes loginStudent(String studentId) {
        StudentAttributes student = getStudent(studentId);
        loginAsStudent(student.getGoogleId());

        return student;
    }

    private FeedbackQuestionAttributes getQuestion(
            FeedbackSessionAttributes session, int questionNumber) {
        String sessionName = session.getFeedbackSessionName();
        String courseId = session.getCourseId();
        return logic.getFeedbackQuestion(sessionName, courseId, questionNumber);
    }

    private void setStartTime(FeedbackSessionAttributes session, int days)
            throws InvalidParametersException, EntityDoesNotExistException {
        String sessionName = session.getFeedbackSessionName();
        String courseId = session.getCourseId();
        Instant startTime = TimeHelper.getInstantDaysOffsetFromNow(days);

        logic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(sessionName, courseId)
                        .withStartTime(startTime)
                        .build());
    }

    private void setEndTime(FeedbackSessionAttributes session, int days)
            throws InvalidParametersException, EntityDoesNotExistException {
        String sessionName = session.getFeedbackSessionName();
        String courseId = session.getCourseId();
        Instant endTime = TimeHelper.getInstantDaysOffsetFromNow(days);

        logic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(sessionName, courseId)
                        .withEndTime(endTime)
                        .build());
    }

    private void setInstructorDeadline(FeedbackSessionAttributes session,
                                       InstructorAttributes instructor,
                                       int days)
            throws InvalidParametersException, EntityDoesNotExistException {
        String sessionName = session.getFeedbackSessionName();
        String courseId = session.getCourseId();

        Map<String, Instant> deadlines = Map.of(instructor.getEmail(), TimeHelper.getInstantDaysOffsetFromNow(days));

        logic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(sessionName, courseId)
                        .withInstructorDeadlines(deadlines)
                        .build());
    }

    private void setStudentDeadline(FeedbackSessionAttributes session, StudentAttributes student, int days)
            throws InvalidParametersException, EntityDoesNotExistException {
        String sessionName = session.getFeedbackSessionName();
        String courseId = session.getCourseId();

        Map<String, Instant> deadlines = Map.of(student.getEmail(), TimeHelper.getInstantDaysOffsetFromNow(days));

        logic.updateFeedbackSession(
                FeedbackSessionAttributes.updateOptionsBuilder(sessionName, courseId)
                        .withStudentDeadlines(deadlines)
                        .build());
    }

    private String[] buildSubmissionParams(FeedbackSessionAttributes session,
                                           int questionNumber,
                                           Intent intent) {
        FeedbackQuestionAttributes question = getQuestion(session, questionNumber);
        return buildSubmissionParams(question, intent);
    }

    private String[] buildSubmissionParams(FeedbackQuestionAttributes question,
                                           Intent intent) {
        String questionId = question != null ? question.getId() : "";

        return new String[] {Const.ParamsNames.FEEDBACK_QUESTION_ID, questionId, Const.ParamsNames.INTENT,
                intent.toString()};
    }

    private String[] setPreviewPerson(String[] submissionParams, String previewPerson) {
        return new String[] {submissionParams[0], submissionParams[1], submissionParams[2], submissionParams[3],
                Const.ParamsNames.PREVIEWAS, previewPerson};
    }

    private String[] setModeratorPerson(String[] submissionParams, String moderatorPerson) {
        return new String[] {submissionParams[0], submissionParams[1], submissionParams[2], submissionParams[3],
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatorPerson};
    }

    private void setCommentInSectionInstructorPrivilege(FeedbackSessionAttributes session,
                                                        InstructorAttributes instructor, boolean value)
            throws Exception {
        String courseId = session.getCourseId();

        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, value);

        logic.updateInstructor(InstructorAttributes.updateOptionsWithEmailBuilder(courseId, instructor.getEmail())
                .withPrivileges(instructorPrivileges).build());
    }

    private List<String> extractStudentEmails(List<StudentAttributes> students) {
        return students.stream().map(recipient -> recipient.getEmail()).collect(Collectors.toList());
    }

    private List<String> extractStudentTeams(List<StudentAttributes> students) {
        return students.stream().map(recipient -> recipient.getTeam()).collect(Collectors.toList());
    }

    private FeedbackResponsesRequest buildRequestBodyWithStudentRecipientsEmail(
            List<StudentAttributes> recipients) {
        List<String> emails = extractStudentEmails(recipients);
        return buildRequestBody(emails);
    }

    private FeedbackResponsesRequest buildRequestBodyWithStudentRecipientsTeam(
            List<StudentAttributes> recipients) {
        List<String> teams = extractStudentTeams(recipients);
        return buildRequestBody(teams);
    }

    private List<String> extractInstructorEmails(
            List<InstructorAttributes> students) {
        return students.stream().map(recipient -> recipient.getEmail()).collect(Collectors.toList());
    }

    private FeedbackResponsesRequest buildRequestBodyWithInstructorRecipients(List<InstructorAttributes> recipients) {
        List<String> emails = extractInstructorEmails(recipients);
        return buildRequestBody(emails);
    }

    private FeedbackResponsesRequest buildRequestBody(List<String> values) {
        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();

        for (String value : values) {

            FeedbackTextResponseDetails responseDetails = new FeedbackTextResponseDetails("Response for " + value);
            FeedbackResponsesRequest.FeedbackResponseRequest response =
                    new FeedbackResponsesRequest.FeedbackResponseRequest(value,
                            responseDetails);

            responses.add(response);
        }

        FeedbackResponsesRequest requestBody = new FeedbackResponsesRequest();
        requestBody.setResponses(responses);
        return requestBody;
    }

    private List<FeedbackResponseData> callExecute(FeedbackResponsesRequest requestBody, String[] submissionParams) {
        SubmitFeedbackResponsesAction action = getAction(requestBody, submissionParams);
        JsonResult result = getJsonResult(action);

        FeedbackResponsesData output = (FeedbackResponsesData) result.getOutput();
        return output.getResponses();
    }

    private void validateOutputForStudentRecipientsByEmail(List<FeedbackResponseData> responses, String giverEmail,
                                                           List<StudentAttributes> recipients) {
        int responsesSize = responses.size();
        assertEquals(recipients.size(), responsesSize);

        List<String> recipientEmails = extractStudentEmails(recipients);

        validateOutput(responses, giverEmail, recipientEmails);
    }

    private void validateOutputForStudentRecipientsByTeam(List<FeedbackResponseData> responses, String giverTeam,
                                                          List<StudentAttributes> recipients) {
        int responsesSize = responses.size();
        assertEquals(recipients.size(), responsesSize);

        List<String> recipientTeams = extractStudentTeams(recipients);

        validateOutput(responses, giverTeam, recipientTeams);
    }

    private void validateOutputForInstructorRecipients(List<FeedbackResponseData> responses, String giverEmail,
                                                       List<InstructorAttributes> recipients) {
        int responsesSize = responses.size();
        assertEquals(recipients.size(), responsesSize);

        List<String> recipientEmails = extractInstructorEmails(recipients);

        validateOutput(responses, giverEmail, recipientEmails);
    }

    private void validateOutput(List<FeedbackResponseData> responses, String giverValue, List<String> recipientValues) {
        for (int i = 0; i < recipientValues.size(); i++) {
            FeedbackResponseData response = responses.get(i);
            String recipientValue = recipientValues.get(i);

            assertEquals(giverValue, response.getGiverIdentifier());
            assertEquals(recipientValue, response.getRecipientIdentifier());

            FeedbackResponseDetails responseDetails = response.getResponseDetails();
            assertEquals(StringEscapeUtils.unescapeHtml(
                            SanitizationHelper.sanitizeForRichText("Response for " + recipientValue)),
                    StringEscapeUtils.unescapeHtml(responseDetails.getAnswerString()));
        }
    }

    private void validateStudentDatabaseByTeam(
            FeedbackSessionAttributes session,
            FeedbackQuestionAttributes question,
            String giverTeam, List<StudentAttributes> recipients) {
        List<String> teams = extractStudentTeams(recipients);

        validateDatabase(session, question, giverTeam, teams);
    }

    private void validateStudentDatabaseByEmail(
            FeedbackSessionAttributes session,
            FeedbackQuestionAttributes question,
            String giverTeam, List<StudentAttributes> recipients) {
        List<String> teams = extractStudentEmails(recipients);

        validateDatabase(session, question, giverTeam, teams);
    }

    private void validateInstructorDatabaseByEmail(
            FeedbackSessionAttributes session,
            FeedbackQuestionAttributes question,
            String giverTeam, List<InstructorAttributes> recipients) {
        List<String> teams = extractInstructorEmails(recipients);

        validateDatabase(session, question, giverTeam, teams);
    }

    private void validateDatabase(FeedbackSessionAttributes session, FeedbackQuestionAttributes question,
                                  String giverValue, List<String> recipientValues) {

        for (String recipientValue : recipientValues) {
            FeedbackResponseAttributes response = logic.getFeedbackResponse(question.getId(), giverValue,
                    recipientValue);

            assertEquals(question.getId(), response.getFeedbackQuestionId());
            assertEquals(giverValue, response.getGiver());

            assertEquals(recipientValue, response.getRecipient());

            assertEquals(session.getFeedbackSessionName(), response.getFeedbackSessionName());
            assertEquals(session.getCourseId(), response.getCourseId());

            FeedbackResponseDetails responseDetails = response.getResponseDetails();
            assertEquals(
                    StringEscapeUtils.unescapeHtml(
                            SanitizationHelper.sanitizeForRichText("Response for " + recipientValue)),
                    StringEscapeUtils.unescapeHtml(responseDetails.getAnswerString()));
        }
    }

    @Override
    protected void testAccessControl() {
        // See each independent test case.
    }

    //GENERAL
    @Test
    public void testAccessControl_feedbackSubmissionQuestionExists_shouldAllow() throws Exception {
        FeedbackSessionAttributes session = getSession("session1InCourse2");
        InstructorAttributes instructor = loginInstructor("instructor1OfCourse2");
        setEndTime(session, 1);
        setInstructorDeadline(session, instructor, 40);

        int questionNumber = 2;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_feedbackSubmissionNoFeedbackQuestionParameter_shouldFail() throws Exception {
        FeedbackSessionAttributes session = getSession("session1InCourse2");
        InstructorAttributes instructor = loginInstructor("instructor1OfCourse2");
        setEndTime(session, 35);
        setInstructorDeadline(session, instructor, 40);

        String[] submissionParams = new String[] {Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString()};

        verifyHttpParameterFailureAcl(submissionParams);
    }

    @Test
    public void testAccessControl_feedbackSubmissionQuestionDoesNotExist_shouldFail() throws Exception {
        FeedbackSessionAttributes session = getSession("session1InCourse2");
        InstructorAttributes instructor = loginInstructor("instructor1OfCourse2");
        setEndTime(session, 35);
        setInstructorDeadline(session, instructor, 40);

        int questionNumber = 222;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyEntityNotFoundAcl(submissionParams);
    }

    @Test
    public void testAccessControl_feedbackSubmissionValidIntent_shouldAllow() throws Exception {
        FeedbackSessionAttributes session = getSession("session1InCourse1");
        StudentAttributes student = loginStudent("student2InCourse1");
        setEndTime(session, 3);
        setStudentDeadline(session, student, 75);

        int questionNumber = 2;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_feedbackSubmissionNoIntentParameter_shouldFail() throws Exception {
        FeedbackSessionAttributes session = getSession("session1InCourse1");
        StudentAttributes student = loginStudent("student2InCourse1");
        setEndTime(session, 3);
        setStudentDeadline(session, student, 72);

        int questionNumber = 2;
        FeedbackQuestionAttributes question = getQuestion(session, questionNumber);
        String[] submissionParams = new String[] {Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId()};

        verifyHttpParameterFailureAcl(submissionParams);
    }

    @Test
    public void testAccessControl_feedbackSubmissionInvalidIntent_shouldFail() throws Exception {
        FeedbackSessionAttributes session = getSession("session1InCourse1");
        StudentAttributes student = loginStudent("student2InCourse1");
        setEndTime(session, 3);
        setStudentDeadline(session, student, 75);

        int questionNumber = 2;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_RESULT);

        verifyHttpParameterFailureAcl(submissionParams);
    }

    @Test
    public void testAccessControl_submissionIsNotOpen_shouldFail() throws Exception {
        FeedbackSessionAttributes session = getSession("session2InCourse1");
        StudentAttributes student = loginStudent("student2InCourse1");
        setStartTime(session, 10);
        setEndTime(session, 20);
        setStudentDeadline(session, student, 30);

        int questionNumber = 2;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_submissionBeforeEndTimeBeforeDeadline_shouldAllow() throws Exception {
        FeedbackSessionAttributes session = getSession("session2InCourse1");
        StudentAttributes student = loginStudent("student3InCourse1");
        setEndTime(session, 7);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        ______TS("No selective deadline; should pass.");
        verifyCanAccess(submissionParams);
        ______TS("Before selective deadline; should pass.");
        setStudentDeadline(session, student, 7);
        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_submissionPastEndTime_shouldAllowIfBeforeDeadline() throws Exception {
        FeedbackSessionAttributes session = getSession("session1InCourse1");
        InstructorAttributes instructor = loginInstructor("instructor1OfCourse1");
        setEndTime(session, -2);

        int questionNumber = 4;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        ______TS("No selective deadline; should pass.");
        verifyCanAccess(submissionParams);

        ______TS("After selective deadline; should fail.");
        setInstructorDeadline(session, instructor, -1);
        verifyCannotAccess(submissionParams);

        ______TS("Before selective deadline; should pass.");
        setInstructorDeadline(session, instructor, 1);
        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_submissionAfterDeadline_shouldFail() throws Exception {
        FeedbackSessionAttributes session = getSession("session2InCourse1");
        InstructorAttributes instructor = loginInstructor("instructor1OfCourse1");
        setEndTime(session, -20);
        setInstructorDeadline(session, instructor, -10);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCannotAccess(submissionParams);
    }

    //STUDENT
    @Test
    public void testAccessControl_studentSubmissionStudentAnswerableQuestion_shouldAllow() throws Exception {
        FeedbackSessionAttributes session = getSession("session1InCourse1");
        StudentAttributes student = loginStudent("student2InCourse1");
        setEndTime(session, 3);
        setStudentDeadline(session, student, 75);

        int questionNumber = 2;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentSubmissionNotStudentAnswerableQuestion_shouldFail() throws Exception {
        FeedbackSessionAttributes session = getSession("session1InCourse1");
        StudentAttributes student = loginStudent("student2InCourse1");
        setEndTime(session, 3);
        setStudentDeadline(session, student, 75);

        int questionNumber = 4;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentSubmissionLoggedOut_shouldFail() throws Exception {
        FeedbackSessionAttributes session = getSession("session1InCourse2");
        StudentAttributes student = getStudent("student1InCourse2");
        setEndTime(session, 1);
        setStudentDeadline(session, student, 1);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentSubmissionLoggedInAsInstructor_shouldFail() throws Exception {
        FeedbackSessionAttributes session = getSession("session1InCourse2");
        StudentAttributes student = getStudent("student1InCourse2");
        setEndTime(session, 1);
        setStudentDeadline(session, student, 1);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        loginInstructor("instructor2OfCourse2");
        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentSubmissionLoggedInAsAdmin_shouldFail() throws Exception {
        FeedbackSessionAttributes session = getSession("session1InCourse2");
        StudentAttributes student = getStudent("student1InCourse2");
        setEndTime(session, 1);
        setStudentDeadline(session, student, 1);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        loginAsAdmin();
        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentSubmissionLoggedInAsAdminMasqueradeAsStudent_shouldFail() throws Exception {
        FeedbackSessionAttributes session = getSession("gracePeriodSession");
        StudentAttributes student = getStudent("student1InCourse1");
        setEndTime(session, 1);
        setStudentDeadline(session, student, 1);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        loginAsAdmin();
        verifyCanMasquerade(student.getGoogleId(), submissionParams);
    }

    //INSTRUCTOR
    @Test
    public void testAccessControl_instructorSubmissionToInstructorAnswerableQuestion_shouldAllow() throws Exception {
        FeedbackSessionAttributes session = getSession("session1InCourse2");
        InstructorAttributes instructor = loginInstructor("instructor2OfCourse2");
        setEndTime(session, 3);
        setInstructorDeadline(session, instructor, 3);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_instructorSubmissionToSelfAnswerableQuestion_shouldAllow() throws Exception {
        FeedbackSessionAttributes session = getSession("session2InCourse2");
        InstructorAttributes instructor = loginInstructor("instructor1OfCourse2");
        setEndTime(session, 3);
        setInstructorDeadline(session, instructor, 3);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_instructorSubmissionToNotInstructorAnswerableQuestion_shouldFail() throws Exception {
        FeedbackSessionAttributes session = getSession("gracePeriodSession");
        InstructorAttributes instructor = loginInstructor("instructor2OfCourse1");
        setEndTime(session, 3);
        setInstructorDeadline(session, instructor, 3);

        int questionNumber = 3;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_instructorSubmissionLoggedOut_shouldFail() throws Exception {
        FeedbackSessionAttributes session = getSession("session2InCourse2");
        InstructorAttributes instructor = getInstructor("instructor2OfCourse2");
        setEndTime(session, 1);
        setInstructorDeadline(session, instructor, 1);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyEntityNotFoundAcl(submissionParams);
    }

    @Test
    public void testAccessControl_instructorSubmissionLoggedInAsAdmin_shouldFail() throws Exception {
        FeedbackSessionAttributes session = getSession("session2InCourse2");
        InstructorAttributes instructor = getInstructor("instructor2OfCourse2");
        setEndTime(session, 1);
        setInstructorDeadline(session, instructor, 1);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        loginAsAdmin();
        verifyEntityNotFoundAcl(submissionParams);
    }

    @Test
    protected void testAccessControl_submissionLoggedInAsAdminMasqueradeAsInstructor_shouldAllow() throws Exception {
        FeedbackSessionAttributes session = getSession("session2InCourse2");
        InstructorAttributes instructor = getInstructor("instructor2OfCourse2");
        setEndTime(session, 1);
        setInstructorDeadline(session, instructor, 1);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        loginAsAdmin();
        verifyCanMasquerade(instructor.getGoogleId(), submissionParams);
    }

    @Test
    public void testAccessControl_instructorSubmissionLoggedInAsStudent_shouldFail() throws Exception {
        FeedbackSessionAttributes session = getSession("session2InCourse1");
        InstructorAttributes instructor = getInstructor("instructor2OfCourse2");
        setEndTime(session, 1);
        setInstructorDeadline(session, instructor, 1);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        loginStudent("student2InCourse2");
        verifyCannotAccess(submissionParams);
    }

    @Test
    protected void testAccessControl_instructorsWithSufficientPreviewPrivilege_shouldAllow() throws Exception {
        FeedbackSessionAttributes session = getSession("closedSession");
        InstructorAttributes instructor = loginInstructor("instructor1OfCourse1");
        setEndTime(session, 1);
        setInstructorDeadline(session, instructor, 1);

        setCommentInSectionInstructorPrivilege(session, instructor, true);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);
        setPreviewPerson(submissionParams, instructor.getEmail());

        verifyCannotAccess(submissionParams);
        verifyCannotMasquerade(instructor.getGoogleId(), submissionParams);
    }

    @Test
    protected void testAccessControl_instructorsWithInsufficientPreviewPrivilege_shouldFail() throws Exception {
        FeedbackSessionAttributes session = getSession("closedSession");
        InstructorAttributes instructor = loginInstructor("instructor1OfCourse1");
        setEndTime(session, 1);
        setInstructorDeadline(session, instructor, 1);

        setCommentInSectionInstructorPrivilege(session, instructor, false);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);
        setPreviewPerson(submissionParams, instructor.getEmail());

        verifyCannotAccess(submissionParams);
        verifyCannotMasquerade(instructor.getGoogleId(), submissionParams);
    }

    @Test
    protected void testAccessControl_instructorsWithInsufficientModeratorPrivilege_shouldFail() throws Exception {
        FeedbackSessionAttributes session = getSession("closedSession");
        InstructorAttributes instructor = loginInstructor("instructor1OfCourse1");
        setEndTime(session, 1);
        setInstructorDeadline(session, instructor, 1);

        setCommentInSectionInstructorPrivilege(session, instructor, false);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);
        setModeratorPerson(submissionParams, instructor.getEmail());

        verifyCannotAccess(submissionParams);
        verifyCannotMasquerade(instructor.getGoogleId(), submissionParams);
    }

    @Override
    public void testExecute() {
        // See each independent test case.
    }

    //GENERAL
    @Test
    public void testExecute_noHttpParameters_shouldFail() {
        loginInstructor("instructor2OfCourse1");

        verifyHttpParameterFailure(new String[] {});
    }

    @Test
    public void testExecute_noFeedbackQuestionId_shouldFail() {
        loginInstructor("instructor2OfCourse1");

        ______TS("Not enough parameters for request; should fail.");
        String[] submissionParams = new String[] {Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString()};
        verifyHttpParameterFailure(submissionParams);
    }

    @Test
    public void testExecute_feedbackQuestionDoesNotExist_shouldFail() {
        loginInstructor("instructor1OfCourse3");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "non-existent id"};
        verifyEntityNotFound(submissionParams);
    }

    @Test
    protected void testExecute_invalidIntent_shouldFail() {
        loginInstructor("instructor2OfCourse1");
        FeedbackSessionAttributes session = getSession("session1InCourse1");
        int questionNumber = 3;

        ______TS("invalid intent STUDENT_RESULT");
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_RESULT);
        verifyHttpParameterFailure(submissionParams);

        ______TS("invalid intent FULL_DETAIL");
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.FULL_DETAIL);
        verifyHttpParameterFailure(submissionParams);
    }

    @Test
    protected void testExecute_noRequestBody_shouldFail() {
        FeedbackSessionAttributes session = getSession("session2InCourse1");
        loginStudent("student4InCourse1");

        int questionNumber = 2;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);
        verifyHttpRequestBodyFailure(null, submissionParams);
    }

    @Test
    protected void testExecute_requestBodyNoRecipient_shouldFail() {
        FeedbackSessionAttributes session = getSession("session2InCourse1");
        loginInstructor("instructor1OfCourse1");

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        ______TS("Null recipient");
        List<String> nullEmail = Collections.singletonList(null);
        FeedbackResponsesRequest requestBody = buildRequestBody(nullEmail);
        verifyInvalidOperation(requestBody, submissionParams);

        ______TS("Empty String recipient");
        requestBody = buildRequestBody(Collections.singletonList(""));
        verifyInvalidOperation(requestBody, submissionParams);

    }

    @Test
    protected void testExecute_noExistingResponses_shouldPass() {
        FeedbackSessionAttributes session = getSession("session2InCourse1");
        StudentAttributes giver = loginStudent("student4InCourse1");

        int questionNumber = 2;
        FeedbackQuestionAttributes question = getQuestion(session, questionNumber);
        String[] submissionParams = buildSubmissionParams(question, Intent.STUDENT_SUBMISSION);

        List<StudentAttributes> recipients = getStudents("student3InCourse1");
        FeedbackResponsesRequest requestBody = buildRequestBodyWithStudentRecipientsEmail(recipients);

        List<FeedbackResponseData> outputResponses = callExecute(requestBody, submissionParams);
        validateOutputForStudentRecipientsByEmail(outputResponses, giver.getEmail(), recipients);
        validateStudentDatabaseByEmail(session, question, giver.getEmail(), recipients);
    }

    @Test
    protected void testExecute_hasExistingResponse_shouldPass() {
        FeedbackSessionAttributes session = getSession("gracePeriodSession");
        InstructorAttributes giver = loginInstructor("helperOfCourse1");

        int questionNumber = 2;
        FeedbackQuestionAttributes question = getQuestion(session, questionNumber);
        String[] submissionParams = buildSubmissionParams(question, Intent.INSTRUCTOR_SUBMISSION);

        List<InstructorAttributes> recipients = Collections.singletonList(giver);
        FeedbackResponsesRequest requestBody = buildRequestBodyWithInstructorRecipients(recipients);

        List<FeedbackResponseData> outputResponses = callExecute(requestBody, submissionParams);
        validateOutputForInstructorRecipients(outputResponses, giver.getEmail(), recipients);
        validateInstructorDatabaseByEmail(session, question, giver.getEmail(), recipients);
    }

    @Test
    protected void testExecute_validRecipientsOfQuestion_shouldPass() {
        FeedbackSessionAttributes session = getSession("session2InCourse1");
        InstructorAttributes giver = loginInstructor("instructor1OfCourse1");

        int questionNumber = 3;
        FeedbackQuestionAttributes question = getQuestion(session, questionNumber);
        String[] submissionParams = buildSubmissionParams(question, Intent.INSTRUCTOR_SUBMISSION);

        List<StudentAttributes> recipients = getStudents("student5InCourse1", "student2InCourse1");
        FeedbackResponsesRequest requestBody = buildRequestBodyWithStudentRecipientsTeam(recipients);

        List<FeedbackResponseData> outputResponses = callExecute(requestBody, submissionParams);
        validateOutputForStudentRecipientsByTeam(outputResponses, giver.getEmail(), recipients);
        validateStudentDatabaseByTeam(session, question, giver.getEmail(), recipients);

    }

    @Test
    protected void testExecute_invalidRecipientOfQuestion_shouldFail() {
        FeedbackSessionAttributes session = getSession("session2InCourse1");
        loginStudent("student4InCourse1");

        int questionNumber = 2;
        FeedbackQuestionAttributes question = getQuestion(session, questionNumber);
        String[] submissionParams = buildSubmissionParams(question, Intent.STUDENT_SUBMISSION);

        List<StudentAttributes> recipients = getStudents("student5InCourse1");
        FeedbackResponsesRequest requestBody = buildRequestBodyWithStudentRecipientsTeam(recipients);

        verifyInvalidOperation(requestBody, submissionParams);
    }

    @Test
    protected void testExecute_tooManyRecipients_shouldPass() {
        FeedbackSessionAttributes session = getSession("session2InCourse1");
        StudentAttributes giver = loginStudent("student4InCourse1");

        int questionNumber = 2;
        FeedbackQuestionAttributes question = getQuestion(session, questionNumber);
        String[] submissionParams = buildSubmissionParams(question, Intent.STUDENT_SUBMISSION);

        List<StudentAttributes> recipients = getStudents("student3InCourse1", "student2InCourse1");
        FeedbackResponsesRequest requestBody = buildRequestBodyWithStudentRecipientsEmail(recipients);

        List<FeedbackResponseData> outputResponses = callExecute(requestBody, submissionParams);
        validateOutputForStudentRecipientsByEmail(outputResponses, giver.getEmail(), recipients);
        validateStudentDatabaseByEmail(session, question, giver.getEmail(), recipients);

    }

}
