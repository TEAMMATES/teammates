package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.ui.template.CourseTable;

public class InstructorHomePageData extends PageData {

    private boolean isSortingDisabled;
    private List<CourseTable> courseTables;
    private String sortCriteria;

    public InstructorHomePageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }

    public void init(List<CourseSummaryBundle> courseList, String sortCriteria) {
        this.sortCriteria = sortCriteria;
        this.isSortingDisabled = courseList.size() < 2;
        setCourseTables(courseList);
    }

    public String getSortCriteria() {
        return sortCriteria;
    }

    public boolean isSortingDisabled() {
        return isSortingDisabled;
    }

    public List<CourseTable> getCourseTables() {
        return courseTables;
    }

    /**
     * Retrieves the link to submit the request to remind particular students.
     * Also contains home page link to return after the action.
     * @return form submit action link
     */
    public String getRemindParticularStudentsLink() {
        return getInstructorFeedbackRemindParticularStudentsLink(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
    }

    /**
     * Retrieves the link to submit the request for resending the session published email.
     * Also contains home page link to return to after the action.
     * @return form submit action link
     */
    public String getSessionResendPublishedEmailLink() {
        return getInstructorFeedbackResendPublishedEmailLink(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
    }

    /**
     * Retrieves the link to submit the request for copy of session.
     * Also contains home page link to return after the action.
     * @return form submit action link
     */
    public String getEditCopyActionLink() {
        return getInstructorFeedbackEditCopyActionLink(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
    }

    private void setCourseTables(List<CourseSummaryBundle> courses) {
        courseTables = new ArrayList<>();
        for (CourseSummaryBundle courseDetails : courses) {
            courseTables.add(new CourseTable(courseDetails.course, null, null));
        }
    }
}
