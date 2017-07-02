package teammates.common.datatransfer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;

/**
 * The search result bundle for {@link FeedbackResponseCommentAttributes}.
 */
public class FeedbackResponseCommentSearchResultBundle extends SearchResultBundle {

    public Map<String, List<FeedbackResponseCommentAttributes>> comments = new HashMap<>();
    public Map<String, List<FeedbackResponseAttributes>> responses = new HashMap<>();
    public Map<String, List<FeedbackQuestionAttributes>> questions = new HashMap<>();
    public Map<String, FeedbackSessionAttributes> sessions = new HashMap<>();
    public Map<String, String> commentGiverTable = new HashMap<>();
    public Map<String, String> responseGiverTable = new HashMap<>();
    public Map<String, String> responseRecipientTable = new HashMap<>();
    public Set<String> instructorEmails = new HashSet<>();
    public Map<String, String> instructorEmailNameTable = new HashMap<>();

}
