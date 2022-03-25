package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.ui.request.DeadlineExtensionsRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Task queue worker action: creates, updates or deletes deadline extensions and sends out appropriate emails.
 */
class DeadlineExtensionsWorkerAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        DeadlineExtensionsRequest request = getAndValidateRequestBody(DeadlineExtensionsRequest.class);
        CourseAttributes course = logic.getCourse(request.getCourseId());
        FeedbackSessionAttributes session = logic.getFeedbackSession(request.getFeedbackSessionName(),
                request.getCourseId());

        if (course == null || session == null) {
            log.severe("Course with id: " + request.getCourseId() + " or feedback session: "
                    + request.getFeedbackSessionName() + " not found when updating deadline extensions");
            return new JsonResult("Failure");
        }

        boolean notifyUsers = request.getNotifyUsers();
        List<EmailWrapper> emailsToSend = new ArrayList<>();

        // Process student deadline extensions
        processDeadlineExtensions(course, session, request.getOldStudentDeadlines(), request.getNewStudentDeadlines(),
                false, notifyUsers, emailsToSend);

        // Process instructor deadline extensions
        processDeadlineExtensions(course, session, request.getOldInstructorDeadlines(), request.getNewInstructorDeadlines(),
                true, notifyUsers, emailsToSend);

        taskQueuer.scheduleEmailsForSending(emailsToSend);

        return new JsonResult("Successful");
    }

    private void processDeadlineExtensions(CourseAttributes course, FeedbackSessionAttributes session,
            Map<String, Instant> oldDeadlines, Map<String, Instant> newDeadlines,
            boolean areInstructors, boolean notifyUsers, List<EmailWrapper> emailsToSend) {

        // Revoke deadline extensions
        Map<String, Instant> deadlinesToRevoke = new HashMap<>(oldDeadlines);
        deadlinesToRevoke.keySet().removeAll(newDeadlines.keySet());

        deadlinesToRevoke.keySet().forEach(email ->
                logic.deleteDeadlineExtension(course.getId(), session.getFeedbackSessionName(), email, areInstructors));

        // Create deadline extensions
        Map<String, Instant> deadlinesToCreate = new HashMap<>(newDeadlines);
        deadlinesToCreate.keySet().removeAll(oldDeadlines.keySet());

        deadlinesToCreate.entrySet()
                .stream()
                .map(entry ->
                    DeadlineExtensionAttributes
                            .builder(course.getId(), session.getFeedbackSessionName(), entry.getKey(), areInstructors)
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
                .map(entry -> DeadlineExtensionAttributes.updateOptionsBuilder(
                                    course.getId(), session.getFeedbackSessionName(), entry.getKey(), areInstructors)
                            .withEndTime(entry.getValue())
                            .build())
                .forEach(updateOptions -> {
                    try {
                        logic.updateDeadlineExtension(updateOptions);
                    } catch (InvalidParametersException | EntityDoesNotExistException e) {
                        log.severe("Unexpected error while updating deadline extension", e);
                    }
                });

        if (notifyUsers) {
            emailsToSend.addAll(getDeadlineRevokedEmails(course, session, deadlinesToRevoke, areInstructors));
            emailsToSend.addAll(getDeadlineCreatedEmails(course, session, deadlinesToCreate, areInstructors));
            emailsToSend.addAll(getDeadlineUpdatedEmails(course, session, deadlinesToUpdate, oldDeadlines, areInstructors));
        }

    }

    private List<EmailWrapper> getDeadlineRevokedEmails(CourseAttributes course,
            FeedbackSessionAttributes session, Map<String, Instant> deadlines, boolean areInstructors) {
        return deadlines.entrySet()
                .stream()
                .map(entry ->
                        emailGenerator.generateDeadlineExtensionEmail(course, session, entry.getValue(),
                                session.getEndTime(), EmailType.DEADLINE_EXTENSION_REVOKED, entry.getKey(), areInstructors))
                .collect(Collectors.toList());
    }

    private List<EmailWrapper> getDeadlineCreatedEmails(CourseAttributes course,
            FeedbackSessionAttributes session, Map<String, Instant> deadlines, boolean areInstructors) {
        return deadlines.entrySet()
                .stream()
                .map(entry ->
                        emailGenerator.generateDeadlineExtensionEmail(course, session, session.getEndTime(),
                                entry.getValue(), EmailType.DEADLINE_EXTENSION_GIVEN, entry.getKey(), areInstructors))
                .collect(Collectors.toList());
    }

    private List<EmailWrapper> getDeadlineUpdatedEmails(CourseAttributes course, FeedbackSessionAttributes session,
            Map<String, Instant> deadlines, Map<String, Instant> oldDeadlines, boolean areInstructors) {
        return deadlines.entrySet()
                .stream()
                .map(entry ->
                        emailGenerator.generateDeadlineExtensionEmail(course, session, oldDeadlines.get(entry.getKey()),
                                entry.getValue(), EmailType.DEADLINE_EXTENSION_UPDATED, entry.getKey(), areInstructors))
                .collect(Collectors.toList());
    }

}
