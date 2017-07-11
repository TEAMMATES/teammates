package teammates.ui.controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.InstructorSearchPageData;

/**
 * Action: Showing the InstructorSearchPage for an instructor.
 */
public class InstructorSearchPageAction extends Action {

    @Override
    protected ActionResult execute() {
        gateKeeper.verifyInstructorPrivileges(account);
        String searchKey = getRequestParamValue(Const.ParamsNames.SEARCH_KEY);
        if (searchKey == null) {
            searchKey = "";
        }

        int numberOfSearchOptions = 0;

        boolean isSearchForStudents = getRequestParamAsBoolean(Const.ParamsNames.SEARCH_STUDENTS);
        if (isSearchForStudents) {
            numberOfSearchOptions++;
        }

        boolean isSearchCommentForResponses = getRequestParamAsBoolean(Const.ParamsNames.SEARCH_COMMENTS_FOR_RESPONSES);
        if (isSearchCommentForResponses) {
            numberOfSearchOptions++;
        }

        FeedbackResponseCommentSearchResultBundle frCommentSearchResults = new FeedbackResponseCommentSearchResultBundle();
        StudentSearchResultBundle studentSearchResults = new StudentSearchResultBundle();
        int totalResultsSize = 0;

        if (searchKey.isEmpty() || numberOfSearchOptions == 0) {
            //display search tips and tutorials
            statusToUser.add(new StatusMessage(Const.StatusMessages.INSTRUCTOR_SEARCH_TIPS, StatusMessageColor.INFO));
        } else {
            //Start searching
            List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(account.googleId);
            if (isSearchCommentForResponses) {
                frCommentSearchResults = logic.searchFeedbackResponseComments(searchKey, instructors);
            }
            if (isSearchForStudents) {
                studentSearchResults = logic.searchStudents(searchKey, instructors);
            }

            totalResultsSize = frCommentSearchResults.numberOfResults + studentSearchResults.numberOfResults;

            Set<String> instructorEmails = new HashSet<>();

            for (InstructorAttributes instructor : instructors) {
                instructorEmails.add(instructor.email);
            }
            totalResultsSize = filterFeedbackResponseCommentResults(frCommentSearchResults, instructors, totalResultsSize);
            removeQuestionsAndResponsesWithoutComments(frCommentSearchResults);

            if (totalResultsSize == 0) {
                statusToUser.add(new StatusMessage(Const.StatusMessages.INSTRUCTOR_SEARCH_NO_RESULTS,
                                                   StatusMessageColor.WARNING));
            }
        }

        InstructorSearchPageData data = new InstructorSearchPageData(account, sessionToken);
        data.init(frCommentSearchResults, studentSearchResults, searchKey, isSearchCommentForResponses, isSearchForStudents);

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_SEARCH, data);
    }

    private int filterFeedbackResponseCommentResults(
            FeedbackResponseCommentSearchResultBundle frCommentSearchResults,
            List<InstructorAttributes> instructors, int totalResultsSize) {

        Iterator<Entry<String, List<FeedbackResponseAttributes>>> iterFr =
                frCommentSearchResults.responses.entrySet().iterator();

        int filteredResultsSize = totalResultsSize;
        while (iterFr.hasNext()) {
            List<FeedbackResponseAttributes> frs = iterFr.next().getValue();
            Iterator<FeedbackResponseAttributes> fr = frs.iterator();

            while (fr.hasNext()) {
                FeedbackResponseAttributes response = fr.next();
                InstructorAttributes instructor = this.getInstructorForCourseId(response.courseId, instructors);

                boolean isVisibleResponse = true;
                boolean isNotAllowedForInstructor =
                            instructor == null
                            || !instructor.isAllowedForPrivilege(
                                    response.giverSection, response.feedbackSessionName,
                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS)
                            || !instructor.isAllowedForPrivilege(
                                    response.recipientSection, response.feedbackSessionName,
                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS);

                if (isNotAllowedForInstructor) {
                    isVisibleResponse = false;
                }
                if (!isVisibleResponse) {
                    int sizeOfCommentList = frCommentSearchResults.comments.get(response.getId()).size();
                    filteredResultsSize -= sizeOfCommentList;
                    // TODO: also need to decrease the size for (fr)CommentSearchResults|studentSearchResults
                    frCommentSearchResults.comments.remove(response.getId());
                    fr.remove();
                }
            }
        }

        Set<String> emailList = frCommentSearchResults.instructorEmails;
        Iterator<Entry<String, List<FeedbackQuestionAttributes>>> iterQn =
                frCommentSearchResults.questions.entrySet().iterator();
        while (iterQn.hasNext()) {
            String fsName = iterQn.next().getKey();
            List<FeedbackQuestionAttributes> questionList = frCommentSearchResults.questions.get(fsName);

            for (int i = questionList.size() - 1; i >= 0; i--) {
                FeedbackQuestionAttributes question = questionList.get(i);
                List<FeedbackResponseAttributes> responseList = frCommentSearchResults.responses.get(question.getId());

                for (int j = responseList.size() - 1; j >= 0; j--) {
                    FeedbackResponseAttributes response = responseList.get(j);
                    List<FeedbackResponseCommentAttributes> commentList =
                            frCommentSearchResults.comments.get(response.getId());

                    for (int k = commentList.size() - 1; k >= 0; k--) {
                        FeedbackResponseCommentAttributes comment = commentList.get(k);

                        if (emailList.contains(comment.giverEmail)) {
                            continue;
                        }

                        boolean isVisibilityFollowingFeedbackQuestion = comment.isVisibilityFollowingFeedbackQuestion;
                        boolean isVisibleToGiver = isVisibilityFollowingFeedbackQuestion
                                                 || comment.isVisibleTo(FeedbackParticipantType.GIVER);

                        if (isVisibleToGiver && emailList.contains(response.giver)) {
                            continue;
                        }

                        boolean isVisibleToReceiver = isVisibilityFollowingFeedbackQuestion
                                                    ? question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)
                                                    : comment.isVisibleTo(FeedbackParticipantType.RECEIVER);

                        if (isVisibleToReceiver && emailList.contains(response.recipient)) {
                            continue;
                        }

                        boolean isVisibleToInstructor = isVisibilityFollowingFeedbackQuestion
                                                      ? question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS)
                                                      : comment.isVisibleTo(FeedbackParticipantType.INSTRUCTORS);

                        if (isVisibleToInstructor) {
                            continue;
                        }
                        commentList.remove(k);
                    }
                    if (commentList.isEmpty()) {
                        responseList.remove(j);
                    }
                }
                if (responseList.isEmpty()) {
                    questionList.remove(i);
                }
            }
            if (questionList.isEmpty()) {
                iterQn.remove();
            }
        }

        return filteredResultsSize;
    }

    private void removeQuestionsAndResponsesWithoutComments(
            FeedbackResponseCommentSearchResultBundle frCommentSearchResults) {
        Iterator<Entry<String, List<FeedbackQuestionAttributes>>> fqsIter =
                frCommentSearchResults.questions.entrySet().iterator();

        while (fqsIter.hasNext()) {
            Iterator<FeedbackQuestionAttributes> fqIter = fqsIter.next().getValue().iterator();

            while (fqIter.hasNext()) {
                FeedbackQuestionAttributes fq = fqIter.next();
                if (frCommentSearchResults.responses.get(fq.getId()).isEmpty()) {
                    fqIter.remove();
                }
            }
        }
    }

    private InstructorAttributes getInstructorForCourseId(String courseId, List<InstructorAttributes> instructors) {
        for (InstructorAttributes instructor : instructors) {
            if (instructor.courseId.equals(courseId)) {
                return instructor;
            }
        }

        return null;
    }

}
