package teammates.test.cases.webapi;

import java.util.ArrayList;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.webapi.action.Intent;
import teammates.ui.webapi.action.UpdateFeedbackResponseAction;
import teammates.ui.webapi.request.FeedbackResponseUpdateRequest;

/**
 * SUT: {@link UpdateFeedbackResponseAction}.
 */
public class UpdateFeedbackResponseActionTest extends BaseActionTest<UpdateFeedbackResponseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        // TODO
    }

    @Test
    public void testExecute_studentFeedbackSubmissionMcqGenerateOptionsForTeams_shouldValidateAnswer() throws Exception {
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");

        // create a question
        FeedbackMcqQuestionDetails feedbackMcqQuestionDetails = new FeedbackMcqQuestionDetails();
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.TEAMS);
        FeedbackQuestionAttributes fqa = logic.createFeedbackQuestion(FeedbackQuestionAttributes.builder()
                .withCourseId(fsa.getCourseId())
                .withFeedbackSessionName(fsa.getFeedbackSessionName())
                .withNumberOfEntitiesToGiveFeedbackTo(2)
                .withQuestionDescription("test")
                .withQuestionNumber(1)
                .withGiverType(FeedbackParticipantType.STUDENTS)
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withQuestionDetails(feedbackMcqQuestionDetails)
                .withShowResponsesTo(new ArrayList<>())
                .withShowGiverNameTo(new ArrayList<>())
                .withShowRecipientNameTo(new ArrayList<>())
                .build());

        // create a response
        FeedbackMcqResponseDetails feedbackMcqResponseDetails = new FeedbackMcqResponseDetails();
        feedbackMcqResponseDetails.setAnswer(studentAttributes.getTeam());
        FeedbackResponseAttributes feedbackResponse =
                FeedbackResponseAttributes
                        .builder(fqa.getId(), studentAttributes.getEmail(), studentAttributes.getEmail())
                        .withGiverSection(studentAttributes.getSection())
                        .withRecipientSection(studentAttributes.getSection())
                        .withCourseId(fqa.getCourseId())
                        .withFeedbackSessionName(fqa.getFeedbackSessionName())
                        .withResponseDetails(feedbackMcqResponseDetails)
                        .build();
        feedbackResponse = logic.createFeedbackResponse(feedbackResponse);

        FeedbackResponseUpdateRequest updateRequest = new FeedbackResponseUpdateRequest();
        updateRequest.setQuestionType(FeedbackQuestionType.MCQ);
        updateRequest.setRecipientIdentifier(studentAttributes.getEmail());
        FeedbackMcqResponseDetails newDetails = new FeedbackMcqResponseDetails();
        newDetails.setAnswer("TEAM_NOT_EXIST");
        updateRequest.setResponseDetails(newDetails);

        String[] params = {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponse.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        InvalidHttpRequestBodyException e = assertThrows(InvalidHttpRequestBodyException.class, () -> {
            loginAsStudent(studentAttributes.getGoogleId());
            UpdateFeedbackResponseAction a = getAction(updateRequest, params);
            getJsonResult(a);
        });
        AssertHelper.assertContains(Const.FeedbackQuestion.MCQ_ERROR_INVALID_OPTION, e.getMessage());
    }

    @Test
    public void testExecute_instructorFeedbackSubmissionMcqGenerateOptionsForTeams_shouldValidateAnswer() throws Exception {
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructorAttributes = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");

        // create a question
        FeedbackMcqQuestionDetails feedbackMcqQuestionDetails = new FeedbackMcqQuestionDetails();
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.TEAMS);
        FeedbackQuestionAttributes fqa = logic.createFeedbackQuestion(FeedbackQuestionAttributes.builder()
                .withCourseId(fsa.getCourseId())
                .withFeedbackSessionName(fsa.getFeedbackSessionName())
                .withNumberOfEntitiesToGiveFeedbackTo(2)
                .withQuestionDescription("test")
                .withQuestionNumber(1)
                .withGiverType(FeedbackParticipantType.INSTRUCTORS)
                .withRecipientType(FeedbackParticipantType.INSTRUCTORS)
                .withQuestionDetails(feedbackMcqQuestionDetails)
                .withShowResponsesTo(new ArrayList<>())
                .withShowGiverNameTo(new ArrayList<>())
                .withShowRecipientNameTo(new ArrayList<>())
                .build());

        // create a response
        FeedbackMcqResponseDetails feedbackMcqResponseDetails = new FeedbackMcqResponseDetails();
        feedbackMcqResponseDetails.setAnswer(studentAttributes.getTeam());
        FeedbackResponseAttributes feedbackResponse =
                FeedbackResponseAttributes
                        .builder(fqa.getId(), instructorAttributes.getEmail(), instructorAttributes.getEmail())
                        .withGiverSection(Const.DEFAULT_SECTION)
                        .withRecipientSection(Const.DEFAULT_SECTION)
                        .withCourseId(fqa.getCourseId())
                        .withFeedbackSessionName(fqa.getFeedbackSessionName())
                        .withResponseDetails(feedbackMcqResponseDetails)
                        .build();
        feedbackResponse = logic.createFeedbackResponse(feedbackResponse);

        // send update request
        FeedbackResponseUpdateRequest updateRequest = new FeedbackResponseUpdateRequest();
        updateRequest.setQuestionType(FeedbackQuestionType.MCQ);
        updateRequest.setRecipientIdentifier(instructorAttributes.getEmail());
        FeedbackMcqResponseDetails newDetails = new FeedbackMcqResponseDetails();
        newDetails.setAnswer("TEAM_NOT_EXIST");
        updateRequest.setResponseDetails(newDetails);

        String[] params = {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponse.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        InvalidHttpRequestBodyException e = assertThrows(InvalidHttpRequestBodyException.class, () -> {
            loginAsInstructor(instructorAttributes.getGoogleId());
            UpdateFeedbackResponseAction a = getAction(updateRequest, params);
            getJsonResult(a);
        });
        AssertHelper.assertContains(Const.FeedbackQuestion.MCQ_ERROR_INVALID_OPTION, e.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        // TODO
    }

}
