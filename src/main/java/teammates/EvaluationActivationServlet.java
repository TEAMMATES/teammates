package teammates;

import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.manager.Accounts;
import teammates.manager.Courses;
import teammates.manager.Evaluations;
import teammates.persistent.Coordinator;
import teammates.persistent.Course;
import teammates.persistent.Evaluation;
import teammates.persistent.Student;

@SuppressWarnings("serial")
public class EvaluationActivationServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		Evaluations evaluations = Evaluations.inst();

		List<Evaluation> evaluationList = evaluations.activateEvaluations();
		List<Student> studentList;

		Course course;

		Accounts accounts = Accounts.inst();

		for (Evaluation e : evaluationList) {
			// Send registration keys to unregistered students
			Courses courses = Courses.inst();
			studentList = courses.getUnregisteredStudentList(e.getCourseID());

			course = courses.getCourse(e.getCourseID());
			Coordinator coord = accounts.getCoordinator(course
					.getCoordinatorID());
			courses.sendRegistrationKeys(studentList, course.getID(),
					course.getName(), coord.getName(), coord.getEmail());

			// Send e-mails to inform the students that an evaluation is opened
			studentList = courses.getStudentList(e.getCourseID());

			evaluations.informStudentsOfEvaluationOpening(studentList,
					e.getCourseID(), e.getName());
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
}
