package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class StudentCourseDetailsPageAction extends Action {

    private StudentCourseDetailsPageData data;

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);

        if (!isJoinedCourse(courseId, account.googleId)) {
            return createPleaseJoinCourseResponse(courseId);
        }

       
        new GateKeeper().verifyAccessible(logic.getStudentForGoogleId(courseId, account.googleId),
                                          logic.getCourse(courseId));

        data = new StudentCourseDetailsPageData(account);

        data.courseDetails = logic.getCourseDetails(courseId);
        data.instructors = logic.getInstructorsForCourse(courseId);
        data.student = logic.getStudentForGoogleId(courseId, account.googleId);
        data.team = logic.getTeamForStudent(data.student);

        statusToAdmin = "studentCourseDetails Page Load<br>" 
                        + "Viewing team details for <span class=\"bold\">[" + courseId + "] " 
                        + data.courseDetails.course.name + "</span>";

        ShowPageResult response = createShowPageResult(Const.ViewURIs.STUDENT_COURSE_DETAILS, data);
        return response;

    }

}
