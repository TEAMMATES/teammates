package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.ui.template.FeedbackQuestionCopyTable;
import teammates.ui.template.FeedbackQuestionTableRow;

public class InstructorFeedbackQuestionCopyPageData extends PageData {

    private final List<FeedbackQuestionAttributes> questions;

    public InstructorFeedbackQuestionCopyPageData(
            AccountAttributes account, String sessionToken, List<FeedbackQuestionAttributes> copiableQuestions) {
        super(account, sessionToken);
        questions = copiableQuestions;
    }

    public FeedbackQuestionCopyTable getCopyQnForm() {
        List<FeedbackQuestionTableRow> copyQuestionRows = buildCopyQuestionsModalRows(questions);
        return new FeedbackQuestionCopyTable(copyQuestionRows);
    }

    private List<FeedbackQuestionTableRow> buildCopyQuestionsModalRows(List<FeedbackQuestionAttributes> copiableQuestions) {
        List<FeedbackQuestionTableRow> copyQuestionRows = new ArrayList<>();

        for (FeedbackQuestionAttributes question : copiableQuestions) {
            String courseId = question.courseId;
            String fsName = question.feedbackSessionName;

            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();

            String qnType = questionDetails.getQuestionTypeDisplayName();
            String qnText = questionDetails.getQuestionText();

            String qnId = question.getId();

            FeedbackQuestionTableRow row =
                    new FeedbackQuestionTableRow(courseId, fsName, qnType, qnText, qnId);
            copyQuestionRows.add(row);
        }

        return copyQuestionRows;
    }
}
