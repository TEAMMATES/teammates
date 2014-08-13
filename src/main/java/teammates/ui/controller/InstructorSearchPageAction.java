package teammates.ui.controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentRecipientType;
import teammates.common.datatransfer.CommentSearchResultBundle;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

/**
 * Action: Showing the InstructorSearchPage for an instructor
 */
public class InstructorSearchPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        new GateKeeper().verifyInstructorPrivileges(account);
        String searchKey = getRequestParamValue(Const.ParamsNames.SEARCH_KEY);
        if(searchKey == null){
            searchKey = "";
        }
        
        int numberOfSearchOptions = 0;

        boolean isSearchForStudents = getRequestParamAsBoolean(Const.ParamsNames.SEARCH_STUDENTS);
        if(isSearchForStudents){
            numberOfSearchOptions++;
        }

        boolean isSearchCommentForStudents = getRequestParamAsBoolean(Const.ParamsNames.SEARCH_COMMENTS_FOR_STUDENTS);
        if(isSearchCommentForStudents){
            numberOfSearchOptions++;
        }
        
        boolean isSearchCommentForResponses = getRequestParamAsBoolean(Const.ParamsNames.SEARCH_COMMENTS_FOR_RESPONSES);
        if(isSearchCommentForResponses){
            numberOfSearchOptions++;
        }
        
        CommentSearchResultBundle commentSearchResults = new CommentSearchResultBundle();
        FeedbackResponseCommentSearchResultBundle frCommentSearchResults = new FeedbackResponseCommentSearchResultBundle();
        StudentSearchResultBundle studentSearchResults = new StudentSearchResultBundle();
        int totalResultsSize = 0;
        
        if(!searchKey.isEmpty() && numberOfSearchOptions != 0){
            //Start searching
            if(isSearchCommentForStudents){
                commentSearchResults = logic.searchComment(searchKey, account.googleId, "");
            }
            if(isSearchCommentForResponses){
                frCommentSearchResults = logic.searchFeedbackResponseComments(searchKey, account.googleId, "");
            }
            if(isSearchForStudents){
                studentSearchResults = logic.searchStudents(searchKey, account.googleId, "");
            }
            
            totalResultsSize = commentSearchResults.getResultSize() + frCommentSearchResults.getResultSize() + studentSearchResults.getResultSize();
            
            List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(account.googleId);
            Set<String> instructorEmails = new HashSet<String>();
            for (InstructorAttributes instructor : instructors) {
                instructorEmails.add(instructor.email);
            }
            totalResultsSize = filterCommentSearchResults(commentSearchResults,
                    totalResultsSize, instructors, instructorEmails);
            totalResultsSize = filterFeedbackResponseCommentResults(frCommentSearchResults,
                    instructors, totalResultsSize);
            
            if(totalResultsSize == 0){
                //TODO: put this status msg into Const
                statusToUser.add("No results found.");
            }
        } else {
            //display search tips and tutorials
            statusToUser.add("Search Tips:<br>"
                    + "<ul>"
                    + "<li>Put more keywords to search for more precise results.</li>"
                    + "<li>Put quotation marks around words <b>\"[any word]\"</b> to search for an exact phrase in an exact order.</li>"
                    + "</ul>");
        }
        
        InstructorSearchPageData data = new InstructorSearchPageData(account);
        data.searchKey = searchKey;
        data.commentSearchResultBundle = commentSearchResults;
        data.feedbackResponseCommentSearchResultBundle = frCommentSearchResults;
        data.studentSearchResultBundle = studentSearchResults;
        data.totalResultsSize = totalResultsSize;
        //TODO: put the followings into a map
        data.isSearchCommentForStudents = isSearchCommentForStudents;
        data.isSearchCommentForResponses = isSearchCommentForResponses;
        data.isSearchForStudents = isSearchForStudents;

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_SEARCH, data);
    }

    private int filterFeedbackResponseCommentResults(
            FeedbackResponseCommentSearchResultBundle frCommentSearchResults,
            List<InstructorAttributes> instructors, int totalResultsSize) {
        Iterator<Entry<String, List<FeedbackResponseAttributes>>> iterFr = frCommentSearchResults.responses.entrySet().iterator();
        while (iterFr.hasNext()) {
            List<FeedbackResponseAttributes> frs = iterFr.next().getValue();
            Iterator<FeedbackResponseAttributes> fr = frs.iterator();
            while (fr.hasNext()) {
                FeedbackResponseAttributes response = fr.next();
                InstructorAttributes instructor = this.getInstructorForCourseId(response.courseId, instructors);
                if (instructor == null || (!(instructor.isAllowedForPrivilege(response.giverSection,
                    response.feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS))
                    || !(instructor.isAllowedForPrivilege(response.giverSection,
                            response.feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS)))) {
                    int sizeOfCommentList = frCommentSearchResults.comments.get(response.getId()).size();
                    totalResultsSize -= sizeOfCommentList;
                    //TODO: also need to decrease the size for commentSearchResults|frCommentSearchResults|studentSearchResults
                    frCommentSearchResults.comments.remove(response.getId());
                    fr.remove();
                }
            }
        }
        
        return totalResultsSize;
    }

    private int filterCommentSearchResults(
            CommentSearchResultBundle commentSearchResults,
            int totalResultsSize, List<InstructorAttributes> instructors,
            Set<String> instructorEmails) {
        Iterator<Entry<String, List<CommentAttributes>>> iter = commentSearchResults.giverCommentTable.entrySet().iterator();
        while (iter.hasNext()) {
            List<CommentAttributes> commentList = iter.next().getValue();
            if (!commentList.isEmpty() 
                    && !isInstructorAllowedToViewComment(commentList.get(0), instructorEmails, instructors)) {
                iter.remove();
                totalResultsSize -= commentList.size();
            }
        }
        return totalResultsSize;
    }
    
    private InstructorAttributes getInstructorForCourseId(String courseId, List<InstructorAttributes> instructors) {
        for (InstructorAttributes instructor : instructors) {
            if (instructor.courseId.equals(courseId)) {
                return instructor;
            }
        }
        
        return null;
    }

    private boolean isInstructorAllowedToViewComment(
            CommentAttributes commentAttributes, Set<String> instructorEmails, List<InstructorAttributes> instructors) {
        if (instructorEmails.contains(commentAttributes.giverEmail)) {
            return true;
        }
        for (InstructorAttributes instructor : instructors) {
            if (instructor.courseId.equals(commentAttributes.courseId)) {
                boolean isForSection = true;
                String section = "None";
                String recipient = "";
                if (commentAttributes.recipients.size() == 0) {
                    // prevent error--however, this should never happen unless there is corruption of data
                    return false;
                }
                for (String recipientInSet : commentAttributes.recipients) {
                    recipient = recipientInSet;
                    break;
                }
                if (commentAttributes.recipientType == CommentRecipientType.PERSON) {
                    StudentAttributes student = logic.getStudentForEmail(commentAttributes.courseId, recipient);
                    if (student == null) {
                        // error checking--comment that is for a student who is deleted or whose email got edited
                        logic.deleteComment(commentAttributes);
                        return false;
                    }
                    section = student.section;
                } else if (commentAttributes.recipientType == CommentRecipientType.TEAM) {
                    List<StudentAttributes> studentsInTeam = logic.getStudentsForTeam(recipient, commentAttributes.courseId);
                    if (studentsInTeam.isEmpty()) {
                        // error checking--no students in the team, delete the comment
                        logic.deleteComment(commentAttributes);
                        return false;
                    }
                    section = studentsInTeam.get(0).section;
                } else if (commentAttributes.recipientType == CommentRecipientType.SECTION) {
                    section = recipient;
                } else if (commentAttributes.recipientType == CommentRecipientType.COURSE) {
                    isForSection = false;
                }
                if (isForSection) {
                    return instructor.isAllowedForPrivilege(section, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS);
                } else {
                    return instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS);
                }
            }
        }
        
        return false;
    }
}
