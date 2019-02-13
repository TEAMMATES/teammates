package teammates.ui.webapi.output;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.ui.webapi.action.GetAccountAction;

import java.util.List;

/**
 * Output format for {@link GetAccountAction}.
 */
public class AccountInfoData extends ApiOutput {

    private final AccountAttributes accountInfo;
    private final List<CourseAttributes> instructorCourses;
    private final List<CourseAttributes> studentCourses;

    public AccountInfoData(AccountAttributes accountInfo, List<CourseAttributes> instructorCourses,
                       List<CourseAttributes> studentCourses) {
        this.accountInfo = accountInfo;
        this.instructorCourses = instructorCourses;
        this.studentCourses = studentCourses;
    }

    public AccountAttributes getAccountInfo() {
        return accountInfo;
    }

    public List<CourseAttributes> getInstructorCourses() {
        return instructorCourses;
    }

    public List<CourseAttributes> getStudentCourses() {
        return studentCourses;
    }

}