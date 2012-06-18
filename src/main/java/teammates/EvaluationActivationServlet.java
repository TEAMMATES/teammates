package teammates;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.manager.Accounts;
import teammates.manager.Courses;
import teammates.manager.Evaluations;
import teammates.persistent.Coordinator;
import teammates.persistent.Course;
import teammates.persistent.Evaluation;
import teammates.persistent.Student;

@SuppressWarnings("serial")
public class EvaluationActivationServlet extends HttpServlet {
	
	private static Logger log = Common.getLogger();
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		activateReadyEvaluations();
	}

	public void activateReadyEvaluations() {
		Evaluations evaluations = Evaluations.inst();

		List<Evaluation> evaluationList = evaluations.setEvaluationsAsActivated();
		List<Student> studentList;

		for (Evaluation e : evaluationList) {
			//TODO: this should be absorbed in to the evaluation opening email
			remindUnregisteredStudents(e.getCourseID());

			log.info("sending evalaution opening email to students in "+e.getCourseID());
			studentList = Courses.inst().getStudentList(e.getCourseID());

			evaluations.informStudentsOfEvaluationOpening(studentList,
					e.getCourseID(), e.getName());
		}
	}

	private void remindUnregisteredStudents(String courseId) {
		List<Student> studentList;
		Courses courses = Courses.inst();
		Course course = courses.getCourse(courseId);
		studentList = courses.getUnregisteredStudentList(courseId);
		Coordinator coord = Accounts.inst().getCoordinator(course
				.getCoordinatorID());
		courses.sendRegistrationKeys(studentList, course.getID(),
				course.getName(), coord.getName(), coord.getEmail());
	}


}
