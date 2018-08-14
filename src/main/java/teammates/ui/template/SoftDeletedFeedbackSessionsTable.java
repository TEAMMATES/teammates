package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

public class SoftDeletedFeedbackSessionsTable {
    private List<SoftDeletedFeedbackSessionsTableRow> rows;

    public SoftDeletedFeedbackSessionsTable() {
        rows = new ArrayList<>();
    }

    public List<SoftDeletedFeedbackSessionsTableRow> getRows() {
        return rows;
    }

    public void setRows(List<SoftDeletedFeedbackSessionsTableRow> rows) {
        this.rows = rows;
    }
}
