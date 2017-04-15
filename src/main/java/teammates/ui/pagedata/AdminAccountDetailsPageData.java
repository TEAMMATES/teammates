package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.ui.template.AdminAccountDetailsInstructorCourseListTableRow;
import teammates.ui.template.AdminAccountDetailsStudentCourseListTableRow;

public class AdminAccountDetailsPageData extends PageData {

    private AccountAttributes accountInformation;
    private List<AdminAccountDetailsInstructorCourseListTableRow> instructorCourseListTable;
    private List<AdminAccountDetailsStudentCourseListTableRow> studentCourseListTable;

    public AdminAccountDetailsPageData(AccountAttributes account, AccountAttributes accountInformation,
                                       List<CourseDetailsBundle> instructorCourseList,
                                       List<CourseAttributes> studentCourseList) {
        super(account);
        this.accountInformation = accountInformation;
        this.instructorCourseListTable = createInstructorCourseListTable(instructorCourseList);
        this.studentCourseListTable = createStudentCourseListTable(studentCourseList);
    }

    private List<AdminAccountDetailsStudentCourseListTableRow> createStudentCourseListTable(
                                    List<CourseAttributes> studentCourseList) {
        List<AdminAccountDetailsStudentCourseListTableRow> courseListTable =
                        new ArrayList<AdminAccountDetailsStudentCourseListTableRow>();

        if (studentCourseList != null) {
            for (CourseAttributes courseDetails : studentCourseList) {
                AdminAccountDetailsStudentCourseListTableRow row =
                        new AdminAccountDetailsStudentCourseListTableRow(
                                                        accountInformation.googleId, courseDetails);
                courseListTable.add(row);
            }
        }

        return courseListTable;
    }

    private List<AdminAccountDetailsInstructorCourseListTableRow> createInstructorCourseListTable(
                                                            List<CourseDetailsBundle> instructorCourseList) {
        List<AdminAccountDetailsInstructorCourseListTableRow> courseListTable =
                new ArrayList<AdminAccountDetailsInstructorCourseListTableRow>();
        if (instructorCourseList != null) {
            for (CourseDetailsBundle courseDetails : instructorCourseList) {
                AdminAccountDetailsInstructorCourseListTableRow row =
                        new AdminAccountDetailsInstructorCourseListTableRow(accountInformation.googleId, courseDetails);
                courseListTable.add(row);
            }
        }

        return courseListTable;
    }

    public AccountAttributes getAccountInformation() {
        return accountInformation;
    }

    public List<AdminAccountDetailsInstructorCourseListTableRow> getInstructorCourseListTable() {
        return instructorCourseListTable;
    }

    public List<AdminAccountDetailsStudentCourseListTableRow> getStudentCourseListTable() {
        return studentCourseListTable;
    }

    public static String getAdminDeleteInstructorFromCourseLink(String instructorId, String courseId) {
        String link = Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.INSTRUCTOR_ID, instructorId);
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);

        return link;
    }

    public static String getAdminDeleteStudentFromCourseLink(String studentId, String courseId) {
        String link = Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_ID, studentId);
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);

        return link;
    }

}
