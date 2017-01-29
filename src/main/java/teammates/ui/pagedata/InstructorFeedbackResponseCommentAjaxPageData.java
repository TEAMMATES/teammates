package teammates.ui.pagedata;

import java.util.HashMap;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.ui.template.FeedbackResponseCommentRow;

/*
 * PageData: to be used for {@link FeedbackResponseCommentAttributes} in Ajax operations
 */
public class InstructorFeedbackResponseCommentAjaxPageData extends PageData {
    public FeedbackResponseCommentAttributes comment;
    public String commentId;
    public String commentTime;
    public String giverName;
    public String recipientName;
    public String showCommentToString;
    public String showGiverNameToString;
    public String errorMessage;
    public boolean isError;
    
    public InstructorFeedbackResponseCommentAjaxPageData(AccountAttributes account) {
        super(account);
    }

    public FeedbackResponseCommentRow getComment() {
        FeedbackResponseCommentRow frc =
                new FeedbackResponseCommentRow(comment, comment.giverEmail, giverName, recipientName,
                                               showCommentToString, showGiverNameToString,
                                               getResponseVisibilities());
        frc.enableEdit();
        frc.enableDelete();

        return frc;
    }
    
    private Map<FeedbackParticipantType, Boolean> getResponseVisibilities() {
        FeedbackParticipantType[] relevantTypes = {
                FeedbackParticipantType.RECEIVER,
                FeedbackParticipantType.OWN_TEAM_MEMBERS,
                FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                FeedbackParticipantType.STUDENTS,
                FeedbackParticipantType.INSTRUCTORS
        };
        
        Map<FeedbackParticipantType, Boolean> responseVisiblities = new HashMap<>();
        for (FeedbackParticipantType type : relevantTypes) {
            responseVisiblities.put(type, true);
        }
        
        return responseVisiblities;
    }

    public String[] getCommentIds() {
        return commentId.split("-");
    }
}
