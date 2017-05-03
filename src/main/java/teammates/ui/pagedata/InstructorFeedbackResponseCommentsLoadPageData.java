package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.template.FeedbackResponseCommentRow;
import teammates.ui.template.InstructorFeedbackResponseComment;

public class InstructorFeedbackResponseCommentsLoadPageData extends PageData {

    private InstructorAttributes instructor;
    private int numberOfPendingComments;
    private int feedbackSessionIndex;
    private Map<FeedbackQuestionAttributes, List<InstructorFeedbackResponseComment>> questionCommentsMap;

    public InstructorFeedbackResponseCommentsLoadPageData(AccountAttributes account, int feedbackSessionIndex,
            int numberOfPendingComments, InstructorAttributes currentInstructor, FeedbackSessionResultsBundle bundle) {
        super(account);
        this.feedbackSessionIndex = feedbackSessionIndex;
        this.numberOfPendingComments = numberOfPendingComments;
        this.instructor = currentInstructor;
        init(bundle);
    }

    private void init(FeedbackSessionResultsBundle bundle) {
        // no visible questions / responses with comments
        if (bundle == null) {
            return;
        }

        questionCommentsMap = new LinkedHashMap<>();

        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responseEntries
                 : bundle.getQuestionResponseMap().entrySet()) {
            FeedbackQuestionAttributes question = bundle.questions.get(responseEntries.getKey().getId());
            Map<FeedbackParticipantType, Boolean> responseVisibilityMap = getResponseVisibilityMap(question);

            List<InstructorFeedbackResponseComment> responseCommentList = buildInstructorFeedbackResponseComments(
                    responseEntries.getValue(), bundle, question, responseVisibilityMap);

            questionCommentsMap.put(question, responseCommentList);
        }
    }

    private List<InstructorFeedbackResponseComment> buildInstructorFeedbackResponseComments(
            List<FeedbackResponseAttributes> responses, FeedbackSessionResultsBundle bundle,
            FeedbackQuestionAttributes question, Map<FeedbackParticipantType, Boolean> responseVisibilityMap) {
        List<InstructorFeedbackResponseComment> responseCommentList = new ArrayList<>();

        for (FeedbackResponseAttributes response : responses) {
            String giverName = bundle.getGiverNameForResponse(response);
            String giverTeamName = bundle.getTeamNameForEmail(response.giver);
            giverName = bundle.appendTeamNameToName(giverName, giverTeamName);

            String recipientName = bundle.getRecipientNameForResponse(response);
            String recipientTeamName = bundle.getTeamNameForEmail(response.recipient);
            recipientName = bundle.appendTeamNameToName(recipientName, recipientTeamName);

            String responseAnswerHtml =
                    response.getResponseDetails().getAnswerHtml(question.getQuestionDetails());

            boolean instructorAllowedToAddComment = isInstructorAllowedForSectionalPrivilege(
                    response.giverSection, response.recipientSection, response.feedbackSessionName,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);

            List<FeedbackResponseCommentAttributes> feedbackResponseCommentsAttributes =
                    bundle.responseComments.get(response.getId());

            List<FeedbackResponseCommentRow> frcList = buildFeedbackResponseComments(
                    feedbackResponseCommentsAttributes, question, response, giverName, recipientName,
                    responseVisibilityMap, bundle.feedbackSession);

            FeedbackResponseCommentRow feedbackResponseCommentAdd = buildFeedbackResponseCommentAdd(
                    question, response, responseVisibilityMap, giverName, recipientName);

            responseCommentList.add(new InstructorFeedbackResponseComment(
                    giverName, recipientName, frcList, responseAnswerHtml,
                    instructorAllowedToAddComment, feedbackResponseCommentAdd));
        }

        return responseCommentList;
    }

    private List<FeedbackResponseCommentRow> buildFeedbackResponseComments(
            List<FeedbackResponseCommentAttributes> feedbackResponseCommentsAttributes,
            FeedbackQuestionAttributes question, FeedbackResponseAttributes response,
            String giverName, String recipientName, Map<FeedbackParticipantType, Boolean> responseVisibilities,
            FeedbackSessionAttributes feedbackSession) {
        List<FeedbackResponseCommentRow> comments = new ArrayList<>();

        for (FeedbackResponseCommentAttributes frca : feedbackResponseCommentsAttributes) {
            boolean isInstructorGiver = frca.giverEmail.equals(instructor.email);
            boolean isInstructorAllowedToModify = isInstructorAllowedForSectionalPrivilege(
                    response.giverSection, response.recipientSection, response.feedbackSessionName,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
            boolean allowedToEditAndDeleteComment = isInstructorGiver || isInstructorAllowedToModify;

            String showCommentToString = getResponseCommentVisibilityString(frca, question);
            String showGiverNameToString = getResponseCommentGiverNameVisibilityString(frca, question);

            String whoCanSeeComment = null;
            boolean isVisibilityIconShown = false;
            boolean isNotificationIconShown = false;
            if (feedbackSession.isPublished()) {
                boolean responseCommentPublicToRecipient = !frca.showCommentTo.isEmpty();
                isVisibilityIconShown = responseCommentPublicToRecipient;

                if (isVisibilityIconShown) {
                    whoCanSeeComment = getTypeOfPeopleCanViewComment(frca, question);
                }

                isNotificationIconShown = frca.sendingState == CommentSendingState.PENDING;
            }

            FeedbackResponseCommentRow frc = new FeedbackResponseCommentRow(
                    frca, frca.giverEmail, giverName, recipientName, showCommentToString,
                    showGiverNameToString, responseVisibilities);

            frc.setExtraClass(getExtraClass(frca.giverEmail, instructor.email, isVisibilityIconShown));

            if (allowedToEditAndDeleteComment) {
                frc.enableEdit();
                frc.enableDelete();
                frc.enableEditDeleteOnHover();
            }
            if (isVisibilityIconShown) {
                frc.enableVisibilityIcon(whoCanSeeComment);
            }
            if (isNotificationIconShown) {
                frc.enableNotificationIcon();
            }

            comments.add(frc);
        }

        return comments;
    }

    private boolean isInstructorAllowedForSectionalPrivilege(String giverSection, String recipientSection,
            String feedbackSessionName, String privilege) {
        return instructor != null
               && instructor.isAllowedForPrivilege(
                   giverSection, feedbackSessionName, privilege)
               && instructor.isAllowedForPrivilege(
                   recipientSection, feedbackSessionName, privilege);
    }

    private FeedbackResponseCommentRow buildFeedbackResponseCommentAdd(FeedbackQuestionAttributes question,
            FeedbackResponseAttributes response, Map<FeedbackParticipantType, Boolean> responseVisibilityMap,
            String giverName, String recipientName) {
        FeedbackResponseCommentAttributes frca = new FeedbackResponseCommentAttributes(
                question.courseId, question.feedbackSessionName, question.getFeedbackQuestionId(), response.getId());

        FeedbackParticipantType[] relevantTypes = {
                FeedbackParticipantType.GIVER,
                FeedbackParticipantType.RECEIVER,
                FeedbackParticipantType.OWN_TEAM_MEMBERS,
                FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                FeedbackParticipantType.STUDENTS,
                FeedbackParticipantType.INSTRUCTORS
        };

        frca.showCommentTo = new ArrayList<>();
        frca.showGiverNameTo = new ArrayList<>();
        for (FeedbackParticipantType type : relevantTypes) {
            if (isResponseCommentVisibleTo(question, type)) {
                frca.showCommentTo.add(type);
            }
            if (isResponseCommentGiverNameVisibleTo(question, type)) {
                frca.showGiverNameTo.add(type);
            }
        }

        return new FeedbackResponseCommentRow(
                frca, giverName, recipientName, getResponseCommentVisibilityString(question),
                getResponseCommentGiverNameVisibilityString(question), responseVisibilityMap);
    }

    private Map<FeedbackParticipantType, Boolean> getResponseVisibilityMap(FeedbackQuestionAttributes question) {
        Map<FeedbackParticipantType, Boolean> responseVisibilityMap = new HashMap<>();
        boolean isResponseVisibleToGiver =
                question.isResponseVisibleTo(FeedbackParticipantType.GIVER);

        boolean isResponseVisibleToRecipient =
                question.recipientType != FeedbackParticipantType.SELF
                && question.recipientType != FeedbackParticipantType.NONE
                && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER);

        boolean isResponseVisibleToGiverTeam =
                question.giverType != FeedbackParticipantType.INSTRUCTORS
                && question.giverType != FeedbackParticipantType.SELF
                && question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS);

        boolean isResponseVisibleToRecipientTeam =
                question.recipientType != FeedbackParticipantType.INSTRUCTORS
                && question.recipientType != FeedbackParticipantType.SELF
                && question.recipientType != FeedbackParticipantType.NONE
                && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);

        boolean isResponseVisibleToStudents =
                question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS);

        boolean isResponseVisibleToInstructors =
                question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS);

        responseVisibilityMap.put(FeedbackParticipantType.GIVER, isResponseVisibleToGiver);
        responseVisibilityMap.put(FeedbackParticipantType.RECEIVER, isResponseVisibleToRecipient);
        responseVisibilityMap.put(FeedbackParticipantType.OWN_TEAM_MEMBERS, isResponseVisibleToGiverTeam);
        responseVisibilityMap.put(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS, isResponseVisibleToRecipientTeam);
        responseVisibilityMap.put(FeedbackParticipantType.STUDENTS, isResponseVisibleToStudents);
        responseVisibilityMap.put(FeedbackParticipantType.INSTRUCTORS, isResponseVisibleToInstructors);

        return responseVisibilityMap;
    }

    private String getExtraClass(String giverEmail, String instructorEmail, boolean isPublic) {

        return " giver_display-by-"
             + (giverEmail.equals(instructorEmail) ? "you" : "others")
             + " status_display-"
             + (isPublic ? "public" : "private");
    }

    public int getNumberOfPendingComments() {
        return numberOfPendingComments;
    }

    public Map<FeedbackQuestionAttributes, List<InstructorFeedbackResponseComment>> getQuestionCommentsMap() {
        return questionCommentsMap;
    }

    public int getFeedbackSessionIndex() {
        return feedbackSessionIndex;
    }
}
