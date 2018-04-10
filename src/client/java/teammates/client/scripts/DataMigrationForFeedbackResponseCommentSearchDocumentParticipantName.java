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

    public static void main(String[] args) throws IOException {
        new DataMigrationForFeedbackResponseCommentSearchDocumentParticipantName().doOperationRemotely();
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
        FeedbackQuestionAttributes question = FeedbackQuestionsLogic.inst()
                .getFeedbackQuestion(comment.feedbackQuestionId);

        if (question == null) {
            return false;
        }

        Document document = index.get(comment.getId().toString());

        if (isFixRequiredForGiverName(document, question) || isFixRequiredForReceiverName(document, question)) {
            println("Fix of response participant name required in document: " + document.getId());
            return true;
        }

        return false;
    }

    private boolean isFixRequiredForGiverName(Document document, FeedbackQuestionAttributes question) {
        if (question.giverType != FeedbackParticipantType.TEAMS) {
            return false;
        }

        String responseGiverName = StringHelper.extractContentFromQuotedString(
                document.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_GIVER_NAME).getText());

        return Const.USER_UNKNOWN_TEXT.equals(responseGiverName);
    }

    private boolean isFixRequiredForReceiverName(Document document, FeedbackQuestionAttributes question) {
        if (question.recipientType != FeedbackParticipantType.TEAMS) {
            return false;
        }

        String responseReceiverName = StringHelper.extractContentFromQuotedString(
                document.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_RECEIVER_NAME).getText());

        return Const.USER_UNKNOWN_TEXT.equals(responseReceiverName);
    }

}
