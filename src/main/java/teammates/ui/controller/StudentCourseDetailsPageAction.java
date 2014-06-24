package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
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

       
        new GateKeeper().verifyAccessible(
                logic.getStudentForGoogleId(courseId, account.googleId),
                logic.getCourse(courseId));

        data = new StudentCourseDetailsPageData(account);

        data.courseDetails = logic.getCourseDetails(courseId);
        data.instructors = logic.getInstructorsForCourse(courseId);

        data.student = logic.getStudentForGoogleId(courseId, account.googleId);

        data.team = getTeam(logic.getTeamsForCourse(courseId), data.student);

        statusToAdmin = "studentCourseDetails Page Load<br>" +
                "Viewing team details for <span class=\"bold\">[" + courseId
                + "] " +
                data.courseDetails.course.name + "</span>";

        ShowPageResult response = createShowPageResult(
                Const.ViewURIs.STUDENT_COURSE_DETAILS, data);
        return response;

    }

    private TeamDetailsBundle getTeam(List<TeamDetailsBundle> teams,
            StudentAttributes student) {
        if (student.team == null || student.team.trim().isEmpty()) {
            return null;
        } else {
            for (TeamDetailsBundle team : teams) {
                if (team.name.equals(student.team)) {
                    return team;
                }
            }
            return null;
        }
    }

}
