package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.ui.template.ElementTag;
import teammates.ui.template.RecoveryCoursesTable;
import teammates.ui.template.RecoveryCoursesTableRow;

/**
 * This is the PageData object for the 'Recovery' page.
 */
public class InstructorRecoveryPageData extends PageData {

    private boolean isUsingAjax;

    private RecoveryCoursesTable recoveryCourses;
    private String courseIdToShow;
    private String courseNameToShow;
    private Map<String, InstructorAttributes> instructorsForCourses;

    public InstructorRecoveryPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }

    public void init(List<CourseAttributes> recoveryCoursesParam,
                     Map<String, InstructorAttributes> instructorsForCoursesParam) {
        init(recoveryCoursesParam, instructorsForCoursesParam, "", "");
    }

    public void init(List<CourseAttributes> recoveryCoursesParam,
                     Map<String, InstructorAttributes> instructorsForCoursesParam, String courseIdToShowParam,
                     String courseNameToShowParam) {
        this.instructorsForCourses = instructorsForCoursesParam;
        this.recoveryCourses = convertToRecoveryCoursesTable(recoveryCoursesParam);
        this.courseIdToShow = courseIdToShowParam;
        this.courseNameToShow = courseNameToShowParam;
    }

    public void setUsingAjax(boolean isUsingAjax) {
        this.isUsingAjax = isUsingAjax;
    }

    public boolean isUsingAjax() {
        return this.isUsingAjax;
    }

    public String getCourseIdToShow() {
        return courseIdToShow;
    }

    public String getCourseNameToShow() {
        return courseNameToShow;
    }

    public RecoveryCoursesTable getRecoveryCourses() {
        return recoveryCourses;
    }

    private RecoveryCoursesTable convertToRecoveryCoursesTable(List<CourseAttributes> courses) {
        RecoveryCoursesTable recoveryCourses = new RecoveryCoursesTable();

        int idx = -1;

        for (CourseAttributes course : courses) {
            idx++;

            List<ElementTag> actionsParam = new ArrayList<>();

            ElementTag restoreButton = createButton("Restore", "btn btn-default btn-xs t_course_restore" + idx, "",
                    getInstructorRecoveryLink(),
                    Const.Tooltips.COURSE_RESTORE, false, "");

            String deleteLink = getInstructorCourseDeleteLink(course.getId(), false);
            Boolean hasDeletePermission = instructorsForCourses.get(course.getId()).isAllowedForPrivilege(
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);
            ElementTag deleteButton = createButton("Delete Permanently", "btn btn-default btn-xs course-delete-link "
                            + "t_course_delete" + idx, "", deleteLink, Const.Tooltips.COURSE_DELETE,
                    !hasDeletePermission, "color: red");
            deleteButton.setAttribute("data-course-id", course.getId());

            actionsParam.add(restoreButton);
            actionsParam.add(deleteButton);

            RecoveryCoursesTableRow row = new RecoveryCoursesTableRow(SanitizationHelper.sanitizeForHtml(course.getId()),
                    SanitizationHelper.sanitizeForHtml(course.getName()),
                    course.getCreatedAtDateString(),
                    course.getCreatedAtDateStamp(),
                    course.getCreatedAtFullDateTimeString(),
                    course.getDeletedAtDateString(),
                    course.getDeletedAtDateStamp(),
                    course.getDeletedAtFullDateTimeString(),
                    this.getInstructorCourseStatsLink(course.getId()),
                    actionsParam);
            recoveryCourses.getRows().add(row);
        }

        return recoveryCourses;
    }

    private ElementTag createButton(String content, String buttonClass, String id, String href, String title,
                                    boolean isDisabled, String style) {
        ElementTag button = new ElementTag(content);

        button.setAttribute("class", buttonClass);

        if (id != null && !id.isEmpty()) {
            button.setAttribute("id", id);
        }

        if (href != null && !href.isEmpty()) {
            button.setAttribute("href", href);
        }

        if (title != null && !title.isEmpty()) {
            button.setAttribute("title", title);
            button.setAttribute("data-toggle", "tooltip");
            button.setAttribute("data-placement", "top");
        }

        if (isDisabled) {
            button.setAttribute("disabled", null);
        }

        if (style != null && !style.isEmpty()) {
            button.setAttribute("style", style);
        }
        return button;
    }
}
