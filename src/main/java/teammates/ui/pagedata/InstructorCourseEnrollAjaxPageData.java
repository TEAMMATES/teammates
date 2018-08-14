package teammates.ui.pagedata;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

public class InstructorCourseEnrollAjaxPageData extends PageData {
    public List<StudentAttributes> students;

    public InstructorCourseEnrollAjaxPageData(AccountAttributes account, String sessionToken,
                                              List<StudentAttributes> students) {
        super(account, sessionToken);
        this.students = students;
    }
}
