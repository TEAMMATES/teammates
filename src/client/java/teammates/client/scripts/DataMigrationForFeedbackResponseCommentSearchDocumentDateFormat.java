package teammates.client.scripts;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.PutException;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.gson.JsonParser;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.client.scripts.util.LoopHelper;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.util.Const;
import teammates.common.util.retry.MaximumRetriesExceededException;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.storage.search.FeedbackResponseCommentSearchDocument;
import teammates.storage.search.SearchManager;

/**
 * Script to fix the date format in old {@link FeedbackResponseCommentSearchDocument}s created before V5.93.
 *
 * <p>Before V5.93, we used GSON's default JSON serializer, which output the dates in en-US format.
 * From V5.93 onwards, we have been using our own JSON serializer which writes dates in our prescribed format.
 * Comment search documents now exist in both formats, which the current codebase has to handle.</p>
 *
 * <p>This script migrates all comment search documents to the newer prescribed date format.</p>
 */
public class DataMigrationForFeedbackResponseCommentSearchDocumentDateFormat extends RemoteApiClient {
    /**
     * Will not perform updates on the datastore if true.
     */
    private static final boolean isPreview = true;

    /**
     * Number of comments/documents to process per cycle.
     * Maximum documents that can be updated in one request is 200 (limit imposed by GAE Search API).
     */
    private static final int batchSize = 200;

    private static final DateFormat oldDateFormat =
            DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US);
    private static final DateFormat newDateFormat = new SimpleDateFormat(Const.SystemParams.DEFAULT_DATE_TIME_FORMAT);

    private static final Index index = SearchServiceFactory.getSearchService()
            .getIndex(IndexSpec.newBuilder().setName(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT));
    private static final JsonParser jsonParser = new JsonParser();

    public static void main(String[] args) throws IOException {
        new DataMigrationForFeedbackResponseCommentSearchDocumentDateFormat().doOperationRemotely();
    }

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
        int numberOfUnaffectedDocuments = 0;
        LoopHelper loopHelper = new LoopHelper(batchSize, "comments analyzed.");

        for (FeedbackResponseCommentAttributes comment : allComments) {
            loopHelper.recordLoop();

            Document document = getDocument(comment);
            if (document == null) {
                continue;
            }

            numberOfDocuments++;

            String sampleDateString = extractSampleDateString(document);
            if (isInDateFormat(sampleDateString, oldDateFormat)) {
                commentsToFix.add(comment);
            } else if (isInDateFormat(sampleDateString, newDateFormat)) {
                numberOfUnaffectedDocuments++;
            } else {
                println("Unrecognised date format (" + sampleDateString + ") for:\n" + comment);
            }
        }

        println("\n################# Analysis Results #################");
        println("Total number of comments: " + loopHelper.getCount());
        println("Total number of documents: " + numberOfDocuments + "\n");

        println("Number of documents already in new date format: " + numberOfUnaffectedDocuments);
        println("Number of documents in unrecognizable date format: "
                + (numberOfDocuments - commentsToFix.size() - numberOfUnaffectedDocuments) + "\n");

        println("Number of documents in old date format: " + commentsToFix.size());
        println("####################################################\n");

        return commentsToFix;
    }

    private Document getDocument(FeedbackResponseCommentAttributes comment) {
        return index.get(comment.getId().toString());
    }

    private String extractSampleDateString(Document document) {
        String frcaJson = document.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_COMMENT_ATTRIBUTE).getText();
        return jsonParser.parse(frcaJson).getAsJsonObject().getAsJsonPrimitive("createdAt").getAsString();
    }

    private boolean isInDateFormat(String dateString, DateFormat dateFormat) {
        try {
            dateFormat.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void fixDocuments(List<FeedbackResponseCommentAttributes> commentsToFix) {
        println("Running data migration for " + commentsToFix.size() + " documents in old date format...");
        println("Preview: " + isPreview);

        List<Document> documentsToUpdate = new ArrayList<>();

        LoopHelper loopHelper = new LoopHelper(batchSize, "documents processed.");

        for (FeedbackResponseCommentAttributes commentToFix : commentsToFix) {
            loopHelper.recordLoop();
            queueDocumentUpdate(commentToFix, documentsToUpdate);

            if (documentsToUpdate.size() == batchSize) {
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

        if (!isPreview) {
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
