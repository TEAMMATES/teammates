package teammates.client.scripts;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;

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
public class DataMigrationForSessions extends DataMigrationBaseScript<Key<FeedbackSession>> {

    public static void main(String[] args) throws IOException {
        new DataMigrationForSessions().doOperationRemotely();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isPreview() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Key<FeedbackSession>> getEntities() {
        return ofy().load().type(FeedbackSession.class).keys().list();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void printPreviewInformation(Key<FeedbackSession> sessionKey) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void migrate(Key<FeedbackSession> sessionKey) {
        ofy().transact(new VoidWork() {
            @Override
            public void vrun() {
                FeedbackSession session = ofy().load().key(sessionKey).now();
                ofy().save().entity(session).now();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postAction() {
        // nothing to do
    }
}
