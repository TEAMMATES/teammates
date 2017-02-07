package teammates.common.datatransfer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The search result bundle for {@link FeedbackResponseCommentAttributes}.
 */
public class FeedbackResponseCommentSearchResultBundle extends SearchResultBundle {
    
    public Map<String, List<FeedbackResponseCommentAttributes>> comments =
            new HashMap<String, List<FeedbackResponseCommentAttributes>>();
    public Map<String, List<FeedbackResponseAttributes>> responses = new HashMap<String, List<FeedbackResponseAttributes>>();
    public Map<String, List<FeedbackQuestionAttributes>> questions = new HashMap<String, List<FeedbackQuestionAttributes>>();
    public Map<String, FeedbackSessionAttributes> sessions = new HashMap<String, FeedbackSessionAttributes>();
    public Map<String, String> commentGiverTable = new HashMap<String, String>();
    public Map<String, String> responseGiverTable = new HashMap<String, String>();
    public Map<String, String> responseRecipientTable = new HashMap<String, String>();
    public Set<String> instructorEmails = new HashSet<String>();
    
}
