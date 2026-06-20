package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringEscapeUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
import teammates.test.GroupNames;
import teammates.test.ResponseEntityHelper;
import teammates.ui.output.FeedbackQuestionResponsesData;
import teammates.ui.output.FeedbackResponseData;
import teammates.ui.request.FeedbackResponsesRequest;
import teammates.ui.request.Intent;

/**
 * SUT: {@link SubmitFeedbackResponsesAction}.
 */
public class SubmitFeedbackResponsesActionIT extends BaseActionIT<SubmitFeedbackResponsesAction> {
    private DataBundle typicalBundle;
    private FeedbackQuestion currentQuestionForSubmission;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
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
        loginAsInstructor(instructor);
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
        loginAsStudent(student);
        return student;
    }

    private FeedbackQuestion getQuestion(
            FeedbackSession session, int questionNumber) {
        Set<FeedbackQuestion> questions = session.getFeedbackQuestions();
        return questions.stream()
                .filter(question -> question.getQuestionNumber() == questionNumber)
                .findFirst()
                .orElse(null);
    }

    private void setStartTime(FeedbackSession session, int days) {
        inTransaction(() -> {
            Instant startTime = TimeHelper.getInstantDaysOffsetFromNow(days);
            logic.getFeedbackSession(session.getId()).setStartTime(startTime);
        });
    }

    private void setEndTime(FeedbackSession session, int days) {
        inTransaction(() -> {
            Instant endTime = TimeHelper.getInstantDaysOffsetFromNow(days);
            logic.getFeedbackSession(session.getId()).setEndTime(endTime);
        });
    }

    private void setUserDeadlineExtension(FeedbackSession session, User user, int days) {
        inTransaction(() -> {
            FeedbackSession managedSession = logic.getFeedbackSession(session.getId());
            User managedUser = logic.getUser(user.getId());
            Instant endTime = TimeHelper.getInstantDaysOffsetFromNow(days);
            DeadlineExtension existingDeadline = logic.getDeadlineExtensionEntityForUser(managedSession, managedUser);
            if (existingDeadline != null) {
                existingDeadline.setEndTime(endTime);
                logic.updateDeadlineExtension(existingDeadline);
            } else {
                DeadlineExtension newDeadline = new DeadlineExtension(managedUser, endTime);
                managedSession.addDeadlineExtension(newDeadline);
                logic.createDeadlineExtension(newDeadline);
            }
        });
    }

    private void deleteDeadlineExtensionForUser(FeedbackSession session, User user) {
        inTransaction(() -> {
            FeedbackSession managedSession = logic.getFeedbackSession(session.getId());
            User managedUser = logic.getUser(user.getId());
            DeadlineExtension existingDeadlineEndTime = logic.getDeadlineExtensionEntityForUser(managedSession, managedUser);
            if (existingDeadlineEndTime == null) {
                return;
            }

            managedSession.getDeadlineExtensions().remove(existingDeadlineEndTime);
            logic.deleteDeadlineExtension(existingDeadlineEndTime);
        });
    }

    private String[] buildSubmissionParams(FeedbackSession session, int questionNumber, Intent intent) {
        FeedbackQuestion question = getQuestion(session, questionNumber);
        return buildSubmissionParams(question, intent);
    }

    private String[] buildSubmissionParams(FeedbackQuestion question, Intent intent) {
        currentQuestionForSubmission = question;
        String sessionId = question != null
                ? question.getFeedbackSession().getId().toString()
                : UUID.randomUUID().toString();

        return new String[] {Const.ParamsNames.FEEDBACK_SESSION_ID, sessionId, Const.ParamsNames.INTENT,
                intent.toString()};
    }

    private void setSubmitSessionInSectionsInstructorPrivilege(FeedbackSession session,
                                                        Instructor instructor, boolean value) {
        String courseId = session.getCourseId();

        InstructorPrivileges runtimePrivileges = new InstructorPrivileges(instructor.getId());
        runtimePrivileges.updatePrivilege(Const.InstructorPermissions.CAN_SUBMIT_SESSION, value);

        inTransaction(() -> {
            Instructor updatedInstructor = logic.getInstructor(instructor.getId());
            updatedInstructor.getCourse().setId(courseId);
            updatedInstructor.setRole(InstructorPermissionRole.CUSTOM);
            logic.saveInstructorPrivileges(updatedInstructor, runtimePrivileges);
        });
    }

    private List<String> extractStudentEmails(List<Student> students) {
        return students.stream().map(User::getEmail).toList();
    }

    private List<String> extractStudentTeams(List<Student> students) {
        return students.stream().map(Student::getTeamName).distinct().toList();
    }

    private List<String> extractInstructorEmails(
            List<Instructor> students) {
        return students.stream().map(User::getEmail).toList();
    }

    // The recipient key format must stay consistent with ResponseRecipient#getKey() / ResponseGiver#getKey().
    private List<Recipient> studentEmailRecipients(List<Student> students) {
        return students.stream()
                .map(s -> new Recipient("STUDENT:" + s.getId(), s.getEmail()))
                .toList();
    }

    private List<Recipient> studentTeamRecipients(List<Student> students) {
        return students.stream()
                .map(s -> new Recipient("TEAM:" + s.getTeamId(), s.getTeamName()))
                .distinct()
                .toList();
    }

    private List<Recipient> instructorRecipientsList(List<Instructor> instructors) {
        return instructors.stream()
                .map(i -> new Recipient("INSTRUCTOR:" + i.getId(), i.getEmail()))
                .toList();
    }

    private FeedbackResponsesRequest buildRequestBodyWithStudentRecipientsEmail(
            List<Student> recipients) {
        return buildRequestBody(studentEmailRecipients(recipients));
    }

    private FeedbackResponsesRequest buildRequestBodyWithStudentRecipientsTeam(
            List<Student> recipients) {
        return buildRequestBody(studentTeamRecipients(recipients));
    }

    private FeedbackResponsesRequest buildRequestBodyWithInstructorRecipients(List<Instructor> recipients) {
        return buildRequestBody(instructorRecipientsList(recipients));
    }

    private FeedbackResponsesRequest buildRequestBody(List<Recipient> recipients) {
        List<FeedbackResponsesRequest.FeedbackResponseRequest> responses = new ArrayList<>();

        for (Recipient recipient : recipients) {
            FeedbackTextResponseDetails responseDetails =
                    new FeedbackTextResponseDetails("Response for " + recipient.label());
            FeedbackResponsesRequest.FeedbackResponseRequest response =
                    new FeedbackResponsesRequest.FeedbackResponseRequest(recipient.key(), responseDetails);

            responses.add(response);
        }

        FeedbackResponsesRequest requestBody = new FeedbackResponsesRequest();
        requestBody.setQuestionResponses(Map.of(currentQuestionForSubmission.getId(), responses));
        return requestBody;
    }

    private List<FeedbackResponseData> callExecute(FeedbackResponsesRequest requestBody,
                                                   String[] submissionParams) {
        SubmitFeedbackResponsesAction action = getAction(requestBody, submissionParams);
        JsonResult result = getJsonResult(action);

        FeedbackQuestionResponsesData output = (FeedbackQuestionResponsesData) result.getOutput();
        return output.getQuestionResponses().values().stream()
                .flatMap(List::stream)
                .toList();
    }

    private void validateOutputForStudentRecipientsByEmail(List<FeedbackResponseData> responses, String giverKey,
                                                           List<Student> recipients) {
        List<Recipient> expected = studentEmailRecipients(recipients);
        assertEquals(expected.size(), responses.size());

        validateOutput(responses, giverKey, expected);
    }

    private void validateOutputForStudentRecipientsByTeam(List<FeedbackResponseData> responses, String giverKey,
                                                          List<Student> recipients) {
        List<Recipient> expected = studentTeamRecipients(recipients);
        assertEquals(expected.size(), responses.size());

        validateOutput(responses, giverKey, expected);
    }

    private void validateOutputForInstructorRecipients(List<FeedbackResponseData> responses, String giverKey,
                                                       List<Instructor> recipients) {
        List<Recipient> expected = instructorRecipientsList(recipients);
        assertEquals(expected.size(), responses.size());

        validateOutput(responses, giverKey, expected);
    }

    private void validateOutput(List<FeedbackResponseData> responses, String giverKey, List<Recipient> recipients) {
        for (Recipient expected : recipients) {
            FeedbackResponseData response = responses.stream()
                    .filter(r -> expected.key().equals(r.getRecipientIdentifier()))
                    .findFirst()
                    .orElseThrow();

            assertEquals(giverKey, response.getGiverIdentifier());
            assertEquals(expected.key(), response.getRecipientIdentifier());

            FeedbackResponseDetails responseDetails = response.getResponseDetails();
            assertEquals(StringEscapeUtils.unescapeHtml(
                            SanitizationHelper.sanitizeForRichText("Response for " + expected.label())),
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
        List<FeedbackResponse> responses = inTransaction(() -> logic.getFeedbackQuestion(feedbackQuestion.getId())
                .getFeedbackResponses().stream()
                .toList());
        for (String recipientEmail : recipientEmails) {
            List<FeedbackResponse> feedbackResponses = responses.stream()
                    .filter(response -> ResponseEntityHelper.getIdentifier(response.getGiver()).equals(giverEmail))
                    .filter(response -> ResponseEntityHelper.getIdentifier(response.getRecipient()).equals(recipientEmail))
                    .toList();

            for (FeedbackResponse feedbackResponse : feedbackResponses) {
                FeedbackQuestion frFeedbackQuestion = feedbackResponse.getFeedbackQuestion();

                assertEquals(frFeedbackQuestion, feedbackQuestion);
                assertEquals(ResponseEntityHelper.getIdentifier(feedbackResponse.getGiver()), giverEmail);
                assertEquals(ResponseEntityHelper.getIdentifier(feedbackResponse.getRecipient()), recipientEmail);

                assertEquals(session.getName(), feedbackQuestion.getFeedbackSession().getName());
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
    @Test(groups = GroupNames.INTEGRATION)
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

        ______TS("Failure with instructors: feedback session does not exist");
        setStartTime(session, -1);
        setEndTime(session, 3);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, "00000000-0000-0000-0000-000000000000"};

        verifyEntityNotFoundAcl(submissionParams);

        ______TS("Failure with students: no feedback session parameter");
        loginStudent("student1InCourse1");
        setStartTime(session, -1);
        setEndTime(session, 3);

        questionNumber = 2;
        FeedbackQuestion question = getQuestion(session, questionNumber);
        submissionParams = new String[] {Const.ParamsNames.FEEDBACK_SESSION_ID,
                question.getFeedbackSession().getId().toString()};

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

        verifyCanMasquerade(student.getAccountId(), submissionParams);

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

        verifyCanMasquerade(instructor.getAccountId(), submissionParams);

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
        verifyCannotMasquerade(instructor.getAccountId(), submissionParams);

        ______TS("Failure with instructor: instructor has no modify session comment privileges");
        loginInstructor("instructor1OfCourse1");
        setStartTime(session, -1);
        setEndTime(session, 3);

        setSubmitSessionInSectionsInstructorPrivilege(session, instructor, false);

        questionNumber = 4;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        verifyCannotAccess(submissionParams);
        verifyCannotMasquerade(instructor.getAccountId(), submissionParams);

        // Reset privileges
        setSubmitSessionInSectionsInstructorPrivilege(session, instructor, true);
    }

    @Override
    @Test(groups = GroupNames.INTEGRATION)
    public void testExecute() {
        ______TS("Failure: invalid http parameters");
        loginInstructor("instructor1OfCourse1");

        verifyHttpParameterFailure();

        ______TS("Failure: not feedback question parameter specified");
        String[] submissionParams = new String[] {Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString()};
        verifyHttpParameterFailure(submissionParams);

        ______TS("Failure: feedback session does not exist");
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, "00000000-0000-0000-0000-000000000000"};
        verifyEntityNotFound(submissionParams);

        ______TS("Failure: no request body");
        loginStudent("student1InCourse1");
        FeedbackSession session = getSession("session1InCourse1");
        int questionNumber = 2;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.STUDENT_SUBMISSION);
        verifyHttpRequestBodyFailure(null, submissionParams);

        ______TS("Failure: request body has no recipient");
        loginInstructor("instructor1OfCourse1");

        questionNumber = 4;
        submissionParams = buildSubmissionParams(session, questionNumber, Intent.INSTRUCTOR_SUBMISSION);

        // Null recipient key
        FeedbackResponsesRequest requestBody = buildRequestBody(
                Collections.singletonList(new Recipient(null, "no key")));
        verifyHttpRequestBodyFailure(requestBody, submissionParams);

        // Empty recipient key
        requestBody = buildRequestBody(Collections.singletonList(new Recipient("", "empty key")));
        verifyHttpRequestBodyFailure(requestBody, submissionParams);

        ______TS("Success: question has no existing responses");
        Instructor instructorGiver = loginInstructor("instructor1OfCourse1");

        questionNumber = 7;
        FeedbackQuestion question = getQuestion(session, questionNumber);
        submissionParams = buildSubmissionParams(question, Intent.INSTRUCTOR_SUBMISSION);

        List<Instructor> instructorRecipients = Collections.singletonList(instructorGiver);
        requestBody = buildRequestBodyWithInstructorRecipients(instructorRecipients);

        List<FeedbackResponseData> outputResponses = callExecute(requestBody, submissionParams);
        validateOutputForInstructorRecipients(
                outputResponses, "INSTRUCTOR:" + instructorGiver.getId(), instructorRecipients);
        validateInstructorDatabaseByEmail(session, question, instructorGiver.getEmail(), instructorRecipients);

        ______TS("Success: instructor is a valid giver of the question to student team");
        instructorGiver = loginInstructor("instructor1OfCourse1");

        questionNumber = 8;
        question = getQuestion(session, questionNumber);
        submissionParams = buildSubmissionParams(question, Intent.INSTRUCTOR_SUBMISSION);

        List<Student> studentRecipients = getStudents("student2InCourse1", "student3InCourse1");
        requestBody = buildRequestBodyWithStudentRecipientsTeam(studentRecipients);

        outputResponses = callExecute(requestBody, submissionParams);
        validateOutputForStudentRecipientsByTeam(
                outputResponses, "INSTRUCTOR:" + instructorGiver.getId(), studentRecipients);
        validateStudentDatabaseByTeam(session, question, instructorGiver.getEmail(), studentRecipients);

        ______TS("Success: question has existing responses");
        Student studentGiver = loginStudent("student1InCourse1");

        questionNumber = 2;
        question = getQuestion(session, questionNumber);
        submissionParams = buildSubmissionParams(question, Intent.STUDENT_SUBMISSION);

        studentRecipients = getStudents("student3InCourse1");
        requestBody = buildRequestBodyWithStudentRecipientsEmail(studentRecipients);

        outputResponses = callExecute(requestBody, submissionParams);
        validateOutputForStudentRecipientsByEmail(outputResponses, "STUDENT:" + studentGiver.getId(), studentRecipients);
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
        validateOutputForStudentRecipientsByEmail(outputResponses, "STUDENT:" + studentGiver.getId(), studentRecipients);
        validateStudentDatabaseByEmail(session, question, studentGiver.getEmail(), studentRecipients);
    }

    /**
     * A recipient's submission key and the email/team label used for its response text.
     *
     * @param key the recipient key, in the same format as {@code ResponseRecipient#getKey()}
     * @param label the email (or team name) used to label the response text
     */
    private record Recipient(String key, String label) {
    }
}
