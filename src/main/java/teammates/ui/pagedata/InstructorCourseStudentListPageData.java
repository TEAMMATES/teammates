package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.AccountAttributes;

public class InstructorCourseStudentListPageData extends PageData {

    private List<StudentAttributes> students;

    public InstructorCourseStudentListPageData(AccountAttributes account, String sessionToken,
    											List<StudentAttributes> students) {
        super(account, sessionToken);
        this.students = students;
    }
}