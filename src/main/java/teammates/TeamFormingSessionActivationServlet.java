package teammates;

import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.manager.Accounts;
import teammates.manager.Courses;
import teammates.manager.TeamForming;
import teammates.persistent.Coordinator;
import teammates.persistent.Course;
import teammates.persistent.Student;
import teammates.persistent.TeamFormingSession;

@SuppressWarnings("serial")
public class TeamFormingSessionActivationServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		TeamForming teamForming = TeamForming.inst();

		List<TeamFormingSession> teamFormingSessionList = teamForming.activateTeamFormingSessions();
		List<Student> studentList;

		Course course;

		Accounts accounts = Accounts.inst();

		for (TeamFormingSession t : teamFormingSessionList) {
			// Send registration keys to unregistered students
			Courses courses = Courses.inst();
			studentList = courses.getUnregisteredStudentList(t.getCourseID());

			course = courses.getCourse(t.getCourseID());
			Coordinator coord = accounts.getCoordinator(course
					.getCoordinatorID());
			courses.sendRegistrationKeys(studentList, course.getID(),
					course.getName(), coord.getName(), coord.getEmail());

			// Send e-mails to inform the students that an team forming is opened
			studentList = courses.getStudentList(t.getCourseID());

			teamForming.informStudentsOfTeamFormingSessionOpening(studentList,
					t.getCourseID(), t.getDeadline());
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
}
