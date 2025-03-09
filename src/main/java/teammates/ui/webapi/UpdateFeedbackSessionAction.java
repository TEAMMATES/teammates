package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
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
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.User;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.request.FeedbackSessionUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates a feedback session.
 */
public class UpdateFeedbackSessionAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        if (isCourseMigrated(courseId)) {
            FeedbackSession feedbackSession = getNonNullSqlFeedbackSession(feedbackSessionName, courseId);

            gateKeeper.verifyAccessible(
                    sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId()),
                    feedbackSession,
                    Const.InstructorPermissions.CAN_MODIFY_SESSION);
        } else {
            FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

            gateKeeper.verifyAccessible(
                    logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                    feedbackSession,
                    Const.InstructorPermissions.CAN_MODIFY_SESSION);
        }
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        if (isCourseMigrated(courseId)) {
            FeedbackSession feedbackSession = getNonNullSqlFeedbackSession(feedbackSessionName, courseId);
            assert feedbackSession != null;

            FeedbackSessionUpdateRequest updateRequest =
                    getAndValidateRequestBody(FeedbackSessionUpdateRequest.class);

            List<DeadlineExtension> prevDeadlineExtensions = feedbackSession.getDeadlineExtensions();

            Map<String, DeadlineExtension> oldStudentDeadlines = new HashMap<>();
            Map<String, DeadlineExtension> oldInstructorDeadlines = new HashMap<>();
            for (DeadlineExtension de : prevDeadlineExtensions) {
                if (de.getUser() instanceof Student) {
                    oldStudentDeadlines.put(de.getUser().getEmail(), de);
                } else if (de.getUser() instanceof Instructor) {
                    oldInstructorDeadlines.put(de.getUser().getEmail(), de);
                }
            }

            // check that students and instructors are valid
            // These ensure the existence checks are only done whenever necessary in order to reduce data reads.
            Map<String, Instant> studentDeadlines = updateRequest.getStudentDeadlines();
            boolean hasInvalidStudentEmails = !oldStudentDeadlines.keySet()
                    .containsAll(studentDeadlines.keySet())
                    && sqlLogic.verifyStudentsExistInCourse(courseId, new ArrayList<>(studentDeadlines.keySet()));
            if (hasInvalidStudentEmails) {
                throw new EntityNotFoundException("There are students which do not exist in the course.");
            }
            Map<String, Instant> instructorDeadlines = updateRequest.getInstructorDeadlines();
            boolean hasInvalidInstructorEmails = !oldInstructorDeadlines.keySet()
                    .containsAll(instructorDeadlines.keySet())
                    && sqlLogic.verifyInstructorsExistInCourse(courseId, new ArrayList<>(instructorDeadlines.keySet()));
            if (hasInvalidInstructorEmails) {
                throw new EntityNotFoundException("There are instructors which do not exist in the course.");
            }

            String timeZone = feedbackSession.getCourse().getTimeZone();
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

            // deadline check
            studentDeadlines = studentDeadlines.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                            entry.getValue(), timeZone, true)));
            instructorDeadlines = instructorDeadlines.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                            entry.getValue(), timeZone, true)));

            feedbackSession.setInstructions(updateRequest.getInstructions());
            feedbackSession.setStartTime(startTime);
            feedbackSession.setEndTime(endTime);
            feedbackSession.setGracePeriod(updateRequest.getGracePeriod());
            feedbackSession.setSessionVisibleFromTime(sessionVisibleTime);
            feedbackSession.setResultsVisibleFromTime(resultsVisibleTime);
            feedbackSession.setClosingSoonEmailEnabled(updateRequest.isClosingSoonEmailEnabled());
            feedbackSession.setPublishedEmailEnabled(updateRequest.isPublishedEmailEnabled());
            feedbackSession.setDeadlineExtensions(prevDeadlineExtensions);
            try {
                feedbackSession = sqlLogic.updateFeedbackSession(feedbackSession);
            } catch (InvalidParametersException ipe) {
                throw new InvalidHttpRequestBodyException(ipe);
            } catch (EntityDoesNotExistException ednee) {
                // Entity existence has been verified before, and this exception should not happen
                log.severe("Unexpected error", ednee);
                return new JsonResult(ednee.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }

            boolean notifyAboutDeadlines = getBooleanRequestParamValue(Const.ParamsNames.NOTIFY_ABOUT_DEADLINES);

            List<EmailWrapper> emailsToSend = new ArrayList<>();

            emailsToSend.addAll(processDeadlineExtensions(courseId, feedbackSession,
                    oldStudentDeadlines, studentDeadlines,
                    false, notifyAboutDeadlines));
            emailsToSend.addAll(processDeadlineExtensions(courseId, feedbackSession,
                    oldInstructorDeadlines, instructorDeadlines,
                    true, notifyAboutDeadlines));

            taskQueuer.scheduleEmailsForSending(emailsToSend);

            return new JsonResult(new FeedbackSessionData(feedbackSession));
        } else {
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
                                .withIsClosingSoonEmailEnabled(updateRequest.isClosingSoonEmailEnabled())
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

            emailsToSend.addAll(processDeadlineExtensions(courseId, feedbackSession,
                    oldStudentDeadlines, studentDeadlines,
                    false, notifyAboutDeadlines));
            emailsToSend.addAll(processDeadlineExtensions(courseId, feedbackSession,
                    oldInstructorDeadlines, instructorDeadlines,
                    true, notifyAboutDeadlines));

            taskQueuer.scheduleEmailsForSending(emailsToSend);

            return new JsonResult(new FeedbackSessionData(feedbackSession));
        }
    }

    private List<EmailWrapper> processDeadlineExtensions(String courseId, FeedbackSession session,
            Map<String, DeadlineExtension> oldDeadlines, Map<String, Instant> newDeadlines,
            boolean areInstructors, boolean notifyUsers) {
        // check if same
        Predicate<DeadlineExtension> oldDeadlineNeedsChanges =
                de -> !newDeadlines.containsKey(de.getUser().getEmail())
                || !newDeadlines.get(de.getUser().getEmail()).equals(de.getEndTime());

        boolean hasChanges = newDeadlines.size() > oldDeadlines.size()
                || oldDeadlines.values().stream().anyMatch(oldDeadlineNeedsChanges);
        if (!hasChanges) {
            return Collections.emptyList();
        }
        // Revoke deadline extensions
        Map<String, DeadlineExtension> deadlinesToRevoke = new HashMap<>(oldDeadlines);
        deadlinesToRevoke.keySet().removeAll(newDeadlines.keySet());

        deadlinesToRevoke.values().forEach(de ->
                sqlLogic.deleteDeadlineExtension(de));

        // Create deadline extensions
        Map<String, Instant> deadlinesToCreate = new HashMap<>(newDeadlines);
        deadlinesToCreate.keySet().removeAll(oldDeadlines.keySet());

        deadlinesToCreate.entrySet()
                .stream()
                .map(entry -> {
                    User u = areInstructors
                            ? sqlLogic.getInstructorForEmail(courseId, entry.getKey())
                            : sqlLogic.getStudentForEmail(courseId, entry.getKey());
                    return new DeadlineExtension(u, session, entry.getValue());
                })
                .forEach(deadlineExtension -> {
                    try {
                        sqlLogic.createDeadlineExtension(deadlineExtension);
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

        deadlinesToUpdate
                .entrySet()
                .forEach(entry -> {
                    try {
                        DeadlineExtension deToUpdate = oldDeadlines.get(entry.getKey());
                        deToUpdate.setEndTime(entry.getValue());
                        sqlLogic.updateDeadlineExtension(deToUpdate);
                    } catch (InvalidParametersException | EntityDoesNotExistException e) {
                        log.severe("Unexpected error while updating deadline extension", e);
                    }
                });

        Map<String, Instant> revokedDeadlinesEmailToInstantMap = new HashMap<>();
        deadlinesToRevoke.entrySet().forEach(entry ->
                revokedDeadlinesEmailToInstantMap.put(entry.getKey(), entry.getValue().getEndTime()));

        Map<String, Instant> oldDeadlinesEmailToInstantMap = new HashMap<>();
        oldDeadlines.entrySet().forEach(entry ->
                oldDeadlinesEmailToInstantMap.put(entry.getKey(), entry.getValue().getEndTime()));

        List<EmailWrapper> emailsToSend = new ArrayList<>();
        if (notifyUsers) {
            Course course = sqlLogic.getCourse(courseId);
            emailsToSend.addAll(sqlEmailGenerator
                    .generateDeadlineRevokedEmails(course, session,
                            revokedDeadlinesEmailToInstantMap, areInstructors));
            emailsToSend.addAll(sqlEmailGenerator
                    .generateDeadlineGrantedEmails(course, session, deadlinesToCreate, areInstructors));
            emailsToSend.addAll(sqlEmailGenerator
                    .generateDeadlineUpdatedEmails(course, session, deadlinesToUpdate,
                            oldDeadlinesEmailToInstantMap, areInstructors));
        }
        return emailsToSend;
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
