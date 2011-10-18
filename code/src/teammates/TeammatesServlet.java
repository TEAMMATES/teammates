package teammates;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.exception.AccountExistsException;
import teammates.exception.CourseDoesNotExistException;
import teammates.exception.CourseExistsException;
import teammates.exception.EvaluationExistsException;
import teammates.exception.GoogleIDExistsInCourseException;
import teammates.exception.RegistrationKeyInvalidException;
import teammates.exception.RegistrationKeyTakenException;
import teammates.jdo.Coordinator;
import teammates.jdo.Course;
import teammates.jdo.CourseDetailsForStudent;
import teammates.jdo.CourseSummaryForCoordinator;
import teammates.jdo.CourseSummaryForStudent;
import teammates.jdo.EnrollmentReport;
import teammates.jdo.Evaluation;
import teammates.jdo.EvaluationDetailsForCoordinator;
import teammates.jdo.Student;
import teammates.jdo.Submission;
import teammates.jdo.SubmissionDetailsForCoordinator;
import teammates.jdo.SubmissionDetailsForStudent;
import teammates.jdo.SubmissionResultsForStudent;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

@SuppressWarnings("serial")
public class TeammatesServlet extends HttpServlet {
	private HttpServletRequest req;
	private HttpServletResponse resp;

	// OPERATIONS
	private static final String OPERATION_ADMINISTRATOR_ADDCOORDINATOR = "administrator_addcoordinator";
	private static final String OPERATION_ADMINISTRATOR_CLEANUP = "administrator_cleanup";
	private static final String OPERATION_ADMINISTRATOR_LOGINATD = "administrator_loginatd";
	private static final String OPERATION_ADMINISTRATOR_LOGINMANAGESYSTEM = "administrator_loginmanagesystem";
	private static final String OPERATION_ADMINISTRATOR_LOGOUT = "administrator_logout";
	private static final String OPERATION_ADMINISTRATOR_SENDREGISTRATIONKEYS = "administrator_sendregistrationkeys";

	private static final String OPERATION_COORDINATOR_ADDCOURSE = "coordinator_addcourse";
	private static final String OPERATION_COORDINATOR_ADDEVALUATION = "coordinator_addevaluation";
	private static final String OPERATION_COORDINATOR_ARCHIVECOURSE = "coordinator_archivecourse";
	private static final String OPERATION_COORDINATOR_DELETEALLSTUDENTS = "coordinator_deleteallstudents";
	private static final String OPERATION_COORDINATOR_DELETECOURSE = "coordinator_deletecourse";
	private static final String OPERATION_COORDINATOR_DELETEEVALUATION = "coordinator_deleteevaluation";
	private static final String OPERATION_COORDINATOR_DELETESTUDENT = "coordinator_deletestudent";
	private static final String OPERATION_COORDINATOR_EDITEVALUATION = "coordinator_editevaluation";
	private static final String OPERATION_COORDINATOR_EDITEVALUATIONRESULTS = "coordinator_editevaluationresults";
	private static final String OPERATION_COORDINATOR_EDITSTUDENT = "coordinator_editstudent";
	private static final String OPERATION_COORDINATOR_ENROLSTUDENTS = "coordinator_enrolstudents";
	private static final String OPERATION_COORDINATOR_GETCOURSE = "coordinator_getcourse";
	private static final String OPERATION_COORDINATOR_GETCOURSELIST = "coordinator_getcourselist";
	private static final String OPERATION_COORDINATOR_GETEVALUATIONLIST = "coordinator_getevaluationlist";
	private static final String OPERATION_COORDINATOR_GETSTUDENTLIST = "coordinator_getstudentlist";
	private static final String OPERATION_COORDINATOR_GETSUBMISSIONLIST = "coordinator_getsubmissionlist";
	private static final String OPERATION_COORDINATOR_INFORMSTUDENTSOFEVALUATIONCHANGES = "coordinator_informstudentsofevaluationchanges";
	private static final String OPERATION_COORDINATOR_LOGIN = "coordinator_login";
	private static final String OPERATION_COORDINATOR_LOGOUT = "coordinator_logout";
	private static final String OPERATION_COORDINATOR_PUBLISHEVALUATION = "coordinator_publishevaluation";
	private static final String OPERATION_COORDINATOR_UNPUBLISHEVALUATION = "coordinator_unpublishevaluation";
	private static final String OPERATION_COORDINATOR_REMINDSTUDENTS = "coordinator_remindstudents";
	private static final String OPERATION_COORDINATOR_SENDREGISTRATIONKEY = "coordinator_sendregistrationkey";
	private static final String OPERATION_COORDINATOR_SENDREGISTRATIONKEYS = "coordinator_sendregistrationkeys";
	private static final String OPERATION_COORDINATOR_UNARCHIVECOURSE = "coordinator_unarchivecourse";

	// TESTING OPERATIONS
	private static final String OPERATION_TEST_OPENEVALUATION = "test_openevaluation";
	private static final String OPERATION_TEST_CLOSEEVALUATION = "test_closeevaluation";
	private static final String OPERATION_TEST_AWAITEVALUATION = "test_awaitevaluation";

	private static final String OPERATION_STUDENT_ARCHIVECOURSE = "student_archivecourse";
	private static final String OPERATION_STUDENT_DELETECOURSE = "student_deletecourse";
	private static final String OPERATION_STUDENT_GETCOURSE = "student_getcourse";
	private static final String OPERATION_STUDENT_GETCOURSELIST = "student_getcourselist";
	private static final String OPERATION_STUDENT_GETPENDINGEVALUATIONLIST = "student_getpendingevaluationlist";
	private static final String OPERATION_STUDENT_GETPASTEVALUATIONLIST = "student_getpastevaluationlist";
	private static final String OPERATION_STUDENT_GETSUBMISSIONLIST = "student_getsubmissionlist";
	private static final String OPERATION_STUDENT_GETSUBMISSIONRESULTSLIST = "student_getsubmissionresultslist";
	private static final String OPERATION_STUDENT_JOINCOURSE = "student_joincourse";
	private static final String OPERATION_STUDENT_LOGIN = "student_login";
	private static final String OPERATION_STUDENT_LOGOUT = "student_logout";
	private static final String OPERATION_STUDENT_SUBMITEVALUATION = "student_submitevaluation";
	private static final String OPERATION_STUDENT_UNARCHIVECOURSE = "student_unarchivecourse";

	// PARAMETERS
	private static final String COORDINATOR_EMAIL = "coordinatoremail";
	private static final String COORDINATOR_GOOGLEID = "coordinatorgoogleID";
	private static final String COORDINATOR_NAME = "coordinatorname";

	private static final String COURSE_COORDINATORNAME = "coordinatorname";
	private static final String COURSE_ID = "courseid";
	private static final String COURSE_NAME = "coursename";
	private static final String COURSE_NUMBEROFTEAMS = "coursenumberofteams";
	private static final String COURSE_STATUS = "coursestatus";

	private static final String EVALUATION_ACTIVATED = "activated";
	private static final String EVALUATION_COMMENTSENABLED = "commentsstatus";
	private static final String EVALUATION_DEADLINE = "deadline";
	private static final String EVALUATION_DEADLINETIME = "deadlinetime";
	private static final String EVALUATION_GRACEPERIOD = "graceperiod";
	private static final String EVALUATION_INSTRUCTIONS = "instr";
	private static final String EVALUATION_NAME = "evaluationname";
	private static final String EVALUATION_NUMBEROFCOMPLETEDEVALUATIONS = "numberofevaluations";
	private static final String EVALUATION_NUMBEROFEVALUATIONS = "numberofcompletedevaluations";
	private static final String EVALUATION_PUBLISHED = "published";
	private static final String EVALUATION_START = "start";
	private static final String EVALUATION_STARTTIME = "starttime";
	private static final String EVALUATION_TIMEZONE = "timezone";

	private static final String STUDENT_COMMENTS = "comments";
	private static final String STUDENT_COMMENTSEDITED = "commentsedited";
	private static final String STUDENT_COMMENTSTOSTUDENT = "commentstostudent";
	private static final String STUDENT_COURSEID = "courseid";
	private static final String STUDENT_EDITCOMMENTS = "editcomments";
	private static final String STUDENT_EDITEMAIL = "editemail";
	private static final String STUDENT_EDITGOOGLEID = "editgoogleid";
	private static final String STUDENT_EDITNAME = "editname";
	private static final String STUDENT_EDITTEAMNAME = "editteamname";
	private static final String STUDENT_EMAIL = "email";
	private static final String STUDENT_FROMSTUDENT = "fromemail";
	private static final String STUDENT_FROMSTUDENTCOMMENTS = "fromstudentcomments";
	private static final String STUDENT_FROMSTUDENTNAME = "fromname";
	private static final String STUDENT_ID = "id";
	private static final String STUDENT_INFORMATION = "information";
	private static final String STUDENT_JUSTIFICATION = "justification";
	private static final String STUDENT_NAME = "name";
	private static final String STUDENT_NAMEEDITED = "nameedited";
	private static final String STUDENT_NUMBEROFSUBMISSIONS = "numberofsubmissions";
	private static final String STUDENT_POINTS = "points";
	private static final String STUDENT_POINTSBUMPRATIO = "pointsbumpratio";
	private static final String STUDENT_REGKEY = "regkey";
	private static final String STUDENT_STATUS = "status";
	private static final String STUDENT_TEAMMATE = "teammate";
	private static final String STUDENT_TEAMMATES = "teammates";
	private static final String STUDENT_TEAMNAME = "teamname";
	private static final String STUDENT_TEAMNAMEEDITED = "teamnameedited";
	private static final String STUDENT_TOSTUDENT = "toemail";
	private static final String STUDENT_TOSTUDENTCOMMENTS = "tostudentcomments";
	private static final String STUDENT_TOSTUDENTNAME = "toname";

	// MESSAGES
	private static final String MSG_COURSE_ADDED = "course added";
	private static final String MSG_COURSE_EXISTS = "course exists";
	private static final String MSG_COURSE_NOTEAMS = "course has no teams";
	private static final String MSG_EVALUATION_ADDED = "evaluation added";
	private static final String MSG_EVALUATION_DEADLINEPASSED = "evaluation deadline passed";
	private static final String MSG_EVALUATION_EDITED = "evaluation edited";
	private static final String MSG_EVALUATION_EXISTS = "evaluation exists";
	private static final String MSG_EVALUATION_UNABLETOCHANGETEAMS = "evaluation ongoing unable to change teams";
	private static final String MSG_EVALUATION_REMAINED = "evaluation remained";
	private static final String MSG_STATUS_OPENING = "<status>";
	private static final String MSG_STATUS_CLOSING = "</status>";
	private static final String MSG_STUDENT_COURSEJOINED = "course joined";
	private static final String MSG_STUDENT_GOOGLEIDEXISTSINCOURSE = "googleid exists in course";
	private static final String MSG_STUDENT_REGISTRATIONKEYINVALID = "registration key invalid";
	private static final String MSG_STUDENT_REGISTRATIONKEYTAKEN = "registration key taken";

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		// Initialization
		this.req = req;
		this.resp = resp;

		this.resp.setContentType("text/xml");
		this.resp.setHeader("Cache-Control", "no-cache");

		// Processing
		String operation = this.req.getParameter("operation");

		if (operation == null) {
			System.out.println("no operation specified");
			return;
		}

		System.out.println(Thread.currentThread().getId() + ": " + operation);

		if (operation.equals(OPERATION_ADMINISTRATOR_ADDCOORDINATOR)) {
			administratorAddCoordinator();
		}

		else if (operation.equals(OPERATION_ADMINISTRATOR_CLEANUP)) {
			administratorCleanUp();
		}

		else if (operation.equals(OPERATION_ADMINISTRATOR_LOGINATD)) {
			administratorLoginATD();
		}

		else if (operation.equals(OPERATION_ADMINISTRATOR_LOGINMANAGESYSTEM)) {
			administratorLoginManageSystem();
		}

		else if (operation.equals(OPERATION_ADMINISTRATOR_LOGOUT)) {
			administratorLogout();
		}

		else if (operation.equals(OPERATION_ADMINISTRATOR_SENDREGISTRATIONKEYS)) {
			administratorSendRegistrationKeys();
		}

		else if (operation.equals(OPERATION_COORDINATOR_ADDCOURSE)) {
			coordinatorAddCourse();
		}

		else if (operation.equals(OPERATION_COORDINATOR_ADDEVALUATION)) {
			coordinatorAddEvaluation();
		}

		else if (operation.equals(OPERATION_COORDINATOR_ARCHIVECOURSE)) {
			coordinatorArchiveCourse();
		}

		else if (operation.equals(OPERATION_COORDINATOR_DELETEALLSTUDENTS)) {
			coordinatorDeleteAllStudents();
		}

		else if (operation.equals(OPERATION_COORDINATOR_DELETECOURSE)) {
			coordinatorDeleteCourse();
		}

		else if (operation.equals(OPERATION_COORDINATOR_DELETEEVALUATION)) {
			coordinatorDeleteEvaluation();
		}

		else if (operation.equals(OPERATION_COORDINATOR_DELETESTUDENT)) {
			coordinatorDeleteStudent();
		}

		else if (operation.equals(OPERATION_COORDINATOR_EDITEVALUATION)) {
			coordinatorEditEvaluation();
		}

		else if (operation.equals(OPERATION_COORDINATOR_EDITEVALUATIONRESULTS)) {
			coordinatorEditEvaluationResults();
		}

		else if (operation.equals(OPERATION_COORDINATOR_EDITSTUDENT)) {
			coordinatorEditStudent();
		}

		else if (operation.equals(OPERATION_COORDINATOR_ENROLSTUDENTS)) {
			coordinatorEnrolStudents();
		}

		else if (operation.equals(OPERATION_COORDINATOR_GETCOURSE)) {
			coordinatorGetCourse();
		}

		else if (operation.equals(OPERATION_COORDINATOR_GETCOURSELIST)) {
			coordinatorGetCourseList();
		}

		else if (operation.equals(OPERATION_COORDINATOR_GETEVALUATIONLIST)) {
			coordinatorGetEvaluationList();
		}

		else if (operation.equals(OPERATION_COORDINATOR_GETSTUDENTLIST)) {
			coordinatorGetStudentList();
		}

		else if (operation.equals(OPERATION_COORDINATOR_GETSUBMISSIONLIST)) {
			coordinatorGetSubmissionList();
		}

		else if (operation
				.equals(OPERATION_COORDINATOR_INFORMSTUDENTSOFEVALUATIONCHANGES)) {
			coordinatorInformStudentsOfEvaluationChanges();
		}

		else if (operation.equals(OPERATION_COORDINATOR_LOGIN)) {
			coordinatorLogin();
		}

		else if (operation.equals(OPERATION_COORDINATOR_LOGOUT)) {
			coordinatorLogout();
		}

		else if (operation.equals(OPERATION_COORDINATOR_PUBLISHEVALUATION)) {
			coordinatorPublishEvaluation();
		}

		else if (operation.equals(OPERATION_COORDINATOR_UNPUBLISHEVALUATION)) {
			coordinatorUnpublishEvaluation();
		}

		else if (operation.equals(OPERATION_COORDINATOR_REMINDSTUDENTS)) {
			coordinatorRemindStudents();
		}

		else if (operation.equals(OPERATION_COORDINATOR_SENDREGISTRATIONKEY)) {
			coordinatorSendRegistrationKey();
		}

		else if (operation.equals(OPERATION_COORDINATOR_SENDREGISTRATIONKEYS)) {
			coordinatorSendRegistrationKeys();
		}

		else if (operation.equals(OPERATION_COORDINATOR_UNARCHIVECOURSE)) {
			coordinatorUnarchiveCourse();
		}

		else if (operation.equals(OPERATION_STUDENT_ARCHIVECOURSE)) {
			studentArchiveCourse();
		}

		else if (operation.equals(OPERATION_STUDENT_DELETECOURSE)) {
			studentDeleteCourse();
		}

		else if (operation.equals(OPERATION_STUDENT_GETCOURSE)) {
			studentGetCourse();
		}

		else if (operation.equals(OPERATION_STUDENT_GETCOURSELIST)) {
			studentGetCourseList();
		}

		else if (operation.equals(OPERATION_STUDENT_GETPASTEVALUATIONLIST)) {
			studentGetPastEvaluationList();
		}

		else if (operation.equals(OPERATION_STUDENT_GETPENDINGEVALUATIONLIST)) {
			studentGetPendingEvaluationList();
		}

		else if (operation.equals(OPERATION_STUDENT_GETSUBMISSIONLIST)) {
			studentGetSubmissionList();
		}

		else if (operation.equals(OPERATION_STUDENT_GETSUBMISSIONRESULTSLIST)) {
			studentGetSubmissionResultsList();
		}

		else if (operation.equals(OPERATION_STUDENT_JOINCOURSE)) {
			studentJoinCourse();
		}

		else if (operation.equals(OPERATION_STUDENT_LOGIN)) {
			studentLogin();
		}

		else if (operation.equals(OPERATION_STUDENT_LOGOUT)) {
			studentLogout();
		}

		else if (operation.equals(OPERATION_STUDENT_SUBMITEVALUATION)) {
			studentSubmitEvaluation();
		}

		else if (operation.equals(OPERATION_STUDENT_UNARCHIVECOURSE)) {
			studentUnarchiveCourse();
		}

		else if (operation.equals(OPERATION_TEST_OPENEVALUATION)) {
			testOpenEvaluation();
		}

		else if (operation.equals(OPERATION_TEST_CLOSEEVALUATION)) {
			testCloseEvaluation();
		}

		else if (operation.equals(OPERATION_TEST_AWAITEVALUATION)) {
			testAwaitEvaluation();
		} else {
			System.out.println("unknown command");
		}
		// Clean-up
		this.resp.flushBuffer();
	}

	private void administratorAddCoordinator() {
		String googleID = req.getParameter(COORDINATOR_GOOGLEID);
		String name = req.getParameter(COORDINATOR_NAME);
		String email = req.getParameter(COORDINATOR_EMAIL);

		Accounts accounts = Accounts.inst();

		try {
			accounts.addCoordinator(googleID, name, email);
		}

		catch (AccountExistsException e) {

		}

	}

	private void administratorCleanUp() {
		Courses courses = Courses.inst();
		String courseID = req.getParameter(COURSE_ID);

		courses.deleteCoordinatorCourse(courseID);

	}

	private void administratorLoginATD() throws IOException {
		Accounts accounts = Accounts.inst();

		resp.getWriter().write(
				"<url><![CDATA[" + accounts.getLoginPage("/atd.jsp")
						+ "]]></url>");
	}

	private void administratorLoginManageSystem() throws IOException {
		Accounts accounts = Accounts.inst();

		resp.getWriter().write(
				"<url><![CDATA[" + accounts.getLoginPage("/administrator.jsp")
						+ "]]></url>");
	}

	private void administratorLogout() throws IOException {
		Accounts accounts = Accounts.inst();

		resp.getWriter().write(
				"<url><![CDATA[" + accounts.getLogoutPage("/admin.html")
						+ "]]></url>");
	}

	private void administratorSendRegistrationKeys() {
		String email = req.getParameter(STUDENT_EMAIL);

		// Create dud Student objects with e-mail provided by tester
		List<Student> studentList = new ArrayList<Student>();

		for (int x = 0; x < 40; x++) {
			Student s = new Student(email, "Admin", ("Comments"),
					"Test Course", "Test TeamName");
			s.setRegistrationKey((long) 1111);
			studentList.add(s);
		}

		// Send the keys to the dud Student objects with e-mail provided by
		// tester
		Courses courses = Courses.inst();
		courses.sendRegistrationKeys(studentList, "Test CourseID",
				"Test Course", "ADMIN", Config.TEAMMATES_APP_ACCOUNT);
	}

	private void coordinatorAddCourse() throws IOException, ServletException {
		Accounts accounts = Accounts.inst();
		String googleID = accounts.getUser().getNickname().toLowerCase();

		Courses courses = Courses.inst();

		try {
			courses.addCourse(req.getParameter(COURSE_ID),
					req.getParameter(COURSE_NAME), googleID);

			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_COURSE_ADDED + MSG_STATUS_CLOSING);
		}

		catch (CourseExistsException e) {
			resp.getWriter()
					.write(MSG_STATUS_OPENING + MSG_COURSE_EXISTS
							+ MSG_STATUS_CLOSING);
		}
	}

	private void coordinatorAddEvaluation() throws IOException {
		String courseID = req.getParameter(COURSE_ID);

		Courses courses = Courses.inst();
		int numberOfTeams = courses.getNumberOfTeams(courseID);

		if (numberOfTeams == 0) {
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_COURSE_NOTEAMS
							+ MSG_STATUS_CLOSING);
		}

		else {
			String name = req.getParameter(EVALUATION_NAME);
			String instructions = req.getParameter(EVALUATION_INSTRUCTIONS);

			boolean commentsEnabled = Boolean.parseBoolean(req
					.getParameter(EVALUATION_COMMENTSENABLED));

			String startDate = req.getParameter(EVALUATION_START);
			int startTime = Integer.parseInt(req
					.getParameter(EVALUATION_STARTTIME));

			String deadlineDate = req.getParameter(EVALUATION_DEADLINE);
			int deadlineTime = Integer.parseInt(req
					.getParameter(EVALUATION_DEADLINETIME));

			double timeZone = Double.parseDouble(req
					.getParameter(EVALUATION_TIMEZONE));

			int gracePeriod = Integer.parseInt(req
					.getParameter(EVALUATION_GRACEPERIOD));

			Date start = Utils.convertToDate(startDate, startTime);
			Date deadline = Utils.convertToDate(deadlineDate, deadlineTime);

			// Add the evaluation
			Evaluations evaluations = Evaluations.inst();

			try {
				evaluations
						.addEvaluation(courseID, name, instructions,
								commentsEnabled, start, deadline, timeZone,
								gracePeriod);

				resp.getWriter().write(
						MSG_STATUS_OPENING + MSG_EVALUATION_ADDED
								+ MSG_STATUS_CLOSING);
			}

			catch (EvaluationExistsException e) {
				resp.getWriter().write(
						MSG_STATUS_OPENING + MSG_EVALUATION_EXISTS
								+ MSG_STATUS_CLOSING);
			}
		}

	}

	private void coordinatorArchiveCourse() throws IOException,
			ServletException {
		Courses courses = Courses.inst();

		courses.archiveCoordinatorCourse(req.getParameter(COURSE_ID));
	}

	private void coordinatorDeleteAllStudents() {
		Courses courses = Courses.inst();
		courses.deleteAllStudents(req.getParameter(COURSE_ID));

	}

	private void coordinatorDeleteCourse() {
		Courses courses = Courses.inst();
		String courseID = req.getParameter(COURSE_ID);

		courses.deleteCoordinatorCourse(courseID);

		Evaluations evaluations = Evaluations.inst();
		evaluations.deleteEvaluations(courseID);
	}

	private void coordinatorDeleteEvaluation() {
		String courseID = req.getParameter(COURSE_ID);
		String name = req.getParameter(EVALUATION_NAME);

		Evaluations evaluations = Evaluations.inst();
		evaluations.deleteEvaluation(courseID, name);
	}

	private void coordinatorDeleteStudent() {
		Courses courses = Courses.inst();

		courses.deleteStudent(req.getParameter(COURSE_ID),
				req.getParameter(STUDENT_EMAIL));

	}

	private void coordinatorEditEvaluation() throws IOException {
		String courseID = req.getParameter(COURSE_ID);
		String name = req.getParameter(EVALUATION_NAME);
		String newInstructions = req.getParameter(EVALUATION_INSTRUCTIONS);

		boolean newCommentsEnabled = Boolean.parseBoolean(req
				.getParameter(EVALUATION_COMMENTSENABLED));

		String newStartDate = req.getParameter(EVALUATION_START);
		int newStartTime = Integer.parseInt(req
				.getParameter(EVALUATION_STARTTIME));

		String newDeadlineDate = req.getParameter(EVALUATION_DEADLINE);
		int newDeadlineTime = Integer.parseInt(req
				.getParameter(EVALUATION_DEADLINETIME));

		int newGracePeriod = Integer.parseInt(req
				.getParameter(EVALUATION_GRACEPERIOD));

		Date newStart = Utils.convertToDate(newStartDate, newStartTime);
		Date newDeadline = Utils
				.convertToDate(newDeadlineDate, newDeadlineTime);

		Evaluations evaluations = Evaluations.inst();

		boolean edited = evaluations.editEvaluation(courseID, name,
				(newInstructions), newCommentsEnabled, newStart, newDeadline,
				newGracePeriod);

		if (edited) {
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_EVALUATION_EDITED
							+ MSG_STATUS_CLOSING);
		}

		else {
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_EVALUATION_REMAINED
							+ MSG_STATUS_CLOSING);
		}

	}

	private void coordinatorEditEvaluationResults() {
		List<Submission> submissionList = new ArrayList<Submission>();

		int numberOfSubmissions = Integer.parseInt(req
				.getParameter(STUDENT_NUMBEROFSUBMISSIONS));

		String fromStudent = "";
		String toStudent = "";
		int points = 0;
		String justification = "";
		String commentsToStudent = "";

		String courseID = req.getParameter(COURSE_ID);
		String evaluationName = req.getParameter(EVALUATION_NAME);
		String teamName = req.getParameter(STUDENT_TEAMNAME);

		for (int x = 0; x < numberOfSubmissions; x++) {
			fromStudent = req.getParameter(STUDENT_FROMSTUDENT + x);
			toStudent = req.getParameter(STUDENT_TOSTUDENT + x);
			points = Integer.parseInt(req.getParameter(STUDENT_POINTS + x));
			justification = req.getParameter(STUDENT_JUSTIFICATION + x);
			commentsToStudent = req.getParameter(STUDENT_COMMENTSTOSTUDENT + x);

			submissionList.add(new Submission(fromStudent, toStudent, courseID,
					evaluationName, teamName, points, new Text(justification),
					new Text(commentsToStudent)));
		}

		Evaluations evaluations = Evaluations.inst();

		evaluations.editSubmissions(submissionList);
	}

	/**
	 * Edit the logic of this function Student information change shouldn't be
	 * constraint by evaluation status Name (can change at any time) update
	 * student table only Team (can change at any time, changes wouldn�t affect
	 * existing submissions) update student table only Google ID (fixed after
	 * registration, checking done at js step) Email (can change at any time,
	 * per course basis, must update THAT course�s submission) update student
	 * table, submission table Comments (can change at any time)
	 * 
	 * @throws IOException
	 * @author wangsha
	 */
	private void coordinatorEditStudent() throws IOException {
		String courseID = req.getParameter(COURSE_ID);
		String email = req.getParameter(STUDENT_EMAIL);
		String newName = req.getParameter(STUDENT_EDITNAME);
		String newTeamName = req.getParameter(STUDENT_EDITTEAMNAME);
		String newEmail = req.getParameter(STUDENT_EDITEMAIL);
		String newGoogleID = req.getParameter(STUDENT_EDITGOOGLEID);
		String newComments = req.getParameter(STUDENT_EDITCOMMENTS);

		if (newGoogleID == null) {
			newGoogleID = "";
		}

		if (newComments == null) {
			newComments = "";
		}

		Courses courses = Courses.inst();
		Evaluations evaluations = Evaluations.inst();

		System.out.println(newEmail + "|" + email);

		// Check duplicate email
		Student dupStudent = courses.getStudentWithEmail(courseID, newEmail);
		if (dupStudent != null && !dupStudent.getID().equals(newGoogleID)) {
			System.out.println(courses.getStudentWithEmail(courseID, newEmail)
					.getID());
			System.out.println(newGoogleID);

			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_EVALUATION_UNABLETOCHANGETEAMS
							+ MSG_STATUS_CLOSING);
			return;
		}

		// Update Student info for THAT course
		courses.editStudent(courseID, email, newName, newTeamName, newEmail,
				newGoogleID, newComments);

		// Update Submission info for THAT course
		evaluations.editSubmissions(courseID, email, newEmail);

		resp.getWriter()
				.write(MSG_STATUS_OPENING + MSG_EVALUATION_EDITED
						+ MSG_STATUS_CLOSING);
	}

	private void coordinatorEnrolStudents() throws IOException {
		String information = req.getParameter(STUDENT_INFORMATION);
		String courseID = req.getParameter(COURSE_ID);

		// Break down the input into Student objects
		List<Student> studentList = new ArrayList<Student>();

		String entries[] = information.split("\n");
		String fields[];
		String name;
		String email;
		String teamName;
		String comments;
		Set<String> emails = new HashSet<String>();

		for (int x = 0; x < entries.length; x++) {

			if (entries[x].equals("")) {
				// do nothing;
			} else {
				fields = entries[x].split("\t");
				name = fields[1];
				email = fields[2];
				teamName = fields[0];
				// Comments for student are optional
				if (fields.length == 4) {
					comments = (fields[3]);
				}

				else {
					comments = "";
				}

				if (!emails.contains(email)) {
					studentList.add(new Student(email, name, comments,
							courseID, teamName));
					emails.add(email);
				}
			}

		}

		List<EnrollmentReport> enrollmentReportList = new ArrayList<EnrollmentReport>();

		// Check to see if there is an ongoing evaluation. If there is, do not
		// edit
		// students' teams.
		Courses courses = Courses.inst();

		/*
		 * List<Student> currentStudentList = courses.getStudentList(courseID);
		 * 
		 * Evaluations evaluations = Evaluations.inst();
		 * 
		 * if (evaluations.isEvaluationOngoing(courseID)) { for (Student s :
		 * studentList) { for (Student cs : currentStudentList) { if
		 * (s.getEmail().equals(cs.getEmail()) &&
		 * !s.getTeamName().equals(cs.getTeamName())) {
		 * s.setTeamName(cs.getTeamName()); } } } }
		 */

		// Add and edit Student objects in the datastore
		enrollmentReportList.addAll(courses
				.enrolStudents(studentList, courseID));

		resp.getWriter().write(
				"<enrollmentreports>"
						+ parseEnrollmentReportListToXML(enrollmentReportList)
								.toString() + "</enrollmentreports>");
	}

	private void coordinatorGetCourse() throws IOException, ServletException {
		Courses courses = Courses.inst();
		Course course = courses.getCourse(req.getParameter(COURSE_ID));

		CourseSummaryForCoordinator courseSummary = new CourseSummaryForCoordinator(
				course.getID(), course.getName(), course.isArchived(),
				courses.getNumberOfTeams(course.getID()));

		ArrayList<CourseSummaryForCoordinator> courseSummaryList = new ArrayList<CourseSummaryForCoordinator>();
		courseSummaryList.add(courseSummary);

		resp.getWriter().write(
				"<courses>"
						+ parseCourseSummaryForCoordinatorListToXML(
								courseSummaryList).toString() + "</courses>");

	}

	private void coordinatorGetCourseList() throws IOException,
			ServletException {
		Accounts accounts = Accounts.inst();
		String googleID = accounts.getUser().getNickname().toLowerCase();

		Courses courses = Courses.inst();

		List<Course> courseList = courses.getCoordinatorCourseList(googleID);
		ArrayList<CourseSummaryForCoordinator> courseSummaryList = new ArrayList<CourseSummaryForCoordinator>();

		for (Course c : courseList) {
			CourseSummaryForCoordinator cs = new CourseSummaryForCoordinator(
					c.getID(), c.getName(), c.isArchived(),
					courses.getNumberOfTeams(c.getID()));
			courseSummaryList.add(cs);
		}

		resp.getWriter().write(
				"<courses>"
						+ parseCourseSummaryForCoordinatorListToXML(
								courseSummaryList).toString() + "</courses>");
	}

	private void coordinatorGetEvaluationList() throws IOException {
		Accounts accounts = Accounts.inst();
		String googleID = accounts.getUser().getNickname().toLowerCase();

		Courses courses = Courses.inst();
		List<Course> courseList = courses.getCoordinatorCourseList(googleID);

		Evaluations evaluations = Evaluations.inst();
		List<Evaluation> evaluationList = evaluations
				.getEvaluationList(courseList);

		List<EvaluationDetailsForCoordinator> evaluationDetailsList = new ArrayList<EvaluationDetailsForCoordinator>();

		int numberOfCompletedEvaluations = 0;
		int numberOfEvaluations = 0;

		for (Evaluation e : evaluationList) {
			if (courses.getCourse(e.getCourseID()).isArchived() != true) {
				numberOfCompletedEvaluations = evaluations
						.getNumberOfCompletedEvaluations(e.getCourseID(),
								e.getName());
				numberOfEvaluations = evaluations.getNumberOfEvaluations(
						e.getCourseID(), e.getName());

				evaluationDetailsList.add(new EvaluationDetailsForCoordinator(e
						.getCourseID(), e.getName(), e.getInstructions(), e
						.isCommentsEnabled(), e.getStart(), e.getDeadline(), e
						.getTimeZone(), e.getGracePeriod(), e.isPublished(), e
						.isActivated(), numberOfCompletedEvaluations,
						numberOfEvaluations));
			}
		}

		resp.getWriter().write(
				"<evaluations>"
						+ parseEvaluationDetailsForCoordinatorListToXML(
								evaluationDetailsList).toString()
						+ "</evaluations>");
	}

	private void coordinatorGetStudentList() throws IOException,
			ServletException {
		Courses courses = Courses.inst();
		List<Student> studentList = courses.getStudentList(req
				.getParameter(COURSE_ID));

		resp.getWriter().write(
				"<students>" + parseStudentListToXML(studentList).toString()
						+ "</students>");

	}

	private void coordinatorGetSubmissionList() throws IOException {
		String courseID = req.getParameter(COURSE_ID);
		String evaluationName = req.getParameter(EVALUATION_NAME);

		Evaluations evaluations = Evaluations.inst();
		List<Submission> submissionList = evaluations.getSubmissionList(
				courseID, evaluationName);

		List<SubmissionDetailsForCoordinator> submissionDetailsList = new ArrayList<SubmissionDetailsForCoordinator>();

		String fromStudentName = "";
		String toStudentName = "";

		String fromStudentComments = null;
		String toStudentComments = null;
		Student student = null;

		float pointsBumpRatio = 0;

		Courses courses = Courses.inst();
		List<Student> studentList = courses.getStudentList(courseID);

		for (Submission s : submissionList) {
			student = null;
			// search for student
			for (Student stu : studentList) {
				if (stu.getEmail().equals(s.getFromStudent())) {
					student = stu;
				}
			}
			if (student == null) {
				fromStudentName = "[deleted]" + s.getFromStudent();
				fromStudentComments = "";
			} else {
				fromStudentName = student.getName();
				fromStudentComments = student.getComments();
			}

			student = courses.getStudentWithEmail(courseID, s.getToStudent());
			if (student == null) {
				toStudentName = "[deleted]" + s.getToStudent();
				toStudentComments = ("");
			} else {
				toStudentName = student.getName();
				toStudentComments = student.getComments();
			}

			// filter submisstion list by fromStudent
			List<Submission> fromList = new LinkedList<Submission>();
			for (Submission fs : submissionList) {
				if (fs.getFromStudent().equals(s.getFromStudent()))					
					fromList.add(fs);
			}

			pointsBumpRatio = evaluations.calculatePointsBumpRatio(courseID,
					evaluationName, s.getFromStudent(), fromList);

			submissionDetailsList.add(new SubmissionDetailsForCoordinator(
					courseID, evaluationName, fromStudentName, toStudentName, s
							.getFromStudent(), s.getToStudent(),
					fromStudentComments, toStudentComments, s.getTeamName(), s
							.getPoints(), pointsBumpRatio,
					s.getJustification(), s.getCommentsToStudent()));

		}

		resp.getWriter().write(
				"<submissions>"
						+ parseSubmissionDetailsForCoordinatorListToXML(
								submissionDetailsList).toString()
						+ "</submissions>");

	}

	private void coordinatorInformStudentsOfEvaluationChanges() {
		String courseID = req.getParameter(COURSE_ID);
		String evaluationName = req.getParameter(EVALUATION_NAME);

		Courses courses = Courses.inst();
		List<Student> studentList = courses.getStudentList(courseID);

		Evaluations evaluations = Evaluations.inst();
		evaluations.informStudentsOfChanges(studentList, courseID,
				evaluationName);

	}

	private void coordinatorLogin() throws IOException, ServletException {
		Accounts accounts = Accounts.inst();
		resp.getWriter().write(
				"<url><![CDATA[" + accounts.getLoginPage("/coordinator.jsp")
						+ "]]></url>");

	}

	private void coordinatorLogout() throws IOException, ServletException {
		Accounts accounts = Accounts.inst();
		resp.getWriter().write(
				"<url><![CDATA[" + accounts.getLogoutPage("") + "]]></url>");

	}

	private void coordinatorPublishEvaluation() {
		String courseID = req.getParameter(COURSE_ID);
		String name = req.getParameter(EVALUATION_NAME);

		Courses courses = Courses.inst();
		List<Student> studentList = courses.getStudentList(courseID);

		Evaluations evaluations = Evaluations.inst();
		evaluations.publishEvaluation(courseID, name, studentList);

	}

	private void coordinatorRemindStudents() {
		String courseID = req.getParameter(COURSE_ID);
		String evaluationName = req.getParameter(EVALUATION_NAME);

		Courses courses = Courses.inst();
		List<Student> studentList = courses.getStudentList(courseID);

		// Filter out students who have submitted the evaluation
		Evaluations evaluations = Evaluations.inst();
		Evaluation evaluation = evaluations.getEvaluation(courseID,
				evaluationName);

		if (evaluation == null) {
			System.err
					.println(String
							.format("Evaluation not found. CourseID = %s. Evaluation Name = %s",
									courseID, evaluationName));
			return;
		}

		List<Student> studentsToRemindList = new ArrayList<Student>();

		for (Student s : studentList) {
			if (!evaluations.isEvaluationSubmitted(evaluation, s.getEmail())) {
				studentsToRemindList.add(s);
			}
		}

		Date deadline = evaluation.getDeadline();

		evaluations.remindStudents(studentsToRemindList, courseID,
				evaluationName, deadline);

	}

	private void coordinatorSendRegistrationKey() {
		Courses courses = Courses.inst();
		Accounts accounts = Accounts.inst();

		String courseID = req.getParameter(COURSE_ID);
		String email = req.getParameter(STUDENT_EMAIL);

		Course course = courses.getCourse(courseID);
		Student student = courses.getStudentWithEmail(courseID, email);

		List<Student> studentList = new ArrayList<Student>();
		studentList.add(student);

		String courseName = course.getName();
		Coordinator coord = accounts.getCoordinator(course.getCoordinatorID());

		courses.sendRegistrationKeys(studentList, courseID, courseName,
				coord.getName(), coord.getEmail());
	}

	private void coordinatorSendRegistrationKeys() {
		Courses courses = Courses.inst();
		Accounts accounts = Accounts.inst();

		// Get unregistered students
		List<Student> studentList = courses.getUnregisteredStudentList(req
				.getParameter(COURSE_ID));

		if (!studentList.isEmpty()) {
			Course course = courses.getCourse(studentList.get(0).getCourseID());

			String courseID = course.getID();
			String courseName = course.getName();
			Coordinator coord = accounts.getCoordinator(course
					.getCoordinatorID());

			courses.sendRegistrationKeys(studentList, courseID, courseName,
					coord.getName(), coord.getEmail());
		}
	}

	private void coordinatorUnarchiveCourse() throws IOException,
			ServletException {
		Courses courses = Courses.inst();

		courses.unarchiveCoordinatorCourse(req.getParameter(COURSE_ID));
	}

	private void coordinatorUnpublishEvaluation() {
		String courseID = req.getParameter(COURSE_ID);
		String name = req.getParameter(EVALUATION_NAME);

		Evaluations evaluations = Evaluations.inst();
		evaluations.unpublishEvaluation(courseID, name);

	}

	private StringBuffer parseCourseDetailsForStudentToXML(
			CourseDetailsForStudent courseDetails) {
		StringBuffer sb = new StringBuffer();

		sb.append("<coursedetails>");
		sb.append("<" + COURSE_ID + "><![CDATA[" + courseDetails.getCourseID()
				+ "]]></" + COURSE_ID + ">");
		sb.append("<" + COURSE_NAME + "><![CDATA["
				+ courseDetails.getCourseName() + "]]></" + COURSE_NAME + ">");
		sb.append("<" + COURSE_COORDINATORNAME + "><![CDATA["
				+ courseDetails.getCoordinatorName() + "]]></"
				+ COURSE_COORDINATORNAME + ">");
		sb.append("<" + STUDENT_TEAMNAME + "><![CDATA["
				+ courseDetails.getTeamName() + "]]></" + STUDENT_TEAMNAME
				+ ">");
		sb.append("<" + STUDENT_NAME + "><![CDATA["
				+ courseDetails.getStudentName() + "]]></" + STUDENT_NAME + ">");
		sb.append("<" + STUDENT_EMAIL + "><![CDATA["
				+ courseDetails.getStudentEmail() + "]]></" + STUDENT_EMAIL
				+ ">");
		sb.append("<" + STUDENT_TEAMMATES + ">");

		for (String s : courseDetails.getTeammateList()) {
			sb.append("<" + STUDENT_TEAMMATE + "><![CDATA[" + s + "]]></"
					+ STUDENT_TEAMMATE + ">");
		}

		sb.append("</" + STUDENT_TEAMMATES + ">");
		sb.append("</coursedetails>");

		return sb;
	}

	private StringBuffer parseCourseSummaryForCoordinatorListToXML(
			ArrayList<CourseSummaryForCoordinator> courseSummaryList) {
		StringBuffer sb = new StringBuffer();

		for (CourseSummaryForCoordinator cs : courseSummaryList) {
			sb.append("<coursesummary>");
			sb.append("<" + COURSE_ID + "><![CDATA[" + cs.getID() + "]]></"
					+ COURSE_ID + ">");
			sb.append("<" + COURSE_NAME + "><![CDATA[" + cs.getName() + "]]></"
					+ COURSE_NAME + ">");
			sb.append("<" + COURSE_STATUS + ">" + cs.isArchived() + "</"
					+ COURSE_STATUS + ">");
			sb.append("<" + COURSE_NUMBEROFTEAMS + ">" + cs.getNumberOfTeams()
					+ "</" + COURSE_NUMBEROFTEAMS + ">");
			sb.append("</coursesummary>");
		}

		return sb;
	}

	private StringBuffer parseCourseSummaryForStudentListToXML(
			ArrayList<CourseSummaryForStudent> courseSummaryList) {
		StringBuffer sb = new StringBuffer();

		for (CourseSummaryForStudent cs : courseSummaryList) {
			sb.append("<coursesummary>");
			sb.append("<" + COURSE_ID + "><![CDATA[" + cs.getID() + "]]></"
					+ COURSE_ID + ">");
			sb.append("<" + COURSE_NAME + "><![CDATA[" + cs.getName() + "]]></"
					+ COURSE_NAME + ">");
			sb.append("<" + COURSE_STATUS + ">" + cs.isArchived() + "</"
					+ COURSE_STATUS + ">");
			sb.append("<" + STUDENT_TEAMNAME + "><![CDATA[" + cs.getTeamName()
					+ "]]></" + STUDENT_TEAMNAME + ">");
			sb.append("</coursesummary>");
		}

		return sb;
	}

	private StringBuffer parseEnrollmentReportListToXML(
			List<EnrollmentReport> enrollmentReportList) {
		StringBuffer sb = new StringBuffer();

		for (EnrollmentReport er : enrollmentReportList) {
			sb.append("<enrollmentreport>");
			sb.append("<" + STUDENT_NAME + "><![CDATA[" + er.getName()
					+ "]]></" + STUDENT_NAME + ">");
			sb.append("<" + STUDENT_EMAIL + "><![CDATA[" + er.getEmail()
					+ "]]></" + STUDENT_EMAIL + ">");
			sb.append("<" + STUDENT_STATUS + "><![CDATA[" + er.getStatus()
					+ "]]></" + STUDENT_STATUS + ">");
			sb.append("<" + STUDENT_NAMEEDITED + ">" + er.isNameEdited() + "</"
					+ STUDENT_NAMEEDITED + ">");
			sb.append("<" + STUDENT_TEAMNAMEEDITED + ">"
					+ er.isTeamNameEdited() + "</" + STUDENT_TEAMNAMEEDITED
					+ ">");
			sb.append("<" + STUDENT_COMMENTSEDITED + ">"
					+ er.isCommentsEdited() + "</" + STUDENT_COMMENTSEDITED
					+ ">");
			sb.append("</enrollmentreport>");
		}

		return sb;
	}

	private StringBuffer parseEvaluationDetailsForCoordinatorListToXML(
			List<EvaluationDetailsForCoordinator> evaluationDetailsList) {
		StringBuffer sb = new StringBuffer();

		for (EvaluationDetailsForCoordinator e : evaluationDetailsList) {
			sb.append("<evaluation>");

			sb.append("<" + COURSE_ID + "><![CDATA[" + e.getCourseID()
					+ "]]></" + COURSE_ID + ">");
			sb.append("<" + EVALUATION_NAME + "><![CDATA[" + e.getName()
					+ "]]></" + EVALUATION_NAME + ">");
			sb.append("<" + EVALUATION_COMMENTSENABLED + "><![CDATA["
					+ e.isCommentsEnabled() + "]]></"
					+ EVALUATION_COMMENTSENABLED + ">");
			sb.append("<" + EVALUATION_INSTRUCTIONS + "><![CDATA["
					+ e.getInstructions() + "]]></" + EVALUATION_INSTRUCTIONS
					+ ">");
			sb.append("<" + EVALUATION_START + "><![CDATA["
					+ DateFormat.getDateTimeInstance().format(e.getStart())
					+ "]]></" + EVALUATION_START + ">");
			sb.append("<" + EVALUATION_DEADLINE + "><![CDATA["
					+ DateFormat.getDateTimeInstance().format(e.getDeadline())
					+ "]]></" + EVALUATION_DEADLINE + ">");
			sb.append("<" + EVALUATION_TIMEZONE + "><![CDATA["
					+ e.getTimeZone() + "]]></" + EVALUATION_TIMEZONE + ">");
			sb.append("<" + EVALUATION_GRACEPERIOD + "><![CDATA["
					+ e.getGracePeriod() + "]]></" + EVALUATION_GRACEPERIOD
					+ ">");
			sb.append("<" + EVALUATION_PUBLISHED + "><![CDATA["
					+ e.isPublished() + "]]></" + EVALUATION_PUBLISHED + ">");
			sb.append("<" + EVALUATION_ACTIVATED + "><![CDATA["
					+ e.isActivated() + "]]></" + EVALUATION_ACTIVATED + ">");
			sb.append("<" + EVALUATION_NUMBEROFCOMPLETEDEVALUATIONS
					+ "><![CDATA[" + e.getNumberOfCompletedEvaluations()
					+ "]]></" + EVALUATION_NUMBEROFCOMPLETEDEVALUATIONS + ">");
			sb.append("<" + EVALUATION_NUMBEROFEVALUATIONS + "><![CDATA["
					+ e.getNumberOfEvaluations() + "]]></"
					+ EVALUATION_NUMBEROFEVALUATIONS + ">");

			sb.append("</evaluation>");
		}

		return sb;
	}

	private StringBuffer parseEvaluationListToXML(
			List<Evaluation> evaluationList) {
		StringBuffer sb = new StringBuffer();

		for (Evaluation e : evaluationList) {
			sb.append("<evaluation>");
			sb.append("<" + COURSE_ID + "><![CDATA[" + e.getCourseID()
					+ "]]></" + COURSE_ID + ">");
			sb.append("<" + EVALUATION_NAME + "><![CDATA[" + e.getName()
					+ "]]></" + EVALUATION_NAME + ">");
			sb.append("<" + EVALUATION_START + ">"
					+ DateFormat.getDateTimeInstance().format(e.getStart())
					+ "</" + EVALUATION_START + ">");
			sb.append("<" + EVALUATION_DEADLINE + ">"
					+ DateFormat.getDateTimeInstance().format(e.getDeadline())
					+ "</" + EVALUATION_DEADLINE + ">");
			sb.append("<" + EVALUATION_TIMEZONE + "><![CDATA["
					+ e.getTimeZone() + "]]></" + EVALUATION_TIMEZONE + ">");
			sb.append("<" + EVALUATION_GRACEPERIOD + ">" + e.getGracePeriod()
					+ "</" + EVALUATION_GRACEPERIOD + ">");
			sb.append("<" + EVALUATION_INSTRUCTIONS + "><![CDATA["
					+ e.getInstructions() + "]]></" + EVALUATION_INSTRUCTIONS
					+ ">");
			sb.append("<" + EVALUATION_COMMENTSENABLED + "><![CDATA["
					+ e.isCommentsEnabled() + "]]></"
					+ EVALUATION_COMMENTSENABLED + ">");
			sb.append("<" + EVALUATION_PUBLISHED + ">" + e.isPublished() + "</"
					+ EVALUATION_PUBLISHED + ">");
			sb.append("</evaluation>");
		}

		return sb;
	}

	private StringBuffer parseStudentListToXML(List<Student> studentList) {
		StringBuffer sb = new StringBuffer();

		for (Student s : studentList) {
			sb.append("<student>");
			sb.append("<" + STUDENT_NAME + "><![CDATA[" + s.getName() + "]]></"
					+ STUDENT_NAME + ">");
			sb.append("<" + STUDENT_EMAIL + "><![CDATA[" + s.getEmail()
					+ "]]></" + STUDENT_EMAIL + ">");
			sb.append("<" + STUDENT_ID + "><![CDATA[" + s.getID() + "]]></"
					+ STUDENT_ID + ">");
			sb.append("<" + STUDENT_COMMENTS + "><![CDATA[" + s.getComments()
					+ "]]></" + STUDENT_COMMENTS + ">");
			sb.append("<"
					+ STUDENT_REGKEY
					+ "><![CDATA["
					+ KeyFactory.createKeyString(Student.class.getSimpleName(),
							s.getRegistrationKey()) + "]]></" + STUDENT_REGKEY
					+ ">");
			sb.append("<" + STUDENT_COURSEID + "><![CDATA[" + s.getCourseID()
					+ "]]></" + STUDENT_COURSEID + ">");
			sb.append("<" + STUDENT_TEAMNAME + "><![CDATA[" + s.getTeamName()
					+ "]]></" + STUDENT_TEAMNAME + ">");
			sb.append("</student>");
		}

		return sb;
	}

	private StringBuffer parseSubmissionDetailsForCoordinatorListToXML(
			List<SubmissionDetailsForCoordinator> submissionDetailsList) {
		StringBuffer sb = new StringBuffer();

		for (SubmissionDetailsForCoordinator s : submissionDetailsList) {
			sb.append("<submission>");
			sb.append("<" + STUDENT_TEAMNAME + "><![CDATA[" + s.getTeamName()
					+ "]]></" + STUDENT_TEAMNAME + ">");
			sb.append("<" + STUDENT_FROMSTUDENTNAME + "><![CDATA["
					+ s.getFromStudentName() + "]]></"
					+ STUDENT_FROMSTUDENTNAME + ">");
			sb.append("<" + STUDENT_TOSTUDENTNAME + "><![CDATA["
					+ s.getToStudentName() + "]]></" + STUDENT_TOSTUDENTNAME
					+ ">");
			sb.append("<" + STUDENT_FROMSTUDENT + "><![CDATA["
					+ s.getFromStudent() + "]]></" + STUDENT_FROMSTUDENT + ">");
			sb.append("<" + STUDENT_TOSTUDENT + "><![CDATA[" + s.getToStudent()
					+ "]]></" + STUDENT_TOSTUDENT + ">");
			sb.append("<" + STUDENT_FROMSTUDENTCOMMENTS + "><![CDATA["
					+ s.getFromStudentComments() + "]]></"
					+ STUDENT_FROMSTUDENTCOMMENTS + ">");
			sb.append("<" + STUDENT_TOSTUDENTCOMMENTS + "><![CDATA["
					+ s.getToStudentComments() + "]]></"
					+ STUDENT_TOSTUDENTCOMMENTS + ">");
			sb.append("<" + COURSE_ID + "><![CDATA[" + s.getCourseID()
					+ "]]></" + COURSE_ID + ">");
			sb.append("<" + EVALUATION_NAME + "><![CDATA["
					+ s.getEvaluationName() + "]]></" + EVALUATION_NAME + ">");
			sb.append("<" + STUDENT_POINTS + ">" + s.getPoints() + "</"
					+ STUDENT_POINTS + ">");
			sb.append("<" + STUDENT_POINTSBUMPRATIO + ">"
					+ s.getPointsBumpRatio() + "</" + STUDENT_POINTSBUMPRATIO
					+ ">");
			sb.append("<" + STUDENT_JUSTIFICATION + "><![CDATA["
					+ s.getJustification().getValue() + "]]></"
					+ STUDENT_JUSTIFICATION + ">");
			sb.append("<" + STUDENT_COMMENTSTOSTUDENT + "><![CDATA["
					+ s.getCommentsToStudent().getValue() + "]]></"
					+ STUDENT_COMMENTSTOSTUDENT + ">");
			sb.append("</submission>");
		}

		return sb;
	}

	private StringBuffer parseSubmissionDetailsForStudentListToXML(
			List<SubmissionDetailsForStudent> submissionDetailsList) {
		StringBuffer sb = new StringBuffer();

		for (SubmissionDetailsForStudent s : submissionDetailsList) {
			sb.append("<submission>");
			sb.append("<" + STUDENT_TEAMNAME + "><![CDATA[" + s.getTeamName()
					+ "]]></" + STUDENT_TEAMNAME + ">");
			sb.append("<" + STUDENT_FROMSTUDENTNAME + "><![CDATA["
					+ s.getFromStudentName() + "]]></"
					+ STUDENT_FROMSTUDENTNAME + ">");
			sb.append("<" + STUDENT_TOSTUDENTNAME + "><![CDATA["
					+ s.getToStudentName() + "]]></" + STUDENT_TOSTUDENTNAME
					+ ">");
			sb.append("<" + STUDENT_FROMSTUDENT + "><![CDATA["
					+ s.getFromStudent() + "]]></" + STUDENT_FROMSTUDENT + ">");
			sb.append("<" + STUDENT_TOSTUDENT + "><![CDATA[" + s.getToStudent()
					+ "]]></" + STUDENT_TOSTUDENT + ">");
			sb.append("<" + COURSE_ID + "><![CDATA[" + s.getCourseID()
					+ "]]></" + COURSE_ID + ">");
			sb.append("<" + EVALUATION_NAME + "><![CDATA["
					+ s.getEvaluationName() + "]]></" + EVALUATION_NAME + ">");
			sb.append("<" + STUDENT_POINTS + ">" + s.getPoints() + "</"
					+ STUDENT_POINTS + ">");
			sb.append("<" + STUDENT_JUSTIFICATION + "><![CDATA["
					+ s.getJustification().getValue() + "]]></"
					+ STUDENT_JUSTIFICATION + ">");
			sb.append("<" + STUDENT_COMMENTSTOSTUDENT + "><![CDATA["
					+ s.getCommentsToStudent().getValue() + "]]></"
					+ STUDENT_COMMENTSTOSTUDENT + ">");
			sb.append("</submission>");
		}

		return sb;
	}

	private StringBuffer parseSubmissionResultsForStudentListToXML(
			List<SubmissionResultsForStudent> submissionDetailsList) {
		StringBuffer sb = new StringBuffer();

		for (SubmissionResultsForStudent s : submissionDetailsList) {
			sb.append("<submission>");
			sb.append("<" + STUDENT_TEAMNAME + "><![CDATA[" + s.getTeamName()
					+ "]]></" + STUDENT_TEAMNAME + ">");
			sb.append("<" + STUDENT_FROMSTUDENTNAME + "><![CDATA["
					+ s.getFromStudentName() + "]]></"
					+ STUDENT_FROMSTUDENTNAME + ">");
			sb.append("<" + STUDENT_TOSTUDENTNAME + "><![CDATA["
					+ s.getToStudentName() + "]]></" + STUDENT_TOSTUDENTNAME
					+ ">");
			sb.append("<" + STUDENT_FROMSTUDENT + "><![CDATA["
					+ s.getFromStudent() + "]]></" + STUDENT_FROMSTUDENT + ">");
			sb.append("<" + STUDENT_TOSTUDENT + "><![CDATA[" + s.getToStudent()
					+ "]]></" + STUDENT_TOSTUDENT + ">");
			sb.append("<" + STUDENT_FROMSTUDENTCOMMENTS + "><![CDATA["
					+ s.getFromStudentComments() + "]]></"
					+ STUDENT_FROMSTUDENTCOMMENTS + ">");
			sb.append("<" + STUDENT_TOSTUDENTCOMMENTS + "><![CDATA["
					+ s.getToStudentComments() + "]]></"
					+ STUDENT_TOSTUDENTCOMMENTS + ">");
			sb.append("<" + COURSE_ID + "><![CDATA[" + s.getCourseID()
					+ "]]></" + COURSE_ID + ">");
			sb.append("<" + EVALUATION_NAME + "><![CDATA["
					+ s.getEvaluationName() + "]]></" + EVALUATION_NAME + ">");
			sb.append("<" + STUDENT_POINTS + ">" + s.getPoints() + "</"
					+ STUDENT_POINTS + ">");
			sb.append("<" + STUDENT_POINTSBUMPRATIO + ">"
					+ s.getPointsBumpRatio() + "</" + STUDENT_POINTSBUMPRATIO
					+ ">");
			sb.append("<" + STUDENT_JUSTIFICATION + "><![CDATA["
					+ s.getJustification().getValue() + "]]></"
					+ STUDENT_JUSTIFICATION + ">");
			sb.append("<" + STUDENT_COMMENTSTOSTUDENT + "><![CDATA["
					+ s.getCommentsToStudent().getValue() + "]]></"
					+ STUDENT_COMMENTSTOSTUDENT + ">");
			sb.append("</submission>");
		}

		return sb;
	}

	private void studentArchiveCourse() {
		Accounts accounts = Accounts.inst();
		String googleID = accounts.getUser().getNickname();

		String courseID = req.getParameter(COURSE_ID);

		Courses courses = Courses.inst();
		courses.archiveStudentCourse(courseID, googleID);
	}

	private void studentDeleteCourse() {
		Accounts accounts = Accounts.inst();
		String googleID = accounts.getUser().getNickname();

		String courseID = req.getParameter(COURSE_ID);

		Courses courses = Courses.inst();
		courses.deleteStudentCourse(courseID, googleID);

	}

	private void studentGetCourse() throws IOException {
		Accounts accounts = Accounts.inst();
		String googleID = accounts.getUser().getNickname();

		Courses courses = Courses.inst();
		String courseID = req.getParameter(COURSE_ID);

		Course course = courses.getCourse(courseID);
		String courseName = course.getName();
		String coordinatorName = accounts.getCoordinatorName(course
				.getCoordinatorID());

		Student student = courses.getStudentWithID(courseID, googleID);

		String studentEmail = student.getEmail();
		String studentName = student.getName();
		String teamName = courses.getTeamName(courseID, studentEmail);

		ArrayList<String> teammateList = new ArrayList<String>();
		List<Student> studentList = courses.getStudentList(courseID);

		for (Student s : studentList) {
			if (!s.getID().equals(googleID) && s.getTeamName().equals(teamName)) {
				teammateList.add(s.getName());
			}
		}

		CourseDetailsForStudent courseDetails = new CourseDetailsForStudent(
				courseID, courseName, coordinatorName, teamName, studentName,
				studentEmail, teammateList);

		resp.getWriter().write(
				parseCourseDetailsForStudentToXML(courseDetails).toString());
	}

	private void studentGetCourseList() throws IOException {
		Accounts accounts = Accounts.inst();
		String googleID = accounts.getUser().getNickname();

		Courses courses = Courses.inst();
		List<Student> studentList = courses.getStudentCourseList(googleID);

		ArrayList<CourseSummaryForStudent> courseSummaryList = new ArrayList<CourseSummaryForStudent>();

		String courseID = "";
		String courseName = "";
		String teamName = "";

		boolean archived = false;

		Course course = null;

		for (Student s : studentList) {
			courseID = s.getCourseID();
			course = courses.getCourse(courseID);
			courseName = course.getName();
			archived = s.isCourseArchived();
			teamName = courses.getTeamName(courseID, s.getEmail());

			courseSummaryList.add(new CourseSummaryForStudent(courseID,
					courseName, teamName, archived));
		}

		resp.getWriter().write(
				"<courses>"
						+ parseCourseSummaryForStudentListToXML(
								courseSummaryList).toString() + "</courses>");
	}

	private void studentGetPastEvaluationList() throws IOException {
		Accounts accounts = Accounts.inst();
		String googleID = accounts.getUser().getNickname();

		Courses courses = Courses.inst();
		List<Student> studentList = courses.getStudentCourseList(googleID);

		List<Course> courseList = new ArrayList<Course>();

		for (Student s : studentList) {
			courseList.add(courses.getCourse(s.getCourseID()));
		}

		Evaluations evaluations = Evaluations.inst();
		List<Evaluation> evaluationList = evaluations
				.getEvaluationList(courseList);

		// Filter evaluationList - make sure archived and unsubmitted(unless
		// evaluation is published and open evaluations are not
		// returned to student
		List<Evaluation> filteredEvaluationList = new ArrayList<Evaluation>();

		String email = "";
		Student student = null;

		Calendar now = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		Calendar deadline = Calendar.getInstance();

		for (Evaluation e : evaluationList) {
			// Fix the time zone accordingly
			now.add(Calendar.MILLISECOND,
					(int) (60 * 60 * 1000 * e.getTimeZone()));

			student = courses.getStudentWithID(e.getCourseID(), googleID);

			if (student != null) {
				email = student.getEmail();

				start.setTime(e.getStart());
				deadline.setTime(e.getDeadline());

				if (e.isPublished()) {
					filteredEvaluationList.add(e);
				}

				else if (now.after(deadline) && !student.isCourseArchived()) {
					filteredEvaluationList.add(e);
				}

				else {
					if (evaluations.isEvaluationSubmitted(e, email)
							&& (now.after(start))
							&& !student.isCourseArchived()) {
						filteredEvaluationList.add(e);
					}
				}
			}

			else {
				continue;
			}

			// Revert the time zone change
			now.add(Calendar.MILLISECOND,
					(int) (-60 * 60 * 1000 * e.getTimeZone()));
		}

		resp.getWriter().write(
				"<evaluations>"
						+ parseEvaluationListToXML(filteredEvaluationList)
								.toString() + "</evaluations>");
	}

	private void studentGetPendingEvaluationList() throws IOException {
		Accounts accounts = Accounts.inst();
		String googleID = accounts.getUser().getNickname();

		Courses courses = Courses.inst();
		List<Student> studentList = courses.getStudentCourseList(googleID);

		List<Course> courseList = new ArrayList<Course>();

		for (Student s : studentList) {
			courseList.add(courses.getCourse(s.getCourseID()));
		}

		Evaluations evaluations = Evaluations.inst();
		List<Evaluation> evaluationList = evaluations
				.getEvaluationList(courseList);

		// Filter evaluationList - make sure archived, submitted and closed
		// evaluations are not returned to student
		List<Evaluation> filteredEvaluationList = new ArrayList<Evaluation>();

		String email = "";
		Student student = null;

		Calendar now = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		Calendar deadline = Calendar.getInstance();

		for (Evaluation e : evaluationList) {
			// Fix the time zone accordingly
			now.add(Calendar.MILLISECOND,
					(int) (60 * 60 * 1000 * e.getTimeZone()));

			student = courses.getStudentWithID(e.getCourseID(), googleID);

			if (student != null) {
				email = student.getEmail();

				start.setTime(e.getStart());
				deadline.setTime(e.getDeadline());

				if (!evaluations.isEvaluationSubmitted(e, email)
						&& (now.after(start) && now.before(deadline))
						&& !student.isCourseArchived()) {
					filteredEvaluationList.add(e);
				}
			}

			else {
				continue;
			}

			// Revert time zone change
			now.add(Calendar.MILLISECOND,
					(int) (-60 * 60 * 1000 * e.getTimeZone()));
		}

		resp.getWriter().write(
				"<evaluations>"
						+ parseEvaluationListToXML(filteredEvaluationList)
								.toString() + "</evaluations>");

	}

	private void studentGetSubmissionList() throws IOException {
		Accounts accounts = Accounts.inst();
		String googleID = accounts.getUser().getNickname();

		String courseID = req.getParameter(COURSE_ID);
		String evaluationName = req.getParameter(EVALUATION_NAME);

		// fromStudent is the Student's email
		Courses courses = Courses.inst();
		Student student = courses.getStudentWithID(courseID, googleID);

		String fromStudent = student.getEmail();
		String fromStudentName = student.getName();

		// wangsha
		// should be historical teamname, not the latest one
		String teamName = student.getTeamName();

		Evaluations evaluations = Evaluations.inst();
		List<Submission> submissionList = evaluations
				.getSubmissionFromStudentList(courseID, evaluationName,
						fromStudent);

		List<SubmissionDetailsForStudent> submissionDetailsList = new ArrayList<SubmissionDetailsForStudent>();

		for (Submission s : submissionList) {
			student = courses.getStudentWithEmail(courseID, s.getToStudent());
			// huy - Fix when student is already deleted.
			if (student == null) {
				student = new Student();
				student.setEmail(s.getToStudent());
				student.setName("[deleted]" + student.getEmail());
			}

			// Always return the student's own submission first
			if (s.getToStudent().equals(fromStudent)) {
				submissionDetailsList.add(
						0,
						new SubmissionDetailsForStudent(courseID,
								evaluationName, fromStudentName, student
										.getName(), fromStudent, student
										.getEmail(), s.getTeamName(), s
										.getPoints(), s.getJustification(), s
										.getCommentsToStudent()));
			}

			else {
				submissionDetailsList.add(new SubmissionDetailsForStudent(
						courseID, evaluationName, fromStudentName, student
								.getName(), fromStudent, student.getEmail(), s
								.getTeamName(), s.getPoints(), s
								.getJustification(), s.getCommentsToStudent()));
			}
		}

		resp.getWriter().write(
				"<submissions>"
						+ parseSubmissionDetailsForStudentListToXML(
								submissionDetailsList).toString()
						+ "</submissions>");
	}

	private void studentGetSubmissionResultsList() throws IOException {
		Accounts accounts = Accounts.inst();
		String googleID = accounts.getUser().getNickname();

		String courseID = req.getParameter(COURSE_ID);
		String evaluationName = req.getParameter(EVALUATION_NAME);

		Courses courses = Courses.inst();
		Student student = courses.getStudentWithID(courseID, googleID);

		Evaluations evaluations = Evaluations.inst();
		List<Submission> submissionList = evaluations.getSubmissionList(
				courseID, evaluationName);

		String toStudent = student.getEmail();

		// Filter the submission list to only from the target student's team
		String teamName = student.getTeamName();

		List<Submission> filteredSubmissionList = new ArrayList<Submission>();

		for (Submission s : submissionList) {
			if (s.getTeamName().equals(teamName)) {
				filteredSubmissionList.add(s);
			}
		}

		System.out.println("filtered number: " + filteredSubmissionList.size() );
		List<SubmissionResultsForStudent> submissionResultsList = new ArrayList<SubmissionResultsForStudent>();

		String fromStudentName = "";
		String toStudentName = "";

		String fromStudentComments = null;
		String toStudentComments = null;

		float pointsBumpRatio = 0;

		for (Submission s : filteredSubmissionList) {
			student = courses.getStudentWithEmail(courseID, s.getFromStudent());

			if (student != null) {
				fromStudentName = student.getName();
				fromStudentComments = student.getComments();
			} else {
				fromStudentName = "[deleted]" + s.getFromStudent();
				fromStudentComments = ("");
			}

			student = courses.getStudentWithEmail(courseID, s.getToStudent());
			if (student != null) {
				toStudentName = student.getName();
				toStudentComments = student.getComments();
			} else {
				toStudentName = "[deleted]" + s.getToStudent();
				toStudentComments = "";
			}
			
			List<Submission> fromList = new ArrayList<Submission>();
			for (Submission fs : submissionList) {
				if (fs.getFromStudent().equals(s.getFromStudent())) {
					fromList.add(fs);
				}
			}

			pointsBumpRatio = evaluations.calculatePointsBumpRatio(courseID,
					evaluationName, s.getFromStudent(), fromList);

			if (s.getFromStudent().equals(toStudent)
					&& s.getToStudent().equals(toStudent)) {
				submissionResultsList.add(
						0,
						new SubmissionResultsForStudent(courseID,
								evaluationName, fromStudentName, toStudentName,
								s.getFromStudent(), s.getToStudent(),
								fromStudentComments, toStudentComments, s
										.getTeamName(), s.getPoints(),
								pointsBumpRatio, s.getJustification(), s
										.getCommentsToStudent()));
			}

			else {
				submissionResultsList.add(new SubmissionResultsForStudent(
						courseID, evaluationName, fromStudentName,
						toStudentName, s.getFromStudent(), s.getToStudent(),
						fromStudentComments, toStudentComments,
						s.getTeamName(), s.getPoints(), pointsBumpRatio, s
								.getJustification(), s.getCommentsToStudent()));
			}
		}

		resp.getWriter().write(
				"<submissions>"
						+ parseSubmissionResultsForStudentListToXML(
								submissionResultsList).toString()
						+ "</submissions>");

	}

	private void studentJoinCourse() throws IOException {
		Accounts accounts = Accounts.inst();
		String googleID = accounts.getUser().getNickname();

		String registrationKey = req.getParameter(STUDENT_REGKEY);

		Courses courses = Courses.inst();

		try {
			courses.joinCourse(registrationKey, googleID);
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_STUDENT_COURSEJOINED
							+ MSG_STATUS_CLOSING);
		}

		catch (RegistrationKeyInvalidException e) {
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_STUDENT_REGISTRATIONKEYINVALID
							+ MSG_STATUS_CLOSING);
		}

		catch (GoogleIDExistsInCourseException e) {
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_STUDENT_GOOGLEIDEXISTSINCOURSE
							+ MSG_STATUS_CLOSING);
		}

		catch (RegistrationKeyTakenException e) {
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_STUDENT_REGISTRATIONKEYTAKEN
							+ MSG_STATUS_CLOSING);
		}

	}

	private void studentLogin() throws IOException {
		Accounts accounts = Accounts.inst();
		resp.getWriter().write(
				"<url><![CDATA[" + accounts.getLoginPage("/student.jsp")
						+ "]]></url>");
	}

	private void studentLogout() throws IOException {
		Accounts accounts = Accounts.inst();
		resp.getWriter().write(
				"<url><![CDATA[" + accounts.getLogoutPage("") + "]]></url>");
	}

	private void studentSubmitEvaluation() throws IOException {
		List<Submission> submissionList = new ArrayList<Submission>();

		int numberOfSubmissions = Integer.parseInt(req
				.getParameter(STUDENT_NUMBEROFSUBMISSIONS));

		String fromStudent = "";
		String toStudent = "";
		int points = 0;
		Text justification = new Text("");
		Text commentsToStudent = new Text("");

		String courseID = req.getParameter(COURSE_ID);
		String evaluationName = req.getParameter(EVALUATION_NAME);
		String teamName = req.getParameter(STUDENT_TEAMNAME);

		// Make sure the deadline is not up yet, including grace period
		Evaluations evaluations = Evaluations.inst();

		Calendar now = Calendar.getInstance();
		Calendar deadline = Calendar.getInstance();

		Evaluation evaluation = evaluations.getEvaluation(courseID,
				evaluationName);
		deadline.setTime(evaluation.getDeadline());
		deadline.set(Calendar.MINUTE, deadline.get(Calendar.MINUTE)
				+ evaluation.getGracePeriod());

		now.add(Calendar.MILLISECOND,
				(int) (60 * 60 * 1000 * evaluation.getTimeZone()));

		if (now.after(deadline)) {
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_EVALUATION_DEADLINEPASSED
							+ MSG_STATUS_CLOSING);
		}

		else {

			for (int x = 0; x < numberOfSubmissions; x++) {
				fromStudent = req.getParameter(STUDENT_FROMSTUDENT + x);
				toStudent = req.getParameter(STUDENT_TOSTUDENT + x);
				points = Integer.parseInt(req.getParameter(STUDENT_POINTS + x));
				justification = new Text(req.getParameter(STUDENT_JUSTIFICATION
						+ x));
				commentsToStudent = new Text(
						req.getParameter(STUDENT_COMMENTSTOSTUDENT + x));

				submissionList.add(new Submission(fromStudent, toStudent,
						courseID, evaluationName, teamName, points,
						justification, commentsToStudent));
			}

			evaluations.editSubmissions(submissionList);

		}

		resp.getWriter().write(MSG_STATUS_OPENING + "nil" + MSG_STATUS_CLOSING);
	}

	private void studentUnarchiveCourse() {
		Accounts accounts = Accounts.inst();
		String googleID = accounts.getUser().getNickname();

		String courseID = req.getParameter(COURSE_ID);

		Courses courses = Courses.inst();
		List<Student> studentList = courses.getStudentList(courseID);

		for (Student s : studentList) {
			if (s.getID().equals(googleID)) {
				courses.unarchiveStudentCourse(courseID, s.getEmail());
				break;
			}
		}

	}

	private void testOpenEvaluation() throws IOException {
		String courseID = req.getParameter(COURSE_ID);
		String name = req.getParameter(EVALUATION_NAME);
		System.out.println(courseID + "|" + name + "|"
				+ System.currentTimeMillis());
		Evaluations evaluations = Evaluations.inst();

		boolean edited = evaluations.openEvaluation(courseID, name);

		resp.setContentType("text/plain");
		if (edited) {
			resp.getWriter().write("success");
		} else {
			resp.getWriter().write("fail");
		}

	}

	private void testCloseEvaluation() throws IOException {
		String courseID = req.getParameter(COURSE_ID);
		String name = req.getParameter(EVALUATION_NAME);

		Evaluations evaluations = Evaluations.inst();

		boolean edited = evaluations.closeEvaluation(courseID, name);

		resp.setContentType("text/plain");
		if (edited) {
			resp.getWriter().write("success");
		} else {
			resp.getWriter().write("fail");
		}
	}

	private void testAwaitEvaluation() throws IOException {
		String courseID = req.getParameter(COURSE_ID);
		String name = req.getParameter(EVALUATION_NAME);

		Evaluations evaluations = Evaluations.inst();

		boolean edited = evaluations.awaitEvaluation(courseID, name);

		resp.setContentType("text/plain");
		if (edited) {
			resp.getWriter().write("success");
		} else {
			resp.getWriter().write("fail");
		}

	}
}
