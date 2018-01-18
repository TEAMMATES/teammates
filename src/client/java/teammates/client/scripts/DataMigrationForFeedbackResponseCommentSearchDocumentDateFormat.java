package teammates.client.scripts;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.google.appengine.api.search.Document;
import com.google.gson.JsonParser;

import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.util.Const;

/**
 * Script to fix the date format in old {@link FeedbackResponseCommentSearchDocument}s created before V5.93.
 *
 * <p>Before V5.93, we used GSON's default JSON serializer, which output the dates in en-US format.
 * From V5.93 onwards, we have been using our own JSON serializer which writes dates in our prescribed format.
 * Comment search documents now exist in both formats, which the current codebase has to handle.</p>
 *
 * <p>This script migrates all comment search documents to the newer prescribed date format.</p>
 */
public class DataMigrationForFeedbackResponseCommentSearchDocumentDateFormat
        extends DataMigrationForFeedbackResponseCommentSearchDocument {

    private static final DateFormat oldDateFormat =
            DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US);
    private static final DateFormat newDateFormat = new SimpleDateFormat(Const.SystemParams.DEFAULT_DATE_TIME_FORMAT);

    private static final JsonParser jsonParser = new JsonParser();

    private int numberOfUnaffectedDocuments;
    private int numberOfDocumentsToUpdate;
    private int numberOfDocumentsInUnrecognizableDateFormat;

    @Override
    protected boolean isPreview() {
        return true;
    }

    public static void main(String[] args) throws IOException {
        new DataMigrationForFeedbackResponseCommentSearchDocumentDateFormat().doOperationRemotely();
    }

    @Override
    protected boolean isFixRequired(FeedbackResponseCommentAttributes comment, Document document) {
        String sampleDateString = extractSampleDateString(document);
        if (isInDateFormat(sampleDateString, oldDateFormat)) {
            numberOfDocumentsToUpdate++;
            return true;
        }

        if (isInDateFormat(sampleDateString, newDateFormat)) {
            numberOfUnaffectedDocuments++;
        } else {
            numberOfDocumentsInUnrecognizableDateFormat++;
            println("Unrecognised date format (" + sampleDateString + ") for:\n" + comment);
        }

        return false;
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

    @Override
    protected void displayAnalysisResults() {
        println("Number of documents already in new date format: " + numberOfUnaffectedDocuments);
        println("Number of documents in unrecognizable date format: " + numberOfDocumentsInUnrecognizableDateFormat);
        println("Number of documents in old date format: " + numberOfDocumentsToUpdate);
    }
}
