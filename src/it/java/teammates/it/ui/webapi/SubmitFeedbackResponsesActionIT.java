package teammates.it.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.FeedbackResponseData;
import teammates.ui.output.FeedbackResponsesData;
import teammates.ui.request.FeedbackResponsesRequest;
import teammates.ui.request.Intent;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.SubmitFeedbackResponsesAction;

/**
 * SUT: {@link SubmitFeedbackResponsesAction}.
 */
public class SubmitFeedbackResponsesActionIT extends BaseActionIT<SubmitFeedbackResponsesAction> {
    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSES;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    private FeedbackSession getSession(String sessionId) {
        return typicalBundle.feedbackSessions.get(sessionId);
    }

    private Instructor getInstructor(String instructorId) {
        return typicalBundle.instructors.get(instructorId);
    }

    private Instructor loginInstructor(String instructorId) {
        Instructor instructor = getInstructor(instructorId);
        loginAsInstructor(instructor.getGoogleId());

        return instructor;
    }

    private Student getStudent(String studentId) {
        return typicalBundle.students.get(studentId);        
    }

    private List<Student> getStudents(String... studentIds) {
        List<Student> students = new ArrayList<>();
        for (String studentId : studentIds) {
            Student student = getStudent(studentId);
            students.add(student);
        }

        return students;
    }

    private Student loginStudent(String studentId) {
        Student student = getStudent(studentId);
        loginAsStudent(student.getGoogleId());

        return student;
    }

    private FeedbackQuestion getQuestion(
            FeedbackSession session, int questionNumber) {
        return logic.getFeedbackQuestionForSessionQuestionNumber(session.getId(), questionNumber);
    }

    private void setStartTime(FeedbackSession session, int days)
            throws InvalidParametersException, EntityDoesNotExistException {
        String sessionName = session.getName();
        String courseId = session.getCourseId();
        Instant startTime = TimeHelper.getInstantDaysOffsetFromNow(days);

        session.setName(sessionName);
        session.getCourse().setId(courseId);
        session.setStartTime(startTime);

        logic.updateFeedbackSession(session);
    }

    private void setEndTime(FeedbackSession session, int days)
            throws InvalidParametersException, EntityDoesNotExistException {
        String sessionName = session.getName();
        String courseId = session.getCourseId();
        Instant endTime = TimeHelper.getInstantDaysOffsetFromNow(days);

        session.setName(sessionName);
        session.getCourse().setId(courseId);
        session.setEndTime(endTime);

        logic.updateFeedbackSession(session);
    }

    private void setInstructorDeadlineExtension(FeedbackSession session,
                                       Instructor instructor,
                                       int days)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {        
        DeadlineExtension deadline = 
                new DeadlineExtension(instructor, session, TimeHelper.getInstantDaysOffsetFromNow(days));
        logic.createDeadlineExtension(deadline);

        List<DeadlineExtension> deadlines = new ArrayList<DeadlineExtension>();
        deadlines.add(deadline);
        
        session.setDeadlineExtensions(deadlines);

        logic.updateFeedbackSession(session);
    }

    private void setStudentDeadlineExtension(FeedbackSession session, Student student, int days)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        String sessionName = session.getName();
        String courseId = session.getCourseId();

        DeadlineExtension deadline = 
                new DeadlineExtension(student, session, TimeHelper.getInstantDaysOffsetFromNow(days));
        logic.createDeadlineExtension(deadline);

        List<DeadlineExtension> deadlines = new ArrayList<DeadlineExtension>();
        deadlines.add(deadline);

        session.setName(sessionName);
        session.getCourse().setId(courseId);
        session.setDeadlineExtensions(deadlines);

        logic.updateFeedbackSession(session);
    }

    private String[] buildSubmissionParams(FeedbackSession session,
                                           int questionNumber,
                                           Intent intent) {
        FeedbackQuestion question = getQuestion(session, questionNumber);
        return buildSubmissionParams(question, intent);
    }

    private String[] buildSubmissionParams(FeedbackQuestion question,
                                           Intent intent) {
        String questionId = question != null ? question.getId().toString() : "";

        return new String[] {Const.ParamsNames.FEEDBACK_QUESTION_ID, questionId, Const.ParamsNames.INTENT,
                intent.toString()};
    }

    private void setCommentInSectionInstructorPrivilege(FeedbackSession session,
                                                        Instructor instructor, boolean value)
            throws Exception {
        String courseId = session.getCourseId();

        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, value);

        instructor.getCourse().setId(courseId);
        instructor.setPrivileges(instructorPrivileges);

        logic.updateToEnsureValidityOfInstructorsForTheCourse(courseId, instructor);
    }

    private List<String> extractStudentEmails(List<Student> students) {
        return students.stream().map(recipient -> recipient.getEmail()).collect(Collectors.toList());
    }

    private List<String> extractStudentTeams(List<Student> students) {
        return students.stream().map(recipient -> recipient.getTeam().getName()).collect(Collectors.toList());
    }

    private FeedbackResponsesRequest buildRequestBodyWithStudentRecipientsEmail(
            List<Student> recipients) {
        List<String> emails = extractStudentEmails(recipients);
        return buildRequestBody(emails);
    }

    private FeedbackResponsesRequest buildRequestBodyWithStudentRecipientsTeam(
            List<Student> recipients) {
        List<String> teams = extractStudentTeams(recipients);
        return buildRequestBody(teams);
    }

    private List<String> extractInstructorEmails(
            List<Instructor> students) {
        return students.stream().map(recipient -> recipient.getEmail()).collect(Collectors.toList());
    }

    private FeedbackResponsesRequest buildRequestBodyWithInstructorRecipients(List<Instructor> recipients) {
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
                                                           List<Student> recipients) {
        int responsesSize = responses.size();
        assertEquals(recipients.size(), responsesSize);

        List<String> recipientEmails = extractStudentEmails(recipients);

        validateOutput(responses, giverEmail, recipientEmails);
    }

    private void validateOutputForStudentRecipientsByTeam(List<FeedbackResponseData> responses, String giverTeam,
                                                          List<Student> recipients) {
        int responsesSize = responses.size();
        assertEquals(recipients.size(), responsesSize);

        List<String> recipientTeams = extractStudentTeams(recipients);

        validateOutput(responses, giverTeam, recipientTeams);
    }

    private void validateOutputForInstructorRecipients(List<FeedbackResponseData> responses, String giverEmail,
                                                       List<Instructor> recipients) {
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
        FeedbackSession session,
        FeedbackQuestion question,
        String giverTeam, List<Student> recipients) {
        List<String> studentTeamNames = extractStudentTeams(recipients);

        validateDatabaseWithRecipientEmails(session, question, giverTeam, studentTeamNames);
    }

    private void validateStudentDatabaseByEmail(
            FeedbackSession session,
            FeedbackQuestion question,
            String giverEmail, List<Student> recipients) {
        List<String> studentRecipientEmails = extractStudentEmails(recipients);

        validateDatabaseWithRecipientEmails(session, question, giverEmail, studentRecipientEmails);
    }

    private void validateInstructorDatabaseByEmail(
            FeedbackSession session,
            FeedbackQuestion question,
            String giverEmail, List<Instructor> recipients) {
        List<String> instructorRecipientEmails = extractInstructorEmails(recipients);

        validateDatabaseWithRecipientEmails(session, question, giverEmail, instructorRecipientEmails);
    }

    private void validateDatabaseWithRecipientEmails(FeedbackSession session, FeedbackQuestion question,
    String giverEmail, List<String> recipientEmails) {

        for (String recipientEmail : recipientEmails) {
            List<FeedbackResponse> feedbackResponses = logic.
                getFeedbackResponsesFromGiverAndRecipientForCourse(session.getCourseId(), giverEmail, recipientEmail);

            for (FeedbackResponse feedbackResponse : feedbackResponses) {
                FeedbackQuestion feedbackQuestion = feedbackResponse.getFeedbackQuestion();

                assertEquals(feedbackResponse.getGiver(), giverEmail);
                assertEquals(feedbackResponse.getRecipient(), recipientEmail);

                assertEquals(session.getName(), feedbackQuestion.getFeedbackSessionName());
                assertEquals(session.getCourseId(), feedbackQuestion.getCourseId());
                
                FeedbackResponseDetails responseDetails = feedbackResponse.getFeedbackResponseDetailsCopy();
                assertEquals(
                        StringEscapeUtils.unescapeHtml(
                                SanitizationHelper.sanitizeForRichText("Response for " + recipientEmail)),
                        StringEscapeUtils.unescapeHtml(responseDetails.getAnswerString()));
            }
        }
    }

    @Override
    protected void testAccessControl() {
        // See each independent test case.
    }

    @Test
    public void testAccessControl_feedbackSubmissionQuestionExists_shouldAllow() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Instructor instructor = loginInstructor("instructor1OfCourse1");
        setEndTime(session, 1);
        setInstructorDeadlineExtension(session, instructor, 40);

        int questionNumber = 4;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_feedbackSubmissionNoFeedbackQuestionParameter_shouldFail() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Instructor instructor = loginInstructor("instructor1OfCourse1");
        setEndTime(session, 35);
        setInstructorDeadlineExtension(session, instructor, 40);

        String[] submissionParams = new String[] {Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString()};

        verifyHttpParameterFailureAcl(submissionParams);
    }

    @Test
    public void testAccessControl_feedbackSubmissionQuestionDoesNotExist_shouldFail() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Instructor instructor = loginInstructor("instructor1OfCourse1");
        setEndTime(session, 35);
        setInstructorDeadlineExtension(session, instructor, 40);

        int questionNumber = 222;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyEntityNotFoundAcl(submissionParams);
    }

    @Test
    public void testAccessControl_feedbackSubmissionValidIntent_shouldAllow() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Student student = loginStudent("student1InCourse1");
        setEndTime(session, 3);
        setStudentDeadlineExtension(session, student, 75);

        int questionNumber = 2;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_feedbackSubmissionNoIntentParameter_shouldFail() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Student student = loginStudent("student1InCourse1");
        setEndTime(session, 3);
        setStudentDeadlineExtension(session, student, 72);

        int questionNumber = 2;
        FeedbackQuestion question = getQuestion(session, questionNumber);
        String[] submissionParams = new String[] {Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId().toString()};

        verifyHttpParameterFailureAcl(submissionParams);
    }

    @Test
    public void testAccessControl_feedbackSubmissionInvalidIntent_shouldFail() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Student student = loginStudent("student1InCourse1");
        setEndTime(session, 3);
        setStudentDeadlineExtension(session, student, 75);

        int questionNumber = 2;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_RESULT);

        verifyHttpParameterFailureAcl(submissionParams);
    }

    @Test
    public void testAccessControl_submissionIsNotOpen_shouldFail() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Student student = loginStudent("student1InCourse1");
        setStartTime(session, 10);
        setEndTime(session, 20);
        setStudentDeadlineExtension(session, student, 30);

        int questionNumber = 2;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_submissionBeforeEndTimeBeforeDeadline_shouldAllow() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Student student = loginStudent("student1InCourse1");
        setEndTime(session, 7);

        int questionNumber = 2;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        ______TS("No selective deadline; should pass.");
        verifyCanAccess(submissionParams);
        ______TS("With selective deadline; should pass.");
        setStudentDeadlineExtension(session, student, 7);
        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_submissionPastEndTime_shouldAllowIfBeforeDeadline() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Instructor instructor = loginInstructor("instructor1OfCourse1");
        setEndTime(session, -2);

        int questionNumber = 4;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        ______TS("With selective deadline, should pass.");
        setInstructorDeadlineExtension(session, instructor, 1);
        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_submissionAfterDeadline_shouldFail() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Instructor instructor = loginInstructor("instructor1OfCourse1");
        setEndTime(session, -20);
        setInstructorDeadlineExtension(session, instructor, -10);

        int questionNumber = 4;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentSubmissionStudentAnswerableQuestion_shouldAllow() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Student student = loginStudent("student1InCourse1");
        setEndTime(session, 3);
        setStudentDeadlineExtension(session, student, 75);

        int questionNumber = 2;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentSubmissionNotStudentAnswerableQuestion_shouldFail() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Student student = loginStudent("student1InCourse1");
        setEndTime(session, 3);
        setStudentDeadlineExtension(session, student, 75);

        int questionNumber = 4;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentSubmissionLoggedOut_shouldFail() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Student student = getStudent("student1InCourse1");
        setEndTime(session, 1);
        setStudentDeadlineExtension(session, student, 1);

        int questionNumber = 2;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);
        
        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentSubmissionLoggedInAsInstructor_shouldFail() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Student student = getStudent("student1InCourse1");
        setEndTime(session, 1);
        setStudentDeadlineExtension(session, student, 1);

        int questionNumber = 2;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        loginInstructor("instructor1OfCourse1");
        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentSubmissionLoggedInAsAdmin_shouldFail() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Student student = getStudent("student1InCourse1");
        setEndTime(session, 1);
        setStudentDeadlineExtension(session, student, 1);

        int questionNumber = 2;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        loginAsAdmin();
        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentSubmissionLoggedInAsAdminMasqueradeAsStudent_shouldFail() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Student student = getStudent("student1InCourse1");
        setEndTime(session, 1);
        setStudentDeadlineExtension(session, student, 1);

        int questionNumber = 2;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        loginAsAdmin();
        verifyCanMasquerade(student.getGoogleId(), submissionParams);
    }

    @Test
    public void testAccessControl_instructorSubmissionToInstructorAnswerableQuestion_shouldAllow() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Instructor instructor = loginInstructor("instructor1OfCourse1");
        setEndTime(session, 3);
        setInstructorDeadlineExtension(session, instructor, 3);

        int questionNumber = 4;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_instructorSubmissionToSelfAnswerableQuestion_shouldAllow() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Instructor instructor = loginInstructor("instructor1OfCourse1");
        setEndTime(session, 3);
        setInstructorDeadlineExtension(session, instructor, 3);

        int questionNumber = 3;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_instructorSubmissionToNotInstructorAnswerableQuestion_shouldFail() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Instructor instructor = loginInstructor("instructor1OfCourse1");
        setEndTime(session, 3);
        setInstructorDeadlineExtension(session, instructor, 3);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_instructorSubmissionLoggedOut_shouldFail() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Instructor instructor = getInstructor("instructor1OfCourse1");
        setEndTime(session, 1);
        setInstructorDeadlineExtension(session, instructor, 1);

        int questionNumber = 3;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_instructorSubmissionLoggedInAsAdmin_shouldFail() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Instructor instructor = getInstructor("instructor1OfCourse1");
        setEndTime(session, 1);
        setInstructorDeadlineExtension(session, instructor, 1);

        int questionNumber = 4;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        loginAsAdmin();
        verifyCannotAccess(submissionParams);
    }

    @Test
    protected void testAccessControl_submissionLoggedInAsAdminMasqueradeAsInstructor_shouldAllow() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Instructor instructor = getInstructor("instructor1OfCourse1");
        setEndTime(session, 1);
        setInstructorDeadlineExtension(session, instructor, 1);

        int questionNumber = 4;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        loginAsAdmin();
        verifyCanMasquerade(instructor.getGoogleId(), submissionParams);
    }

    @Test
    public void testAccessControl_instructorSubmissionLoggedInAsStudent_shouldFail() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Instructor instructor = getInstructor("instructor1OfCourse1");
        setEndTime(session, 1);
        setInstructorDeadlineExtension(session, instructor, 1);

        int questionNumber = 4;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        loginStudent("student1InCourse1");
        verifyCannotAccess(submissionParams);
    }

    @Test
    protected void testAccessControl_instructorsWithSufficientPreviewPrivilege_shouldAllow() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Instructor instructor = loginInstructor("instructor1OfCourse1");
        setEndTime(session, 1);
        setInstructorDeadlineExtension(session, instructor, 1);

        setCommentInSectionInstructorPrivilege(session, instructor, true);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCannotAccess(submissionParams);
        verifyCannotMasquerade(instructor.getGoogleId(), submissionParams);
    }

    @Test
    protected void testAccessControl_instructorsWithInsufficientPreviewPrivilege_shouldFail() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Instructor instructor = loginInstructor("instructor1OfCourse1");
        setEndTime(session, 1);
        setInstructorDeadlineExtension(session, instructor, 1);

        setCommentInSectionInstructorPrivilege(session, instructor, false);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCannotAccess(submissionParams);
        verifyCannotMasquerade(instructor.getGoogleId(), submissionParams);
    }

    @Test
    protected void testAccessControl_instructorsWithInsufficientModeratorPrivilege_shouldFail() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Instructor instructor = loginInstructor("instructor1OfCourse1");
        setEndTime(session, 1);
        setInstructorDeadlineExtension(session, instructor, 1);

        setCommentInSectionInstructorPrivilege(session, instructor, false);

        int questionNumber = 1;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCannotAccess(submissionParams);
        verifyCannotMasquerade(instructor.getGoogleId(), submissionParams);
    }

    @Override
    public void testExecute() {
        // See each independent test case.
    }

    @Test
    public void testExecute_noHttpParameters_shouldFail() {
        loginInstructor("instructor1OfCourse1");

        verifyHttpParameterFailure(new String[] {});
    }

    @Test
    public void testExecute_noFeedbackQuestionId_shouldFail() {
        loginInstructor("instructor1OfCourse1");

        ______TS("Not enough parameters for request; should fail.");
        String[] submissionParams = new String[] {Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString()};
        verifyHttpParameterFailure(submissionParams);
    }

    @Test
    public void testExecute_feedbackQuestionDoesNotExist_shouldFail() {
        loginInstructor("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "non-existent id"};
        verifyEntityNotFound(submissionParams);
    }

    @Test
    protected void testExecute_invalidIntent_shouldFail() {
        loginInstructor("instructor1OfCourse1");
        FeedbackSession session = getSession("session1InCourse1");
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
        FeedbackSession session = getSession("session1InCourse1");
        loginStudent("student1InCourse1");

        int questionNumber = 2;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);
        verifyHttpRequestBodyFailure(null, submissionParams);
    }

    @Test
    protected void testExecute_requestBodyNoRecipient_shouldFail() {
        FeedbackSession session = getSession("session1InCourse1");
        loginInstructor("instructor1OfCourse1");

        int questionNumber = 4;
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
        FeedbackSession session = getSession("session1InCourse1");
        Instructor giver = loginInstructor("instructor1OfCourse1");

        int questionNumber = 7;
        FeedbackQuestion question = getQuestion(session, questionNumber);
        String[] submissionParams = buildSubmissionParams(question, Intent.INSTRUCTOR_SUBMISSION);

        List<Instructor> recipients = Collections.singletonList(giver);
        FeedbackResponsesRequest requestBody = buildRequestBodyWithInstructorRecipients(recipients);

        List<FeedbackResponseData> outputResponses = callExecute(requestBody, submissionParams);
        validateOutputForInstructorRecipients(outputResponses, giver.getEmail(), recipients);
        validateInstructorDatabaseByEmail(session, question, giver.getEmail(), recipients);

    }

    @Test
    protected void testExecute_hasExistingResponse_shouldPass() {
        FeedbackSession session = getSession("session1InCourse1");
        Student giver = loginStudent("student1InCourse1");

        int questionNumber = 2;
        FeedbackQuestion question = getQuestion(session, questionNumber);
        String[] submissionParams = buildSubmissionParams(question, Intent.STUDENT_SUBMISSION);

        List<Student> recipients = getStudents("student3InCourse1");
        FeedbackResponsesRequest requestBody = buildRequestBodyWithStudentRecipientsEmail(recipients);

        List<FeedbackResponseData> outputResponses = callExecute(requestBody, submissionParams);
        validateOutputForStudentRecipientsByEmail(outputResponses, giver.getEmail(), recipients);
        validateStudentDatabaseByEmail(session, question, giver.getEmail(), recipients);
    }

    @Test
    protected void testExecute_validRecipientsOfQuestion_shouldPass() {
        FeedbackSession session = getSession("session1InCourse1");
        Instructor giver = loginInstructor("instructor1OfCourse1");

        int questionNumber = 8;
        FeedbackQuestion question = getQuestion(session, questionNumber);
        String[] submissionParams = buildSubmissionParams(question, Intent.INSTRUCTOR_SUBMISSION);

        List<Student> recipients = getStudents("student2InCourse1", "student3InCourse1");
        FeedbackResponsesRequest requestBody = buildRequestBodyWithStudentRecipientsTeam(recipients);

        List<FeedbackResponseData> outputResponses = callExecute(requestBody, submissionParams);
        validateOutputForStudentRecipientsByTeam(outputResponses, giver.getEmail(), recipients);
        validateStudentDatabaseByTeam(session, question, giver.getEmail(), recipients);
    }

    @Test
    protected void testExecute_invalidRecipientOfQuestion_shouldFail() {
        FeedbackSession session = getSession("session1InCourse1");
        loginStudent("student1InCourse1");

        int questionNumber = 6;
        FeedbackQuestion question = getQuestion(session, questionNumber);
        String[] submissionParams = buildSubmissionParams(question, Intent.STUDENT_SUBMISSION);

        List<Student> recipients = getStudents("student2InCourse1");
        FeedbackResponsesRequest requestBody = buildRequestBodyWithStudentRecipientsTeam(recipients);

        verifyInvalidOperation(requestBody, submissionParams);
    }

    @Test
    protected void testExecute_tooManyRecipients_shouldPass() {
        FeedbackSession session = getSession("session1InCourse1");
        Student giver = loginStudent("student1InCourse1");

        int questionNumber = 9;
        FeedbackQuestion question = getQuestion(session, questionNumber);
        String[] submissionParams = buildSubmissionParams(question, Intent.STUDENT_SUBMISSION);

        List<Student> recipients = getStudents("student3InCourse1", "student2InCourse1");
        FeedbackResponsesRequest requestBody = buildRequestBodyWithStudentRecipientsEmail(recipients);

        List<FeedbackResponseData> outputResponses = callExecute(requestBody, submissionParams);
        validateOutputForStudentRecipientsByEmail(outputResponses, giver.getEmail(), recipients);
        validateStudentDatabaseByEmail(session, question, giver.getEmail(), recipients);
    }

}