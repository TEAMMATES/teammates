package teammates.client.scripts;

import java.io.IOException;
import java.lang.reflect.Field;

import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.storage.entity.FeedbackResponseComment;

/**
 * Script to set commentGiverType as INSTRUCTOR in all comments by instructor.
 *
 * <p>See issue #8830</p>
 */

public class DataMigrationForInstructorFeedbackResponseComments extends
        DataMigrationEntitiesBaseScript<FeedbackResponseComment> {

    public static void main(String[] args) throws IOException {
        new DataMigrationForInstructorFeedbackResponseComments().doOperationRemotely();
    }

    @Override
    protected Query<FeedbackResponseComment> getFilterQuery() {
        return ofy().load().type(FeedbackResponseComment.class);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(FeedbackResponseComment comment) {
        try {
            Field commentGiverType = comment.getClass().getDeclaredField("commentGiverType");
            commentGiverType.setAccessible(true);
            return commentGiverType.get(comment) == null;
        } catch (ReflectiveOperationException e) {
            return true;
        }
    }

    @Override
    protected void migrateEntity(FeedbackResponseComment comment) {
        comment.setCommentGiverType(FeedbackParticipantType.INSTRUCTORS);
        comment.setIsCommentFromFeedbackParticipant(false);

        saveEntityDeferred(comment);
    }
}
