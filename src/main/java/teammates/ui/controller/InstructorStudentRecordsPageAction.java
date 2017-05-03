package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.attributes.CommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.InstructorStudentRecordsPageData;

public class InstructorStudentRecordsPageAction extends Action {

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId));

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        Assumption.assertNotNull(studentEmail);

        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);

        if (student == null) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_NOT_FOUND_FOR_RECORDS,
                                               StatusMessageColor.DANGER));
            isError = true;
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        }

        String showCommentBox = getRequestParamValue(Const.ParamsNames.SHOW_COMMENT_BOX);

        List<CommentAttributes> comments =
                logic.getCommentsForReceiverVisibleToInstructor(
                        courseId, CommentParticipantType.PERSON, studentEmail, instructor.email);

        CommentAttributes.sortCommentsByCreationTimeDescending(comments);

        Map<String, List<CommentAttributes>> giverEmailToCommentsMap =
                mapCommentsToGiverEmail(comments, instructor);

        Map<String, String> giverEmailToGiverNameMap = mapGiverNameToGiverEmail(courseId, giverEmailToCommentsMap.keySet());

        List<FeedbackSessionAttributes> sessions = logic.getFeedbackSessionsListForInstructor(account.googleId, false);

        filterFeedbackSessions(courseId, sessions, instructor, student);

        Collections.sort(sessions, FeedbackSessionAttributes.DESCENDING_ORDER);

        StudentProfileAttributes studentProfile = null;

        boolean isInstructorAllowedToViewStudent = instructor.isAllowedForPrivilege(student.section,
                                                        Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
        boolean isStudentWithProfile = !student.googleId.isEmpty();
        if (isInstructorAllowedToViewStudent && isStudentWithProfile) {
            studentProfile = logic.getStudentProfile(student.googleId);
            Assumption.assertNotNull(studentProfile);
        } else {
            if (student.googleId.isEmpty()) {
                statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_NOT_JOINED_YET_FOR_RECORDS,
                                                   StatusMessageColor.WARNING));
            } else if (!isInstructorAllowedToViewStudent) {
                statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_PROFILE_UNACCESSIBLE_TO_INSTRUCTOR,
                                                   StatusMessageColor.WARNING));
            }
        }

        if (sessions.isEmpty() && comments.isEmpty()) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.INSTRUCTOR_NO_STUDENT_RECORDS,
                                               StatusMessageColor.WARNING));
        }

        List<String> sessionNames = new ArrayList<String>();
        for (FeedbackSessionAttributes fsa : sessions) {
            sessionNames.add(fsa.getFeedbackSessionName());
        }

        InstructorStudentRecordsPageData data =
                new InstructorStudentRecordsPageData(
                        account, student, courseId, showCommentBox, studentProfile,
                        giverEmailToCommentsMap, giverEmailToGiverNameMap, sessionNames, instructor);

        statusToAdmin = "instructorStudentRecords Page Load<br>"
                      + "Viewing <span class=\"bold\">" + studentEmail + "'s</span> records "
                      + "for Course <span class=\"bold\">[" + courseId + "]</span><br>"
                      + "Number of sessions: " + sessions.size() + "<br>"
                      + "Student Profile: " + (studentProfile == null ? "No Profile"
                                                                      : studentProfile.toString());

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_STUDENT_RECORDS, data);
    }

    private void filterFeedbackSessions(String courseId, List<FeedbackSessionAttributes> feedbacks,
                                        InstructorAttributes instructor, StudentAttributes student) {
        Iterator<FeedbackSessionAttributes> iterFs = feedbacks.iterator();
        while (iterFs.hasNext()) {
            FeedbackSessionAttributes tempFs = iterFs.next();
            if (!tempFs.getCourseId().equals(courseId)
                    || !instructor.isAllowedForPrivilege(student.section, tempFs.getSessionName(),
                                                         Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS)) {
                iterFs.remove();
            }
        }
    }

    /**
     * Maps emails of instructors to the comments they gave.
     * @return A map with instructor email => comments mappings.
     */
    private Map<String, List<CommentAttributes>> mapCommentsToGiverEmail(
            List<CommentAttributes> comments, InstructorAttributes instructor) {
        Map<String, List<CommentAttributes>> giverEmailToCommentsMap =
                new TreeMap<String, List<CommentAttributes>>();
        // add an element representing the current instructor to allow "no comments" to display correctly
        giverEmailToCommentsMap.put(InstructorStudentRecordsPageData.COMMENT_GIVER_NAME_THAT_COMES_FIRST,
                                    new ArrayList<CommentAttributes>());

        for (CommentAttributes comment : comments) {
            boolean isCurrentInstructorGiver = comment.giverEmail.equals(instructor.email);
            String key = isCurrentInstructorGiver
                       ? InstructorStudentRecordsPageData.COMMENT_GIVER_NAME_THAT_COMES_FIRST
                       : comment.giverEmail;

            List<CommentAttributes> commentList = giverEmailToCommentsMap.get(key);
            if (commentList == null) {
                commentList = new ArrayList<CommentAttributes>();
                giverEmailToCommentsMap.put(key, commentList);
            }
            commentList.add(comment);
        }
        return giverEmailToCommentsMap;
    }

    /**
     * Maps emails of instructors giving the comments to their names.
     * @return A map with instructor email => instructor name mappings.
     */
    private Map<String, String> mapGiverNameToGiverEmail(String courseId, Set<String> giverEmails) {
        Map<String, String> giverEmailToGiverNameMap = new HashMap<String, String>();
        giverEmailToGiverNameMap.put(InstructorStudentRecordsPageData.COMMENT_GIVER_NAME_THAT_COMES_FIRST,
                                     Const.DISPLAYED_NAME_FOR_SELF_IN_COMMENTS);

        // keep the original naming of an anonymous giver
        giverEmailToGiverNameMap.put(Const.DISPLAYED_NAME_FOR_ANONYMOUS_COMMENT_PARTICIPANT,
                                     Const.DISPLAYED_NAME_FOR_ANONYMOUS_COMMENT_PARTICIPANT);
        for (String giverEmail : giverEmails) {
            if (!giverEmailToGiverNameMap.containsKey(giverEmail)) {
                InstructorAttributes giverInstructor = logic.getInstructorForEmail(courseId, giverEmail);
                Assumption.assertNotNull(giverInstructor);
                giverEmailToGiverNameMap.put(giverEmail, giverInstructor.displayedName + " " + giverInstructor.name);
            }
        }
        return giverEmailToGiverNameMap;
    }

}
