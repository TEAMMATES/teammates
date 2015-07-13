package teammates.ui.template;

import java.util.List;

public class InstructorCommentsForStudentsTable {
    private String giverEmail;
    private String giverName;
    private List<Comment> rows;
    
    public InstructorCommentsForStudentsTable(String giverEmail, String giverName, List<Comment> rows) {
        this.giverEmail = giverEmail;
        this.giverName = giverName;
        this.rows = rows;
    }
    
    public String getGiverEmail() {
        return giverEmail;
    }
    
    public String getGiverName() {
        return giverName;
    }
    
    public List<Comment> getRows() {
        return rows;
    }
}
