package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

public class ArchivedCoursesTable {
    private List<ArchivedCoursesTableRow> rows;

    public ArchivedCoursesTable() {
        rows = new ArrayList<>();
    }

    public List<ArchivedCoursesTableRow> getRows() {
        return rows;
    }

    public void setRows(List<ArchivedCoursesTableRow> rows) {
        this.rows = rows;
    }
}
