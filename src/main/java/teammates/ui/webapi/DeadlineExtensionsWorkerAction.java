package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import teammates.ui.request.DeadlineExtensionRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Task queue worker action: creates, updates or deletes deadline extensions and sends out appropriate emails.
 */
class DeadlineExtensionsWorkerAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        DeadlineExtensionRequest deadlineExtensionRequest = getAndValidateRequestBody(DeadlineExtensionRequest.class);
        CourseAttributes course = logic.getCourse(deadlineExtensionRequest.getCourseId());
        FeedbackSessionAttributes session = logic.getFeedbackSession(deadlineExtensionRequest.getFeedbackSessionName(),
                deadlineExtensionRequest.getCourseId());

        if (course == null || session == null) {
            log.severe("Course with id: " + deadlineExtensionRequest.getCourseId() + " or feedback session: "
                    + deadlineExtensionRequest.getFeedbackSessionName() + " not found when updating deadline extensions");
            return new JsonResult("Failure");
        }

        boolean notifyUsers = deadlineExtensionRequest.getNotifyUsers();

        createOrUpdateDeadlineExtensions(course, session,
                deadlineExtensionRequest.getStudentExtensionsToModify(), false, notifyUsers);
        createOrUpdateDeadlineExtensions(course, session,
                deadlineExtensionRequest.getInstructorExtensionsToModify(), true, notifyUsers);
        revokeDeadlineExtensions(course, session,
                deadlineExtensionRequest.getStudentExtensionsToRevoke(), false, notifyUsers);
        revokeDeadlineExtensions(course, session,
                deadlineExtensionRequest.getInstructorExtensionsToRevoke(), true, notifyUsers);

        return new JsonResult("Successful");
    }

    private void createOrUpdateDeadlineExtensions(CourseAttributes course, FeedbackSessionAttributes session,
            Map<String, Long> extensionsToModify, boolean isInstructorMap, boolean notifyUsers) {
        List<EmailWrapper> emailsToSend = new ArrayList<>();
        for (var entry : extensionsToModify.entrySet()) {
            String email = entry.getKey();
            Instant endTime = Instant.ofEpochMilli(entry.getValue());
            DeadlineExtensionAttributes deadlineExtension =
                    logic.getDeadlineExtension(course.getId(), session.getFeedbackSessionName(), email, isInstructorMap);
            EmailWrapper emailWrapper;

            if (deadlineExtension == null) {
                emailWrapper = createDeadlineExtension(course, session, email, endTime, isInstructorMap, notifyUsers);
            } else {
                emailWrapper = updateDeadlineExtension(course, session, email,
                        deadlineExtension.getEndTime(), endTime, isInstructorMap, notifyUsers);
            }

            if (emailWrapper != null) {
                emailsToSend.add(emailWrapper);
            }
        }
        taskQueuer.scheduleEmailsForSending(emailsToSend);
    }

    private void revokeDeadlineExtensions(CourseAttributes course, FeedbackSessionAttributes session,
            List<String> extensionsToRevoke, boolean isInstructor, boolean notifyUsers) {
        List<EmailWrapper> emailsToSend = extensionsToRevoke
                .stream()
                .map(email -> revokeDeadlineExtension(course, session, email, isInstructor, notifyUsers))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        taskQueuer.scheduleEmailsForSending(emailsToSend);
    }

    private EmailWrapper createDeadlineExtension(CourseAttributes course, FeedbackSessionAttributes session,
            String userEmail, Instant endTime, boolean isInstructor, boolean notifyUsers) {
        DeadlineExtensionAttributes deadlineExtension = DeadlineExtensionAttributes
                .builder(course.getId(), session.getFeedbackSessionName(), userEmail, isInstructor)
                .withEndTime(endTime)
                .build();

        try {
            logic.createDeadlineExtension(deadlineExtension);
        } catch (InvalidParametersException | EntityAlreadyExistsException e) {
            log.severe("Unexpected error while creating deadline extension", e);
            return null;
        }

        if (!notifyUsers) {
            return null;
        }

        return emailGenerator.generateDeadlineExtensionEmail(course, session, session.getEndTime(), endTime,
                EmailType.DEADLINE_EXTENSION_GIVEN, userEmail, isInstructor);
    }

    private EmailWrapper updateDeadlineExtension(
            CourseAttributes course, FeedbackSessionAttributes session, String userEmail, Instant oldEndTime,
            Instant endTime, boolean isInstructor, boolean notifyUsers) {
        DeadlineExtensionAttributes.UpdateOptions updateOptions =
                DeadlineExtensionAttributes.updateOptionsBuilder(
                        course.getId(), session.getFeedbackSessionName(), userEmail, isInstructor)
                        .withEndTime(endTime)
                        .build();

        try {
            logic.updateDeadlineExtension(updateOptions);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            log.severe("Unexpected error while updating deadline extension", e);
            return null;
        }

        if (!notifyUsers) {
            return null;
        }

        return emailGenerator.generateDeadlineExtensionEmail(course, session, oldEndTime, endTime,
                EmailType.DEADLINE_EXTENSION_UPDATED, userEmail, isInstructor);
    }

    private EmailWrapper revokeDeadlineExtension(CourseAttributes course, FeedbackSessionAttributes session,
            String userEmail, boolean isInstructor, boolean notifyUsers) {
        DeadlineExtensionAttributes deadlineExtension =
                logic.getDeadlineExtension(course.getId(), session.getFeedbackSessionName(), userEmail, isInstructor);
        logic.deleteDeadlineExtension(course.getId(), session.getFeedbackSessionName(), userEmail, isInstructor);

        if (deadlineExtension == null) {
            log.severe("Unexpected error while revoking deadline extension");
            return null;
        }

        if (!notifyUsers) {
            return null;
        }

        return emailGenerator.generateDeadlineExtensionEmail(course, session, deadlineExtension.getEndTime(),
                session.getEndTime(), EmailType.DEADLINE_EXTENSION_REVOKED, userEmail, isInstructor);
    }

}
