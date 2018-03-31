package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.ui.template.AdminAccountDetailsInstructorCourseListTableRow;
import teammates.ui.template.AdminAccountDetailsStudentCourseListTableRow;

public class AdminAccountDetailsPageData extends PageData {

    private AccountAttributes accountInformation;
    private List<AdminAccountDetailsInstructorCourseListTableRow> instructorCourseListTable;
    private List<AdminAccountDetailsStudentCourseListTableRow> studentCourseListTable;

    public AdminAccountDetailsPageData(AccountAttributes account, String sessionToken, AccountAttributes accountInformation,
                                       List<CourseDetailsBundle> instructorCourseList,
                                       List<CourseAttributes> studentCourseList) {
        super(account, sessionToken);
        this.accountInformation = accountInformation;
        this.instructorCourseListTable = createInstructorCourseListTable(instructorCourseList);
        this.studentCourseListTable = createStudentCourseListTable(studentCourseList);
    }

    private List<AdminAccountDetailsStudentCourseListTableRow> createStudentCourseListTable(
                                    List<CourseAttributes> studentCourseList) {
        List<AdminAccountDetailsStudentCourseListTableRow> courseListTable = new ArrayList<>();

        if (studentCourseList != null) {
            for (CourseAttributes courseDetails : studentCourseList) {
                AdminAccountDetailsStudentCourseListTableRow row =
                        new AdminAccountDetailsStudentCourseListTableRow(accountInformation.googleId, courseDetails,
                                getSessionToken());
                courseListTable.add(row);
            }
        }

        return courseListTable;
    }

    private List<AdminAccountDetailsInstructorCourseListTableRow> createInstructorCourseListTable(
                                                            List<CourseDetailsBundle> instructorCourseList) {
        List<AdminAccountDetailsInstructorCourseListTableRow> courseListTable = new ArrayList<>();
        if (instructorCourseList != null) {
            for (CourseDetailsBundle courseDetails : instructorCourseList) {
                AdminAccountDetailsInstructorCourseListTableRow row = new AdminAccountDetailsInstructorCourseListTableRow(
                        accountInformation.googleId, courseDetails, getSessionToken());
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

}
