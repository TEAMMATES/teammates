package teammates.client.scripts;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.PutException;
import com.google.appengine.api.search.SearchServiceFactory;

import teammates.client.scripts.util.LoopHelper;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.util.Const;
import teammates.common.util.retry.MaximumRetriesExceededException;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.search.FeedbackResponseCommentSearchDocument;
import teammates.storage.search.SearchManager;

/**
 * Describes common operations for data migration for FeedbackResponseCommentSearchDocument class.
 */
public abstract class DataMigrationForFeedbackResponseCommentSearchDocument
        extends DataMigrationForEntities<FeedbackResponseCommentAttributes> {

    protected static final Index index = SearchServiceFactory.getSearchService()
            .getIndex(IndexSpec.newBuilder().setName(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT));

    /**
     * Number of comments/documents to process per cycle.
     * Maximum documents that can be updated in one request is 200 (limit imposed by GAE Search API).
     */
    private static final int BATCH_SIZE = 200;

    private List<FeedbackResponseCommentAttributes> commentsToMigrate = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    protected List<FeedbackResponseCommentAttributes> getEntities() {
        return new FeedbackResponseCommentsDb().getAllFeedbackResponseComments();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void migrate(FeedbackResponseCommentAttributes comment) {
        commentsToMigrate.add(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postAction() {
        if (isPreview()) {
            return;
        }

        LoopHelper loopHelper = new LoopHelper(BATCH_SIZE, "documents processed.");

        List<Document> documentsToUpdate = new ArrayList<>();

        for (FeedbackResponseCommentAttributes comment : commentsToMigrate) {
            loopHelper.recordLoop();
            documentsToUpdate.add(new FeedbackResponseCommentSearchDocument(comment).build());

            if (documentsToUpdate.size() == BATCH_SIZE) {
                updateAndClearDocuments(documentsToUpdate);
            }
        }
        updateAndClearDocuments(documentsToUpdate);

    }

    private void updateAndClearDocuments(List<Document> documentsToUpdate) {
        if (documentsToUpdate.isEmpty()) {
            return;
        }

        println("Batch updating " + documentsToUpdate.size() + " documents...");

        try {
            invokePutDocumentsWithRetry(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT, documentsToUpdate);
            println("Batch update succeeded.");
        } catch (PutException e) {
            println("Batch update failed with non-transient errors.");
        } catch (MaximumRetriesExceededException e) {
            println("Batch update failed after maximum retries.");
        }

        documentsToUpdate.clear();
    }

    /**
     * Reflects private static method {@link SearchManager#putDocumentsWithRetry}.
     *
     * @throws PutException when only non-transient errors are encountered.
     * @throws MaximumRetriesExceededException with list of failed {@link Document}s as final data and
     *         final {@link com.google.appengine.api.search.OperationResult}'s message as final message,
     *         if operation fails after maximum retries.
     */
    private static void invokePutDocumentsWithRetry(String indexName, List<Document> documents)
            throws PutException, MaximumRetriesExceededException {
        try {
            Method method = SearchManager.class.getDeclaredMethod("putDocumentsWithRetry", String.class, List.class);
            method.setAccessible(true);
            method.invoke(null, indexName, documents);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            Throwable originalException = e.getCause();
            if (originalException instanceof PutException) {
                throw (PutException) originalException;
            } else if (originalException instanceof MaximumRetriesExceededException) {
                throw (MaximumRetriesExceededException) originalException;
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
