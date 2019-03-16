package teammates.ui.webapi.action;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Gets account's information.
 */
public class GetAccountsAction extends Action {

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

        AccountInfoData output = new AccountInfoData(accountInfo, instructorCourses, studentCourses);
        return new JsonResult(output);
    }

    /**
     * Output format for account info.
     */
    public static class AccountInfoData extends ApiOutput {

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

}
