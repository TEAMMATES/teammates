package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

public class RecoveryCoursesTable {
    private List<RecoveryCoursesTableRow> rows;

    public RecoveryCoursesTable() {
        rows = new ArrayList<>();
    }

    public List<RecoveryCoursesTableRow> getRows() {
        return rows;
    }

    public void setRows(List<RecoveryCoursesTableRow> rows) {
        this.rows = rows;
    }
}
