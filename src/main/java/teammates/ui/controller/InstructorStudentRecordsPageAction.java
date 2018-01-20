package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.InstructorStudentRecordsPageData;

public class InstructorStudentRecordsPageAction extends Action {

    @Override
    public ActionResult execute() {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId));

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_EMAIL, studentEmail);

        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);

        if (student == null) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_NOT_FOUND_FOR_RECORDS,
                                               StatusMessageColor.DANGER));
            isError = true;
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        }

        List<FeedbackSessionAttributes> sessions = logic.getFeedbackSessionsListForInstructor(account.googleId, false);

        filterFeedbackSessions(courseId, sessions, instructor, student);

        sessions.sort(FeedbackSessionAttributes.DESCENDING_ORDER);

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

        if (sessions.isEmpty()) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.INSTRUCTOR_NO_STUDENT_RECORDS,
                                               StatusMessageColor.WARNING));
        }

        List<String> sessionNames = new ArrayList<>();
        for (FeedbackSessionAttributes fsa : sessions) {
            sessionNames.add(fsa.getFeedbackSessionName());
        }

        InstructorStudentRecordsPageData data =
                new InstructorStudentRecordsPageData(account, student, sessionToken, courseId, studentProfile, sessionNames);

        statusToAdmin = "instructorStudentRecords Page Load<br>"
                      + "Viewing <span class=\"bold\">" + studentEmail + "'s</span> records "
                      + "for Course <span class=\"bold\">[" + courseId + "]</span><br>"
                      + "Number of sessions: " + sessions.size() + "<br>"
                      + "Student Profile: "
                      + (studentProfile == null ? "No Profile"
                                                : SanitizationHelper.sanitizeForHtmlTag(studentProfile.toString()));

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_STUDENT_RECORDS, data);
    }

    private void filterFeedbackSessions(String courseId, List<FeedbackSessionAttributes> feedbacks,
                                        InstructorAttributes instructor, StudentAttributes student) {
        feedbacks.removeIf(tempFs -> !tempFs.getCourseId().equals(courseId)
                || !instructor.isAllowedForPrivilege(student.section, tempFs.getSessionName(),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
    }

}
