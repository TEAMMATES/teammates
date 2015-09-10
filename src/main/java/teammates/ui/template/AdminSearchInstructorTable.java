package teammates.ui.template;

import java.util.List;

public class AdminSearchInstructorTable {
    private List<AdminSearchInstructorRow> instructorRows;

    public AdminSearchInstructorTable(List<AdminSearchInstructorRow> instructorRows) {
        this.instructorRows = instructorRows;
    }

    public List<AdminSearchInstructorRow> getInstructorRows() {
        return instructorRows;
    }
}
