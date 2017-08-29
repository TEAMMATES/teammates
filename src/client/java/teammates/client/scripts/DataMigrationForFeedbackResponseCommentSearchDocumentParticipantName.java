package teammates.client.scripts;

import java.io.IOException;

import com.google.appengine.api.search.Document;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.FeedbackQuestionsLogic;

/**
 * Script to fix the giver and recipient name for feedback response in search documents.
 *
 * <p>When giver or recipient of response is a team they become "Unknown user" during inserting in search document.
 * Use this script to correct displaying participant name in documents that were created before fix.</p>
 *
 * <p>See issue #7655.</p>
 */
public class DataMigrationForFeedbackResponseCommentSearchDocumentParticipantName
        extends DataMigrationForFeedbackResponseCommentSearchDocument {

    private int numberOfUnaffectedDocuments;
    private int numberOfDocumentsToUpdate;

    @Override
    protected boolean isPreview() {
        return true;
    }

    public static void main(String[] args) throws IOException {
        new DataMigrationForFeedbackResponseCommentSearchDocumentParticipantName().doOperationRemotely();
    }

    @Override
    protected boolean isFixRequired(FeedbackResponseCommentAttributes comment, Document document) {
        FeedbackQuestionAttributes question = FeedbackQuestionsLogic.inst()
                .getFeedbackQuestion(comment.feedbackQuestionId);

        if (question == null) {
            return false;
        }

        if (isFixRequiredForGiverName(document, question) || isFixRequiredForReceiverName(document, question)) {
            numberOfDocumentsToUpdate++;
            println("Fix of response participant name required in document: " + document.getId());
            return true;
        }

        numberOfUnaffectedDocuments++;

        return false;
    }

    private boolean isFixRequiredForGiverName(Document document,
                                              FeedbackQuestionAttributes question) {
        if (question.giverType != FeedbackParticipantType.TEAMS) {
            return false;
        }

        String responseGiverName = StringHelper.extractContentFromQuotedString(
                document.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_GIVER_NAME).getText());

        return Const.USER_UNKNOWN_TEXT.equals(responseGiverName);
    }

    private boolean isFixRequiredForReceiverName(Document document,
                                              FeedbackQuestionAttributes question) {
        if (question.recipientType != FeedbackParticipantType.TEAMS) {
            return false;
        }

        String responseReceiverName = StringHelper.extractContentFromQuotedString(
                document.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_RECEIVER_NAME).getText());

        return Const.USER_UNKNOWN_TEXT.equals(responseReceiverName);
    }

    @Override
    protected void displayAnalysisResults() {
        println("Number of unaffected documents: " + numberOfUnaffectedDocuments);
        println("Number of documents with invalid response giver or recipient name: " + numberOfDocumentsToUpdate);
    }
}
