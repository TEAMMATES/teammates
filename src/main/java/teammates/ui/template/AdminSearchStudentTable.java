package teammates.ui.template;

import java.util.List;

public class AdminSearchStudentTable {
    private List<AdminSearchStudentRow> studentRows;

    public AdminSearchStudentTable(final List<AdminSearchStudentRow> studentRows) {
        this.studentRows = studentRows;
    }

    public List<AdminSearchStudentRow> getStudentRows() {
        return studentRows;
    }
}
