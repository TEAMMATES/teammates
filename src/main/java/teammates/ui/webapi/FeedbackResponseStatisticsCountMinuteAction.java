package teammates.ui.webapi;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import com.googlecode.objectify.ObjectifyService;

import teammates.common.util.Logger;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseStatisticsMinute;

/**
 *  Cron job: schedules feedback statistics count every minute.
 */
public class FeedbackResponseStatisticsCountMinuteAction extends AdminOnlyAction {
    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() {
        ZoneOffset currentOffset = OffsetDateTime.now().getOffset();
        Instant intervalEndTime = LocalDateTime.now()
                            .truncatedTo(ChronoUnit.SECONDS)
                            .withSecond(0)
                            .toInstant(currentOffset);

        Instant intervalRepresentativeTime = intervalEndTime.minusSeconds(30);
        Instant intervalStartTime = intervalEndTime.minusSeconds(30).minusMillis(1);
        try {
            int count = ObjectifyService.ofy().load()
                    .type(FeedbackResponse.class)
                    .project("createdAt")
                    .filter("createdAt >", intervalStartTime)
                    .filter("createdAt <", intervalEndTime)
                    .list()
                    .size();

            FeedbackResponseStatisticsMinute newEntry = new FeedbackResponseStatisticsMinute(
                    intervalRepresentativeTime.toString(), count);
            ObjectifyService.ofy().save().entities(newEntry).now();
        } catch (Exception e) {
            log.severe("Unexpected error", e);
        }
        return new JsonResult("Successful");
    }
}
