package teammates.ui.webapi.output;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;

/**
 * API output format for a comment search result.
 */
public class CommentSearchResultData extends SessionResultsData {
    private final FeedbackSessionData feedbackSession;

    public CommentSearchResultData(FeedbackSessionAttributes session,
                                   FeedbackResponseCommentSearchResultBundle bundle) {
        super();
        feedbackSession = new FeedbackSessionData(session);
        bundle.questions.forEach((key, value) -> {
            questions.addAll(buildQuestions(value, bundle));
        });
    }

    private List<QuestionOutput> buildQuestions(List<FeedbackQuestionAttributes> questions,
                                                FeedbackResponseCommentSearchResultBundle bundle) {
        List<QuestionOutput> output = new ArrayList<>();
        for (FeedbackQuestionAttributes question : questions) {
            output.add(new QuestionOutput(question, buildResponses(bundle.responses.get(question.getId()), bundle)));
        }
        return output;
    }

    private List<ResponseOutput> buildResponses(List<FeedbackResponseAttributes> responses,
                                                FeedbackResponseCommentSearchResultBundle bundle) {
        List<ResponseOutput> output = new ArrayList<>();
        for (FeedbackResponseAttributes response : responses) {
            output.add(ResponseOutput.builder()
                    .withResponseId(response.getId())
                    .withGiver(response.giver)
                    .withGiverSection(response.giverSection)
                    .withRecipient(response.recipient)
                    .withRecipientSection(response.recipientSection)
                    .withParticipantComment(getParticipantComment(bundle.comments.get(response.getId()), bundle))
                    .withInstructorComments(getInstructorComments(bundle.comments.get(response.getId()), bundle))
                    .build());
        }
        return output;
    }

    private List<CommentOutput> getInstructorComments(List<FeedbackResponseCommentAttributes> comments,
                                                      FeedbackResponseCommentSearchResultBundle bundle) {
        List<CommentOutput> output = new ArrayList<>();
        for (FeedbackResponseCommentAttributes comment : comments) {
            if (comment.isCommentFromFeedbackParticipant()) {
                continue;
            }
            output.add(CommentOutput.builder(comment)
                    .withCommentGiver(comment.commentGiver)
                    .withLastEditorName(bundle.commentGiverEmailToNameTable.get(comment.lastEditorEmail))
                    .build());
        }
        return output;
    }

    private CommentOutput getParticipantComment(List<FeedbackResponseCommentAttributes> comments,
                                                FeedbackResponseCommentSearchResultBundle bundle) {
        for (FeedbackResponseCommentAttributes comment : comments) {
            if (comment.isCommentFromFeedbackParticipant()) {
                return CommentOutput.builder(comment)
                        .withCommentGiver(comment.commentGiver)
                        .withLastEditorName(bundle.commentGiverEmailToNameTable.get(comment.lastEditorEmail))
                        .build();
            }
        }
        return null;
    }

    public FeedbackSessionData getFeedbackSession() {
        return feedbackSession;
    }
}
