package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;

public class FeedbackResponsePersonRow {

    private String personName;
    private List<FeedbackResponseRow> responses;

    public FeedbackResponsePersonRow(String personName, String personType,
                                     List<FeedbackResponseAttributes> responses,
                                     FeedbackSessionResultsBundle results, Boolean showPcRow) {
        this.personName = personName;
        this.responses = new ArrayList<>();
        for (FeedbackResponseAttributes response : responses) {
            this.responses.add(new FeedbackResponseRow(personType, response, results, showPcRow));
        }
    }

    public String getPersonName() {
        return personName;
    }

    public List<FeedbackResponseRow> getResponses() {
        return responses;
    }

}
