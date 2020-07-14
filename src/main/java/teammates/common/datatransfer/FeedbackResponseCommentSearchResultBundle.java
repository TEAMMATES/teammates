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

    /**
     * Session name to FeedbackSessionAttributes map.
     */
    public Map<String, FeedbackSessionAttributes> sessions = new HashMap<>();
    /**
     * Session name to FeedbackQuestionAttributes map.
     */
    public Map<String, List<FeedbackQuestionAttributes>> questions = new HashMap<>();
    /**
     * Question id to FeedbackResponseAttributes map.
     */
    public Map<String, List<FeedbackResponseAttributes>> responses = new HashMap<>();
    /**
     * Response id to FeedbackResponseCommentAttributes map.
     */
    public Map<String, List<FeedbackResponseCommentAttributes>> comments = new HashMap<>();
    /**
     * Response id to response giver name.
     */
    public Map<String, String> responseGiverTable = new HashMap<>();
    /**
     * Response id to response recipient name.
     */
    public Map<String, String> responseRecipientTable = new HashMap<>();
    /**
     * Comment id to comment giver name.
     */
    public Map<String, String> commentGiverTable = new HashMap<>();
    /**
     * Comment giver email to comment giver name.
     */
    public Map<String, String> commentGiverEmailToNameTable = new HashMap<>();

    public Set<String> instructorEmails = new HashSet<>();

}
