package teammates.client.scripts.sql;

import java.time.Duration;

// CHECKSTYLE.OFF:ImportOrder
import com.googlecode.objectify.cmd.Query;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.sqlentity.Course;

// CHECKSTYLE.ON:ImportOrder
/**
 * Data migration class for feddback sessions.
 */
@SuppressWarnings("PMD")
public class DataMigrationForFeedbackSessionSql
        extends DataMigrationEntitiesBaseScriptSql<FeedbackSession, teammates.storage.sqlentity.FeedbackSession> {

    public static void main(String[] args) {
        new DataMigrationForFeedbackSessionSql().doOperationRemotely();
    }

    @Override
    protected Query<FeedbackSession> getFilterQuery() {
        return ofy().load().type(teammates.storage.entity.FeedbackSession.class);
    }

    @Override
    protected boolean isPreview() {
        return false;
    }

    @Override
    protected void setMigrationCriteria() {
        // No migration criteria currently needed.
    }

    @Override
    protected boolean isMigrationNeeded(FeedbackSession entity) {
        return true;
    }

    @Override
    protected void migrateEntity(FeedbackSession oldEntity) throws Exception {
        HibernateUtil.beginTransaction();
        Course course = HibernateUtil.get(teammates.storage.sqlentity.Course.class, oldEntity.getCourseId());
        HibernateUtil.commitTransaction();

        teammates.storage.sqlentity.FeedbackSession newFeedbackSession = new teammates.storage.sqlentity.FeedbackSession(
                oldEntity.getFeedbackSessionName(),
                course,
                oldEntity.getCreatorEmail(),
                oldEntity.getInstructions(),
                oldEntity.getStartTime(),
                oldEntity.getEndTime(),
                oldEntity.getSessionVisibleFromTime(),
                oldEntity.getResultsVisibleFromTime(),
                Duration.ofMinutes(oldEntity.getGracePeriod()),
                oldEntity.isOpeningEmailEnabled(),
                oldEntity.isClosingEmailEnabled(),
                oldEntity.isPublishedEmailEnabled()
        );

        newFeedbackSession.setClosedEmailSent(oldEntity.isSentClosedEmail());
        newFeedbackSession.setClosingSoonEmailSent(oldEntity.isSentClosingEmail());
        newFeedbackSession.setOpenEmailSent(oldEntity.isSentOpenEmail());
        newFeedbackSession.setOpeningSoonEmailSent(oldEntity.isSentOpeningSoonEmail());
        newFeedbackSession.setPublishedEmailSent(oldEntity.isSentPublishedEmail());
        newFeedbackSession.setDeletedAt(oldEntity.getDeletedTime());

        saveEntityDeferred(newFeedbackSession);
    }

}
