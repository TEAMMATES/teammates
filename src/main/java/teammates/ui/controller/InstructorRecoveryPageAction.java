package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.pagedata.InstructorRecoveryPageData;

/**
 * Action: loading of the 'Recovery' page for an instructor.
 */
public class InstructorRecoveryPageAction extends Action {

    @Override
    public ActionResult execute() {

        gateKeeper.verifyInstructorPrivileges(account);

        InstructorRecoveryPageData data = new InstructorRecoveryPageData(account, sessionToken);
        String isUsingAjax = getRequestParamValue(Const.ParamsNames.IS_USING_AJAX);
        data.setUsingAjax(isUsingAjax != null);

        Map<String, InstructorAttributes> instructorsForCourses = new HashMap<>();
        List<CourseAttributes> recoveryCourses = new ArrayList<>();

        if (data.isUsingAjax()) {
            List<InstructorAttributes> instructorList = logic.getInstructorsForGoogleId(data.account.googleId);
            for (InstructorAttributes instructor : instructorList) {
                instructorsForCourses.put(instructor.courseId, instructor);
            }

            recoveryCourses = logic.getRecoveryCoursesForInstructor(instructorList);
        }

        data.init(recoveryCourses, instructorsForCourses);

        statusToAdmin = "instructorRecovery Page Load<br>Total courses: " + recoveryCourses.size();

        /* Explanation: Create the appropriate result object and return it.*/
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_RECOVERY, data);
    }
}
