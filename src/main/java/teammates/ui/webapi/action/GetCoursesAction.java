package teammates.ui.webapi.action;

import java.util.Comparator;
import java.util.List;

import teammates.common.datatransfer.attributes.CourseAttributes;

/**
 * Get a list of course for current user.
 */
public class GetCoursesAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        gateKeeper.verifyInstructorPrivileges(logic.getAccount(userInfo.getId()));
    }

    @Override
    public ActionResult execute() {
        List<CourseAttributes> courses =
                logic.getCoursesForInstructor(logic.getInstructorsForGoogleId(userInfo.getId(), true));

        courses.sort(Comparator.comparing(CourseAttributes::getId));

        return new JsonResult(new CourseInfo.CoursesResponse(courses));
    }

}
