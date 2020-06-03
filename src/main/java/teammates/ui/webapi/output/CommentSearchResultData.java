package teammates.ui.webapi.output;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            output.add(new QuestionOutput(question, null,
                    buildResponses(bundle.responses.get(question.getId()), bundle)));
        }
        return output;
    }

    private List<ResponseOutput> buildResponses(List<FeedbackResponseAttributes> responses,
                                                FeedbackResponseCommentSearchResultBundle bundle) {
        List<ResponseOutput> output = new ArrayList<>();
        for (FeedbackResponseAttributes response : responses) {
            output.add(new ResponseOutput(response.getId(), response.giver, null, null, null,
                    response.giverSection, response.recipient, null, null, response.recipientSection,
                    response.responseDetails, getStudentComment(bundle.comments.get(response.getId()), bundle),
                    buildComments(bundle.comments.get(response.getId()), bundle)));
        }
        return output;
    }

    private List<CommentOutput> buildComments(List<FeedbackResponseCommentAttributes> comments,
                                              FeedbackResponseCommentSearchResultBundle bundle) {
        List<CommentOutput> output = new ArrayList<>();
        for (FeedbackResponseCommentAttributes comment : comments) {
            output.add(new CommentOutput(comment, comment.commentGiver,
                    bundle.commentGiverEmailToNameTable.get(comment.lastEditorEmail)));
        }
        return output;
    }

    private CommentOutput getStudentComment(List<FeedbackResponseCommentAttributes> comments,
                                     FeedbackResponseCommentSearchResultBundle bundle) {
        for (String email : bundle.instructorEmails) {
            bundle.commentGiverEmailToNameTable.remove(email);
        }
        Optional<FeedbackResponseCommentAttributes> optComment = comments.stream().filter(c ->
                bundle.commentGiverEmailToNameTable.containsValue(c.commentGiver)).reduce((c1, c2) -> c1);

        return optComment.map(comment -> new CommentOutput(comment, comment.commentGiver,
                bundle.commentGiverEmailToNameTable.get(comment.lastEditorEmail))).orElse(null);
    }

    public FeedbackSessionData getFeedbackSession() {
        return feedbackSession;
    }
}
