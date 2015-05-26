package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class FeedbackQuestionBundle {
    public FeedbackSessionAttributes feedbackSession;
    public FeedbackQuestionAttributes question;
    public List<FeedbackResponseAttributes> responseList;
    public Map<String, String> recipientList;

    public FeedbackQuestionBundle(FeedbackSessionAttributes feedbackSession,
                                  FeedbackQuestionAttributes question,
                                  List<FeedbackResponseAttributes> responseList,
                                  Map<String, String> recipientList) {
        this.feedbackSession = feedbackSession;
        this.question = question;
        this.responseList = responseList;

        List<Map.Entry<String, String>> sortedRecipientList = new ArrayList<Map.Entry<String, String>>(recipientList.entrySet());

        Collections.sort(sortedRecipientList, new recipientComparator());
        this.recipientList = new LinkedHashMap<String, String>();

        for (Map.Entry<String, String> entry : sortedRecipientList) {
            this.recipientList.put(entry.getKey(), entry.getValue());
        }
    }

    private class recipientComparator implements Comparator<Map.Entry<String, String>> {
        public int compare(Map.Entry<String, String> recipient1, Map.Entry<String, String> recipient2) {
            // Sort by value (name) first.
            if (!recipient1.getValue().equals(recipient2.getValue())) {
                return recipient1.getValue().compareTo(recipient2.getValue());
            }

            // Sort by key (email) if name is same.
            return recipient1.getKey().compareTo(recipient2.getKey());
        }
    }

    public Set<String> getRecipientEmails(String feedbackQuestionId) {
        HashSet<String> result = new HashSet<String>();

        for (Entry<String, String> entry : this.recipientList.entrySet()) {
            result.add(entry.getKey());
        }

        return result;
    }
}
