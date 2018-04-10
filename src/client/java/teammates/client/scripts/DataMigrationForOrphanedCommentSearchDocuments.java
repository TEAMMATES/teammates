package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.gson.JsonParser;
import com.googlecode.objectify.Key;

import teammates.client.scripts.util.LoopHelper;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.storage.entity.FeedbackResponseComment;

/**
 * Deletes {@link Document}s that use the old date format and correspond to
 * {@link FeedbackResponseCommentAttributes} that no longer exist.
 */
public class DataMigrationForOrphanedCommentSearchDocuments extends DataMigrationBaseScript<Document> {

    private static final Index index = SearchServiceFactory.getSearchService()
            .getIndex(IndexSpec.newBuilder().setName(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT));

    private static final JsonParser jsonParser = new JsonParser();

    /**
     * Number of comments/documents to process per cycle.
     * Maximum documents that can be updated in one request is 200 (limit imposed by GAE Search API).
     */
    private static final int BATCH_SIZE = 200;

    private List<Document> documentsToDelete = new ArrayList<>();
    private int numOfDocumentsNotDeleted;

    public static void main(String[] args) throws IOException {
        new DataMigrationForOrphanedCommentSearchDocuments().doOperationRemotely();
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected List<Document> getEntities() {
        List<Document> allResults = new ArrayList<>();
        Cursor cursor = Cursor.newBuilder().build();
        while (cursor != null) {
            cursor = getMoreResults(cursor, allResults);
        }
        return allResults;
    }

    // Required as the Search API can only return a maximum of 1000 documents at once
    private Cursor getMoreResults(Cursor cursor, List<Document> allResults) {
        QueryOptions options = QueryOptions.newBuilder().setLimit(1000).setCursor(cursor).build();

        Query query = Query.newBuilder().setOptions(options).build("");

        Results<ScoredDocument> results = index.search(query);
        allResults.addAll(results.getResults());

        return results.getCursor();
    }

    @Override
    protected boolean isMigrationNeeded(Document document) {
        if (!isInOldFormat(document)) {
            return false;
        }
        if (!isOrphanedDocument(document)) {
            numOfDocumentsNotDeleted++;
            return false;
        }
        return true;
    }

    private boolean isInOldFormat(Document document) {
        // These 4 fields store more than just simple strings/numbers, but representations of entity attributes
        String frcaJson = document.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_COMMENT_ATTRIBUTE).getText();
        String fraJson = document.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_ATTRIBUTE).getText();
        String fqaJson = document.getOnlyField(Const.SearchDocumentField.FEEDBACK_QUESTION_ATTRIBUTE).getText();
        String fsaJson = document.getOnlyField(Const.SearchDocumentField.FEEDBACK_SESSION_ATTRIBUTE).getText();

        try {
            // Attempt to deserialize all 4 complex fields
            JsonUtils.fromJson(frcaJson, FeedbackResponseCommentAttributes.class); // Instant fields may fail
            JsonUtils.fromJson(fsaJson, FeedbackSessionAttributes.class); // Instand and ZoneId fields may fail
            // These 2 have Instant fields that are transient and thus should not be affected,
            // but include them anyway to be safe
            JsonUtils.fromJson(fraJson, FeedbackResponseAttributes.class); // Instant fields may fail
            JsonUtils.fromJson(fqaJson, FeedbackQuestionAttributes.class); // Instant fields may fail

            // All complex fields successfully deserialized; the document is compatible with the current search system
            // and does not require migration
            return false;

        } catch (Exception e) {
            return true;
        }
    }

    private boolean isOrphanedDocument(Document document) {
        String frcaJson = document.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_COMMENT_ATTRIBUTE).getText();
        Long parentCommentId = jsonParser.parse(frcaJson).getAsJsonObject()
                .getAsJsonPrimitive("feedbackResponseCommentId").getAsLong();
        return isCommentNonExistent(parentCommentId);
    }

    private boolean isCommentNonExistent(Long commentId) {
        Key commentKey = ofy().load().filterKey(Key.create(FeedbackResponseComment.class, commentId)).keys().first().now();
        return commentKey == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void printPreviewInformation(Document document) {
        // nothing to do
    }

    @Override
    protected void migrate(Document document) {
        documentsToDelete.add(document);
    }

    @Override
    protected void postAction() {
        println("Number of documents in the old format that have existing parent comments: " + numOfDocumentsNotDeleted);
        if (numOfDocumentsNotDeleted > 0) {
            println("Warning: these documents will not be deleted. Please run the other migration script again.");
        }

        if (isPreview()) {
            return;
        }

        LoopHelper loopHelper = new LoopHelper(BATCH_SIZE, "documents processed.");

        List<Document> batch = new ArrayList<>(BATCH_SIZE);

        for (Document document : documentsToDelete) {
            loopHelper.recordLoop();
            batch.add(document);

            if (batch.size() == BATCH_SIZE) {
                batchDeleteAndClear(batch);
            }
        }
        batchDeleteAndClear(batch);
    }

    private void batchDeleteAndClear(List<Document> batch) {
        println("Batch deleting " + batch.size() + " documents...");
        index.delete(batch.stream().map(Document::getId).collect(Collectors.toList()));
        batch.clear();
    }

}
