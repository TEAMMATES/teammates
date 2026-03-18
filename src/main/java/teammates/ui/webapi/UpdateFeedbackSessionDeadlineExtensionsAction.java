package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.User;
import teammates.ui.output.FeedbackSessionDeadlineExtensionsData;
import teammates.ui.request.FeedbackSessionDeadlineExtensionsUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates the deadline extensions for a feedback session.
 */
public class UpdateFeedbackSessionDeadlineExtensionsAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        gateKeeper.verifyAccessible(
                sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId()),
                feedbackSession,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
        assert feedbackSession != null;

        FeedbackSessionDeadlineExtensionsUpdateRequest updateRequest =
                getAndValidateRequestBody(FeedbackSessionDeadlineExtensionsUpdateRequest.class);

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

        Map<String, Instant> studentDeadlines = updateRequest.getStudentDeadlines();
        boolean hasInvalidStudentEmails = !oldStudentDeadlines.keySet()
                .containsAll(studentDeadlines.keySet())
                && !sqlLogic.verifyStudentsExistInCourse(courseId, new ArrayList<>(studentDeadlines.keySet()));
        if (hasInvalidStudentEmails) {
            throw new EntityNotFoundException("There are students which do not exist in the course.");
        }

        Map<String, Instant> instructorDeadlines = updateRequest.getInstructorDeadlines();
        boolean hasInvalidInstructorEmails = !oldInstructorDeadlines.keySet()
                .containsAll(instructorDeadlines.keySet())
                && !sqlLogic.verifyInstructorsExistInCourse(courseId, new ArrayList<>(instructorDeadlines.keySet()));
        if (hasInvalidInstructorEmails) {
            throw new EntityNotFoundException("There are instructors which do not exist in the course.");
        }

        String timeZone = feedbackSession.getCourse().getTimeZone();

        studentDeadlines = studentDeadlines.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry ->
                        teammates.common.util.TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                                entry.getValue(), timeZone, true)));
        instructorDeadlines = instructorDeadlines.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry ->
                        teammates.common.util.TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                                entry.getValue(), timeZone, true)));

        boolean notifyAboutDeadlines = getBooleanRequestParamValue(Const.ParamsNames.NOTIFY_ABOUT_DEADLINES);

        List<EmailWrapper> emailsToSend = new ArrayList<>();

        emailsToSend.addAll(processDeadlineExtensions(courseId, feedbackSession,
                oldStudentDeadlines, studentDeadlines,
                false, notifyAboutDeadlines));
        emailsToSend.addAll(processDeadlineExtensions(courseId, feedbackSession,
                oldInstructorDeadlines, instructorDeadlines,
                true, notifyAboutDeadlines));

        taskQueuer.scheduleEmailsForSending(emailsToSend);

        // Refresh to get updated deadline extensions
        feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
        List<DeadlineExtension> deadlineExtensions = feedbackSession.getDeadlineExtensions();
        String updatedTimeZone = feedbackSession.getCourse().getTimeZone();
        return new JsonResult(new FeedbackSessionDeadlineExtensionsData(updatedTimeZone, deadlineExtensions));
    }

    private List<EmailWrapper> processDeadlineExtensions(String courseId, FeedbackSession session,
            Map<String, DeadlineExtension> oldDeadlines, Map<String, Instant> newDeadlines,
            boolean areInstructors, boolean notifyUsers) {
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
                        && !entry.getValue().equals(oldDeadlines.get(entry.getKey()).getEndTime()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        deadlinesToUpdate.entrySet().forEach(entry -> {
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
            teammates.storage.sqlentity.Course course = sqlLogic.getCourse(courseId);
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
}
