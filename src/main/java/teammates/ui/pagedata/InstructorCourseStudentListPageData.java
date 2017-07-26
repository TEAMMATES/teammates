package teammates.ui.pagedata;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

public class InstructorCourseStudentListPageData extends PageData {

    public List<StudentAttributes> students;

    public InstructorCourseStudentListPageData(AccountAttributes account,
                String sessionToken, List<StudentAttributes> students) {
        super(account, sessionToken);
        this.students = students;
    }
}
