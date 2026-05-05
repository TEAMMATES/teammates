package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.storage.entity.Course;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.DeadlineExtensionsData;
import teammates.ui.request.DeadlineExtensionsUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Updates the deadline extensions for a feedback session.
 */
public class UpdateDeadlineExtensionsAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        gateKeeper.verifyAccessible(
                logic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId()),
                feedbackSession,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        DeadlineExtensionsUpdateRequest updateRequest =
                getAndValidateRequestBody(DeadlineExtensionsUpdateRequest.class);

        Set<DeadlineExtension> prevDeadlineExtensions = feedbackSession.getDeadlineExtensions();
        String courseId = feedbackSession.getCourseId();

        // Use userIds to map to users to avoid querying the db for each user multiple times during processing later on
        Map<UUID, Student> studentsByUserId = logic.getStudentsForCourse(courseId).stream()
                .collect(Collectors.toMap(Student::getId, s -> s));
        Map<UUID, Instructor> instructorsByUserId = logic.getInstructorsByCourse(courseId).stream()
                .collect(Collectors.toMap(Instructor::getId, i -> i));

        Map<String, DeadlineExtension> oldStudentDeadlines = prevDeadlineExtensions.stream()
                .filter(de -> studentsByUserId.containsKey(de.getUserId()))
                .collect(Collectors.toMap(
                        de -> studentsByUserId.get(de.getUserId()).getEmail(), de -> de));
        Map<String, DeadlineExtension> oldInstructorDeadlines = prevDeadlineExtensions.stream()
                .filter(de -> instructorsByUserId.containsKey(de.getUserId()))
                .collect(Collectors.toMap(
                        de -> instructorsByUserId.get(de.getUserId()).getEmail(), de -> de));

        Map<String, Instant> studentDeadlines = updateRequest.getStudentDeadlines();
        Set<String> allStudentEmails = studentsByUserId.values().stream()
                .map(Student::getEmail).collect(Collectors.toSet());
        boolean hasInvalidStudentEmails = !allStudentEmails.containsAll(studentDeadlines.keySet());
        if (hasInvalidStudentEmails) {
            throw new EntityNotFoundException("There are students which do not exist in the course.");
        }

        Map<String, Instant> instructorDeadlines = updateRequest.getInstructorDeadlines();
        Set<String> allInstructorEmails = instructorsByUserId.values().stream()
                .map(Instructor::getEmail).collect(Collectors.toSet());
        boolean hasInvalidInstructorEmails = !allInstructorEmails.containsAll(instructorDeadlines.keySet());
        if (hasInvalidInstructorEmails) {
            throw new EntityNotFoundException("There are instructors which do not exist in the course.");
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

        // Refresh to get updated deadline extensions
        feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        Set<DeadlineExtension> deadlineExtensions = feedbackSession.getDeadlineExtensions();
        DeadlineExtensionsData responseData = new DeadlineExtensionsData(
                deadlineExtensions, studentsByUserId, instructorsByUserId);

        return new JsonResult(responseData);
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
                logic.deleteDeadlineExtension(de));
        session.getDeadlineExtensions().removeAll(deadlinesToRevoke.values());

        // Create deadline extensions
        Map<String, Instant> deadlinesToCreate = new HashMap<>(newDeadlines);
        deadlinesToCreate.keySet().removeAll(oldDeadlines.keySet());

        deadlinesToCreate.entrySet()
                .stream()
                .map(entry -> {
                    User u = areInstructors
                            ? logic.getInstructorForEmail(courseId, entry.getKey())
                            : logic.getStudentForEmail(courseId, entry.getKey());
                    DeadlineExtension newDeadlineExtension = new DeadlineExtension(u, entry.getValue());
                    session.addDeadlineExtension(newDeadlineExtension);
                    return newDeadlineExtension;
                })
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
                        && !entry.getValue().equals(oldDeadlines.get(entry.getKey()).getEndTime()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        deadlinesToUpdate.entrySet().forEach(entry -> {
            try {
                DeadlineExtension deToUpdate = oldDeadlines.get(entry.getKey());
                deToUpdate.setEndTime(entry.getValue());
                logic.updateDeadlineExtension(deToUpdate);
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
            Course course = logic.getCourse(courseId);
            emailsToSend.addAll(emailGenerator
                    .generateDeadlineRevokedEmails(course, session,
                            revokedDeadlinesEmailToInstantMap, areInstructors));
            emailsToSend.addAll(emailGenerator
                    .generateDeadlineGrantedEmails(course, session, deadlinesToCreate, areInstructors));
            emailsToSend.addAll(emailGenerator
                    .generateDeadlineUpdatedEmails(course, session, deadlinesToUpdate,
                            oldDeadlinesEmailToInstantMap, areInstructors));
        }
        return emailsToSend;
    }
}
