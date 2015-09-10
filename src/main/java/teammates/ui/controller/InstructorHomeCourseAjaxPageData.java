package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.ui.template.CourseTable;
import teammates.ui.template.ElementTag;
import teammates.ui.template.HomeFeedbackSessionRow;
import teammates.ui.template.InstructorHomeFeedbackSessionRow;

public class InstructorHomeCourseAjaxPageData extends PageData {

    private static final int MAX_CLOSED_SESSION_STATS = 3;
    
    private CourseTable courseTable;
    private int index;
    
    public InstructorHomeCourseAjaxPageData(AccountAttributes account) {
        super(account);
    }
    
    public void init(int tableIndex, CourseSummaryBundle courseSummary, InstructorAttributes instructor, int pendingComments,
                     List<String> sectionNames) {
        this.index = tableIndex;
        this.courseTable = createCourseTable(
                courseSummary.course, instructor, courseSummary.feedbackSessions, pendingComments,
                sectionNames);
    }
    
    public CourseTable getCourseTable() {
        return courseTable;
    }
    
    public int getIndex() {
        return index;
    }
    
    private CourseTable createCourseTable(CourseAttributes course, InstructorAttributes instructor,
            List<FeedbackSessionAttributes> feedbackSessions, int pendingCommentsCount,
            List<String> sectionNames) {
        String courseId = course.id;
        return new CourseTable(course,
                               createCourseTableLinks(instructor, courseId, pendingCommentsCount),
                               createSessionRows(feedbackSessions, instructor, courseId, sectionNames));
    }
    
    private ElementTag createButton(String text, String className, String href, String tooltip) {
        return new ElementTag(text, "class", className, "href", href, "title", tooltip);
    }
    
    private void addAttributeIf(boolean shouldAdd, ElementTag button, String key, String value) {
        if (shouldAdd) {
            button.setAttribute(key, value);
        }
    }
    
    private List<ElementTag> createCourseTableLinks(InstructorAttributes instructor, String courseId,
            int pendingCommentsCount) {
        String disabled = "disabled";
        String className = "btn btn-primary btn-xs btn-tm-actions course-";
        
        ElementTag enroll = createButton("Enroll",
                                         className + "enroll-for-test",
                                         getInstructorCourseEnrollLink(courseId),
                                         Const.Tooltips.COURSE_ENROLL);
        addAttributeIf(!instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT),
                       enroll, disabled, disabled);
        
        ElementTag view = createButton("View",
                                       className + "view-for-test",
                                       getInstructorCourseDetailsLink(courseId),
                                       Const.Tooltips.COURSE_DETAILS);
        
        ElementTag edit = createButton("Edit",
                                       className + "edit-for-test",
                                       getInstructorCourseEditLink(courseId),
                                       Const.Tooltips.COURSE_EDIT);
        
        ElementTag add = createButton("Add Session",
                                      className + "add-eval-for-test",
                                      getInstructorFeedbacksLink(courseId),
                                      Const.Tooltips.COURSE_ADD_FEEDBACKSESSION);
        addAttributeIf(!instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION),
                       add, disabled, disabled);
        
        ElementTag archive = createButton("Archive",
                                          className + "archive-for-test",
                                          getInstructorCourseArchiveLink(courseId, true, true),
                                          Const.Tooltips.COURSE_ARCHIVE);
        addAttributeIf(true, archive, "onclick", "return toggleArchiveCourseConfirmation('" + courseId + "')");
        
        ElementTag delete = createButton("Delete",
                                         className + "delete-for-test",
                                         getInstructorCourseDeleteLink(courseId, true),
                                         Const.Tooltips.COURSE_DELETE);
        addAttributeIf(!instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE),
                       delete, disabled, disabled);
        addAttributeIf(true, delete, "onclick", "return toggleDeleteCourseConfirmation('" + courseId + "')");
        
        if (pendingCommentsCount <= 0) {
            return Arrays.asList(enroll, view, edit, add, archive, delete);
        }
        
        String pendingGraphic = "<span class=\"badge\">" + pendingCommentsCount + "</span>"
                                + "<span class=\"glyphicon glyphicon-comment\"></span>"
                                + "<span class=\"glyphicon glyphicon-arrow-right\"></span>"
                                + "<span class=\"glyphicon glyphicon-envelope\"></span>";
        String plural = pendingCommentsCount > 1 ? "s" : "";
        
        ElementTag pending = createButton(
                pendingGraphic,
                className + "notify-pending-comments-for-test",
                getInstructorStudentCommentClearPendingLink(courseId),
                String.format(Const.Tooltips.COURSE_EMAIL_PENDING_COMMENTS,
                              pendingCommentsCount, plural));

        return Arrays.asList(enroll, view, edit, add, archive, pending, delete);
    }
    
    private List<HomeFeedbackSessionRow> createSessionRows(List<FeedbackSessionAttributes> sessions,
            InstructorAttributes instructor, String courseId, List<String> sectionNames) {
        List<HomeFeedbackSessionRow> rows = new ArrayList<>();
        
        int statsToDisplayLeft = MAX_CLOSED_SESSION_STATS;
        for (FeedbackSessionAttributes session : sessions) {
            
            boolean isRecent = session.isOpened() || session.isWaitingToOpen();
            if (!isRecent && statsToDisplayLeft > 0
                          && !TimeHelper.isOlderThanAYear(session.createdTime)) {
                isRecent = true;
                --statsToDisplayLeft;
            }
            
            InstructorHomeFeedbackSessionRow row = new InstructorHomeFeedbackSessionRow(
                    sanitizeForHtml(session.feedbackSessionName),
                    getInstructorHoverMessageForFeedbackSession(session),
                    getInstructorStatusForFeedbackSession(session),
                    getInstructorFeedbackStatsLink(session.courseId, session.feedbackSessionName),
                    isRecent,
                    getInstructorFeedbackSessionActions(
                            session, false, instructor, sectionNames));

            rows.add(row);
        }
        
        return rows;
    }
}
