package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.SanitizationHelper;
import teammates.ui.template.CommentRow;
import teammates.ui.template.CommentsForStudentsTable;
import teammates.ui.template.CoursePagination;

/**
 * PageData: the data to be used in the InstructorCommentsPage.
 */
public class InstructorCommentsPageData extends PageData {
    public static final String COMMENT_GIVER_NAME_THAT_COMES_FIRST = "0you";

    private boolean isViewingDraft;
    private boolean isDisplayArchive;
    private String courseId;
    private String courseName;
    private CoursePagination coursePagination;
    private List<FeedbackSessionAttributes> feedbackSessions;
    private int numberOfPendingComments;

    private List<CommentsForStudentsTable> commentsForStudentsTables;

    public InstructorCommentsPageData(AccountAttributes account) {
        super(account);
    }

    public void init(boolean isViewingDraft, boolean isDisplayArchive, String courseId, String courseName,
                     List<String> coursePaginationList, Map<String, List<CommentAttributes>> comments,
                     Map<String, List<Boolean>> commentModifyPermissions, CourseRoster roster,
                     List<FeedbackSessionAttributes> feedbackSessions, int numberOfPendingComments) {
        this.isViewingDraft = isViewingDraft;
        this.isDisplayArchive = isDisplayArchive;
        this.courseId = courseId;
        this.courseName = courseName;
        this.feedbackSessions = feedbackSessions;
        this.numberOfPendingComments = numberOfPendingComments;

        setCoursePagination(coursePaginationList);
        setCommentsForStudentsTables(comments, commentModifyPermissions, roster);

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

    public List<FeedbackSessionAttributes> getFeedbackSessions() {
        return feedbackSessions;
    }

    public int getNumberOfPendingComments() {
        return numberOfPendingComments;
    }

    public List<CommentsForStudentsTable> getCommentsForStudentsTables() {
        return commentsForStudentsTables;
    }

    public boolean isDisplayArchive() {
        return isDisplayArchive;
    }

    public boolean isViewingDraft() {
        return isViewingDraft;
    }

    private void setCoursePagination(List<String> coursePaginationList) {
        String previousPageLink = retrievePreviousPageLink(coursePaginationList);
        String nextPageLink = retrieveNextPageLink(coursePaginationList);
        String activeCourse = coursePaginationList.contains(courseId) ? courseId : "";
        String activeCourseClass = isViewingDraft ? "" : "active";
        String userCommentsLink = getInstructorCommentsLink();
        coursePagination = new CoursePagination(previousPageLink, nextPageLink, coursePaginationList,
                                                activeCourse, activeCourseClass, userCommentsLink);
    }

    private void setCommentsForStudentsTables(
            Map<String, List<CommentAttributes>> comments, Map<String, List<Boolean>> commentModifyPermissions,
            CourseRoster roster) {
        Map<String, String> giverEmailToGiverNameMap = getGiverEmailToGiverNameMap(comments, roster);
        commentsForStudentsTables = new ArrayList<CommentsForStudentsTable>();

        for (Map.Entry<String, List<CommentAttributes>> entry : comments.entrySet()) {
            String giverEmail = entry.getKey();
            String giverName = giverEmailToGiverNameMap.get(giverEmail);
            CommentsForStudentsTable table =
                    new CommentsForStudentsTable(
                            giverName, createCommentRows(giverEmail, giverName, entry.getValue(),
                                                         commentModifyPermissions, roster));
            String extraClass;
            if (giverEmail.equals(COMMENT_GIVER_NAME_THAT_COMES_FIRST)) {
                extraClass = "giver_display-by-you";
            } else {
                extraClass = "giver_display-by-others";
            }
            table.withExtraClass(extraClass);
            commentsForStudentsTables.add(table);
        }
    }

    private List<CommentRow> createCommentRows(
            String giverEmail, String giverName, List<CommentAttributes> commentsForGiver,
            Map<String, List<Boolean>> commentModifyPermissions, CourseRoster roster) {
        String unsanitizedGiverName = SanitizationHelper.desanitizeFromHtml(giverName);

        List<CommentRow> rows = new ArrayList<CommentRow>();
        for (int i = 0; i < commentsForGiver.size(); i++) {
            CommentAttributes comment = commentsForGiver.get(i);
            String recipientDetails = getRecipientNames(comment.recipients, courseId, null, roster);
            String unsanitizedRecipientDetails = SanitizationHelper.desanitizeFromHtml(recipientDetails);

            Boolean isInstructorAllowedToModifyCommentInSection = commentModifyPermissions.get(giverEmail).get(i);
            String typeOfPeopleCanViewComment = getTypeOfPeopleCanViewComment(comment);
            CommentRow commentDiv = new CommentRow(comment, unsanitizedGiverName, unsanitizedRecipientDetails);
            String extraClass;
            if (comment.showCommentTo.isEmpty()) {
                extraClass = "status_display-private";
            } else {
                extraClass = "status_display-public";
            }
            commentDiv.withExtraClass(extraClass);
            commentDiv.setVisibilityIcon(typeOfPeopleCanViewComment);
            commentDiv.setNotificationIcon(comment.isPendingNotification());
            if (isInstructorAllowedToModifyCommentInSection) {
                commentDiv.setEditDeleteEnabled(true);
                commentDiv.setFromCommentsPage();
                commentDiv.setPlaceholderNumComments();
            }

            rows.add(commentDiv);
        }
        return rows;
    }

    private String retrievePreviousPageLink(List<String> coursePaginationList) {
        int courseIdx = coursePaginationList.indexOf(courseId);
        String previousPageLink = "javascript:;";
        if (courseIdx >= 1) {
            previousPageLink = getInstructorCommentsLink() + "&courseid=" + coursePaginationList.get(courseIdx - 1);
        }
        return previousPageLink;
    }

    private String retrieveNextPageLink(List<String> coursePaginationList) {
        int courseIdx = coursePaginationList.indexOf(courseId);
        String nextPageLink = "javascript:;";
        if (courseIdx < coursePaginationList.size() - 1) {
            nextPageLink = getInstructorCommentsLink() + "&courseid=" + coursePaginationList.get(courseIdx + 1);
        }
        return nextPageLink;
    }

    private Map<String, String> getGiverEmailToGiverNameMap(
            Map<String, List<CommentAttributes>> comments, CourseRoster roster) {

        Map<String, String> giverEmailToGiverNameMap = new HashMap<String, String>();
        for (String giverEmail : comments.keySet()) {

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

}
