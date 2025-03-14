package teammates.ui.webapi;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.common.util.RequestTracer;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * Cron job: schedules feedback session closing soon emails to be sent.
 */
public class FeedbackSessionClosingSoonRemindersAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() {
        executeForDatastoreFeedbackSessions();

        List<FeedbackSession> sessions = sqlLogic.getFeedbackSessionsClosingWithinTimeLimit();

        for (FeedbackSession session : sessions) {
            RequestTracer.checkRemainingTime();
            List<EmailWrapper> emailsToBeSent = sqlEmailGenerator.generateFeedbackSessionClosingSoonEmails(session);
            try {
                taskQueuer.scheduleEmailsForSending(emailsToBeSent);
                session.setClosingSoonEmailSent(true);
            } catch (Exception e) {
                log.severe("Unexpected error", e);
            }
        }

        executeForDatastoreExtendedDeadlines();

        // Group deadline extensions by feedback sessions
        Collection<List<DeadlineExtension>> groupedDeadlineExtensions =
                sqlLogic.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail()
                    .stream()
                    .collect(Collectors.groupingBy(de -> de.getFeedbackSession()))
                    .values();

        for (var deadlineExtensions : groupedDeadlineExtensions) {
            RequestTracer.checkRemainingTime();

            FeedbackSession session = deadlineExtensions.get(0).getFeedbackSession();
            if (!session.isClosingSoonEmailEnabled()) {
                continue;
            }

            List<EmailWrapper> emailsToBeSent = sqlEmailGenerator
                    .generateFeedbackSessionClosingWithExtensionEmails(session, deadlineExtensions);
            taskQueuer.scheduleEmailsForSending(emailsToBeSent);

            for (var de : deadlineExtensions) {
                de.setClosingSoonEmailSent(true);
            }
        }

        return new JsonResult("Successful");
    }

    private void executeForDatastoreFeedbackSessions() {
        List<FeedbackSessionAttributes> sessions = logic.getFeedbackSessionsClosingWithinTimeLimit();

        for (FeedbackSessionAttributes session : sessions) {
            if (isCourseMigrated(session.getCourseId())) {
                continue;
            }

            RequestTracer.checkRemainingTime();
            List<EmailWrapper> emailsToBeSent = emailGenerator.generateFeedbackSessionClosingSoonEmails(session);
            try {
                taskQueuer.scheduleEmailsForSending(emailsToBeSent);
                logic.updateFeedbackSession(
                        FeedbackSessionAttributes
                                .updateOptionsBuilder(session.getFeedbackSessionName(), session.getCourseId())
                                .withSentClosingSoonEmail(true)
                                .build());
            } catch (Exception e) {
                log.severe("Unexpected error", e);
            }
        }
    }

    private void executeForDatastoreExtendedDeadlines() {
        // group deadline extensions by courseId and feedbackSessionName
        Collection<List<DeadlineExtensionAttributes>> groupedDeadlineExtensionsAttributes =
                logic.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail()
                        .stream()
                        .collect(Collectors.groupingBy(de -> de.getCourseId() + "%" + de.getFeedbackSessionName()))
                        .values();

        for (var deadlineExtensions : groupedDeadlineExtensionsAttributes) {
            String courseId = deadlineExtensions.get(0).getCourseId();
            if (isCourseMigrated(courseId)) {
                continue;
            }

            RequestTracer.checkRemainingTime();
            String feedbackSessionName = deadlineExtensions.get(0).getFeedbackSessionName();
            FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);
            if (feedbackSession == null || !feedbackSession.isClosingSoonEmailEnabled()) {
                continue;
            }

            List<DeadlineExtensionAttributes> validDeadlineExtensions =
                    filterValidDeadlineExtensions(deadlineExtensions, feedbackSession);
            List<EmailWrapper> emailsToBeSent = emailGenerator
                    .generateFeedbackSessionClosingWithExtensionEmails(feedbackSession, validDeadlineExtensions);
            taskQueuer.scheduleEmailsForSending(emailsToBeSent);

            try {
                for (var deadlineExtension : validDeadlineExtensions) {
                    DeadlineExtensionAttributes.UpdateOptions updateOptions = DeadlineExtensionAttributes
                            .updateOptionsBuilder(courseId, feedbackSessionName,
                                    deadlineExtension.getUserEmail(), deadlineExtension.getIsInstructor())
                            .withSentClosingSoonEmail(true)
                            .build();
                    logic.updateDeadlineExtension(updateOptions);
                }
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                log.severe("Unexpected error", e);
            }
        }
    }

    /**
     * Remove invalid deadline extensions from the given {@code deadlineExtensions}.
     *
     * <p>Deadline Extensions may not be synced up with the deadlines in feedback session.
     * Treat deadlines in feedback session as the single source of truth and verify their existence before sending emails.
     */
    private List<DeadlineExtensionAttributes> filterValidDeadlineExtensions(
            List<DeadlineExtensionAttributes> deadlineExtensions, FeedbackSessionAttributes session) {
        Map<String, Instant> studentDeadlines = session.getStudentDeadlines();
        Map<String, Instant> instructorDeadlines = session.getInstructorDeadlines();

        return deadlineExtensions.stream()
                .filter(de ->
                    de.getIsInstructor() && isValidDeadlineExtension(de, instructorDeadlines)
                            || !de.getIsInstructor() && isValidDeadlineExtension(de, studentDeadlines))
                .collect(Collectors.toList());
    }

    private boolean isValidDeadlineExtension(DeadlineExtensionAttributes deadlineExtension,
            Map<String, Instant> actualDeadlines) {
        return deadlineExtension.getEndTime().equals(actualDeadlines.get(deadlineExtension.getUserEmail()));
    }

}
