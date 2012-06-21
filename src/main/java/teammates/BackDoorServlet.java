package teammates;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityAlreadyExistsException;
import teammates.api.EntityDoesNotExistException;
import teammates.datatransfer.DataBundle;
import teammates.exception.CourseDoesNotExistException;
import teammates.jdo.EnrollmentReport;
import teammates.manager.Courses;
import teammates.manager.Emails;
import teammates.manager.Evaluations;
import teammates.manager.TeamForming;
import teammates.persistent.Course;
import teammates.persistent.Evaluation;
import teammates.persistent.Student;
import teammates.persistent.Submission;
import teammates.persistent.TeamFormingSession;

import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@SuppressWarnings("serial")
public class BackDoorServlet extends HttpServlet {
	
	public static final String OPERATION_CREATE_COORD = "OPERATION_CREATE_COORD";
	public static final String OPERATION_DELETE_COORD = "OPERATION_DELETE_COORD";
	public static final String OPERATION_DELETE_COORD_NON_CASCADE = "OPERATION_DELETE_COORD_NON_CASCADE";
	public static final String OPERATION_DELETE_COURSE = "OPERATION_DELETE_COURSE";
	public static final String OPERATION_DELETE_COURSE_BY_ID_NON_CASCADE = "OPERATION_DELETE_COURSE_BY_ID_NON_CASCADE";
	public static final String OPERATION_DELETE_EVALUATION = "OPERATION_DELETE_EVALUATION";
	public static final String OPERATION_DELETE_STUDENT = "OPERATION_DELETE_STUDENT";
	public static final String OPERATION_DELETE_TEAM_FORMING_LOG = "OPERATION_DELETE_TEAM_FORMING_LOG";
	public static final String OPERATION_DELETE_TEAM_PROFILE = "OPERATION_DELETE_TEAM_PROFILE";
	public static final String OPERATION_DELETE_TFS = "OPERATION_DELETE_TFS";
	public static final String OPERATION_EDIT_EVALUATION = "OPERATION_EDIT_COORD";
	public static final String OPERATION_EDIT_STUDENT = "OPERATION_EDIT_STUDENT";
	public static final String OPERATION_EDIT_SUBMISSION = "OPERATION_EDIT_SUBMISSION";
	public static final String OPERATION_EDIT_TEAM_PROFILE = "OPERATION_EDIT_TEAM_PROFILE";
	public static final String OPERATION_EDIT_TFS = "OPERATION_EDIT_TFS";
	public static final String OPERATION_GET_COORD_AS_JSON = "OPERATION_GET_COORD_AS_JSON";
	public static final String OPERATION_GET_COURSES_BY_COORD = "get_courses_by_coord";
	public static final String OPERATION_GET_COURSE_AS_JSON = "OPERATION_GET_COURSE_AS_JSON";
	public static final String OPERATION_GET_STUDENT_AS_JSON = "OPERATION_GET_STUDENT_AS_JSON";
	public static final String OPERATION_GET_EVALUATION_AS_JSON = "OPERATION_GET_EVALUATION_AS_JSON";
	public static final String OPERATION_GET_KEY_FOR_STUDENT = "OPERATION_GET_KEY_FOR_STUDENT";
	public static final String OPERATION_GET_SUBMISSION_AS_JSON = "OPERATION_GET_SUBMISSION_AS_JSON";
	public static final String OPERATION_GET_TEAM_FORMING_LOG_AS_JSON = "OPERATION_GET_TEAM_FORMING_LOG_AS_JSON";
	public static final String OPERATION_GET_TEAM_PROFILE_AS_JSON = "OPERATION_GET_TEAM_PROFILE_AS_JSON";
	public static final String OPERATION_GET_TFS_AS_JSON = "OPERATION_GET_TFS_AS_JSON";
	public static final String OPERATION_PERSIST_DATABUNDLE = "OPERATION_PERSIST_DATABUNDLE";
	public static final String OPERATION_SYSTEM_ACTIVATE_AUTOMATED_REMINDER = "activate_auto_reminder";

	public static final String PARAMETER_BACKDOOR_KEY = "PARAM_BACKDOOR_KEY";
	public static final String PARAMETER_BACKDOOR_OPERATION = "PARAMETER_BACKDOOR_OPERATION";
	public static final String PARAMETER_COURSE_ID = "PARAMETER_COURSE_ID";
	public static final String PARAMETER_COORD_EMAIL = "PARAMETER_COORD_EMAIL";
	public static final String PARAMETER_COORD_ID = "PARAMETER_COORD_ID";
	public static final String PARAMETER_COORD_NAME = "PARAMETER_COORD_NAME";
	public static final String PARAMETER_DATABUNDLE_JSON = "PARAMETER_DATABUNDLE_JSON";
	public static final String PARAMETER_EVALUATION_NAME = "PARAMETER_EVALUATION_NAME";
	public static final String PARAMETER_JASON_STRING = "PARAMETER_JASON_STRING";
	public static final String PARAMETER_REVIEWER_EMAIL = "PARAMETER_REVIEWER_EMAIL";
	public static final String PARAMETER_REVIEWEE_EMAIL = "PARAMETER_REVIEWEE_EMAIL";
	public static final String PARAMETER_STUDENT_EMAIL = "PARAMETER_STUDENT_EMAIL";
	public static final String PARAMETER_STUDENT_ID = "PARAMETER_STUDENT_ID";
	public static final String PARAMETER_TEAM_NAME = "PARAMETER_TEAM_NAME";

	private HttpServletRequest req;
	private HttpServletResponse resp;
	private static final Logger log = Common.getLogger();

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		doPost(req, resp);
	}

	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		this.req = req;
		this.resp = resp;

		// TODO: Change to JSON/XML
		resp.setContentType("text/plain");

		String action = req.getParameter(PARAMETER_BACKDOOR_OPERATION);
		log.info(action);

		String returnValue;

		String auth = req.getParameter(PARAMETER_BACKDOOR_KEY);
		if (!auth.equals(Config.inst().API_AUTH_CODE)) {
			returnValue = "Not authorized to access Backdoor Services";

		} else {

			try {
				returnValue = executeBackendAction(req, action);
			} catch (Exception e) {
				returnValue = Common.BACKEND_STATUS_FAILURE + e.getMessage();
			}
		}
		
		resp.getWriter().write(returnValue);
		resp.flushBuffer();
	}

	private String executeBackendAction(HttpServletRequest req, String action)
			throws Exception {
		// TODO: reorder in alphabetical order
		BackDoorLogic backDoorLogic = new BackDoorLogic();
		if (action.equals(OPERATION_CREATE_COORD)) {
			String coordID = req.getParameter(PARAMETER_COORD_ID);
			String coordName = req.getParameter(PARAMETER_COORD_NAME);
			String coordEmail = req.getParameter(PARAMETER_COORD_EMAIL);
			backDoorLogic.createCoord(coordID, coordName, coordEmail);
		} else if (action.equals(OPERATION_DELETE_COORD)) {
			String coordID = req.getParameter(PARAMETER_COORD_ID);
			backDoorLogic.deleteCoord(coordID);
		} else if (action.equals(OPERATION_DELETE_COURSE)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			backDoorLogic.deleteCourse(courseId);
		} else if (action.equals(OPERATION_DELETE_EVALUATION)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			String evaluationName = req.getParameter(PARAMETER_EVALUATION_NAME);
			backDoorLogic.deleteEvaluation(courseId, evaluationName);
		} else if (action.equals(OPERATION_DELETE_STUDENT)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			String email = req.getParameter(PARAMETER_STUDENT_EMAIL);
			backDoorLogic.deleteStudent(courseId, email);
		} else if (action.equals(OPERATION_DELETE_TEAM_FORMING_LOG)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			backDoorLogic.deleteStudentActions(courseId);
		} else if (action.equals(OPERATION_DELETE_TEAM_PROFILE)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			String teamName = req.getParameter(PARAMETER_TEAM_NAME);
			backDoorLogic.deleteTeamProfile(courseId, teamName);
		} else if (action.equals(OPERATION_DELETE_TFS)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			backDoorLogic.deleteTfs(courseId);
		} else if (action.equals(OPERATION_GET_COORD_AS_JSON)) {
			String coordID = req.getParameter(PARAMETER_COORD_ID);
			return backDoorLogic.getCoordAsJson(coordID);
		} else if (action.equals(OPERATION_GET_COURSE_AS_JSON)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			return backDoorLogic.getCourseAsJson(courseId);
		} else if (action.equals(OPERATION_GET_COURSES_BY_COORD)) {
			String coordID = req.getParameter(PARAMETER_COORD_ID);
			return getCoursesByCoordID(coordID);
		} else if (action.equals(OPERATION_GET_STUDENT_AS_JSON)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			String email = req.getParameter(PARAMETER_STUDENT_EMAIL);
			return backDoorLogic.getStudentAsJson(courseId, email);
		} else if (action.equals(OPERATION_GET_KEY_FOR_STUDENT)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			String email = req.getParameter(PARAMETER_STUDENT_EMAIL);
			return backDoorLogic.getKeyForStudent(courseId, email);
		} else if (action.equals(OPERATION_GET_EVALUATION_AS_JSON)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			String evaluationName = req.getParameter(PARAMETER_EVALUATION_NAME);
			return backDoorLogic.getEvaluationAsJson(courseId, evaluationName);
		} else if (action.equals(OPERATION_GET_TFS_AS_JSON)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			return backDoorLogic.getTfsAsJson(courseId);
		} else if (action.equals(OPERATION_GET_TEAM_FORMING_LOG_AS_JSON)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			return backDoorLogic.getTeamFormingLogAsJson(courseId);
		} else if (action.equals(OPERATION_GET_TEAM_PROFILE_AS_JSON)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			String teamName = req.getParameter(PARAMETER_TEAM_NAME);
			return backDoorLogic.getTeamProfileAsJson(courseId, teamName);
		} else if (action.equals(OPERATION_GET_SUBMISSION_AS_JSON)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			String evaluationName = req.getParameter(PARAMETER_EVALUATION_NAME);
			String reviewerId = req.getParameter(PARAMETER_REVIEWER_EMAIL);
			String revieweeId = req.getParameter(PARAMETER_REVIEWEE_EMAIL);
			return backDoorLogic.getSubmissionAsJson(courseId, evaluationName,
					reviewerId, revieweeId);
		} else if (action.equals(OPERATION_PERSIST_DATABUNDLE)) {
			String dataBundleJsonString = req
					.getParameter(PARAMETER_DATABUNDLE_JSON);
			DataBundle dataBundle = Common.getTeammatesGson().fromJson(
					dataBundleJsonString, DataBundle.class);
			backDoorLogic.persistNewDataBundle(dataBundle);
		} else if (action.equals(OPERATION_EDIT_EVALUATION)) {
			String newValues = req.getParameter(PARAMETER_JASON_STRING);
			backDoorLogic.editEvaluationAsJson(newValues);
		} else if (action.equals(OPERATION_EDIT_SUBMISSION)) {
			String newValues = req.getParameter(PARAMETER_JASON_STRING);
			backDoorLogic.editSubmissionAsJson(newValues);
		} else if (action.equals(OPERATION_EDIT_STUDENT)) {
			String originalEmail = req.getParameter(PARAMETER_STUDENT_EMAIL);
			String newValues = req.getParameter(PARAMETER_JASON_STRING);
			backDoorLogic.editStudentAsJson(originalEmail, newValues);
		} else if (action.equals(OPERATION_EDIT_TFS)) {
			String newValues = req.getParameter(PARAMETER_JASON_STRING);
			backDoorLogic.editTfsAsJson(newValues);
		} else if (action.equals(OPERATION_EDIT_TEAM_PROFILE)) {
			String originalTeamName = req.getParameter(PARAMETER_TEAM_NAME);
			String newValues = req.getParameter(PARAMETER_JASON_STRING);
			backDoorLogic.editTeamProfileAsJson(originalTeamName, newValues);
		} else {
			throw new Exception("Unknown command: " + action);
		}
		return Common.BACKEND_STATUS_SUCCESS;
	}

	/**
	 * Open an evaluation to students
	 */
	protected void evaluationOpen() throws IOException {
		log.fine("Opening evaluation.");
		String courseID = req.getParameter("course_id");
		String name = req.getParameter("evaluation_name");

		boolean edited = Evaluations.inst().openEvaluation(courseID, name);

		if (edited) {
			resp.getWriter().write("ok");
		} else {
			resp.getWriter().write("fail");
		}
	}

	/**
	 * Close an evaluation
	 */
	protected void evaluationClose() throws IOException {
		log.fine("Closing evaluation.");
		String courseID = req.getParameter("course_id");
		String name = req.getParameter("evaluation_name");

		boolean edited = Evaluations.inst().closeEvaluation(courseID, name);

		if (edited) {
			resp.getWriter().write("ok");
		} else {
			resp.getWriter().write("fail");
		}
	}

	/**
	 * Publish an evaluation
	 */
	protected void evaluationPublish() throws IOException {
		String courseID = req.getParameter("course_id");
		String name = req.getParameter("evaluation_name");

		Courses courses = Courses.inst();
		List<Student> studentList = courses.getStudentList(courseID);

		boolean edited = Evaluations.inst().publishEvaluation(courseID, name,
				studentList);

		if (edited) {
			resp.getWriter().write("ok");
		} else {
			resp.getWriter().write("fail");
		}
	}

	/**
	 * Unpublish an evaluation
	 */
	protected void evaluationUnpublish() throws IOException {
		String courseID = req.getParameter("course_id");

		String name = req.getParameter("evaluation_name");
		boolean edited = Evaluations.inst().unpublishEvaluation(courseID, name);
		if (edited) {
			resp.getWriter().write("ok");
		} else {
			resp.getWriter().write("fail");
		}
	}

	protected void evaluationAdd() throws IOException {
		String json = req.getParameter("evaluation");

		Gson gson = new Gson();
		Evaluation e = gson.fromJson(json, Evaluation.class);

		boolean edited = false;
		try {
			edited = Evaluations.inst().addEvaluation(e);
		} catch (EntityAlreadyExistsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// TODO take a snapshot of submissions

		if (edited) {
			resp.getWriter().write("ok");
		} else {
			resp.getWriter().write("fail");
		}
	}

	/**
	 * Enroll students to course. Copied directly from TeammatesServlet.
	 * 
	 * TODO: take a look into the logic again.
	 * 
	 * @param studentList
	 * @param course
	 * @throws
	 */
	protected void enrollStudents() throws IOException {
		log.fine("Enrolling students.");
		String courseId = req.getParameter("course_id");
		String str_json = req.getParameter("students");

		Gson gson = new Gson();
		Type listType = new TypeToken<List<Student>>() {
		}.getType();
		List<Student> studentList = gson.fromJson(str_json, listType);

		// Remove ID (Google ID) from studentList because if it's present, the
		// student will already be joined the course.
		for (Student s : studentList) {
			s.setID("");
		}

		List<EnrollmentReport> enrollmentReportList = new ArrayList<EnrollmentReport>();

		// Check to see if there is an ongoing evaluation. If there is, do not
		// edit
		// students' teams.
		Courses courses = Courses.inst();
		List<Student> currentStudentList = courses.getStudentList(courseId);
		Evaluations evaluations = Evaluations.inst();

		if (evaluations.isEvaluationOngoing(courseId)) {
			for (Student s : studentList) {
				for (Student cs : currentStudentList) {
					if (s.getEmail().equals(cs.getEmail())
							&& !s.getTeamName().equals(cs.getTeamName())) {
						s.setTeamName(cs.getTeamName());
					}
				}
			}
		}
		// Add and edit Student objects in the datastore
		boolean edited = enrollmentReportList.addAll(courses.enrollStudents(
				studentList, courseId));

		if (edited) {
			resp.getWriter().write("ok");
		} else {
			resp.getWriter().write("fail");
		}
	}

	/**
	 * Delete all courses, evaluations, students, submissions. Except
	 * Coordinator
	 */
	@SuppressWarnings("unchecked")
	protected void totalCleanup() throws IOException {
		if (!Config.inst().APP_PRODUCTION_MOLD) {
			log.fine("Cleaning up.");

			// Delete all courses
			getPM().deletePersistentAll(Courses.inst().getAllCourses());

			// Delete all evaluations
			List<Evaluation> evals = Evaluations.inst().getAllEvaluations();
			getPM().deletePersistentAll(evals);

			// Delete all submissions
			List<Submission> submissions = (List<Submission>) getPM().newQuery(
					Submission.class).execute();
			getPM().deletePersistentAll(submissions);

			// Delete all students
			List<Student> students = (List<Student>) getPM().newQuery(
					Student.class).execute();
			getPM().deletePersistentAll(students);

			resp.getWriter().write("ok");
		} else {
			resp.getWriter().write(
					"production mode, total cleaning up disabled");
		}

	}

	/**
	 * Clean up course, evaluation, submission related to the coordinator
	 * 
	 * @author wangsha
	 * @date Sep 8, 2011
	 */
	// TODO: this method does not do a 'total cleanup'
	protected void totalCleanupByCoordinator() {
		String coordID = req.getParameter("coordinator_id");
		Courses.inst().deleteCoordinatorCourses(coordID);

	}

	/**
	 * Clean up everything about a particular course
	 * 
	 * @throws EntityDoesNotExistException
	 */
	protected void cleanupCourse() throws EntityDoesNotExistException {

		String courseID = req.getParameter("course_id");
		log.fine("APIServlet.cleanupCourse() courseID = " + courseID);
		cascadeCleanupCourse(courseID);
	}

	/**
	 * Deletes a course and all data associated with it: students, evaluations,
	 * team profiles, team-forming sessions
	 * 
	 * @param courseID
	 * @throws EntityDoesNotExistException
	 */
	private void cascadeCleanupCourse(String courseID)
			throws EntityDoesNotExistException {

		try {
			Courses.inst().cleanUpCourse(courseID);
		} catch (CourseDoesNotExistException e) {
			log.fine("Course " + courseID + " could not be deleted because "
					+ "it does not exist");
			return;
		}
		Evaluations.inst().deleteEvaluations(courseID);
		TeamForming teamForming = TeamForming.inst();
		if (teamForming.getTeamFormingSession(courseID, null) != null)
			teamForming.deleteTeamFormingSession(courseID);
		if (teamForming.getTeamFormingLogList(courseID) != null)
			teamForming.deleteTeamFormingLog(courseID);

	}

	protected void courseAdd() throws IOException {
		log.info("APIServlet adding new course: ");
		String googleID = req.getParameter("google_id");
		String courseJson = req.getParameter("course");

		Gson gson = new Gson();
		Course c = gson.fromJson(courseJson, Course.class);
		c.setCoordinatorID(googleID);
		getPM().makePersistent(c);

		log.info("Course added: coord: " + c.getCoordinatorID()
				+ " course id: " + c.getID() + " course name: " + c.getName());

		resp.getWriter().write("ok");
	}

	protected void studentSubmitFeedbacks() throws IOException {

		String course_id = req.getParameter("course_id");
		String evaluation_name = req.getParameter("evaluation_name");
		String student_email = req.getParameter("student_email");
		log.fine("Submitting feedback for student." + student_email);

		/*
		 * huy- Unable to use Transaction here. It says transaction batch
		 * operation must be on the same entity group (and must not be root
		 * entity). However it works for studentsJoinCourse below. ??? Aug 17 -
		 * It doesn't work for Join Course below either.
		 * http://code.google.com/appengine
		 * /docs/java/datastore/transactions.html
		 * #What_Can_Be_Done_In_a_Transaction
		 */

		Query query = getPM().newQuery(Submission.class);
		query.setFilter("courseID == course_id");
		query.setFilter("evaluationName == evaluation_name");
		query.setFilter("fromStudent == student_email");
		query.declareParameters("String course_id, String evaluation_name, String student_email");
		@SuppressWarnings("unchecked")
		List<Submission> submissions = (List<Submission>) query.execute(
				course_id, evaluation_name, student_email);

		for (Submission submission : submissions) {
			submission.setPoints(100);
			submission.setCommentsToStudent(new Text(String.format(
					"This is a public comment from %s to %s.", student_email,
					submission.getToStudent())));
			submission.setJustification(new Text(String.format(
					"This is a justification from %s to %s", student_email,
					submission.getToStudent())));
		}

		// Store back to datastore
		log.fine(getPM().makePersistentAll(submissions).toString());

		resp.getWriter().write("ok");
	}

	/**
	 * Special Submission Function for Testing Evaluation Points
	 * 
	 * @param points
	 *            defined in scenario.json
	 * @author xialin
	 **/
	protected void studentSubmitDynamicFeedbacks() throws IOException {

		String course_id = req.getParameter("course_id");
		String evaluation_name = req.getParameter("evaluation_name");
		String student_email = req.getParameter("student_email");
		String team_name = req.getParameter("team_name");
		String submission_points = req.getParameter("submission_points");
		String[] pointsArray = submission_points.split(", ");

		Query studentQuery = getPM().newQuery(Student.class);
		studentQuery.setFilter("courseID == course_id");
		studentQuery.setFilter("teamName == team_name");
		studentQuery.declareParameters("String course_id, String team_name");
		@SuppressWarnings("unchecked")
		List<Student> students = (List<Student>) studentQuery.execute(
				course_id, team_name);

		Query query = getPM().newQuery(Submission.class);
		query.setFilter("courseID == course_id");
		query.setFilter("evaluationName == evaluation_name");
		query.setFilter("fromStudent == student_email");
		query.declareParameters("String course_id, String evaluation_name, String student_email");
		@SuppressWarnings("unchecked")
		List<Submission> submissions = (List<Submission>) query.execute(
				course_id, evaluation_name, student_email);

		int position = 0;
		for (Submission submission : submissions) {
			for (int i = 0; i < students.size(); i++) {
				if (submission.getToStudent().equalsIgnoreCase(
						students.get(i).getEmail()))
					position = i;
				int point = Integer.valueOf(pointsArray[position]);
				submission.setPoints(point);
				submission.setCommentsToStudent(new Text(String.format(
						"This is a public comment from %s to %s.",
						student_email, submission.getToStudent())));
				submission.setJustification(new Text(String.format(
						"This is a justification from %s to %s", student_email,
						submission.getToStudent())));
			}
		}

		getPM().makePersistentAll(submissions);
		resp.getWriter().write("ok");

	}

	protected void teamFormingSessionAdd() throws IOException,
			EntityAlreadyExistsException {
		String json = req.getParameter("teamformingsession");

		Gson gson = new Gson();
		TeamFormingSession e = gson.fromJson(json, TeamFormingSession.class);

		try {
			TeamForming teamForming = TeamForming.inst();
			teamForming.createTeamFormingSession(e.getCourseID(), e.getStart(),
					e.getDeadline(), e.getTimeZone(), e.getGracePeriod(),
					e.getInstructions(), e.getProfileTemplate());
			resp.getWriter().write("ok");
		} catch (EntityAlreadyExistsException ex) {
			resp.getWriter().write("fail");
		}
	}

	protected void teamFormingSessionOpen() throws IOException {
		log.fine("Opening team forming session.");
		String courseID = req.getParameter("course_id");

		boolean edited = TeamForming.inst().openTeamFormingSession(courseID);

		if (edited) {
			resp.getWriter().write("ok");
		} else {
			resp.getWriter().write("fail");
		}
	}

	protected void createProfileOfExistingTeams() throws IOException {
		log.fine("Creating profiles of existing teams.");
		String courseId = req.getParameter("course_id");
		String courseName = req.getParameter("course_name");
		String teamName = req.getParameter("team_name");
		Text teamProfile = new Text(req.getParameter("team_profile"));

		// Add the team forming session
		TeamForming teamForming = TeamForming.inst();

		try {
			teamForming.createTeamProfile(courseId, courseName, teamName,
					teamProfile);
			resp.getWriter().write("ok");
		}

		catch (EntityAlreadyExistsException e) {
			resp.getWriter().write("fail");
		}
	}

	protected void studentsJoinCourse() throws IOException {
		log.fine("Joining course for students.");

		// Set the Student.ID to emails.
		String course_id = req.getParameter("course_id");
		String str_json_students = req.getParameter("students");
		Type listType = new TypeToken<List<Student>>() {
		}.getType();
		Gson gson = new Gson();
		List<Student> students = gson.fromJson(str_json_students, listType);

		// Construct a Map< Email --> Student>
		HashMap<String, Student> mapStudents = new HashMap<String, Student>();
		for (Student s : students) {
			mapStudents.put(s.getEmail(), s);
		}

		// Query all Datastore's Student objects with CourseID received

		Query query = getPM().newQuery(Student.class);
		query.setFilter("courseID == course_id");
		query.declareParameters("String course_id");
		@SuppressWarnings("unchecked")
		List<Student> datastoreStudents = (List<Student>) query
				.execute(course_id);

		for (Student dsStudent : datastoreStudents) {
			Student jsStudent = mapStudents.get(dsStudent.getEmail());
			if (jsStudent != null) {
				dsStudent.setID(jsStudent.getID());
			}
		}
		// Store back to datastore
		getPM().makePersistentAll(datastoreStudents);

		// TODO: Is this correct?
		resp.getWriter().write("Fail: something wrong");
	}

	protected void emailStressTesting() throws IOException {
		Emails emails = new Emails();
		String account = req.getParameter("account");
		int size = Integer.parseInt(req.getParameter("size"));

		emails.mailStressTesting(account, size);
		resp.getWriter().write("ok");

	}

	/**
	 * request to automatedReminders servlet
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	protected void activateAutomatedReminder() throws IOException,
			ServletException {
		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher("/automatedreminders");
		dispatcher.forward(this.req, this.resp);
	}

	private String getCoursesByCoordID(String coordID) {
		String query = "select from " + Course.class.getName()
				+ " where coordinatorID == '" + coordID + "'";

		@SuppressWarnings("unchecked")
		List<Course> courseList = (List<Course>) getPM().newQuery(query)
				.execute();
		String courseIDs = "";

		for (Course c : courseList) {
			courseIDs = courseIDs + c.getID() + " ";
		}
		return courseIDs.trim();
	}

}
