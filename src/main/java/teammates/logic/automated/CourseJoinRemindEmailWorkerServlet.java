package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.HttpRequestHelper;
import teammates.logic.core.StudentsLogic;

@SuppressWarnings("serial")
public class CourseJoinRemindEmailWorkerServlet extends WorkerServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String courseId = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String studentEmail = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.STUDENT_EMAIL);
        Assumption.assertNotNull(studentEmail);
        
        try {
            StudentsLogic.inst().sendRegistrationInviteToStudent(courseId, studentEmail);
        } catch (EntityDoesNotExistException e) {
            log.severe("Unexpected error while sending emails " + TeammatesException.toStringWithStackTrace(e));
        }
    }

}
