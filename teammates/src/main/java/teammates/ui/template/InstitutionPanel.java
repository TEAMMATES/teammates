package teammates.ui.template;

import java.util.List;

public class InstitutionPanel {
    private String institutionName;
    private List<AdminFeedbackSessionRow> feedbackSessionRows;

    public InstitutionPanel(String institutionName, List<AdminFeedbackSessionRow> feedbackSessionRows) {
        this.institutionName = institutionName;
        this.feedbackSessionRows = feedbackSessionRows;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public List<AdminFeedbackSessionRow> getFeedbackSessionRows() {
        return feedbackSessionRows;
    }
}
