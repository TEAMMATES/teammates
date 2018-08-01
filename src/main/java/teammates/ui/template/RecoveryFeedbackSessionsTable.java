package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

public class RecoveryFeedbackSessionsTable {
    private List<RecoveryFeedbackSessionsTableRow> rows;

    public RecoveryFeedbackSessionsTable() {
        rows = new ArrayList<>();
    }

    public List<RecoveryFeedbackSessionsTableRow> getRows() {
        return rows;
    }

    public void setRows(List<RecoveryFeedbackSessionsTableRow> rows) {
        this.rows = rows;
    }
}
