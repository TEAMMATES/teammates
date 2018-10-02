package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

public class SoftDeletedCoursesTable {
    private List<SoftDeletedCoursesTableRow> rows;

    public SoftDeletedCoursesTable() {
        rows = new ArrayList<>();
    }

    public List<SoftDeletedCoursesTableRow> getRows() {
        return rows;
    }

    public void setRows(List<SoftDeletedCoursesTableRow> rows) {
        this.rows = rows;
    }
}
