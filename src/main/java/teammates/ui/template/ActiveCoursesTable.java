package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

public class ActiveCoursesTable {
    private List<ActiveCoursesTableRow> rows;

    public ActiveCoursesTable() {
        rows = new ArrayList<>();
    }

    public List<ActiveCoursesTableRow> getRows() {
        return rows;
    }

    public void setRows(List<ActiveCoursesTableRow> rows) {
        this.rows = rows;
    }
}
