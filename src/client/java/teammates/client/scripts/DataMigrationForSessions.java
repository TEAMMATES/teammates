package teammates.client.scripts;

import java.io.IOException;
import java.lang.reflect.Field;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import teammates.storage.entity.FeedbackSession;

/**
 * Script to load/save all {@link FeedbackSession} entities.
 *
 * <p>The actual load/save operation happens in a transaction to prevent possible inconsistencies arising from concurrent
 * modification of the entity, e.g. if a student submits or an instructor modifies the session in the midst of the load/save.
 * </p>
 *
 * <p>As a result of loading and saving entities, these changes will be made:
 * <ul>
 *     <li>Conversion of all time fields to UTC</li>
 *     <li>Conversion of the timeZone field to a ZoneId string following the course time zone;
 *         removal of the timeZoneDouble field</li>
 *     <li>Population of any missing is*EmailEnabled booleans to default value true</li>
 *     <li>Adjustment of the results visible from time TIME_REPRESENTS_NEVER -> TIME_REPRESENTS_LATER</li>
 * </ul>
 * </p>
 */
public class DataMigrationForSessions extends DataMigrationWithCheckpointForEntities<FeedbackSession> {

    public DataMigrationForSessions() {
        super();
        numberOfScannedKey.set(0L);
        numberOfAffectedEntities.set(0L);
        numberOfUpdatedEntities.set(0L);
    }

    public static void main(String[] args) throws IOException {
        new DataMigrationForSessions().doOperationRemotely();
    }

    @Override
    protected Query<FeedbackSession> getFilterQuery() {
        return ofy().load().type(FeedbackSession.class);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected String getLastPositionOfCursor() {
        return "";
    }

    @Override
    protected int getCursorInformationPrintCycle() {
        return 100;
    }

    @Override
    protected boolean isMigrationNeeded(Key<FeedbackSession> sessionKey) {
        FeedbackSession session = ofy().load().key(sessionKey).now();
        try {
            Field followingCourseTimeZoneField = session.getClass().getDeclaredField("wasFollowingCourseTimeZone");
            followingCourseTimeZoneField.setAccessible(true);
            return !followingCourseTimeZoneField.getBoolean(session);
        } catch (ReflectiveOperationException e) {
            return true;
        }
    }

    @Override
    protected void migrateEntity(Key<FeedbackSession> sessionKey) {
        // simply load then save the entity and let @OnLoad do the migration
        FeedbackSession session = ofy().load().key(sessionKey).now();
        ofy().save().entity(session).now();
    }
}
