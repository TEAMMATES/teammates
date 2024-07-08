package teammates.it.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import teammates.storage.sqlentity.User;
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
        HibernateUtil.flushSession();
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
        HibernateUtil.flushSession();
        return student;
    }

    private FeedbackQuestion getQuestion(
            FeedbackSession session, int questionNumber) {
        return logic.getFeedbackQuestionForSessionQuestionNumber(session.getId(), questionNumber);
    }

    private void setStartTime(FeedbackSession session, int days)
            throws InvalidParametersException, EntityDoesNotExistException {
        Instant startTime = TimeHelper.getInstantDaysOffsetFromNow(days);

        session.setStartTime(startTime);

        logic.updateFeedbackSession(session);
        HibernateUtil.flushSession();
    }

    private void setEndTime(FeedbackSession session, int days)
            throws InvalidParametersException, EntityDoesNotExistException {
        Instant endTime = TimeHelper.getInstantDaysOffsetFromNow(days);

        session.setEndTime(endTime);

        logic.updateFeedbackSession(session);
        HibernateUtil.flushSession();
    }

    private void setUserDeadlineExtension(FeedbackSession session, User user, int days)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        DeadlineExtension newDeadline =
                new DeadlineExtension(user, session, TimeHelper.getInstantDaysOffsetFromNow(days));

        newDeadline.setFeedbackSession(session);
        newDeadline.setUser(user);

        DeadlineExtension existingDeadlineEndTime = logic.getDeadlineExtensionEntityForUser(session, user);
        if (existingDeadlineEndTime != null) {
            newDeadline.setId(existingDeadlineEndTime.getId());
            logic.updateDeadlineExtension(newDeadline);
        } else {
            logic.createDeadlineExtension(newDeadline);
        }
    }

    private void deleteDeadlineExtensionForUser(FeedbackSession session, User user) {
        DeadlineExtension existingDeadlineEndTime = logic.getDeadlineExtensionEntityForUser(session, user);
        if (existingDeadlineEndTime == null) {
            return;
        }

        logic.deleteDeadlineExtension(existingDeadlineEndTime);
    }

    private String[] buildSubmissionParams(FeedbackSession session, int questionNumber, Intent intent) {
        FeedbackQuestion question = getQuestion(session, questionNumber);
        return buildSubmissionParams(question, intent);
    }

    private String[] buildSubmissionParams(FeedbackQuestion question, Intent intent) {
        String questionId = question != null ? question.getId().toString() : "";

        return new String[] {Const.ParamsNames.FEEDBACK_QUESTION_ID, questionId, Const.ParamsNames.INTENT,
                intent.toString()};
    }

    private void setSubmitSessionInSectionsInstructorPrivilege(FeedbackSession session,
                                                        Instructor instructor, boolean value)
            throws Exception {
        String courseId = session.getCourseId();

        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, value);

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
                    new FeedbackResponsesRequest.FeedbackResponseRequest(value, responseDetails);

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

    private void validateStudentDatabaseByTeam(FeedbackSession session, FeedbackQuestion question,
            String giverTeam, List<Student> recipients) {
        List<String> studentTeamNames = extractStudentTeams(recipients);

        validateDatabaseWithRecipientEmails(session, question, giverTeam, studentTeamNames);
    }

    private void validateStudentDatabaseByEmail(FeedbackSession session, FeedbackQuestion question, String giverEmail,
            List<Student> recipients) {
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

    private void validateDatabaseWithRecipientEmails(FeedbackSession session, FeedbackQuestion feedbackQuestion,
            String giverEmail, List<String> recipientEmails) {

        for (String recipientEmail : recipientEmails) {
            List<FeedbackResponse> feedbackResponses =
                    logic.getFeedbackResponsesFromGiverAndRecipientForCourse(
                            session.getCourseId(), giverEmail, recipientEmail);

            for (FeedbackResponse feedbackResponse : feedbackResponses) {
                FeedbackQuestion frFeedbackQuestion = feedbackResponse.getFeedbackQuestion();

                assertEquals(frFeedbackQuestion, feedbackQuestion);
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
    @Test
    protected void testAccessControl() throws Exception {
        FeedbackSession session = getSession("session1InCourse1");
        Student student = loginStudent("student1InCourse1");
        Instructor instructor = loginInstructor("instructor1OfCourse1");
        deleteDeadlineExtensionForUser(session, instructor);
        deleteDeadlineExtensionForUser(session, student);

        ______TS("Typical case with instructors: feedback question exists");
        setStartTime(session, -1);
        setEndTime(session, 1);

        int questionNumber = 4;
        String[] submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCanAccess(submissionParams);

        ______TS("Typical case with student: feedback question exists");
        loginStudent("student1InCourse1");
        setStartTime(session, -1);
        setEndTime(session, 3);

        questionNumber = 2;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        verifyCanAccess(submissionParams);

        ______TS("Failure with instructors: no feedback question parameter");
        loginInstructor("instructor1OfCourse1");
        setStartTime(session, -1);
        setEndTime(session, 3);

        submissionParams = new String[] {Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString()};

        verifyHttpParameterFailureAcl(submissionParams);

        ______TS("Failure with instructors: feedback question does not exist");
        setStartTime(session, -1);
        setEndTime(session, 3);

        questionNumber = 222;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyEntityNotFoundAcl(submissionParams);

        ______TS("Failure with students: no feedback question parameter");
        loginStudent("student1InCourse1");
        setStartTime(session, -1);
        setEndTime(session, 3);

        questionNumber = 2;
        FeedbackQuestion question = getQuestion(session, questionNumber);
        submissionParams = new String[] {Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId().toString()};

        verifyHttpParameterFailureAcl(submissionParams);

        ______TS("Failure with students: invalid intent for action STUDENT_RESULT");
        setStartTime(session, -1);
        setEndTime(session, 3);

        questionNumber = 2;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_RESULT);

        verifyHttpParameterFailureAcl(submissionParams);

        ______TS("Failure with students: submission not open, start time is in the future");
        loginStudent("student1InCourse1");
        setStartTime(session, 2);
        setEndTime(session, 3);

        questionNumber = 2;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        verifyCannotAccess(submissionParams);

        ______TS("Typical success with students: redundant deadline extension");
        setStartTime(session, -1);
        setEndTime(session, 3);

        questionNumber = 2;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        // No selective deadline, should pass.
        verifyCanAccess(submissionParams);

        // With selective deadline, should still pass.
        setUserDeadlineExtension(session, student, 7);
        verifyCanAccess(submissionParams);

        ______TS("Typical success with instructor: deadline extension granted after submission closed");
        loginInstructor("instructor1OfCourse1");
        setStartTime(session, -3);
        setEndTime(session, -2);

        questionNumber = 4;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);
        setUserDeadlineExtension(session, instructor, -1);

        // No selective deadline, should not pass.
        verifyCannotAccess(submissionParams);

        // With selective deadline, should pass
        setUserDeadlineExtension(session, instructor, 1);
        verifyCanAccess(submissionParams);

        ______TS("Failure with instructor: submission after deadline extension expired");
        setStartTime(session, -3);
        setEndTime(session, -2);

        questionNumber = 4;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        setUserDeadlineExtension(session, instructor, -1);
        verifyCannotAccess(submissionParams);

        ______TS("Typical success with student: student answers question with correct giver");
        loginStudent("student1InCourse1");
        deleteDeadlineExtensionForUser(session, student);
        setEndTime(session, 3);
        setStartTime(session, -1);

        questionNumber = 2;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        verifyCanAccess(submissionParams);

        ______TS("Failure with student: student answers question with incorrect giver");
        setStartTime(session, -1);
        setEndTime(session, 3);

        questionNumber = 4;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        verifyCannotAccess(submissionParams);

        ______TS("Failure with student: student logged out");
        logoutUser();
        setStartTime(session, -1);
        setEndTime(session, 3);

        questionNumber = 2;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        verifyCannotAccess(submissionParams);

        ______TS("Failure with student: student logged in as instructor");
        logoutUser();
        loginInstructor("instructor1OfCourse1");
        setStartTime(session, -1);
        setEndTime(session, 3);

        questionNumber = 2;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        verifyCannotAccess(submissionParams);

        loginStudent("student1InCourse1");
        verifyCanAccess(submissionParams);

        ______TS("Failure with student: student logged in as admin");
        logoutUser();
        loginAsAdmin();
        setStartTime(session, -1);
        setEndTime(session, 3);

        questionNumber = 2;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        verifyCannotAccess(submissionParams);

        loginStudent("student1InCourse1");
        verifyCanAccess(submissionParams);

        ______TS("Typical success with student: logged in as admin masquerading as student");
        logoutUser();
        loginAsAdmin();
        setStartTime(session, -1);
        setEndTime(session, 3);

        questionNumber = 2;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);

        verifyCanMasquerade(student.getGoogleId(), submissionParams);

        ______TS("Typical success with instructor: instructor answers question with correct giver");
        loginInstructor("instructor1OfCourse1");
        setStartTime(session, -1);
        setEndTime(session, 3);

        questionNumber = 4;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCanAccess(submissionParams);

        ______TS("Typical success with instructor: instructor answers question to self-answerable question");
        loginInstructor("instructor1OfCourse1");
        setStartTime(session, -1);
        setEndTime(session, 3);

        questionNumber = 3;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCanAccess(submissionParams);

        ______TS("Failure with instructor: instructor answers question with incorrect giver");
        loginInstructor("instructor1OfCourse1");
        setStartTime(session, -1);
        setEndTime(session, 3);

        questionNumber = 1;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCannotAccess(submissionParams);

        ______TS("Failure with instructor: instructor logged out");
        logoutUser();
        setStartTime(session, -1);
        setEndTime(session, 3);

        questionNumber = 3;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCannotAccess(submissionParams);

        ______TS("Failure with instructor: instructor logged in as admin");
        logoutUser();
        loginAsAdmin();
        setStartTime(session, -1);
        setEndTime(session, 3);

        questionNumber = 4;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCannotAccess(submissionParams);

        loginInstructor("instructor1OfCourse1");
        verifyCanAccess(submissionParams);

        ______TS("Typical success with instructor: logged in as admin masquerading as instructor");
        logoutUser();
        loginAsAdmin();
        setStartTime(session, -1);
        setEndTime(session, 3);

        questionNumber = 4;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCanMasquerade(instructor.getGoogleId(), submissionParams);

        ______TS("Failure with instructor: instructor logged in as student");
        loginStudent("student1InCourse1");
        setStartTime(session, -1);
        setEndTime(session, 3);

        questionNumber = 4;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCannotAccess(submissionParams);

        ______TS("Typical success with instructor: instructor has modify session comment privileges");
        loginInstructor("instructor1OfCourse1");
        setStartTime(session, -1);
        setEndTime(session, 3);

        setSubmitSessionInSectionsInstructorPrivilege(session, instructor, true);

        questionNumber = 4;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCanAccess(submissionParams);
        verifyCanMasquerade(instructor.getGoogleId(), submissionParams);

        ______TS("Failure with instructor: instructor has no modify session comment privileges");
        loginInstructor("instructor1OfCourse1");
        setStartTime(session, -1);
        setEndTime(session, 3);

        setSubmitSessionInSectionsInstructorPrivilege(session, instructor, false);

        questionNumber = 4;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCannotAccess(submissionParams);
        verifyCannotMasquerade(instructor.getGoogleId(), submissionParams);

        // Reset privileges
        setSubmitSessionInSectionsInstructorPrivilege(session, instructor, true);
    }

    @Override
    @Test
    public void testExecute() {
        ______TS("Failure: invalid http parameters");
        loginInstructor("instructor1OfCourse1");

        verifyHttpParameterFailure(new String[] {});

        ______TS("Failure: not feedback question parameter specified");
        String[] submissionParams = new String[] {Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString()};
        verifyHttpParameterFailure(submissionParams);

        ______TS("Failure: feedback question does not exist");
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "non-existent id"};
        verifyEntityNotFound(submissionParams);

        ______TS("Failure: instructor has invalid intent");
        FeedbackSession session = getSession("session1InCourse1");
        int questionNumber = 3;

        submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_RESULT);
        verifyHttpParameterFailure(submissionParams);

        submissionParams = buildSubmissionParams(session, questionNumber, Intent.FULL_DETAIL);
        verifyHttpParameterFailure(submissionParams);

        ______TS("Failure: no request body");
        loginStudent("student1InCourse1");

        questionNumber = 2;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);
        verifyHttpRequestBodyFailure(null, submissionParams);

        ______TS("Failure: request body has no recipient");
        loginInstructor("instructor1OfCourse1");

        questionNumber = 4;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        // Null recipient
        List<String> nullEmail = Collections.singletonList(null);
        FeedbackResponsesRequest requestBody = buildRequestBody(nullEmail);
        verifyInvalidOperation(requestBody, submissionParams);

        // Empty String recipient
        requestBody = buildRequestBody(Collections.singletonList(""));
        verifyInvalidOperation(requestBody, submissionParams);

        ______TS("Success: question has no existing responses");
        Instructor instructorGiver = loginInstructor("instructor1OfCourse1");

        questionNumber = 7;
        FeedbackQuestion question = getQuestion(session, questionNumber);
        submissionParams = buildSubmissionParams(question, Intent.INSTRUCTOR_SUBMISSION);

        List<Instructor> instructorRecipients = Collections.singletonList(instructorGiver);
        requestBody = buildRequestBodyWithInstructorRecipients(instructorRecipients);

        List<FeedbackResponseData> outputResponses = callExecute(requestBody, submissionParams);
        validateOutputForInstructorRecipients(outputResponses, instructorGiver.getEmail(), instructorRecipients);
        validateInstructorDatabaseByEmail(session, question, instructorGiver.getEmail(), instructorRecipients);

        ______TS("Success: instructor is a valid giver of the question to student team");
        instructorGiver = loginInstructor("instructor1OfCourse1");

        questionNumber = 8;
        question = getQuestion(session, questionNumber);
        submissionParams = buildSubmissionParams(question, Intent.INSTRUCTOR_SUBMISSION);

        List<Student> studentRecipients = getStudents("student2InCourse1", "student3InCourse1");
        requestBody = buildRequestBodyWithStudentRecipientsTeam(studentRecipients);

        outputResponses = callExecute(requestBody, submissionParams);
        validateOutputForStudentRecipientsByTeam(outputResponses, instructorGiver.getEmail(), studentRecipients);
        validateStudentDatabaseByTeam(session, question, instructorGiver.getEmail(), studentRecipients);

        ______TS("Success: question has existing responses");
        Student studentGiver = loginStudent("student1InCourse1");

        questionNumber = 2;
        question = getQuestion(session, questionNumber);
        submissionParams = buildSubmissionParams(question, Intent.STUDENT_SUBMISSION);

        studentRecipients = getStudents("student3InCourse1");
        requestBody = buildRequestBodyWithStudentRecipientsEmail(studentRecipients);

        outputResponses = callExecute(requestBody, submissionParams);
        validateOutputForStudentRecipientsByEmail(outputResponses, studentGiver.getEmail(), studentRecipients);
        validateStudentDatabaseByEmail(session, question, studentGiver.getEmail(), studentRecipients);

        ______TS("Failure: student is a invalid giver of the question");
        questionNumber = 6;
        question = getQuestion(session, questionNumber);
        submissionParams = buildSubmissionParams(question, Intent.STUDENT_SUBMISSION);

        studentRecipients = getStudents("student2InCourse1");
        requestBody = buildRequestBodyWithStudentRecipientsTeam(studentRecipients);

        verifyInvalidOperation(requestBody, submissionParams);

        ______TS("Success: too many recipients");
        studentGiver = loginStudent("student4InCourse1");

        questionNumber = 9;
        question = getQuestion(session, questionNumber);
        submissionParams = buildSubmissionParams(question, Intent.STUDENT_SUBMISSION);

        studentRecipients = getStudents("student2InCourse1", "student3InCourse1");
        requestBody = buildRequestBodyWithStudentRecipientsEmail(studentRecipients);

        outputResponses = callExecute(requestBody, submissionParams);
        validateOutputForStudentRecipientsByEmail(outputResponses, studentGiver.getEmail(), studentRecipients);
        validateStudentDatabaseByEmail(session, question, studentGiver.getEmail(), studentRecipients);
    }
}
