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

import teammates.client.remoteapi.RemoteApiClient;
import teammates.client.scripts.util.LoopHelper;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.util.Const;
import teammates.common.util.retry.MaximumRetriesExceededException;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.storage.search.FeedbackResponseCommentSearchDocument;
import teammates.storage.search.SearchManager;

/**
 * Describes common operations for data migration for FeedbackResponseCommentSearchDocument class.
 */
public abstract class DataMigrationForFeedbackResponseCommentSearchDocument extends RemoteApiClient {

    /**
     * Number of comments/documents to process per cycle.
     * Maximum documents that can be updated in one request is 200 (limit imposed by GAE Search API).
     */
    private static final int BATCH_SIZE = 200;

    private static final Index index = SearchServiceFactory.getSearchService()
            .getIndex(IndexSpec.newBuilder().setName(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT));

    /**
     * Will not perform updates on the datastore if true.
     */
    protected abstract boolean isPreview();

    @Override
    protected void doOperation() {
        List<FeedbackResponseCommentAttributes> commentsToFix = analyzeAndGetAffectedComments();
        fixDocuments(commentsToFix);
    }

    private List<FeedbackResponseCommentAttributes> analyzeAndGetAffectedComments() {
        println("Analyzing comments and their search documents...");

        List<FeedbackResponseCommentAttributes> allComments =
                FeedbackResponseCommentsLogic.inst().getAllFeedbackResponseComments();

        List<FeedbackResponseCommentAttributes> commentsToFix = new ArrayList<>();

        int numberOfDocuments = 0;
        LoopHelper loopHelper = new LoopHelper(BATCH_SIZE, "comments analyzed.");

        for (FeedbackResponseCommentAttributes comment : allComments) {
            loopHelper.recordLoop();

            Document document = getDocument(comment);
            if (document == null) {
                continue;
            }

            numberOfDocuments++;

            if (isFixRequired(comment, document)) {
                commentsToFix.add(comment);
            }
        }

        println("\n################# Analysis Results #################");
        println("Total number of comments: " + loopHelper.getCount());
        println("Total number of documents: " + numberOfDocuments + "\n");

        displayAnalysisResults();

        println("####################################################\n");

        return commentsToFix;
    }

    /**
     * Check is fix required for the given comment and corresponding document.
     * @param comment comment to check
     * @param document document to check
     * @return {@code true} if fix required otherwise {@code false}
     */
    protected abstract boolean isFixRequired(FeedbackResponseCommentAttributes comment, Document document);

    /**
     * Display results of analysis for data migration.
     */
    protected abstract void displayAnalysisResults();

    private Document getDocument(FeedbackResponseCommentAttributes comment) {
        return index.get(comment.getId().toString());
    }

    private void fixDocuments(List<FeedbackResponseCommentAttributes> commentsToFix) {
        println("Running data migration for " + commentsToFix.size() + " invalid documents");
        println("Preview: " + isPreview());

        List<Document> documentsToUpdate = new ArrayList<>();

        LoopHelper loopHelper = new LoopHelper(BATCH_SIZE, "documents processed.");

        for (FeedbackResponseCommentAttributes commentToFix : commentsToFix) {
            loopHelper.recordLoop();
            queueDocumentUpdate(commentToFix, documentsToUpdate);

            if (documentsToUpdate.size() == BATCH_SIZE) {
                updateAndClearDocuments(documentsToUpdate);
            }
        }
        updateAndClearDocuments(documentsToUpdate);

        println("\nComplete! If there are any failures shown above, please rerun this script.");
    }

    private void queueDocumentUpdate(FeedbackResponseCommentAttributes comment, List<Document> documentsToUpdate) {
        documentsToUpdate.add(new FeedbackResponseCommentSearchDocument(comment).build());
    }

    private void updateAndClearDocuments(List<Document> documentsToUpdate) {
        if (documentsToUpdate.isEmpty()) {
            return;
        }

        println("Batch updating " + documentsToUpdate.size() + " documents...");

        if (!isPreview()) {
            try {
                invokePutDocumentsWithRetry(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT, documentsToUpdate);
                println("Batch update succeeded.");
            } catch (PutException e) {
                println("Batch update failed with non-transient errors.");
            } catch (MaximumRetriesExceededException e) {
                println("Batch update failed after maximum retries.");
            }
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
