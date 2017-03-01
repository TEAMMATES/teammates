package teammates.ui.template;

import java.util.List;

/**
 * Each table contains comments given to students by a giver with a specific
 * email and course ID. Comments by the same giver email but different course IDs
 * e.g same instructor in different courses will be in different tables.
 */
public class CommentsForStudentsTable {
    private String giverDetails;
    private String extraClass = "";
    private List<CommentRow> rows;
    private boolean instructorAllowedToGiveComment;

    // used by StudentRecords to mark a table as representing the instructor using the system
    private boolean isRepresentingSelf;

    public CommentsForStudentsTable(String giverDetails, List<CommentRow> rows) {
        this.giverDetails = giverDetails;
        this.rows = rows;
    }

    public String getGiverDetails() {
        return giverDetails;
    }

    public List<CommentRow> getRows() {
        return rows;
    }

    public String getExtraClass() {
        return extraClass;
    }

    public void withExtraClass(String extraClass) {
        this.extraClass = " " + extraClass;
    }

    public void setInstructorAllowedToGiveComment(boolean isInstructorAllowedToGiveComment) {
        this.instructorAllowedToGiveComment = isInstructorAllowedToGiveComment;
    }

    public boolean isInstructorAllowedToGiveComment() {
        return instructorAllowedToGiveComment;
    }

    public boolean isRepresentingSelf() {
        return isRepresentingSelf;
    }

    public void setIsRepresentingSelf(boolean isRepresentingSelf) {
        this.isRepresentingSelf = isRepresentingSelf;
    }

}
