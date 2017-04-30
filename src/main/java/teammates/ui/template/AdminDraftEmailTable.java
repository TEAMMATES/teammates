package teammates.ui.template;

import java.util.List;

public class AdminDraftEmailTable {
    private int numEmailsDraft;
    private List<AdminDraftEmailRow> rows;

    public AdminDraftEmailTable(int numEmailsDraft, List<AdminDraftEmailRow> rows) {
        this.numEmailsDraft = numEmailsDraft;
        this.rows = rows;
    }

    public int getNumEmailsDraft() {
        return numEmailsDraft;
    }

    public List<AdminDraftEmailRow> getRows() {
        return rows;
    }
}
