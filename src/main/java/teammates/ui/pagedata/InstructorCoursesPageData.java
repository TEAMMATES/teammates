package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.ui.template.ActiveCoursesTable;
import teammates.ui.template.ActiveCoursesTableRow;
import teammates.ui.template.ArchivedCoursesTable;
import teammates.ui.template.ArchivedCoursesTableRow;
import teammates.ui.template.ElementTag;

/**
 * This is the PageData object for the 'Courses' page.
 */
public class InstructorCoursesPageData extends PageData {

    // Flag for deciding if loading the courses table, or the new course form.
    // if true -> loads the courses table, else load the form
    private boolean isUsingAjax;

    private ArchivedCoursesTable archivedCourses;
    private ActiveCoursesTable activeCourses;
    private String courseIdToShow;
    private String courseNameToShow;
    private Map<String, InstructorAttributes> instructorsForCourses;

    public InstructorCoursesPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }

    public void init(List<CourseAttributes> activeCoursesParam, List<CourseAttributes> archivedCoursesParam,
                     Map<String, InstructorAttributes> instructorsForCoursesParam) {
        init(activeCoursesParam, archivedCoursesParam, instructorsForCoursesParam, "", "");
    }

    public void init(List<CourseAttributes> activeCoursesParam, List<CourseAttributes> archivedCoursesParam,
                     Map<String, InstructorAttributes> instructorsForCoursesParam, String courseIdToShowParam,
                     String courseNameToShowParam) {
        this.instructorsForCourses = instructorsForCoursesParam;
        this.activeCourses = convertToActiveCoursesTable(activeCoursesParam);
        this.archivedCourses = convertToArchivedCoursesTable(archivedCoursesParam);
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

    public ActiveCoursesTable getActiveCourses() {
        return activeCourses;
    }

    public ArchivedCoursesTable getArchivedCourses() {
        return archivedCourses;
    }

    private ArchivedCoursesTable convertToArchivedCoursesTable(List<CourseAttributes> archivedCourses) {
        ArchivedCoursesTable archivedCoursesTable = new ArchivedCoursesTable();

        int idx = this.activeCourses.getRows().size() - 1;

        for (CourseAttributes course : archivedCourses) {
            idx++;

            List<ElementTag> actionsParam = new ArrayList<>();

            String unarchiveLink = getInstructorCourseArchiveLink(course.getId(), false, false);
            ElementTag unarchivedButton = createButton("Unarchive", "btn btn-default btn-xs",
                                                       "t_course_unarchive" + idx, unarchiveLink, "", false);

            String deleteLink = getInstructorCourseDeleteLink(course.getId(), false);
            Boolean hasDeletePermission = instructorsForCourses.get(course.getId()).isAllowedForPrivilege(
                                                  Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);
            ElementTag deleteButton = createButton("Delete", "btn btn-default btn-xs course-delete-link",
                                                   "t_course_delete" + idx, deleteLink, Const.Tooltips.COURSE_DELETE,
                                                   !hasDeletePermission);
            deleteButton.setAttribute("data-course-id", course.getId());

            actionsParam.add(unarchivedButton);
            actionsParam.add(deleteButton);

            ArchivedCoursesTableRow row = new ArchivedCoursesTableRow(SanitizationHelper.sanitizeForHtml(course.getId()),
                                                                      SanitizationHelper.sanitizeForHtml(course.getName()),
                                                                      course.getCreatedAtDateString(),
                                                                      course.getCreatedAtDateStamp(),
                                                                      course.getCreatedAtFullDateTimeString(),
                                                                      actionsParam);
            archivedCoursesTable.getRows().add(row);

        }

        return archivedCoursesTable;
    }

    private ActiveCoursesTable convertToActiveCoursesTable(List<CourseAttributes> courses) {
        ActiveCoursesTable activeCourses = new ActiveCoursesTable();

        int idx = -1;

        for (CourseAttributes course : courses) {
            idx++;

            List<ElementTag> actionsParam = new ArrayList<>();

            Boolean hasModifyPermission = instructorsForCourses.get(course.getId()).isAllowedForPrivilege(
                                                   Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
            ElementTag enrollButton = createButton("Enroll", "btn btn-default btn-xs t_course_enroll" + idx, "",
                                                   getInstructorCourseEnrollLink(course.getId()),
                                                   Const.Tooltips.COURSE_ENROLL, !hasModifyPermission);

            ElementTag viewButton = createButton("View", "btn btn-default btn-xs t_course_view" + idx, "",
                                                 getInstructorCourseDetailsLink(course.getId()),
                                                 Const.Tooltips.COURSE_DETAILS, false);

            ElementTag editButton = createButton("Edit", "btn btn-default btn-xs t_course_edit" + idx, "",
                                                 getInstructorCourseEditLink(course.getId()),
                                                 Const.Tooltips.COURSE_EDIT, false);

            ElementTag archiveButton = createButton("Archive", "btn btn-default btn-xs t_course_archive" + idx, "",
                                                    getInstructorCourseArchiveLink(course.getId(), true, false),
                                                    Const.Tooltips.COURSE_ARCHIVE, false);

            String deleteLink = getInstructorCourseDeleteLink(course.getId(), false);
            Boolean hasDeletePermission = instructorsForCourses.get(course.getId()).isAllowedForPrivilege(
                                                   Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);
            ElementTag deleteButton = createButton("Delete", "btn btn-default btn-xs course-delete-link "
                                                   + "t_course_delete" + idx, "", deleteLink, Const.Tooltips.COURSE_DELETE,
                                                   !hasDeletePermission);
            deleteButton.setAttribute("data-course-id", course.getId());

            actionsParam.add(enrollButton);
            actionsParam.add(viewButton);
            actionsParam.add(editButton);
            actionsParam.add(archiveButton);
            actionsParam.add(deleteButton);

            ActiveCoursesTableRow row = new ActiveCoursesTableRow(SanitizationHelper.sanitizeForHtml(course.getId()),
                                                                  SanitizationHelper.sanitizeForHtml(course.getName()),
                                                                  course.getCreatedAtDateString(),
                                                                  course.getCreatedAtDateStamp(),
                                                                  course.getCreatedAtFullDateTimeString(),
                                                                  this.getInstructorCourseStatsLink(course.getId()),
                                                                  actionsParam);
            activeCourses.getRows().add(row);
        }

        return activeCourses;
    }

    private ElementTag createButton(String content, String buttonClass, String id, String href, String title,
                                    boolean isDisabled) {
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
        return button;
    }
}
