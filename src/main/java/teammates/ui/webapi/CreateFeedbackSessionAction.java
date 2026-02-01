package teammates.ui.webapi;

import java.time.Instant;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.HibernateUtil;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.request.FeedbackSessionCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Create a feedback session.
 */
public class CreateFeedbackSessionAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
        Course course = sqlLogic.getCourse(courseId);

        gateKeeper.verifyAccessible(instructor, course, Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        FeedbackSessionCreateRequest createRequest = getAndValidateRequestBody(FeedbackSessionCreateRequest.class);
        String feedbackSessionName = SanitizationHelper.sanitizeTitle(createRequest.getFeedbackSessionName());

        Course course = sqlLogic.getCourse(courseId);
        if (course == null) {
            throw new InvalidHttpParameterException("Failed to find course with the given course id.");
        }
        Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
        if (instructor == null) {
            throw new InvalidHttpParameterException("Failed to find instructor with the given courseId and googleId.");
        }

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
        String visibilityStartAndSessionStartTimeError = FieldValidator
                .getInvalidityInfoForTimeForNewVisibilityStart(sessionVisibleTime, startTime);
        if (!visibilityStartAndSessionStartTimeError.isEmpty()) {
            throw new InvalidHttpRequestBodyException("Invalid session visible time: "
                    + visibilityStartAndSessionStartTimeError);
        }
        Instant resultsVisibleTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                createRequest.getResultsVisibleFromTime(), timeZone, true);

        FeedbackSession feedbackSession = new FeedbackSession(
                feedbackSessionName,
                course,
                instructor.getEmail(),
                createRequest.getInstructions(),
                startTime,
                endTime,
                sessionVisibleTime,
                resultsVisibleTime,
                createRequest.getGracePeriod(),
                true,
                createRequest.isClosingSoonEmailEnabled(),
                createRequest.isPublishedEmailEnabled());

        try {
            feedbackSession = sqlLogic.createFeedbackSession(feedbackSession);
            HibernateUtil.flushSession();
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException("A session named " + feedbackSessionName
                    + " exists already in the course " + course.getName()
                    + " (Course ID: " + courseId + ")", e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }

        if (createRequest.getToCopyCourseId() != null) {
            createCopiedFeedbackQuestions(createRequest.getToCopyCourseId(), courseId,
                    feedbackSessionName, createRequest.getToCopySessionName());
        }
        FeedbackSessionData output = new FeedbackSessionData(feedbackSession);
        InstructorPermissionSet privilege = constructInstructorPrivileges(instructor, feedbackSessionName);
        output.setPrivileges(privilege);

        return new JsonResult(output);
    }

    private void createCopiedFeedbackQuestions(String oldCourseId, String newCourseId,
            String newFeedbackSessionName, String oldFeedbackSessionName) {
        FeedbackSession oldFeedbackSession = sqlLogic.getFeedbackSession(oldFeedbackSessionName, oldCourseId);
        FeedbackSession newFeedbackSession = sqlLogic.getFeedbackSession(newFeedbackSessionName, newCourseId);
        sqlLogic.getFeedbackQuestionsForSession(oldFeedbackSession).forEach(question -> {
            FeedbackQuestion feedbackQuestion = question.makeDeepCopy(newFeedbackSession);
            try {
                sqlLogic.createFeedbackQuestion(feedbackQuestion);
            } catch (InvalidParametersException | EntityAlreadyExistsException e) {
                log.severe("Error when copying feedback question: " + e.getMessage());
            }
        });
    }
}
