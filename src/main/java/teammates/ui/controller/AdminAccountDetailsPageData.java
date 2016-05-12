package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.ui.template.AdminAccountDetailsInstructorCourseListTableRow;
import teammates.ui.template.AdminAccountDetailsStudentCourseListTableRow;

public class AdminAccountDetailsPageData extends PageData {
    
    private AccountAttributes accountInformation;
    private List<AdminAccountDetailsInstructorCourseListTableRow> instructorCourseListTable;
    private List<AdminAccountDetailsStudentCourseListTableRow> studentCourseListTable;
    
    public AdminAccountDetailsPageData(final AccountAttributes account, final AccountAttributes accountInformation, 
                                       final List<CourseDetailsBundle> instructorCourseList, 
                                       final List<CourseAttributes> studentCourseList) {
        super(account);
        this.accountInformation = accountInformation;
        this.instructorCourseListTable = createInstructorCourseListTable(instructorCourseList);
        this.studentCourseListTable = createStudentCourseListTable(studentCourseList);
    }
    
    private List<AdminAccountDetailsStudentCourseListTableRow> createStudentCourseListTable(
                                    final List<CourseAttributes> studentCourseList) {
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
                                                            final List<CourseDetailsBundle> instructorCourseList) {
        List<AdminAccountDetailsInstructorCourseListTableRow> courseListTable = new ArrayList<AdminAccountDetailsInstructorCourseListTableRow>();
        if (instructorCourseList != null) {
            for (CourseDetailsBundle courseDetails : instructorCourseList) {
                AdminAccountDetailsInstructorCourseListTableRow row = new AdminAccountDetailsInstructorCourseListTableRow(accountInformation.googleId, courseDetails);
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
    
    public static String getAdminDeleteInstructorFromCourseLink(final String instructorId, final String courseId){
        String link = Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.INSTRUCTOR_ID, instructorId);
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        
        return link;
    }
    
    public static String getAdminDeleteStudentFromCourseLink(final String studentId, final String courseId){
        String link = Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_ID, studentId);
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        
        return link;
    }

}
