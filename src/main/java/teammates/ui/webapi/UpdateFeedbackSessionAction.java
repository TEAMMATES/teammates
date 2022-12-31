package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.FieldValidator;
import teammates.common.util.Logger;
import teammates.common.util.TimeHelper;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.request.FeedbackSessionUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates a feedback session.
 */
class UpdateFeedbackSessionAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                feedbackSession,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        FeedbackSessionUpdateRequest updateRequest =
                getAndValidateRequestBody(FeedbackSessionUpdateRequest.class);

        Map<String, Instant> oldStudentDeadlines = feedbackSession.getStudentDeadlines();
        Map<String, Instant> oldInstructorDeadlines = feedbackSession.getInstructorDeadlines();
        Map<String, Instant> studentDeadlines = updateRequest.getStudentDeadlines();
        Map<String, Instant> instructorDeadlines = updateRequest.getInstructorDeadlines();
        try {
            // These ensure the existence checks are only done whenever necessary in order to reduce data reads.
            boolean hasExtraStudents = !oldStudentDeadlines.keySet()
                    .containsAll(studentDeadlines.keySet());
            boolean hasExtraInstructors = !oldInstructorDeadlines.keySet()
                    .containsAll(instructorDeadlines.keySet());
            if (hasExtraStudents) {
                logic.verifyAllStudentsExistInCourse(courseId, studentDeadlines.keySet());
            }
            if (hasExtraInstructors) {
                logic.verifyAllInstructorsExistInCourse(courseId, instructorDeadlines.keySet());
            }
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        String timeZone = feedbackSession.getTimeZone();
        Instant startTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                updateRequest.getSubmissionStartTime(), timeZone, true);
        if (!updateRequest.getSubmissionStartTime().equals(feedbackSession.getStartTime())) {
            String startTimeError = FieldValidator.getInvalidityInfoForNewStartTime(startTime, timeZone);
            if (!startTimeError.isEmpty()) {
                throw new InvalidHttpRequestBodyException("Invalid submission opening time: " + startTimeError);
            }
        }
        Instant endTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                updateRequest.getSubmissionEndTime(), timeZone, true);
        if (!updateRequest.getSubmissionEndTime().equals(feedbackSession.getEndTime())) {
            String endTimeError = FieldValidator.getInvalidityInfoForNewEndTime(endTime, timeZone);
            if (!endTimeError.isEmpty()) {
                throw new InvalidHttpRequestBodyException("Invalid submission closing time: " + endTimeError);
            }
        }
        Instant sessionVisibleTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                updateRequest.getSessionVisibleFromTime(), timeZone, true);
        if (!updateRequest.getSessionVisibleFromTime().equals(feedbackSession.getSessionVisibleFromTime())) {
            String visibilityStartAndSessionStartTimeError = FieldValidator
                    .getInvalidityInfoForTimeForNewVisibilityStart(sessionVisibleTime, startTime);
            if (!visibilityStartAndSessionStartTimeError.isEmpty()) {
                throw new InvalidHttpRequestBodyException("Invalid session visible time: "
                        + visibilityStartAndSessionStartTimeError);
            }
        }
        Instant resultsVisibleTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                updateRequest.getResultsVisibleFromTime(), timeZone, true);
        studentDeadlines = studentDeadlines.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                        entry.getValue(), timeZone, true)));
        instructorDeadlines = instructorDeadlines.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                        entry.getValue(), timeZone, true)));
        try {
            feedbackSession = logic.updateFeedbackSession(
                    FeedbackSessionAttributes.updateOptionsBuilder(feedbackSessionName, courseId)
                            .withInstructions(updateRequest.getInstructions())
                            .withStartTime(startTime)
                            .withEndTime(endTime)
                            .withGracePeriod(updateRequest.getGracePeriod())
                            .withSessionVisibleFromTime(sessionVisibleTime)
                            .withResultsVisibleFromTime(resultsVisibleTime)
                            .withIsClosingEmailEnabled(updateRequest.isClosingEmailEnabled())
                            .withIsPublishedEmailEnabled(updateRequest.isPublishedEmailEnabled())
                            .withStudentDeadlines(studentDeadlines)
                            .withInstructorDeadlines(instructorDeadlines)
                            .build());
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        } catch (EntityDoesNotExistException ednee) {
            // Entity existence has been verified before, and this exception should not happen
            log.severe("Unexpected error", ednee);
            return new JsonResult(ednee.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        boolean notifyAboutDeadlines = getBooleanRequestParamValue(Const.ParamsNames.NOTIFY_ABOUT_DEADLINES);

        List<EmailWrapper> emailsToSend = new ArrayList<>();

        emailsToSend.addAll(processDeadlineExtensions(courseId, feedbackSession, oldStudentDeadlines, studentDeadlines,
                false, notifyAboutDeadlines));
        emailsToSend.addAll(processDeadlineExtensions(courseId, feedbackSession, oldInstructorDeadlines, instructorDeadlines,
                true, notifyAboutDeadlines));

        taskQueuer.scheduleEmailsForSending(emailsToSend);

        return new JsonResult(new FeedbackSessionData(feedbackSession));
    }

    private List<EmailWrapper> processDeadlineExtensions(String courseId, FeedbackSessionAttributes session,
            Map<String, Instant> oldDeadlines, Map<String, Instant> newDeadlines,
            boolean areInstructors, boolean notifyUsers) {
        if (oldDeadlines.equals(newDeadlines)) {
            return Collections.emptyList();
        }

        // Revoke deadline extensions
        Map<String, Instant> deadlinesToRevoke = new HashMap<>(oldDeadlines);
        deadlinesToRevoke.keySet().removeAll(newDeadlines.keySet());

        deadlinesToRevoke.keySet().forEach(email ->
                logic.deleteDeadlineExtension(courseId, session.getFeedbackSessionName(), email, areInstructors));

        // Create deadline extensions
        Map<String, Instant> deadlinesToCreate = new HashMap<>(newDeadlines);
        deadlinesToCreate.keySet().removeAll(oldDeadlines.keySet());

        deadlinesToCreate.entrySet()
                .stream()
                .map(entry -> DeadlineExtensionAttributes
                        .builder(courseId, session.getFeedbackSessionName(), entry.getKey(), areInstructors)
                        .withEndTime(entry.getValue())
                        .build())
                .forEach(deadlineExtension -> {
                    try {
                        logic.createDeadlineExtension(deadlineExtension);
                    } catch (InvalidParametersException | EntityAlreadyExistsException e) {
                        log.severe("Unexpected error while creating deadline extension", e);
                    }
                });

        // Update deadline extensions
        Map<String, Instant> deadlinesToUpdate = new HashMap<>(newDeadlines);
        deadlinesToUpdate = deadlinesToUpdate.entrySet().stream()
                .filter(entry -> oldDeadlines.containsKey(entry.getKey())
                        && !entry.getValue().equals(oldDeadlines.get(entry.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        deadlinesToUpdate.entrySet()
                .stream()
                .map(entry -> DeadlineExtensionAttributes
                        .updateOptionsBuilder(courseId, session.getFeedbackSessionName(), entry.getKey(), areInstructors)
                        .withEndTime(entry.getValue())
                        .build())
                .forEach(updateOptions -> {
                    try {
                        logic.updateDeadlineExtension(updateOptions);
                    } catch (InvalidParametersException | EntityDoesNotExistException e) {
                        log.severe("Unexpected error while updating deadline extension", e);
                    }
                });

        List<EmailWrapper> emailsToSend = new ArrayList<>();
        if (notifyUsers) {
            CourseAttributes course = logic.getCourse(courseId);
            emailsToSend.addAll(emailGenerator
                    .generateDeadlineRevokedEmails(course, session, deadlinesToRevoke, areInstructors));
            emailsToSend.addAll(emailGenerator
                    .generateDeadlineGrantedEmails(course, session, deadlinesToCreate, areInstructors));
            emailsToSend.addAll(emailGenerator
                    .generateDeadlineUpdatedEmails(course, session, deadlinesToUpdate, oldDeadlines, areInstructors));
        }
        return emailsToSend;
    }

}
