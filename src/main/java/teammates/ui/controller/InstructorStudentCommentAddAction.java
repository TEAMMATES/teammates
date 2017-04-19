package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.datatransfer.attributes.CommentAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.Url;
import teammates.ui.pagedata.PageData;

/**
 * Action: Create a new {@link CommentAttributes}.
 */
public class InstructorStudentCommentAddAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);

        //used to redirect to studentDetailsPage or studentRecordsPage
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        boolean isFromCommentsPage = getRequestParamAsBoolean(Const.ParamsNames.FROM_COMMENTS_PAGE);
        boolean isFromStudentDetailsPage = getRequestParamAsBoolean(Const.ParamsNames.FROM_STUDENT_DETAILS_PAGE);
        boolean isFromCourseDetailsPage = getRequestParamAsBoolean(Const.ParamsNames.FROM_COURSE_DETAILS_PAGE);
        boolean isFromStudentRecordsPage = !isFromCommentsPage
                                        && !isFromStudentDetailsPage
                                        && !isFromCourseDetailsPage;

        if (isFromStudentDetailsPage || isFromStudentRecordsPage) {
            Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        }

        String commentText = getRequestParamValue(Const.ParamsNames.COMMENT_TEXT);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COMMENT_TEXT, commentText);
        Assumption.assertNotEmpty(commentText);

        verifyAccessibleByInstructor(courseId);

        CommentAttributes comment = extractCommentData();

        try {
            CommentAttributes createdComment = logic.createComment(comment);
            //in case document production requires many retries
            taskQueuer.scheduleSearchableDocumentsProductionForComments(
                    Long.toString(createdComment.getCommentId()));
            String commentPlainText = Jsoup.clean(createdComment.getCommentText(), Whitelist.none());
            statusToUser.add(new StatusMessage(String.format(Const.StatusMessages.COMMENT_ADDED, commentPlainText),
                                               StatusMessageColor.SUCCESS));
            statusToAdmin = "Created Comment for Student:<span class=\"bold\">("
                            + comment.recipients + ")</span> for Course <span class=\"bold\">["
                            + comment.courseId + "]</span><br>"
                            + "<span class=\"bold\">Comment:</span> " + comment.commentText;
        } catch (EntityAlreadyExistsException e) {
            // this exception should not be thrown normally unless GAE creates duplicate commentId
            Assumption.fail("Creating a duplicate comment should not be possible "
                          + "as comments should have different timestamp\n");
        } catch (InvalidParametersException e) {
            // TODO: add a test to cover this branch
            statusToUser.add(new StatusMessage(e.getMessage(), StatusMessageColor.DANGER));
            statusToAdmin = e.getMessage();
            isError = true;
        }

        //TODO: remove fromCommentsPage
        if (isFromCommentsPage) {
            return createRedirectResult(
                           new PageData(account).getInstructorCommentsLink()
                           + "&" + Const.ParamsNames.COURSE_ID + "=" + courseId);
        } else if (isFromStudentDetailsPage) {
            return createRedirectResult(getCourseStudentDetailsLink(courseId, studentEmail));
        } else if (isFromCourseDetailsPage) {
            return createRedirectResult(new PageData(account).getInstructorCourseDetailsLink(courseId));
        } else { //studentRecordsPage by default
            return createRedirectResult(new PageData(account).getInstructorStudentRecordsLink(courseId, studentEmail));
        }
    }

    private void verifyAccessibleByInstructor(String courseId) {
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        CourseAttributes course = logic.getCourse(courseId);
        String recipientType = getRequestParamValue(Const.ParamsNames.RECIPIENT_TYPE);
        CommentParticipantType commentRecipientType = recipientType == null
                                                    ? CommentParticipantType.PERSON
                                                    : CommentParticipantType.valueOf(recipientType);
        String recipients = getRequestParamValue(Const.ParamsNames.RECIPIENTS);
        if (commentRecipientType == CommentParticipantType.COURSE) {
            gateKeeper.verifyAccessible(instructor, course,
                                        Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS);
        } else if (commentRecipientType == CommentParticipantType.SECTION) {
            gateKeeper.verifyAccessible(instructor, course, recipients,
                                        Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS);
        } else if (commentRecipientType == CommentParticipantType.TEAM) {
            List<StudentAttributes> students = logic.getStudentsForTeam(recipients, courseId);
            if (students.isEmpty()) { // considered as a serious bug in coding or user submitted corrupted data
                Assumption.fail();
            } else {
                gateKeeper.verifyAccessible(instructor, course, students.get(0).section,
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS);
            }
        } else { // TODO: modify this after comment for instructor is enabled
            StudentAttributes student = logic.getStudentForEmail(courseId, recipients);
            if (student == null) { // considered as a serious bug in coding or user submitted corrupted data
                Assumption.fail();
            } else {
                gateKeeper.verifyAccessible(instructor, course, student.section,
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS);
            }
        }
    }

    private CommentAttributes extractCommentData() {
        CommentAttributes comment = new CommentAttributes();

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructorDetailForCourse = logic.getInstructorForGoogleId(courseId, account.googleId);
        Assumption.assertNotNull("Account trying to add comment is not an instructor of the course",
                                 instructorDetailForCourse);

        String recipientType = getRequestParamValue(Const.ParamsNames.RECIPIENT_TYPE);
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String recipients = getRequestParamValue(Const.ParamsNames.RECIPIENTS);
        String showCommentTo = getRequestParamValue(Const.ParamsNames.COMMENTS_SHOWCOMMENTSTO);
        String showGiverTo = getRequestParamValue(Const.ParamsNames.COMMENTS_SHOWGIVERTO);
        String showRecipientTo = getRequestParamValue(Const.ParamsNames.COMMENTS_SHOWRECIPIENTTO);
        Text commentText = new Text(getRequestParamValue(Const.ParamsNames.COMMENT_TEXT));

        comment.courseId = courseId;
        comment.giverEmail = instructorDetailForCourse.email;
        comment.recipientType = recipientType == null
                              ? CommentParticipantType.PERSON
                              : CommentParticipantType.valueOf(recipientType);
        comment.recipients = new HashSet<String>();
        if (recipients == null || recipients.isEmpty()) {
            comment.recipients.add(studentEmail);
        } else {
            String[] recipientsArray = recipients.split(",");
            for (String recipient : recipientsArray) {
                comment.recipients.add(recipient.trim());
            }
        }
        comment.status = CommentStatus.FINAL;

        comment.showCommentTo = new ArrayList<CommentParticipantType>();
        if (showCommentTo != null && !showCommentTo.isEmpty()) {
            String[] showCommentToArray = showCommentTo.split(",");
            for (String sct : showCommentToArray) {
                comment.showCommentTo.add(CommentParticipantType.valueOf(sct.trim()));
            }
        }

        comment.showGiverNameTo = new ArrayList<CommentParticipantType>();
        if (showGiverTo != null && !showGiverTo.isEmpty()) {
            String[] showGiverToArray = showGiverTo.split(",");
            for (String sgt : showGiverToArray) {
                comment.showGiverNameTo.add(CommentParticipantType.valueOf(sgt.trim()));
            }
        }

        comment.showRecipientNameTo = new ArrayList<CommentParticipantType>();
        if (showRecipientTo != null && !showRecipientTo.isEmpty()) {
            String[] showRecipientToArray = showRecipientTo.split(",");
            for (String srt : showRecipientToArray) {
                comment.showRecipientNameTo.add(CommentParticipantType.valueOf(srt.trim()));
            }
        }

        //if a comment is public to recipient (except Instructor), it's a pending comment
        if (isCommentPublicToRecipient(comment)) {
            comment.sendingState = CommentSendingState.PENDING;
        }
        comment.createdAt = new Date();
        comment.commentText = commentText;

        return comment;
    }

    private boolean isCommentPublicToRecipient(CommentAttributes comment) {
        return comment.isVisibleTo(CommentParticipantType.PERSON)
               || comment.isVisibleTo(CommentParticipantType.TEAM)
               || comment.isVisibleTo(CommentParticipantType.SECTION)
               || comment.isVisibleTo(CommentParticipantType.COURSE);
    }

    private String getCourseStudentDetailsLink(String courseId, String studentEmail) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = new PageData(account).addUserIdToUrl(link);
        return link;
    }
}
