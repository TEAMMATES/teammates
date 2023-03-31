package teammates.it.ui.webapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
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
import teammates.storage.sqlentity.Team;
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
    String getActionUri() {
        return Const.ResourceURIs.RESPONSES;
    }

    @Override
    String getRequestMethod() {
        return PUT;
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

    private FeedbackQuestion getQuestion(
            FeedbackSession session, int questionNumber) {
        return logic.getFeedbackQuestionForQuestionNumber(
                session.getId(), session.getCourse().getId(), questionNumber);
    }

    private List<String> extractStudentEmails(List<Student> students) {
        return students.stream().map(recipient -> recipient.getEmail()).collect(Collectors.toList());
    }

    private List<Team> extractStudentTeams(List<Student> students) {
        return students.stream().map(recipient -> recipient.getTeam()).collect(Collectors.toList());
    }

    private FeedbackResponsesRequest buildRequestBodyWithStudentRecipientsEmail(
            List<Student> recipients) {
        List<String> emails = extractStudentEmails(recipients);
        return buildRequestBody(emails);
    }

    private FeedbackResponsesRequest buildRequestBodyWithStudentRecipientsTeam(
            List<Student> recipients) {
        List<Team> teams = extractStudentTeams(recipients);
        List<String> teamsName = teams.stream().map(team -> team.getName()).collect(Collectors.toList());
        return buildRequestBody(teamsName);
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

    private void validateOutputForStudentRecipientsByEmail(
            List<FeedbackResponseData> responses, String giverEmail, List<Student> recipients) {
        int responsesSize = responses.size();
        assertEquals(recipients.size(), responsesSize);

        List<String> recipientEmails = extractStudentEmails(recipients);

        validateOutput(responses, giverEmail, recipientEmails);
    }

    private void validateOutput(
            List<FeedbackResponseData> responses, String giverValue, List<String> recipientValues) {
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

    private void validateStudentDatabaseByEmail(
            FeedbackSession session,
            FeedbackQuestion question,
            String giverTeam, List<Student> recipients) {
        List<String> teams = extractStudentEmails(recipients);

        validateDatabase(session, question, giverTeam, teams);
    }

    private void validateDatabase(FeedbackSession session, FeedbackQuestion question,
                                  String giverValue, List<String> recipientValues) {
        for (String recipientValue : recipientValues) {
            FeedbackResponse response = logic.getFeedbackResponse(question.getId(), giverValue,
                    recipientValue);

            assertEquals(question.getId(), response.getFeedbackQuestion().getId());
            assertEquals(giverValue, response.getGiver());

            assertEquals(recipientValue, response.getRecipient());

            assertEquals(session.getName(), response.getFeedbackQuestion().getFeedbackSession().getName());
            assertEquals(session.getCourse().getId(),
                    response.getFeedbackQuestion().getFeedbackSession().getCourse().getId());

            FeedbackResponseDetails responseDetails = response.getFeedbackResponseDetailsCopy();

            assertEquals(
                    StringEscapeUtils.unescapeHtml(
                            SanitizationHelper.sanitizeForRichText("Response for " + recipientValue)),
                    StringEscapeUtils.unescapeHtml(responseDetails.getAnswerString()));
        }
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        FeedbackQuestion forStudentFeedbackQuestion = typicalBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        FeedbackQuestion forInstructorFeedbackQuestion = typicalBundle.feedbackQuestions.get("qn4InSession1InCourse1");

        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("No params");
        loginAsInstructor(instructor.getGoogleId());

        verifyHttpParameterFailure();

        ______TS("Missing feedback question id");
        loginAsInstructor(instructor.getGoogleId());

        String[] params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        verifyHttpParameterFailure(params);

        ______TS("Non existent feedback question");
        Instructor instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");

        loginAsInstructor(instructor1OfCourse2.getGoogleId());

        params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "non-existent-id",
        };

        verifyEntityNotFound(params);

        ______TS("Invalid intent");
        params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID, forStudentFeedbackQuestion.getId().toString(),
        };

        verifyHttpParameterFailure(params);

        ______TS("No request body");
        Student student = typicalBundle.students.get("student1InCourse1");

        loginAsStudent(student.getGoogleId());

        params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID, forStudentFeedbackQuestion.getId().toString(),
        };

        verifyHttpRequestBodyFailure(null, params);

        ______TS("Request body has no recipient, recipient as null");
        loginAsInstructor(instructor.getGoogleId());

        params = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID, forInstructorFeedbackQuestion.getId().toString(),
        };

        List<String> nullEmail = Collections.singletonList(null);
        FeedbackResponsesRequest requestBody = buildRequestBody(nullEmail);

        verifyInvalidOperation(requestBody, params);

        ______TS("Request body has no recipient, recipient as empty string");
        requestBody = buildRequestBody(Collections.singletonList(""));

        verifyInvalidOperation(requestBody, params);

        ______TS("Typical Success Case with no existing responses");
        FeedbackSession feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse2");
        Student giver = typicalBundle.students.get("student1InCourse2");

        loginAsStudent(giver.getGoogleId());

        int questionNumber = 1;
        FeedbackQuestion question = getQuestion(feedbackSession, questionNumber);
        params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId().toString(),
        };

        List<Student> studentRecipients = getStudents("student2InCourse2");
        requestBody = buildRequestBodyWithStudentRecipientsEmail(studentRecipients);

        List<FeedbackResponseData> outputResponses = callExecute(requestBody, params);
        validateOutputForStudentRecipientsByEmail(outputResponses, giver.getEmail(), studentRecipients);
        validateStudentDatabaseByEmail(feedbackSession, question, giver.getEmail(), studentRecipients);

        ______TS("Invalid recipient of question, should fail");
        feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        giver = typicalBundle.students.get("student1InCourse1");

        loginAsStudent(giver.getGoogleId());

        questionNumber = 1;
        question = getQuestion(feedbackSession, questionNumber);
        params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId().toString(),
        };

        studentRecipients = getStudents("student2InCourse1");
        requestBody = buildRequestBodyWithStudentRecipientsTeam(studentRecipients);

        verifyInvalidOperation(requestBody, params);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        // See each independent test case.
    }

    @Test
    public void testAccessControl_instructorSubmissionPastEndTime_shouldAllowIfBeforeDeadline()
            throws Exception {
        FeedbackSession feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion feedbackQuestion = typicalBundle.feedbackQuestions.get("qn4InSession1InCourse1");

        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor.getGoogleId());

        ______TS("Typical Success Case for Instructor submitting before deadline");
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        verifyCanAccess(params);

        ______TS("Instructor submitting after deadline; should fail");
        feedbackSession.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(-2));

        logic.updateFeedbackSession(feedbackSession);

        DeadlineExtension deadlineExtension =
                logic.getDeadlineExtension(instructor.getId(), feedbackSession.getId());

        deadlineExtension.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        logic.updateDeadlineExtension(deadlineExtension);

        verifyCannotAccess(params);

        ______TS("No selective deadline; should fail.");
        List<DeadlineExtension> instructorDEs =
                feedbackSession
                        .getDeadlineExtensions()
                        .stream()
                        .filter(de -> de.getUser().equals(instructor))
                        .collect(Collectors.toList());

        for (DeadlineExtension de : instructorDEs) {
            logic.deleteDeadlineExtension(de);
        }

        verifyCannotAccess(params);
    }

    @Test
    public void testAccessControl_studentSubmissionPastEndTime_shouldAllowIfBeforeDeadline()
            throws Exception {
        FeedbackSession feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestion feedbackQuestion = typicalBundle.feedbackQuestions.get("qn1InSession1InCourse1");

        Student student = typicalBundle.students.get("student1InCourse1");

        loginAsStudent(student.getGoogleId());

        ______TS("Typical Success Case for Student submitting before deadline");
        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestion.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        verifyCanAccess(params);

        ______TS("Student submitting after deadline; should fail");
        feedbackSession.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(-2));

        logic.updateFeedbackSession(feedbackSession);

        DeadlineExtension deadlineExtension =
                logic.getDeadlineExtension(student.getId(), feedbackSession.getId());

        deadlineExtension.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(-1));
        logic.updateDeadlineExtension(deadlineExtension);

        verifyCannotAccess(params);

        ______TS("No selective deadline; should fail.");
        List<DeadlineExtension> studentDEs =
                feedbackSession
                        .getDeadlineExtensions()
                        .stream()
                        .filter(de -> de.getUser().equals(student))
                        .collect(Collectors.toList());

        for (DeadlineExtension de : studentDEs) {
            logic.deleteDeadlineExtension(de);
        }

        verifyCannotAccess(params);
    }
}
