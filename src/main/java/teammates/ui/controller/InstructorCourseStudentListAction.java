package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.ui.pagedata.InstructorCourseStudentListPageData;

public class InstructorCourseStudentListAction extends Action {

    private List<StudentAttributes> students;

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String user = getRequestParamValue(Const.ParamsNames.USER_ID);

        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertPostParamNotNull(Const.ParamsNames.USER_ID, user);

        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getCourse(courseId));

        List<StudentAttributes> students = logic.getStudentsForCourse(courseId);

        InstructorCourseStudentListPageData data = 
                            new InstructorCourseStudentListPageData(logic.getAccount(user), sessionToken, students);

        return createAjaxResult(data);
    }

}