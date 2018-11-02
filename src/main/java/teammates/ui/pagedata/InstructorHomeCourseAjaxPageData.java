package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.ui.template.CourseTable;
import teammates.ui.template.ElementTag;
import teammates.ui.template.HomeFeedbackSessionRow;
import teammates.ui.template.InstructorHomeFeedbackSessionRow;

public class InstructorHomeCourseAjaxPageData extends PageData {

    private CourseTable courseTable;
    private int index;

    public InstructorHomeCourseAjaxPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }

    public void init(int tableIndex, CourseSummaryBundle courseSummary, InstructorAttributes instructor) {
        this.index = tableIndex;
        this.courseTable = createCourseTable(courseSummary.course, instructor, courseSummary.feedbackSessions);
    }

    public CourseTable getCourseTable() {
        return courseTable;
    }

    public int getIndex() {
        return index;
    }

    private CourseTable createCourseTable(CourseAttributes course, InstructorAttributes instructor,
            List<FeedbackSessionAttributes> feedbackSessions) {
        String courseId = course.getId();
        return new CourseTable(course,
                               createCourseTableLinks(instructor, courseId),
                               createSessionRows(feedbackSessions, instructor));
    }

    private ElementTag createButton(String text, String className, String href, String tooltip) {
        return new ElementTag(text, "class", className, "href", href, "title", tooltip);
    }

    private void addAttributeIf(boolean shouldAdd, ElementTag button, String key, String value) {
        if (shouldAdd) {
            button.setAttribute(key, value);
        }
    }

    private List<ElementTag> createCourseTableLinks(InstructorAttributes instructor, String courseId) {
        String className = "btn-tm-actions course-";

        ElementTag students = new ElementTag("Students");
        ElementTag sessions = new ElementTag("Sessions");
        ElementTag instructors = new ElementTag("Instructors");
        ElementTag courses = new ElementTag("Course");

        ElementTag enroll = createButton("Enroll",
                                         className + "enroll-for-test",
                                         getInstructorCourseEnrollLink(courseId),
                                         Const.Tooltips.COURSE_ENROLL);

        ElementTag view = createButton("View / Edit",
                                       className + "view-for-test",
                                       getInstructorCourseDetailsLink(courseId),
                                       Const.Tooltips.COURSE_DETAILS);

        ElementTag edit = createButton("View / Edit",
                                       className + "edit-for-test",
                                       getInstructorCourseEditLink(courseId),
                                       Const.Tooltips.COURSE_EDIT);

        ElementTag add = createButton("Add",
                                      className + "add-eval-for-test",
                                      getInstructorFeedbackSessionsLink(courseId),
                                      Const.Tooltips.COURSE_ADD_FEEDBACKSESSION);

        ElementTag archive = createButton("Archive",
                                          className + "archive-for-test",
                                          getInstructorCourseArchiveLink(courseId, true, true),
                                          Const.Tooltips.COURSE_ARCHIVE);
        addAttributeIf(true, archive, "data-course-id", courseId);

        ElementTag delete = createButton("Delete",
                                         className + "delete-for-test course-move-to-recycle-bin-link",
                                         getInstructorCourseDeleteLink(courseId, true),
                                         Const.Tooltips.COURSE_MOVE_TO_RECYCLE_BIN);
        addAttributeIf(true, delete, "data-course-id", courseId);

        if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) {
            students.addNestedElement(enroll);
        }
        students.addNestedElement(view);

        sessions.addNestedElement(add);

        instructors.addNestedElement(edit);

        courses.addNestedElement(archive);
        courses.addNestedElement(edit);
        if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE)) {
            courses.addNestedElement(delete);
        }

        if (!instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION)) {
            return Arrays.asList(students, instructors, courses);
        }
        return Arrays.asList(students, instructors, sessions, courses);
    }

    private List<HomeFeedbackSessionRow> createSessionRows(List<FeedbackSessionAttributes> sessions,
            InstructorAttributes instructor) {
        List<HomeFeedbackSessionRow> rows = new ArrayList<>();

        for (FeedbackSessionAttributes session : sessions) {
            InstructorHomeFeedbackSessionRow row = new InstructorHomeFeedbackSessionRow(
                    sanitizeForHtml(session.getFeedbackSessionName()),
                    getInstructorSubmissionsTooltipForFeedbackSession(session),
                    getInstructorPublishedTooltipForFeedbackSession(session),
                    getInstructorSubmissionStatusForFeedbackSession(session),
                    getInstructorPublishedStatusForFeedbackSession(session),
                    TimeHelper.formatDateTimeForInstructorHomePage(session.getStartTimeLocal()),
                    session.getStartTimeInIso8601UtcFormat(),
                    TimeHelper.formatDateTimeForDisplay(session.getStartTime(), session.getTimeZone()),
                    TimeHelper.formatDateTimeForInstructorHomePage(session.getEndTimeLocal()),
                    session.getEndTimeInIso8601UtcFormat(),
                    TimeHelper.formatDateTimeForDisplay(session.getEndTime(), session.getTimeZone()),
                    getInstructorFeedbackStatsLink(session.getCourseId(), session.getFeedbackSessionName()),
                    getInstructorFeedbackSessionActions(
                            session, Const.ActionURIs.INSTRUCTOR_HOME_PAGE, instructor));

            rows.add(row);
        }

        return rows;
    }
}
