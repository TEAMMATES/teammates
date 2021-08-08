package teammates.client.scripts;

import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.storage.entity.FeedbackResponseComment;

/**
 * Script to set commentGiverType as INSTRUCTOR in all comments by instructor.
 *
 * <p>See issue #9083</p>
 */
public class DataMigrationForNullCommentGiverType extends
        DataMigrationEntitiesBaseScript<FeedbackResponseComment> {

    public static void main(String[] args) {
        new DataMigrationForNullCommentGiverType().doOperationRemotely();
    }

    @Override
    protected Query<FeedbackResponseComment> getFilterQuery() {
        return ofy().load().type(FeedbackResponseComment.class)
                .filter("commentGiverType =", null);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(FeedbackResponseComment comment) {
        return comment.getCommentGiverType() == null;
    }

    @Override
    protected void migrateEntity(FeedbackResponseComment comment) {
        comment.setCommentGiverType(FeedbackParticipantType.INSTRUCTORS);
        comment.setIsCommentFromFeedbackParticipant(false);

        saveEntityDeferred(comment);
    }

}
