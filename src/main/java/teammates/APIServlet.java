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

import teammates.datatransfer.*;
import teammates.datatransfer.StudentData.UpdateStatus;
import teammates.exception.*;
import teammates.jdo.CourseSummaryForCoordinator;
import teammates.jdo.EnrollmentReport;
import teammates.jdo.EvaluationDetailsForCoordinator;
import teammates.persistent.Coordinator;
import teammates.persistent.Course;
import teammates.persistent.Evaluation;
import teammates.persistent.Student;
import teammates.persistent.Submission;
import teammates.persistent.TeamFormingLog;
import teammates.persistent.TeamFormingSession;
import teammates.persistent.TeamProfile;

import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * The API Servlet.
 * 
 * 
 */
@SuppressWarnings("serial")
public class APIServlet extends HttpServlet {
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
	public static final String OPERATION_GET_SUBMISSION_AS_JSON = "OPERATION_GET_SUBMISSION_AS_JSON";
	public static final String OPERATION_GET_TEAM_FORMING_LOG_AS_JSON = "OPERATION_GET_TEAM_FORMING_LOG_AS_JSON";
	public static final String OPERATION_GET_TEAM_PROFILE_AS_JSON = "OPERATION_GET_TEAM_PROFILE_AS_JSON";
	public static final String OPERATION_GET_TFS_AS_JSON = "OPERATION_GET_TFS_AS_JSON";
	public static final String OPERATION_PERSIST_DATABUNDLE = "OPERATION_PERSIST_DATABUNDLE";
	public static final String OPERATION_SYSTEM_ACTIVATE_AUTOMATED_REMINDER = "activate_auto_reminder";

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
	private static final Logger log = Logger.getLogger(APIServlet.class
			.getName());

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

		// Check for auth code(to prevent misuse)
		String auth = req.getParameter("tm_auth");
		if (!auth.equals(Config.inst().API_AUTH_CODE)) {
			resp.getWriter().write("Authentication fails.");
			resp.flushBuffer();
			return;
		}

		String action = req.getParameter("action");
		log.info(action);
		// TODO: reorder in alphabetical order
		if (action.equals("evaluation_open")) {
			evaluationOpen();
		} else if (action.equals("teamformingsession_open")) {
			teamFormingSessionOpen();
		} else if (action.equals("evaluation_close")) {
			evaluationClose();
		} else if (action.equals("evaluation_add")) {
			evaluationAdd();
		} else if (action.equals("evaluation_publish")) {
			evaluationPublish();
		} else if (action.equals("evaluation_unpublish")) {
			evaluationUnpublish();
		} else if (action.equals("teamformingsession_add")) {
			try {
				teamFormingSessionAdd();
			} catch (EntityAlreadyExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("createteamprofiles")) {
			createProfileOfExistingTeams();
		} else if (action.equals("course_add")) {
			courseAdd();
		} else if (action.equals("cleanup")) {
			totalCleanup();
		} else if (action.equals("cleanup_course")) {
			try {
				cleanupCourse();
			} catch (EntityDoesNotExistException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("cleanup_by_coordinator")) {
			totalCleanupByCoordinator();
		} else if (action.equals("enroll_students")) {
			enrollStudents();
		} else if (action.equals("student_submit_feedbacks")) {
			studentSubmitFeedbacks();
		} else if (action.equals("student_submit_dynamic_feedbacks")) {
			studentSubmitDynamicFeedbacks();
		} else if (action.equals("students_join_course")) {
			studentsJoinCourse();
		} else if (action.equals("email_stress_testing")) {
			emailStressTesting();
		} else if (action.equals("enable_email")) {
			enableEmail();
		} else if (action.equals("disable_email")) {
			disableEmail();
		} else if (action.equals(OPERATION_SYSTEM_ACTIVATE_AUTOMATED_REMINDER)) {
			activateAutomatedReminder();
		} else {
			String returnValue;
			try {
				returnValue = executeBackendAction(req, action);
			} catch (Exception e) {
				returnValue = Common.BACKEND_STATUS_FAILURE + e.getMessage();
			}
			resp.getWriter().write(returnValue);
		}

		resp.flushBuffer();
	}

	private String executeBackendAction(HttpServletRequest req, String action)
			throws Exception {
		if (action.equals(OPERATION_CREATE_COORD)) {
			String coordID = req.getParameter(PARAMETER_COORD_ID);
			String coordName = req.getParameter(PARAMETER_COORD_NAME);
			String coordEmail = req.getParameter(PARAMETER_COORD_EMAIL);
			createCoord(coordID, coordName, coordEmail);
		} else if (action.equals(OPERATION_DELETE_COORD)) {
			String coordID = req.getParameter(PARAMETER_COORD_ID);
			deleteCoord(coordID);
		} else if (action.equals(OPERATION_DELETE_COORD_NON_CASCADE)) {
			String coordId = req.getParameter(PARAMETER_COORD_ID);
			deleteCoordByIdNonCascade(coordId);
		} else if (action.equals(OPERATION_DELETE_COURSE)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			deleteCourse(courseId);
		} else if (action.equals(OPERATION_DELETE_COURSE_BY_ID_NON_CASCADE)) {
			String courseID = req.getParameter(PARAMETER_COURSE_ID);
			deleteCourseByIdNonCascade(courseID);
		} else if (action.equals(OPERATION_DELETE_EVALUATION)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			String evaluationName = req.getParameter(PARAMETER_EVALUATION_NAME);
			deleteEvaluation(courseId, evaluationName);
		} else if (action.equals(OPERATION_DELETE_STUDENT)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			String email = req.getParameter(PARAMETER_STUDENT_EMAIL);
			deleteStudent(courseId, email);
		} else if (action.equals(OPERATION_DELETE_TEAM_FORMING_LOG)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			deleteStudentActions(courseId);
		} else if (action.equals(OPERATION_DELETE_TEAM_PROFILE)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			String teamName = req.getParameter(PARAMETER_TEAM_NAME);
			deleteTeamProfile(courseId, teamName);
		} else if (action.equals(OPERATION_DELETE_TFS)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			deleteTfs(courseId);
		} else if (action.equals(OPERATION_GET_COORD_AS_JSON)) {
			String coordID = req.getParameter(PARAMETER_COORD_ID);
			return getCoordAsJson(coordID);
		} else if (action.equals(OPERATION_GET_COURSE_AS_JSON)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			return getCourseAsJson(courseId);
		} else if (action.equals(OPERATION_GET_COURSES_BY_COORD)) {
			String coordID = req.getParameter(PARAMETER_COORD_ID);
			return getCoursesByCoordID(coordID);
		} else if (action.equals(OPERATION_GET_STUDENT_AS_JSON)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			String email = req.getParameter(PARAMETER_STUDENT_EMAIL);
			return getStudentAsJson(courseId, email);
		} else if (action.equals(OPERATION_GET_EVALUATION_AS_JSON)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			String evaluationName = req.getParameter(PARAMETER_EVALUATION_NAME);
			return getEvaluationAsJson(courseId, evaluationName);
		} else if (action.equals(OPERATION_GET_TFS_AS_JSON)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			return getTfsAsJson(courseId);
		} else if (action.equals(OPERATION_GET_TEAM_FORMING_LOG_AS_JSON)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			return getTeamFormingLogAsJson(courseId);
		} else if (action.equals(OPERATION_GET_TEAM_PROFILE_AS_JSON)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			String teamName = req.getParameter(PARAMETER_TEAM_NAME);
			return getTeamProfileAsJson(courseId, teamName);
		} else if (action.equals(OPERATION_GET_SUBMISSION_AS_JSON)) {
			String courseId = req.getParameter(PARAMETER_COURSE_ID);
			String evaluationName = req.getParameter(PARAMETER_EVALUATION_NAME);
			String reviewerId = req.getParameter(PARAMETER_REVIEWER_EMAIL);
			String revieweeId = req.getParameter(PARAMETER_REVIEWEE_EMAIL);
			return getSubmissionAsJson(courseId, evaluationName, reviewerId,
					revieweeId);
		} else if (action.equals(OPERATION_PERSIST_DATABUNDLE)) {
			String dataBundleJsonString = req
					.getParameter(PARAMETER_DATABUNDLE_JSON);
			persistNewDataBundle(dataBundleJsonString);
		} else if (action.equals(OPERATION_EDIT_EVALUATION)) {
			String newValues = req.getParameter(PARAMETER_JASON_STRING);
			editEvaluationAsJson(newValues);
		} else if (action.equals(OPERATION_EDIT_SUBMISSION)) {
			String newValues = req.getParameter(PARAMETER_JASON_STRING);
			editSubmissionAsJson(newValues);
		} else if (action.equals(OPERATION_EDIT_STUDENT)) {
			String originalEmail = req.getParameter(PARAMETER_STUDENT_EMAIL);
			String newValues = req.getParameter(PARAMETER_JASON_STRING);
			editStudentAsJson(originalEmail, newValues);
		} else if (action.equals(OPERATION_EDIT_TFS)) {
			String newValues = req.getParameter(PARAMETER_JASON_STRING);
			editTfsAsJson(newValues);
		} else if (action.equals(OPERATION_EDIT_TEAM_PROFILE)) {
			String originalTeamName = req.getParameter(PARAMETER_TEAM_NAME);
			String newValues = req.getParameter(PARAMETER_JASON_STRING);
			editTeamProfileAsJson(originalTeamName, newValues);
		} else {
			throw new Exception("Unknown command: " + action);
		}
		return Common.BACKEND_STATUS_SUCCESS;
	}

	@Deprecated
	private void disableEmail() throws IOException {
		Config.inst().emailEnabled = false;
		resp.getWriter().write("ok");
	}

	@Deprecated
	private void enableEmail() throws IOException {
		Config.inst().emailEnabled = true;
		resp.getWriter().write("ok");

	}

	/**
	 * Open an evaluation to students
	 */
	protected void evaluationOpen() throws IOException {
		System.out.println("Opening evaluation.");
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
		System.out.println("Closing evaluation.");
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

		boolean edited = Evaluations.inst().addEvaluation(e);

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
		System.out.println("Enrolling students.");
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
			System.out.println("Cleaning up.");

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
		System.out.println("APIServlet.cleanupCourse() courseID = " + courseID);
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
			System.out.println("Course " + courseID
					+ " could not be deleted because " + "it does not exist");
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
		System.out.println("Submitting feedback for student." + student_email);

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
		System.out.println(getPM().makePersistentAll(submissions));

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
		System.out.println("Opening team forming session.");
		String courseID = req.getParameter("course_id");

		boolean edited = TeamForming.inst().openTeamFormingSession(courseID);

		if (edited) {
			resp.getWriter().write("ok");
		} else {
			resp.getWriter().write("fail");
		}
	}

	protected void createProfileOfExistingTeams() throws IOException {
		System.out.println("Creating profiles of existing teams.");
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
		System.out.println("Joining course for students.");

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

	private void deleteCourseByIdNonCascade(String courseID) {
		Courses courses = Courses.inst();
		courses.deleteCoordinatorCourse(courseID);
	}

	private void deleteCoordByIdNonCascade(String coordId) {
		Accounts accounts = Accounts.inst();
		try {
			accounts.deleteCoordinatorNonCascade(coordId);
		} catch (Exception e) {
			log.warning("problem while trying to delete coordinator" + coordId
					+ "\n error:" + e.getMessage());
		}
	}

	// --------------------- revised API ------------------------------------

	/*
	 * Get methods should throw an exception if the entity was not found. It is
	 * "not expected" that UI will try to access a non-existent entity.
	 */

	private String getCoordAsJson(String coordID) {
		CoordData coordData = getCoord(coordID);
		return Common.getTeammatesGson().toJson(coordData);
	}

	private String getCourseAsJson(String courseId) {
		CourseData course = getCourse(courseId);
		return Common.getTeammatesGson().toJson(course);
	}

	private String getStudentAsJson(String courseId, String email) {
		StudentData student = getStudent(courseId, email);
		return Common.getTeammatesGson().toJson(student);
	}

	private String getEvaluationAsJson(String courseId, String evaluationName) {
		EvaluationData evaluation = getEvaluation(courseId, evaluationName);
		return Common.getTeammatesGson().toJson(evaluation);
	}

	private String getSubmissionAsJson(String courseId, String evaluationName,
			String reviewerEmail, String revieweeEmail) {
		SubmissionData target = getSubmission(courseId, evaluationName,
				reviewerEmail, revieweeEmail);
		return Common.getTeammatesGson().toJson(target);
	}

	private String getTfsAsJson(String courseId) {
		TfsData tfs = getTfs(courseId);
		return Common.getTeammatesGson().toJson(tfs);
	}

	private String getTeamProfileAsJson(String courseId, String teamName) {
		TeamProfileData teamProfile = getTeamProfile(courseId, teamName);
		return Common.getTeammatesGson().toJson(teamProfile);
	}

	private String getTeamFormingLogAsJson(String courseId) {
		List<StudentActionData> teamFormingLogList = getStudentActions(courseId);
		return Common.getTeammatesGson().toJson(teamFormingLogList);
	}

	private void createSubmissions(List<SubmissionData> submissionDataList) {
		ArrayList<Submission> submissions = new ArrayList<Submission>();
		for (SubmissionData sd : submissionDataList) {
			submissions.add(sd.toSubmission());
		}
		Evaluations.inst().editSubmissions(submissions);
	}

	private void editStudentAsJson(String originalEmail, String newValues)
			throws InvalidParametersException {
		StudentData student = Common.getTeammatesGson().fromJson(newValues,
				StudentData.class);
		editStudent(originalEmail, student);
	}

	private void editEvaluationAsJson(String evaluationJson)
			throws InvalidParametersException, EntityDoesNotExistException {
		EvaluationData evaluation = Common.getTeammatesGson().fromJson(
				evaluationJson, EvaluationData.class);
		editEvaluation(evaluation);
	}

	private void editSubmissionAsJson(String submissionJson) {
		SubmissionData submission = Common.getTeammatesGson().fromJson(
				submissionJson, SubmissionData.class);
		ArrayList<SubmissionData> submissionList = new ArrayList<SubmissionData>();
		submissionList.add(submission);
		editSubmission(submissionList);
	}

	private void editTfsAsJson(String tfsJson)
			throws EntityDoesNotExistException {
		TfsData tfs = Common.getTeammatesGson()
				.fromJson(tfsJson, TfsData.class);
		TeamForming.inst().editTeamFormingSession(tfs.course, tfs.startTime,
				tfs.endTime, tfs.gracePeriod, tfs.instructions,
				tfs.profileTemplate, tfs.activated, tfs.timeZone);
	}

	private void editTeamProfileAsJson(String originalTeamName,
			String teamProfileJson) throws EntityDoesNotExistException {
		TeamProfileData teamProfile = Common.getTeammatesGson().fromJson(
				teamProfileJson, TeamProfileData.class);
		TeamForming.inst().editTeamProfile(teamProfile.course, "",
				originalTeamName, teamProfile.team, teamProfile.profile);
	}

	@SuppressWarnings("unused")
	private void ____SYSTEM_level_methods__________________________________() {
	}

	// TODO: This is not only for coordinator right?
	public String coordGetLoginUrl(String redirectUrl) {
		Accounts accounts = Accounts.inst();
		return accounts.getLoginPage(redirectUrl);
	}

	// TODO: This is not only for coordinator right?
	public String coordGetLogoutUrl(String redirectUrl) {
		Accounts accounts = Accounts.inst();
		return accounts.getLogoutPage(redirectUrl);
	}

	/**
	 * @deprecated Use persistNewDataBundle(DataBundle dataBundle)
	 */
	public String persistNewDataBundle(String dataBundleJsonString)
			throws InvalidParametersException, EntityAlreadyExistsException {
		Gson gson = Common.getTeammatesGson();

		DataBundle dataBundle = gson.fromJson(dataBundleJsonString,
				DataBundle.class);
		return persistNewDataBundle(dataBundle);
	}

	/**
	 * Persists given data in the datastore Works ONLY if the data is correct
	 * and new (i.e. these entities do not already exist in the datastore). The
	 * behavior is undefined if incorrect or not new.
	 * 
	 * @param dataBundleJsonString
	 * @return status of the request in the form 'status meassage'+'additional
	 *         info (if any)' e.g., "[BACKEND_STATUS_SUCCESS]" e.g.,
	 *         "[BACKEND_STATUS_FAILURE]NullPointerException at ..."
	 * @throws EntityAlreadyExistsException
	 * @throws InvalidParametersException
	 * @throws Exception
	 */
	public String persistNewDataBundle(DataBundle dataBundle)
			throws InvalidParametersException, EntityAlreadyExistsException {

		HashMap<String, CoordData> coords = dataBundle.coords;
		for (CoordData coord : coords.values()) {
			log.info("API Servlet adding coord :" + coord.id);
			createCoord(coord.id, coord.name, coord.email);
		}

		HashMap<String, CourseData> courses = dataBundle.courses;
		for (CourseData course : courses.values()) {
			log.info("API Servlet adding course :" + course.id);
			createCourse(course.coord, course.id, course.name);
		}

		HashMap<String, StudentData> students = dataBundle.students;
		for (StudentData student : students.values()) {
			log.info("API Servlet adding student :" + student.email
					+ " to course " + student.course);
			createStudent(student);
		}

		HashMap<String, EvaluationData> evaluations = dataBundle.evaluations;
		for (EvaluationData evaluation : evaluations.values()) {
			log.info("API Servlet adding evaluation :" + evaluation.name
					+ " to course " + evaluation.course);
			createEvalution(evaluation);
		}

		// processing is slightly different for submissions because we are
		// adding all submissions in one go
		HashMap<String, SubmissionData> submissionsMap = dataBundle.submissions;
		List<SubmissionData> submissionsList = new ArrayList<SubmissionData>();
		for (SubmissionData submission : submissionsMap.values()) {
			log.info("API Servlet adding submission for "
					+ submission.evaluation + " from " + submission.reviewer
					+ " to " + submission.reviewee);
			submissionsList.add(submission);
		}
		createSubmissions(submissionsList);
		log.info("API Servlet added " + submissionsList.size() + " submissions");

		HashMap<String, TfsData> tfsMap = dataBundle.teamFormingSessions;
		for (TfsData tfs : tfsMap.values()) {
			log.info("API Servlet adding TeamFormingSession to course "
					+ tfs.course);
			createTfs(tfs);
		}

		HashMap<String, TeamProfileData> teamProfiles = dataBundle.teamProfiles;
		for (TeamProfileData teamProfile : teamProfiles.values()) {
			log.info("API Servlet adding TeamProfile of " + teamProfile.team
					+ " in course " + teamProfile.course);
			createTeamProfile(teamProfile);
		}

		HashMap<String, StudentActionData> studentActions = dataBundle.studentActions;
		for (StudentActionData studentAction : studentActions.values()) {
			log.info("API Servlet adding StudentActionData in course "
					+ studentAction.course + " : "
					+ studentAction.action.getValue());
			createStudentAction(studentAction);
		}

		return Common.BACKEND_STATUS_SUCCESS;
	}

	@SuppressWarnings("unused")
	private void ____COORD_level_methods____________________________________() {
	}

	public void createCoord(String coordID, String coordName, String coordEmail)
			throws EntityAlreadyExistsException, InvalidParametersException {
		Common.validateEmail(coordEmail);
		Common.validateCoordName(coordName);
		Common.validateGoogleId(coordID);
		Accounts.inst().addCoordinator(coordID, coordName, coordEmail);
	}

	public CoordData getCoord(String coordID) {
		Coordinator coord = Accounts.inst().getCoordinator(coordID);
		return (coord == null ? null : new CoordData(coord.getGoogleID(),
				coord.getName(), coord.getEmail()));
	}

	public void editCoord(CoordData coord) throws NotImplementedException {
		throw new NotImplementedException("Not implemented because we do "
				+ "not allow editing coordinators");
	}

	public void deleteCoord(String coordId) {
		List<Course> coordCourseList = Courses.inst().getCoordinatorCourseList(
				coordId);
		for (Course course : coordCourseList) {
			deleteCourse(course.getID());
		}
		Accounts.inst().deleteCoord(coordId);
	}

	/**
	 * 
	 * @param coordId
	 * @return null if coordId is null
	 */
	public HashMap<String, CourseData> getCourseListForCoord(String coordId) {
		if (coordId == null)
			return null;
		HashMap<String, CourseSummaryForCoordinator> courseSummaryListForCoord = Courses
				.inst().getCourseSummaryListForCoord(coordId);
		HashMap<String, CourseData> returnList = new HashMap<String, CourseData>();
		for(CourseSummaryForCoordinator csfc: courseSummaryListForCoord.values()){
			CourseData c = new CourseData();
			c.coord = coordId;
			c.id = csfc.getID();
			c.name = csfc.getName();
			c.studentsTotal = csfc.getTotalStudents();
			c.teamsTotal = csfc.getNumberOfTeams();
			c.unregisteredTotal = csfc.getUnregistered();
			returnList.put(c.id, c);
		}
		return returnList;
	}

	public HashMap<String, CourseData> getCourseDetailsListForCoord(
			String coordId) {
		if (coordId == null) {
			return null;
		}
		// TODO: using this method here may not be efficient as it retrieves
		// info not required
		HashMap<String, CourseData> courseList = getCourseListForCoord(coordId);
		ArrayList<EvaluationData> evaluationList = getEvaluationsListForCoord(coordId);
		for (EvaluationData ed : evaluationList) {
			CourseData courseSummary = courseList
					.get(ed.course);
			courseSummary.evaluations.add(ed);
		}
		return courseList;
	}

	public ArrayList<EvaluationData> getEvaluationsListForCoord(
			String coordId) {

		if (coordId == null) {
			return null;
		}

		List<Course> courseList = Courses.inst().getCoordinatorCourseList(
				coordId);
		ArrayList<EvaluationData> evaluationDetailsList = new ArrayList<EvaluationData>();

		for (Course c : courseList) {
			ArrayList<EvaluationDetailsForCoordinator> evaluationsSummaryForCourse = Evaluations.inst()
					.getEvaluationsSummaryForCourse(c.getID());
			for(EvaluationDetailsForCoordinator edfc: evaluationsSummaryForCourse){
				EvaluationData e = new EvaluationData();
				e.course = edfc.getCourseID();
				e.name = edfc.getName();
				e.instructions = edfc.getInstructions();
				e.startTime = edfc.getStart();
				e.endTime = edfc.getDeadline();
				e.timeZone = edfc.getTimeZone();
				e.gracePeriod = edfc.getGracePeriod();
				e.p2pEnabled = edfc.isCommentsEnabled();
				e.published = edfc.isPublished();
				e.activated = edfc.isActivated();
				e.expectedTotal = edfc.numberOfEvaluations;
				e.submittedTotal = edfc.numberOfCompletedEvaluations;
				evaluationDetailsList.add(e);
			}
		}
		return evaluationDetailsList;
	}

	public List<TfsData> getTfsListForCoord(String coordId) {
		if (coordId == null) {
			return null;
		}
		List<Course> courseList = Courses.inst().getCoordinatorCourseList(
				coordId);
		List<TeamFormingSession> teamFormingSessionList = TeamForming.inst()
				.getTeamFormingSessionList(courseList);
		ArrayList<TfsData> returnList = new ArrayList<TfsData>();
		for (TeamFormingSession tfs : teamFormingSessionList) {
			returnList.add(new TfsData(tfs));
		}
		return returnList;
	}

	@SuppressWarnings("unused")
	private void ____COURSE_level_methods__________________________________() {
	}

	public void createCourse(String coordinatorId, String courseId,
			String courseName) throws EntityAlreadyExistsException,
			InvalidParametersException {
		Common.validateGoogleId(coordinatorId);
		Common.validateCourseId(courseId);
		Common.validateCourseName(courseName);
		Courses.inst().addCourse(courseId, courseName, coordinatorId);
	}

	public CourseData getCourse(String courseId) {
		Course c = Courses.inst().getCourse(courseId);
		return (c == null ? null : new CourseData(c.getID(), c.getName(),
				c.getCoordinatorID()));
	}

	public void editCourse(CourseData course) throws NotImplementedException {
		throw new NotImplementedException("Not implemented because we do "
				+ "not allow editing courses");
	}

	public void deleteCourse(String courseId) {
		if (courseId == null) {
			return;
		}
		Evaluations.inst().deleteEvaluations(courseId);
		TeamForming.inst().deleteTeamFormingSession(courseId);
		Courses.inst().deleteCourse(courseId);
	}

	public List<StudentData> getStudentListForCourse(String courseId) {
		if (courseId == null) {
			return null;
		}
		List<Student> studentList = Courses.inst().getStudentList(courseId);
		List<StudentData> returnList = new ArrayList<StudentData>();
		for (Student s : studentList) {
			returnList.add(new StudentData(s));
		}
		return returnList;
	}

	public void sendRegistrationInviteForCourse(String courseId)
			throws InvalidParametersException {
		if (courseId == null) {
			throw new InvalidParametersException(
					Common.ERRORCODE_NULL_PARAMETER, "Course ID cannot be null");
		}
		List<Student> studentList = Courses.inst().getUnregisteredStudentList(
				courseId);

		for (Student s : studentList) {
			sendRegistrationInviteToStudent(courseId, s.getEmail());
		}
	}

	public List<StudentData> enrollStudents(String enrollLines, String courseId)
			throws EnrollException {
		if (enrollLines == null || courseId == null) {
			throw new EnrollException(Common.ERRORCODE_NULL_PARAMETER,
					(enrollLines == null ? "Enroll text" : "Course ID")
							+ " cannot be null");
		}
		ArrayList<StudentData> returnList = new ArrayList<StudentData>();
		String[] linesArray = enrollLines.split(Common.EOL);
		ArrayList<StudentData> studentList = new ArrayList<StudentData>();

		// check if all non-empty lines are formatted correctly
		for (int i = 0; i < linesArray.length; i++) {
			String line = linesArray[i];
			try {
				if (Common.isWhiteSpace(line))
					continue;
				studentList.add(new StudentData(line, courseId));
			} catch (InvalidParametersException e) {
				throw new EnrollException(e.errorCode, "Problem in line : "
						+ line + Common.EOL + e.getMessage());
			}
		}

		// enroll all students
		for (StudentData student : studentList) {
			StudentData studentInfo;
			studentInfo = enrollStudent(student);
			returnList.add(studentInfo);
		}

		// add to return list students not included in the enroll list.
		List<StudentData> studentsInCourse = getStudentListForCourse(courseId);
		for (StudentData student : studentsInCourse) {
			if (!isInEnrollList(student, returnList)) {
				student.updateStatus = StudentData.UpdateStatus.NOT_IN_ENROLL_LIST;
				returnList.add(student);
			}
		}
		return returnList;
	}

	@SuppressWarnings("unused")
	private void ____STUDENT_level_methods__________________________________() {
	}

	public void createStudent(StudentData student)
			throws EntityAlreadyExistsException, InvalidParametersException {
		if (student == null) {
			throw new InvalidParametersException("Student cannot be null");
		}
		Courses.inst().createStudent(new Student(student));
	}

	public StudentData getStudent(String courseId, String email) {
		if (courseId == null || email == null) {
			return null;
		}
		Student student = Accounts.inst().getStudent(courseId, email);
		return (student == null ? null : new StudentData(student));
	}

	public void editStudent(String originalEmail, StudentData student)
			throws InvalidParametersException {
		// TODO: make the implementation more defensive
		String newTeamName = student.team;
		Courses.inst().editStudent(student.course, originalEmail,
				student.name, student.team, student.email, student.id,
				student.comments, student.profile);
		if (TeamForming.inst().getTeamProfile(student.course, student.team) == null) {
			try {
				TeamForming.inst().createTeamProfile(
						new TeamProfile(student.course, "", newTeamName,
								new Text("")));
			} catch (EntityAlreadyExistsException e) {
				log.severe("EntityAlreadyExistsException thrown in "
						+ this.getClass().getCanonicalName() + ":editStudent()");
			}
		}
	}

	public void deleteStudent(String courseId, String studentEmail) {
		Courses.inst().deleteStudent(courseId, studentEmail);
		Evaluations.inst().deleteSubmissionsForStudent(courseId, studentEmail);
		TeamForming.inst().deleteLogsForStudent(courseId, studentEmail);
		// TODO:delete team profile, if the last member
	}

	public StudentData enrollStudent(StudentData student) {
		StudentData.UpdateStatus updateStatus = UpdateStatus.UNMODIFIED;
		try {
			if (isSameAsExistingStudent(student)) {
				updateStatus = UpdateStatus.UNMODIFIED;
			} else if (isModificationToExistingStudent(student)) {
				editStudent(student.email, student);
				updateStatus = UpdateStatus.MODIFIED;
			} else {
				createStudent(student);
				updateStatus = UpdateStatus.NEW;
			}
		} catch (Exception e) {
			updateStatus = UpdateStatus.ERROR;
			log.severe("EntityExistsExcpetion thrown unexpectedly");
			e.printStackTrace();
		}
		student.updateStatus = updateStatus;
		return student;
	}

	public void sendRegistrationInviteToStudent(String courseId,
			String studentEmail) {

		Course course = Courses.inst().getCourse(courseId);
		Student student = Courses.inst().getStudentWithEmail(courseId,
				studentEmail);
		Coordinator coord = Accounts.inst().getCoordinator(
				course.getCoordinatorID());
		List<Student> studentList = new ArrayList<Student>();
		studentList.add(student);

		// TODO: this need not be a batch processing method
		Courses.inst().sendRegistrationKeys(studentList, courseId,
				course.getName(), coord.getName(), coord.getEmail());

	}

	@SuppressWarnings("unused")
	private void ____EVALUATION_level_methods______________________________() {
	}

	public void createEvalution(EvaluationData evaluation)
			throws EntityAlreadyExistsException, InvalidParametersException {
		Evaluations.inst().addEvaluation(evaluation.toEvaluation());
	}

	public EvaluationData getEvaluation(String courseId, String evaluationName) {
		Evaluation e = Evaluations.inst().getEvaluation(courseId,
				evaluationName);
		return (e == null ? null : new EvaluationData(e));
	}

	public void editEvaluation(EvaluationData evaluation)
			throws EntityDoesNotExistException, InvalidParametersException {
		Evaluations.inst().editEvaluation(evaluation.course, evaluation.name,
				evaluation.instructions, evaluation.p2pEnabled,
				evaluation.startTime, evaluation.endTime,
				evaluation.gracePeriod, evaluation.activated,
				evaluation.published, evaluation.timeZone);
	}

	public void deleteEvaluation(String courseId, String evaluationName) {
		Evaluations.inst().deleteEvaluation(courseId, evaluationName);
	}

	public void publishEvaluation(String courseId, String evaluationName)
			throws EntityDoesNotExistException {
		Courses courses = Courses.inst();
		List<Student> studentList = courses.getStudentList(courseId);

		Evaluations.inst().publishEvaluation(courseId, evaluationName,
				studentList);
	}

	public void unpublishEvaluation(String courseId, String evaluationName) {
		Evaluations.inst().unpublishEvaluation(courseId, evaluationName);
	}

	@SuppressWarnings("unused")
	private void ____SUBMISSION_level_methods_____________________________() {
	}

	public void createSubmission(SubmissionData submission)
			throws NotImplementedException {
		throw new NotImplementedException(
				"Not implemented because submissions "
						+ "are created automatically");
	}

	public SubmissionData getSubmission(String courseId, String evaluationName,
			String reviewerEmail, String revieweeEmail) {
		Submission submission = Evaluations.inst().getSubmission(courseId,
				evaluationName, reviewerEmail, revieweeEmail);
		return (submission == null ? null : new SubmissionData(submission));
	}

	// TODO: change to editSubmissions
	public void editSubmission(List<SubmissionData> submissionDataList) {
		ArrayList<Submission> submissions = new ArrayList<Submission>();
		for (SubmissionData sd : submissionDataList) {
			submissions.add(sd.toSubmission());
		}
		Evaluations.inst().editSubmissions(submissions);
	}

	public void deleteSubmission(SubmissionData submission)
			throws NotImplementedException {
		throw new NotImplementedException(
				"Not implemented because submissions "
						+ "are deleted automatically");
	}

	@SuppressWarnings("unused")
	private void ____TFS_level_methods______________________________________() {
	}

	public void createTfs(TfsData tfs) throws EntityAlreadyExistsException,
			InvalidParametersException {
		TeamForming.inst().createTeamFormingSession(tfs.toTfs());
	}

	public TfsData getTfs(String courseId) {
		TeamFormingSession tfs = TeamForming.inst().getTeamFormingSession(
				courseId);
		return (tfs == null ? null : new TfsData(tfs));
	}

	public void editTfs(TfsData tfs) {
		TeamForming.inst().editTeamFormingSession(tfs.course, tfs.startTime,
				tfs.endTime, tfs.gracePeriod, tfs.instructions,
				tfs.profileTemplate);
	}

	public void deleteTfs(String courseId) {
		TeamForming.inst().deleteTeamFormingSession(courseId);
	}

	public void renameTeam(String courseId, String originalTeamName,
			String newTeamName) {
		TeamForming.inst().editStudentsTeam(courseId, originalTeamName,
				newTeamName);
	}

	@SuppressWarnings("unused")
	private void ____TEAM_PROFILE_level_methods_____________________________() {
	}

	public void createTeamProfile(TeamProfileData teamProfile)
			throws EntityAlreadyExistsException, InvalidParametersException {
		TeamForming.inst().createTeamProfile(teamProfile.toTeamProfile());
	}

	public TeamProfileData getTeamProfile(String courseId, String teamName) {
		TeamProfile teamProfile = TeamForming.inst().getTeamProfile(courseId,
				teamName);
		return (teamProfile == null ? null : new TeamProfileData(teamProfile));
	}

	public void editTeamProfile(String originalTeamName,
			TeamProfileData modifieldTeamProfile)
			throws EntityDoesNotExistException {
		TeamForming.inst().editTeamProfile(modifieldTeamProfile.course, "",
				originalTeamName, modifieldTeamProfile.team,
				modifieldTeamProfile.profile);
	}

	public void deleteTeamProfile(String courseId, String teamName) {
		TeamForming.inst().deleteTeamProfile(courseId, teamName);
	}

	@SuppressWarnings("unused")
	private void ____STUDENT_ACTION_level_methods_________________________() {
	}

	public void createStudentAction(StudentActionData studentAction)
			throws InvalidParametersException {
		TeamForming.inst().createTeamFormingLogEntry(
				studentAction.toTeamFormingLog());
	}

	public List<StudentActionData> getStudentActions(String courseId) {
		List<TeamFormingLog> actionList = TeamForming.inst()
				.getTeamFormingLogList(courseId);
		ArrayList<StudentActionData> returnList = new ArrayList<StudentActionData>();
		for (TeamFormingLog tfl : actionList) {
			returnList.add(new StudentActionData(tfl));
		}
		return returnList;
	}

	public void editStudentAction(StudentActionData tfl)
			throws NotImplementedException {
		throw new NotImplementedException(
				"Not implemented because there is no need to " + "edit logs");
	}

	public void deleteStudentActions(String courseId) {
		TeamForming.inst().deleteTeamFormingLog(courseId);
	}

	@SuppressWarnings("unused")
	private void ____helper_methods________________________________________() {
	}

	private boolean isInEnrollList(StudentData student,
			ArrayList<StudentData> studentInfoList) {
		for (StudentData studentInfo : studentInfoList) {
			if (studentInfo.email.equalsIgnoreCase(student.email))
				return true;
		}
		return false;
	}

	private boolean isSameAsExistingStudent(StudentData student) {
		StudentData existingStudent = getStudent(student.course,
				student.email);
		if (existingStudent == null)
			return false;
		return student.isEnrollInfoSameAs(existingStudent);
	}

	private boolean isModificationToExistingStudent(StudentData student) {
		return getStudent(student.course, student.email) != null;
	}
}
