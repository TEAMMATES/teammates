package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.storage.api.FeedbackResponseCommentsDb;

/**
 * Script to set commentGiverType as INSTRUCTOR in all comments by instructor.
 *
 * <p>See issue #8830</p>
 */

public class DataMigrationForInstructorFeedbackResponseComments extends
        DataMigrationForEntities<FeedbackResponseCommentAttributes> {

    private FeedbackResponseCommentsDb commentsDb = new FeedbackResponseCommentsDb();

    public static void main(String[] args) throws IOException {
        DataMigrationForInstructorFeedbackResponseComments migrator =
                new DataMigrationForInstructorFeedbackResponseComments();
        migrator.doOperationRemotely();
    }

    /**
     * If true, the script will not perform actual data migration.
     */
    @Override
    protected boolean isPreview() {
        return true;
    }

    /**
     * Gets all the entities that are to be filtered for migration.
     */
    @Override
    protected List<FeedbackResponseCommentAttributes> getEntities() {
        return commentsDb.getAllFeedbackResponseComments();
    }

    /**
     * Checks whether data migration is needed.
     */
    @Override
    protected boolean isMigrationNeeded(FeedbackResponseCommentAttributes entity) {
        return entity.commentGiverType == null;
    }

    /**
     * Prints information necessary for previewing the migration process.
     */
    @Override
    protected void printPreviewInformation(FeedbackResponseCommentAttributes entity) {
        // nothing to do
    }

    /**
     * Migrates the entity.
     */
    @Override
    protected void migrate(FeedbackResponseCommentAttributes entity) {
        entity.commentGiverType = FeedbackParticipantType.INSTRUCTORS;
        entity.isCommentFromFeedbackParticipant = false;
    }

    @Override
    protected void postAction() {
        // nothing to do
    }
}
