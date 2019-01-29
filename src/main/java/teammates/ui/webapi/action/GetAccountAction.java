package teammates.ui.webapi.action;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Action: gets an account's information.
 */
public class GetAccountAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Only admins can downgrade accounts
        if (!userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() {
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);

        AccountAttributes accountInfo = logic.getAccount(googleId);
        List<CourseAttributes> instructorCourses = logic.getCoursesForInstructor(googleId);
        List<CourseAttributes> studentCourses = logic.getCoursesForStudentAccount(googleId);

        AccountInfo output = new AccountInfo(accountInfo, instructorCourses, studentCourses);
        return new JsonResult(output);
    }

    /**
     * Output format for {@link GetAccountAction}.
     */
    public static class AccountInfo extends ApiOutput {

        private final AccountAttributes accountInfo;
        private final List<CourseAttributes> instructorCourses;
        private final List<CourseAttributes> studentCourses;

        public AccountInfo(AccountAttributes accountInfo, List<CourseAttributes> instructorCourses,
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

}
