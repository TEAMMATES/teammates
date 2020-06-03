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
 * Mapping details retrieved from {@link teammates.storage.search.FeedbackResponseCommentSearchDocument#fromResults}.
 */
public class FeedbackResponseCommentSearchResultBundle extends SearchResultBundle {

    // session name to session map
    public Map<String, FeedbackSessionAttributes> sessions = new HashMap<>();
    // session name to questions map
    public Map<String, List<FeedbackQuestionAttributes>> questions = new HashMap<>();
    // questionId to responses map
    public Map<String, List<FeedbackResponseAttributes>> responses = new HashMap<>();
    // responseId to comment map
    public Map<String, List<FeedbackResponseCommentAttributes>> comments = new HashMap<>();
    // responseId to response giver name
    public Map<String, String> responseGiverTable = new HashMap<>();
    // responseId to response recipient name
    public Map<String, String> responseRecipientTable = new HashMap<>();
    // commentId to comment giver name
    public Map<String, String> commentGiverTable = new HashMap<>();
    // comment giver email to comment giver name
    public Map<String, String> commentGiverEmailToNameTable = new HashMap<>();

    public Set<String> instructorEmails = new HashSet<>();

}
