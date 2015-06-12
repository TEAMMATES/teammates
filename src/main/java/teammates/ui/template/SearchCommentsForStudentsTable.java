package teammates.ui.template;

import java.util.List;

/**
 * Each table contains comments given to students by a giver with a specific
 * email and course ID. Comments by the same giver email but different course IDs
 * e.g same instructor in different courses will be in different tables.
 */
public class SearchCommentsForStudentsTable {
    private String giverDetails;
    private List<CommentRow> rows;
    
    public SearchCommentsForStudentsTable(String giverDetails, List<CommentRow> rows) {
        this.giverDetails = giverDetails;
        this.rows = rows;
    }
    
    public String getGiverDetails() {
        return giverDetails;
    }
    
    public List<CommentRow> getRows() {
        return rows;
    }
}
