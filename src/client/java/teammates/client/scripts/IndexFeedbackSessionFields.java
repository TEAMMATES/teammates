package teammates.client.scripts;

import com.googlecode.objectify.cmd.Query;

import teammates.storage.entity.FeedbackSession;

/**
 * Index the newly-indexable fields of feedback sessions.
 */
public class IndexFeedbackSessionFields extends DataMigrationEntitiesBaseScript<FeedbackSession> {

    public static void main(String[] args) {
        new IndexFeedbackSessionFields().doOperationRemotely();
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
    protected boolean isMigrationNeeded(FeedbackSession session) {
        return true;
    }

    @Override
    protected void migrateEntity(FeedbackSession session) {
        // Save without any update; this will build the previously non-existing indexes
        saveEntityDeferred(session);
    }
}
