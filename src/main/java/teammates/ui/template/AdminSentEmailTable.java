package teammates.ui.template;

import java.util.List;

public class AdminSentEmailTable {
    private int numEmailsSent;
    private List<AdminSentEmailRow> rows;

    public AdminSentEmailTable(int numEmailsSent, List<AdminSentEmailRow> rows) {
        this.numEmailsSent = numEmailsSent;
        this.rows = rows;
    }

    public int getNumEmailsSent() {
        return numEmailsSent;
    }

    public List<AdminSentEmailRow> getRows() {
        return rows;
    }
}
