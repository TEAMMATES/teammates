package teammates.client.scripts;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import com.google.appengine.api.search.Document;
import com.google.gson.JsonParser;

import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.util.Const;

/**
 * Script to fix the date format in old {@link teammates.storage.search.FeedbackResponseCommentSearchDocument}s
 * created before V6.4.0.
 *
 * <p>Before V6.4.0, we used {@link java.util.Date} fields for timestamps. These are serialized using the format
 * {@link Const.SystemParams#DEFAULT_DATE_TIME_FORMAT}.
 * From V6.4.0 onwards, these have been migrated to {@link Instant} fields. These are serialized using
 * {@link java.time.format.DateTimeFormatter#ISO_INSTANT}.
 * Comment search documents now exist in both formats, which the current codebase has to handle.</p>
 *
 * <p>This script migrates all comment search documents to the newer instant format.
 * The @{@link teammates.common.datatransfer.attributes.FeedbackSessionAttributes#timeZone} field is also
 * automatically migrated.</p>
 */
public class DataMigrationForFeedbackResponseCommentSearchDocumentDateFormat
        extends DataMigrationForFeedbackResponseCommentSearchDocument {

    private static final JsonParser jsonParser = new JsonParser();

    public static void main(String[] args) throws IOException {
        new DataMigrationForFeedbackResponseCommentSearchDocumentDateFormat().doOperationRemotely();
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
    protected void printPreviewInformation(FeedbackResponseCommentAttributes comment) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isMigrationNeeded(FeedbackResponseCommentAttributes comment) {
        Document document = index.get(comment.getId().toString());
        if (document == null) {
            return false;
        }
        String sampleDateString = extractSampleDateString(document);
        return !isInInstantFormat(sampleDateString);
    }

    private String extractSampleDateString(Document document) {
        String frcaJson = document.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_COMMENT_ATTRIBUTE).getText();
        return jsonParser.parse(frcaJson).getAsJsonObject().getAsJsonPrimitive("createdAt").getAsString();
    }

    private boolean isInInstantFormat(String dateString) {
        try {
            Instant.parse(dateString);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}
