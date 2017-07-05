package teammates.client.scripts;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.gson.JsonParser;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.client.scripts.util.LoopHelper;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.util.Const;
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
        LoopHelper loopHelper = new LoopHelper(100, "comments analyzed.");

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
            } else {
                if (isInDateFormat(sampleDateString, newDateFormat)) {
                    numberOfUnaffectedDocuments++;
                } else {
                    println("Unrecognised date format (" + sampleDateString + ") for:\n" + comment);
                }
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

    private synchronized boolean isInDateFormat(String dateString, DateFormat dateFormat) {
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

        int numberOfFixedDocuments = 0;
        LoopHelper loopHelper = new LoopHelper(100, "documents fixed.");

        for (FeedbackResponseCommentAttributes commentToFix : commentsToFix) {
            loopHelper.recordLoop();
            try {
                updateDocument(commentToFix);
                numberOfFixedDocuments++;
            } catch (Exception e) {
                println("Failed to fix document for:\n" + commentToFix);
            }
        }

        println("\n############## Data Migration Results ##############");
        println("Number of documents fixed: " + numberOfFixedDocuments);
        println("Number of documents not fixed (error): " + (commentsToFix.size() - numberOfFixedDocuments));
        println("####################################################\n");
    }

    private void updateDocument(FeedbackResponseCommentAttributes comment) {
        if (isPreview) {
            return;
        }
        SearchManager.putDocument(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT,
                new FeedbackResponseCommentSearchDocument(comment).build());
    }
}
