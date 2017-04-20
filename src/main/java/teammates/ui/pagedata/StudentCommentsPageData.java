package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.util.SanitizationHelper;
import teammates.ui.template.CommentRow;
import teammates.ui.template.CommentsForStudentsTable;
import teammates.ui.template.CoursePagination;
import teammates.ui.template.FeedbackResponseCommentRow;
import teammates.ui.template.FeedbackSessionRow;
import teammates.ui.template.QuestionTable;
import teammates.ui.template.ResponseRow;

/**
 * PageData: the data used in the StudentCommentsPage.
 */
public class StudentCommentsPageData extends PageData {

    private String courseId;
    private String courseName;

    private CoursePagination coursePagination;
    private List<FeedbackSessionRow> feedbackSessionRows;

    private List<CommentsForStudentsTable> commentsForStudentsTables;

    public StudentCommentsPageData(AccountAttributes account) {
        super(account);
    }

    public void init(String courseId, String courseName, List<String> coursePaginationList,
                     List<CommentAttributes> comments, CourseRoster roster, String studentEmail,
                     Map<String, FeedbackSessionResultsBundle> feedbackResultBundles) {
        this.courseId = courseId;
        this.courseName = courseName;

        setCoursePagination(coursePaginationList);
        setCommentsForStudentsTables(studentEmail, roster, comments);
        createFeedbackSessionRows(feedbackResultBundles);
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public CoursePagination getCoursePagination() {
        return coursePagination;
    }

    public List<CommentsForStudentsTable> getCommentsForStudentsTables() {
        return commentsForStudentsTables;
    }

    public List<FeedbackSessionRow> getFeedbackSessionRows() {
        return feedbackSessionRows;
    }

    private String retrievePreviousPageLink(List<String> coursePaginationList) {
        int courseIdx = coursePaginationList.indexOf(courseId);
        String previousPageLink = "javascript:;";
        if (courseIdx >= 1) {
            previousPageLink = getStudentCommentsLink() + "&courseid=" + coursePaginationList.get(courseIdx - 1);
        }
        return previousPageLink;
    }

    private String retrieveNextPageLink(List<String> coursePaginationList) {
        int courseIdx = coursePaginationList.indexOf(courseId);
        String nextPageLink = "javascript:;";
        if (courseIdx < coursePaginationList.size() - 1) {
            nextPageLink = getStudentCommentsLink() + "&courseid=" + coursePaginationList.get(courseIdx + 1);
        }
        return nextPageLink;
    }

    private void setCoursePagination(List<String> coursePaginationList) {
        String previousPageLink = retrievePreviousPageLink(coursePaginationList);
        String nextPageLink = retrieveNextPageLink(coursePaginationList);
        String activeCourse = coursePaginationList.contains(courseId) ? courseId : "";
        String activeCourseClass = "active";
        String userCommentsLink = getStudentCommentsLink(false);
        coursePagination = new CoursePagination(previousPageLink, nextPageLink, coursePaginationList,
                                                activeCourse, activeCourseClass, userCommentsLink);
    }

    private void setCommentsForStudentsTables(String studentEmail, CourseRoster roster,
                                              List<CommentAttributes> comments) {
        Map<String, String> giverEmailToGiverNameMap = getGiverEmailToGiverNameMap(roster, comments);
        Map<String, List<CommentAttributes>> giverEmailToCommentsMap = getGiverEmailToCommentsMap(comments);
        commentsForStudentsTables = new ArrayList<CommentsForStudentsTable>();

        for (Map.Entry<String, String> entry : giverEmailToGiverNameMap.entrySet()) {
            String giverEmail = entry.getKey();
            String giverName = entry.getValue();
            List<CommentAttributes> commentsForGiverEmail = giverEmailToCommentsMap.get(giverEmail);
            commentsForStudentsTables
                    .add(new CommentsForStudentsTable(giverName,
                                                      createCommentRows(studentEmail, roster, commentsForGiverEmail)));
        }
    }

    private Map<String, List<CommentAttributes>> getGiverEmailToCommentsMap(List<CommentAttributes> comments) {
        Map<String, List<CommentAttributes>> giverEmailToCommentsMap = new HashMap<String, List<CommentAttributes>>();
        for (CommentAttributes comment : comments) {
            String giverEmail = comment.giverEmail;
            if (!giverEmailToCommentsMap.containsKey(giverEmail)) {
                giverEmailToCommentsMap.put(giverEmail, new ArrayList<CommentAttributes>());
            }
            giverEmailToCommentsMap.get(giverEmail).add(comment);
        }
        return giverEmailToCommentsMap;
    }

    private Map<String, String> getGiverEmailToGiverNameMap(CourseRoster roster, List<CommentAttributes> comments) {

        Map<String, String> giverEmailToGiverNameMap = new HashMap<String, String>();
        for (CommentAttributes comment : comments) {
            String giverEmail = comment.giverEmail;
            InstructorAttributes instructor = roster.getInstructorForEmail(giverEmail);
            String giverDisplay = giverEmail;
            if (giverEmail.equals(InstructorCommentsPageData.COMMENT_GIVER_NAME_THAT_COMES_FIRST)) {
                giverDisplay = "You";
            } else if (instructor != null) {
                String title = instructor.displayedName;
                giverDisplay = title + " " + instructor.name;
            }

            giverEmailToGiverNameMap.put(giverEmail, giverDisplay);
        }
        return giverEmailToGiverNameMap;
    }

    private List<CommentRow> createCommentRows(String studentEmail, CourseRoster roster, List<CommentAttributes> comments) {
        List<CommentRow> commentRows = new ArrayList<CommentRow>();

        for (CommentAttributes comment : comments) {
            String recipientDetails = getRecipientNames(comment.recipients, courseId, studentEmail, roster);
            String unsanitizedRecipientDetails = SanitizationHelper.desanitizeFromHtml(recipientDetails);
            InstructorAttributes instructor = roster.getInstructorForEmail(comment.giverEmail);
            String giverDetails = comment.giverEmail;
            if (instructor != null) {
                giverDetails = instructor.displayedName + " " + instructor.name;
            }
            String unsanitizedGiverDetails = SanitizationHelper.desanitizeFromHtml(giverDetails);
            CommentRow commentRow = new CommentRow(comment, unsanitizedGiverDetails, unsanitizedRecipientDetails);

            commentRows.add(commentRow);
        }

        return commentRows;
    }

    private void createFeedbackSessionRows(
            Map<String, FeedbackSessionResultsBundle> feedbackResultBundles) {
        feedbackSessionRows = new ArrayList<FeedbackSessionRow>();

        for (Map.Entry<String, FeedbackSessionResultsBundle> entry : feedbackResultBundles.entrySet()) {

            FeedbackSessionRow feedbackSessionRow =
                    new FeedbackSessionRow(entry.getKey(), courseId, createFeedbackQuestionTables(entry.getValue()));

            feedbackSessionRows.add(feedbackSessionRow);
        }
    }

    private List<QuestionTable> createFeedbackQuestionTables(
            FeedbackSessionResultsBundle feedbackResultBundle) {
        List<QuestionTable> feedbackQuestionTables = new ArrayList<QuestionTable>();

        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responseEntries
                     : feedbackResultBundle.getQuestionResponseMap().entrySet()) {

            FeedbackQuestionAttributes feedbackQuestion = responseEntries.getKey();
            int questionNumber = feedbackQuestion.questionNumber;
            String questionText = feedbackResultBundle.getQuestionText(feedbackQuestion.getId());
            Map<String, FeedbackQuestionAttributes> questions = feedbackResultBundle.questions;

            FeedbackQuestionAttributes question = questions.get(feedbackQuestion.getId());
            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
            String additionalInfo = questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "");

            QuestionTable feedbackQuestionTable =
                    new QuestionTable(questionNumber, questionText, additionalInfo,
                                      createFeedbackResponseRows(
                                              feedbackResultBundle, questionDetails, responseEntries));
            feedbackQuestionTables.add(feedbackQuestionTable);
        }
        return feedbackQuestionTables;
    }

    private List<ResponseRow> createFeedbackResponseRows(
            FeedbackSessionResultsBundle feedbackResultBundle, FeedbackQuestionDetails questionDetails,
            Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responseEntries) {
        List<ResponseRow> feedbackResponseRows = new ArrayList<ResponseRow>();

        for (FeedbackResponseAttributes responseEntry : responseEntries.getValue()) {
            String giverName =
                    feedbackResultBundle.getGiverNameForResponse(responseEntry);
            String giverTeamName = feedbackResultBundle.getTeamNameForEmail(responseEntry.giver);
            giverName = feedbackResultBundle.appendTeamNameToName(giverName, giverTeamName);

            String recipientName =
                    feedbackResultBundle.getRecipientNameForResponse(responseEntry);
            String recipientTeamName = feedbackResultBundle.getTeamNameForEmail(responseEntry.recipient);
            recipientName = feedbackResultBundle.appendTeamNameToName(recipientName, recipientTeamName);

            String response = responseEntry.getResponseDetails().getAnswerHtml(questionDetails);

            ResponseRow responseRow =
                    new ResponseRow(giverName, recipientName, response,
                                    createFeedbackResponseCommentRows(
                                            feedbackResultBundle, responseEntry));

            feedbackResponseRows.add(responseRow);
        }
        return feedbackResponseRows;
    }

    private List<FeedbackResponseCommentRow> createFeedbackResponseCommentRows(
            FeedbackSessionResultsBundle feedbackResultBundle, FeedbackResponseAttributes responseEntry) {
        List<FeedbackResponseCommentRow> feedbackResponseCommentRows = new ArrayList<FeedbackResponseCommentRow>();
        List<FeedbackResponseCommentAttributes> frcList =
                feedbackResultBundle.responseComments.get(responseEntry.getId());

        for (FeedbackResponseCommentAttributes frc : frcList) {
            FeedbackResponseCommentRow feedbackResponseCommentRow =
                    new FeedbackResponseCommentRow(frc, frc.giverEmail);

            feedbackResponseCommentRows.add(feedbackResponseCommentRow);
        }
        return feedbackResponseCommentRows;
    }
}
