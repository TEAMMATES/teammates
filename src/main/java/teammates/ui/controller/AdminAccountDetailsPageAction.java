package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.ui.pagedata.AdminAccountDetailsPageData;

public class AdminAccountDetailsPageAction extends Action {

    @Override
    protected ActionResult execute() {

        gateKeeper.verifyAdminPrivileges(account);

        String googleId = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        AccountAttributes accountInformation = logic.getAccount(googleId);

        List<CourseDetailsBundle> instructorCourseList;
        try {
            instructorCourseList = new ArrayList<>(logic.getCourseSummariesForInstructor(googleId).values());
        } catch (EntityDoesNotExistException e) {
            //Not an instructor of any course
            instructorCourseList = null;
        }

        List<CourseAttributes> studentCourseList;
        try {
            studentCourseList = logic.getCoursesForStudentAccount(googleId);
        } catch (EntityDoesNotExistException e) {
            //Not a student of any course
            studentCourseList = null;
        }

        AdminAccountDetailsPageData data = new AdminAccountDetailsPageData(account, sessionToken, accountInformation,
                                                                           instructorCourseList, studentCourseList);
        statusToAdmin = "adminAccountDetails Page Load<br>"
                + "Viewing details for " + data.getAccountInformation().name + "(" + googleId + ")";

        return createShowPageResult(Const.ViewURIs.ADMIN_ACCOUNT_DETAILS, data);
    }

}
