package teammates.ui.webapi;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
}
