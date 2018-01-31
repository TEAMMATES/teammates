package teammates.ui.template;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;

public class FeedbackResponsePersonRow {

    private String personName;
    private List<FeedbackResponseRow> responses;

    public FeedbackResponsePersonRow(int fbIndex, int personIndex, String personName, String personType,
                                     List<FeedbackResponseAttributes> responses,
                                     FeedbackSessionResultsBundle results) {
        this.personName = personName;
        this.responses = responses.stream().map(response -> new FeedbackResponseRow(fbIndex, personIndex, personType,
                response, results)).collect(Collectors.toList());
    }

    public String getPersonName() {
        return personName;
    }

    public List<FeedbackResponseRow> getResponses() {
        return responses;
    }

}
