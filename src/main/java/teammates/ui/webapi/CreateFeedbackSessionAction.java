package teammates.ui.webapi;

import java.time.Instant;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.request.FeedbackSessionCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Create a feedback session.
 */
class CreateFeedbackSessionAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        CourseAttributes course = logic.getCourse(courseId);

        gateKeeper.verifyAccessible(instructor, course, Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        CourseAttributes course = logic.getCourse(courseId);
        FeedbackSessionCreateRequest createRequest =
                getAndValidateRequestBody(FeedbackSessionCreateRequest.class);

        String timeZone = course.getTimeZone();
        Instant startTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                createRequest.getSubmissionStartTime(), timeZone, true);
        String startTimeError = FieldValidator.getInvalidityInfoForNewStartTime(startTime, timeZone);
        if (!startTimeError.isEmpty()) {
            throw new InvalidHttpRequestBodyException("Invalid submission opening time: " + startTimeError);
        }
        Instant endTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                createRequest.getSubmissionEndTime(), timeZone, true);
        String endTimeError = FieldValidator.getInvalidityInfoForNewEndTime(endTime, timeZone);
        if (!endTimeError.isEmpty()) {
            throw new InvalidHttpRequestBodyException("Invalid submission closing time: " + endTimeError);
        }
        Instant sessionVisibleTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                createRequest.getSessionVisibleFromTime(), timeZone, true);
        String visibilityStartAndSessionStartTimeError =
                FieldValidator.getInvalidityInfoForTimeForNewVisibilityStart(sessionVisibleTime, startTime);
        if (!visibilityStartAndSessionStartTimeError.isEmpty()) {
            throw new InvalidHttpRequestBodyException("Invalid session visible time: "
                    + visibilityStartAndSessionStartTimeError);
        }
        Instant resultsVisibleTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                createRequest.getResultsVisibleFromTime(), timeZone, true);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        String feedbackSessionName = SanitizationHelper.sanitizeTitle(createRequest.getFeedbackSessionName());

        FeedbackSessionAttributes fs =
                FeedbackSessionAttributes
                        .builder(feedbackSessionName, course.getId())
                        .withCreatorEmail(instructor.getEmail())
                        .withTimeZone(course.getTimeZone())
                        .withInstructions(createRequest.getInstructions())
                        .withStartTime(startTime)
                        .withEndTime(endTime)
                        .withGracePeriod(createRequest.getGracePeriod())
                        .withSessionVisibleFromTime(sessionVisibleTime)
                        .withResultsVisibleFromTime(resultsVisibleTime)
                        .withIsClosingEmailEnabled(createRequest.isClosingEmailEnabled())
                        .withIsPublishedEmailEnabled(createRequest.isPublishedEmailEnabled())
                        .build();

        try {
            logic.createFeedbackSession(fs);
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException("A session named " + feedbackSessionName
                     + " exists already in the course " + course.getName()
                     + " (Course ID: " + courseId + ")", e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }

        if (createRequest.getToCopyCourseId() != null) {
            createFeedbackQuestions(createRequest.getToCopyCourseId(), courseId, feedbackSessionName,
                    createRequest.getToCopySessionName());
        }
        fs = getNonNullFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId());
        FeedbackSessionData output = new FeedbackSessionData(fs);
        InstructorPermissionSet privilege = constructInstructorPrivileges(instructor, feedbackSessionName);
        output.setPrivileges(privilege);

        return new JsonResult(output);
    }

    private void createFeedbackQuestions(String copyCourseId, String newCourseId, String feedbackSessionName,
            String oldSessionName) {
        logic.getFeedbackQuestionsForSession(oldSessionName, copyCourseId).forEach(question -> {
            FeedbackQuestionAttributes attributes = FeedbackQuestionAttributes.builder()
                    .withCourseId(newCourseId)
                    .withFeedbackSessionName(feedbackSessionName)
                    .withGiverType(question.getGiverType())
                    .withRecipientType(question.getRecipientType())
                    .withQuestionNumber(question.getQuestionNumber())
                    .withNumberOfEntitiesToGiveFeedbackTo(question.getNumberOfEntitiesToGiveFeedbackTo())
                    .withShowResponsesTo(question.getShowResponsesTo())
                    .withShowGiverNameTo(question.getShowGiverNameTo())
                    .withShowRecipientNameTo(question.getShowRecipientNameTo())
                    .withQuestionDetails(question.getQuestionDetails())
                    .withQuestionDescription(question.getQuestionDescription())
                    .build();

            try {
                logic.createFeedbackQuestion(attributes);
            } catch (InvalidParametersException e) {
                log.severe("Error when copying feedback question: " + e.getMessage());
            }
        });
    }
}
