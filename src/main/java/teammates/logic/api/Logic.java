package teammates.logic.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import teammates.common.Assumption;
import teammates.common.BuildProperties;
import teammates.common.Common;
import teammates.common.datatransfer.CoordData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.EvalResultData;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.SubmissionData;
import teammates.common.datatransfer.TeamData;
import teammates.common.datatransfer.UserData;
import teammates.common.datatransfer.EvaluationData.EvalStatus;
import teammates.common.datatransfer.StudentData.UpdateStatus;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.exception.NotImplementedException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.logic.Emails;
import teammates.logic.TeamEvalResult;
import teammates.storage.api.AccountsStorage;
import teammates.storage.api.CoursesStorage;
import teammates.storage.api.EvaluationsStorage;

import com.google.appengine.api.datastore.Text; //TODO: remove this dependency
import com.google.appengine.api.users.User;

/**
 * This class represents the API to the business logic of the system. Please
 * refer to DevMan for general policies followed by Logic. As those policies
 * cover most of the behavior of the API, we use very short comments to describe
 * operations here.
 */
public class Logic {

	private static Logger log = Common.getLogger();

	@SuppressWarnings("unused")
	private void ____USER_level_methods__________________________________() {
	}

	/**
	 * Produces the URL the user should use to login to the system
	 * 
	 * @param redirectUrl
	 *            This is the URL the user will be directed to after login.
	 */
	public static String getLoginUrl(String redirectUrl) {
		AccountsStorage accounts = AccountsStorage.inst();
		return accounts.getLoginPage(redirectUrl);
	}

	/**
	 * Produces the URL used to logout the user
	 * 
	 * @param redirectUrl
	 *            This is the URL the user will be directed to after logout.
	 */
	public static String getLogoutUrl(String redirectUrl) {
		AccountsStorage accounts = AccountsStorage.inst();
		return accounts.getLogoutPage(redirectUrl);
	}

	/**
	 * Verifies if the user is logged into his/her Google account
	 */
	public static boolean isUserLoggedIn() {
		AccountsStorage accounts = AccountsStorage.inst();
		return (accounts.getUser() != null);
	}

	/**
	 * @return Returns null if the user is not logged in.
	 */
	public UserData getLoggedInUser() {
		AccountsStorage accounts = AccountsStorage.inst();
		User user = accounts.getUser();
		if (user == null) {
			return null;
		}

		UserData userData = new UserData(user.getNickname());

		// TODO: make more efficient?
		if (accounts.isAdministrator()) {
			userData.isAdmin = true;
		}
		if (accounts.isCoord()) {
			userData.isCoord = true;
		}

		if (accounts.isStudent(user.getNickname())) {
			userData.isStudent = true;
		}
		return userData;
	}

	@SuppressWarnings("unused")
	private void ____ACCESS_control_methods________________________________() {
	}

	// @formatter:off

	protected boolean isInternalCall() {
		String callerClassName = Thread.currentThread().getStackTrace()[4]
				.getClassName();
		String thisClassName = this.getClass().getCanonicalName();
		return callerClassName.equals(thisClassName);
	}

	private void verifyCoordUsingOwnIdOrAbove(String coordId) {
		if (isInternalCall())
			return;
		if (isAdminLoggedIn())
			return;
		if (isOwnId(coordId))
			return;
		throw new UnauthorizedAccessException();
	}

	private void verifyOwnerOfId(String googleId) {
		if (isInternalCall())
			return;
		if (isAdminLoggedIn())
			return;
		if (isOwnId(googleId))
			return;
		throw new UnauthorizedAccessException();
	}

	private void verifyRegisteredUserOrAbove() {
		if (isInternalCall())
			return;
		if (isAdminLoggedIn())
			return;
		if (isCoordLoggedIn())
			return;
		if (isStudentLoggedIn())
			return;
		throw new UnauthorizedAccessException();
	}

	private void verifyCourseOwnerOrAbove(String courseId) {
		if (isInternalCall())
			return;
		if (isAdminLoggedIn())
			return;
		if (isCourseOwner(courseId))
			return;
		throw new UnauthorizedAccessException();
	}

	private void verifyCourseOwnerOrStudentInCourse(String courseId) {
		if (isInternalCall())
			return;
		if (isAdminLoggedIn())
			return;
		if (isCourseOwner(courseId))
			return;
		if (isInCourse(courseId))
			return;
		throw new UnauthorizedAccessException();
	}

	private void verifyAdminLoggedIn() {
		if (isInternalCall())
			return;
		if (isAdminLoggedIn())
			return;
		throw new UnauthorizedAccessException();
	}

	private void verifyLoggedInUserAndAbove() {
		if (isInternalCall())
			return;
		if (isUserLoggedIn())
			return;
		throw new UnauthorizedAccessException();
	}

	private void verifySameStudentOrAdmin(String googleId) {
		if (isInternalCall())
			return;
		if (isAdminLoggedIn())
			return;
		if (isOwnId(googleId))
			return;
		throw new UnauthorizedAccessException();
	}

	private void verifySameStudentOrCourseOwnerOrAdmin(String courseId,
			String googleId) {
		if (isInternalCall())
			return;
		if (isAdminLoggedIn())
			return;
		if (isOwnId(googleId))
			return;
		if (isCourseOwner(courseId))
			return;
		throw new UnauthorizedAccessException();
	}

	private void verifyReviewerOrCourseOwnerOrAdmin(String courseId,
			String reviewerEmail) {
		if (isInternalCall())
			return;
		if (isAdminLoggedIn())
			return;
		if (isCourseOwner(courseId))
			return;
		if (isOwnEmail(courseId, reviewerEmail))
			return;
		throw new UnauthorizedAccessException();
	}

	private void verifySubmissionEditableForUser(SubmissionData submission) {
		if (isInternalCall())
			return;
		if (isAdminLoggedIn())
			return;
		if (isCourseOwner(submission.course))
			return;
		if (isOwnEmail(submission.course, submission.reviewer)
				&& getEvaluationStatus(submission.course, submission.evaluation) == EvalStatus.OPEN)
			return;
		throw new UnauthorizedAccessException();
	}

	private void verfyCourseOwner_OR_EmailOwnerAndPublished(String courseId,
			String evaluationName, String studentEmail) {
		if (isInternalCall())
			return;
		if (isAdminLoggedIn())
			return;
		if (isCourseOwner(courseId))
			return;
		if (isOwnEmail(courseId, studentEmail)
				&& isEvaluationPublished(courseId, evaluationName))
			return;
		throw new UnauthorizedAccessException();
	}

	// @formatter:on

	private boolean isOwnEmail(String courseId, String studentEmail) {
		UserData user = getLoggedInUser();
		if (user == null) {
			return false;
		}
		CourseData course = getCourse(courseId);
		if (course == null) {
			return false;
		}
		StudentData student = getStudent(courseId, studentEmail);
		return student == null ? false : user.id.equals(student.id);
	}

	private boolean isOwnId(String userId) {
		UserData loggedInUser = getLoggedInUser();
		return loggedInUser == null ? false : loggedInUser.id
				.equalsIgnoreCase(userId);
	}

	private boolean isCourseOwner(String courseId) {
		CourseData course = getCourse(courseId);
		UserData user = getLoggedInUser();
		return user != null && course != null
				&& course.coord.equalsIgnoreCase(user.id);
	}

	private boolean isInCourse(String courseId) {
		UserData user = getLoggedInUser();
		if (user == null)
			return false;

		CourseData course = getCourse(courseId);
		if (course == null)
			return false;

		return (null != getStudentInCourseForGoogleId(courseId, user.id));
	}

	private boolean isEvaluationPublished(String courseId, String evaluationName) {
		EvaluationData evaluation = getEvaluation(courseId, evaluationName);
		if (evaluation == null) {
			return false;
		} else {
			return evaluation.getStatus() == EvalStatus.PUBLISHED;
		}
	}

	/**
	 * Verifies if the logged in user has Admin privileges
	 */
	private boolean isAdminLoggedIn() {
		UserData loggedInUser = getLoggedInUser();
		return loggedInUser == null ? false : loggedInUser.isAdmin;
	}

	/**
	 * Verifies if the logged in user has Coord privileges
	 */
	private boolean isCoordLoggedIn() {
		UserData loggedInUser = getLoggedInUser();
		return loggedInUser == null ? false : loggedInUser.isCoord;
	}

	/**
	 * Verifies if the logged in user has Student privileges
	 */
	private boolean isStudentLoggedIn() {
		UserData loggedInUser = getLoggedInUser();
		return loggedInUser == null ? false : loggedInUser.isStudent;
	}

	@SuppressWarnings("unused")
	private void ____COORD_level_methods____________________________________() {
	}

	/**
	 * Access: admin only
	 */
	public void createCoord(String coordId, String coordName, String coordEmail)
			throws EntityAlreadyExistsException, InvalidParametersException {

		// verification process here pending removal once Assumption class is implemented
		Common.verifyNotNull(coordId, "coordinator ID");
		Common.verifyNotNull(coordName, "coordinator Name");
		Common.verifyNotNull(coordEmail, "coordinator Email");

		verifyAdminLoggedIn();

		// same for validation
		Common.validateEmail(coordEmail);
		Common.validateCoordName(coordName);
		Common.validateGoogleId(coordId);

		CoordData coordToAdd = new CoordData(coordId, coordName, coordEmail);
		AccountsStorage.inst().getDb().createCoord(coordToAdd);
	}

	/**
	 * Access: any logged in user
	 */
	public CoordData getCoord(String coordID) {

		Common.verifyNotNull(coordID, "coordinator ID");

		verifyLoggedInUserAndAbove();

		CoordData coord = AccountsStorage.inst().getDb().getCoord(coordID);

		return coord;
	}

	/**
	 * Not implemented
	 */
	public void editCoord(CoordData coord) throws NotImplementedException {
		throw new NotImplementedException("Not implemented because we do "
				+ "not allow editing coordinators");
	}

	/**
	 * Access: Admin only
	 */
	public void deleteCoord(String coordId) {

		Common.verifyNotNull(coordId, "coordinator ID");

		verifyAdminLoggedIn();

		List<CourseData> coordCourseList = CoursesStorage.inst().getDb()
				.getCourseListForCoordinator(coordId);

		for (CourseData courseData : coordCourseList) {
			deleteCourse(courseData.id);
		}
		AccountsStorage.inst().getDb().deleteCoord(coordId);
	}

	/**
	 * Access level: Admin, Coord (for self)
	 * 
	 * @return Returns a less-detailed version of Coord's course data
	 */
	public HashMap<String, CourseData> getCourseListForCoord(String coordId)
			throws EntityDoesNotExistException {

		Common.verifyNotNull(coordId, "coordinator Id");

		verifyCoordUsingOwnIdOrAbove(coordId);

		HashMap<String, CourseData> courseSummaryListForCoord = CoursesStorage
				.inst().getCourseSummaryListForCoord(coordId);
		if (courseSummaryListForCoord.size() == 0) {
			if (getCoord(coordId) == null) {
				throw new EntityDoesNotExistException(
						"Coordinator does not exist :" + coordId);
			}
		}
		return courseSummaryListForCoord;
	}

	/**
	 * Access level: Admin, Coord (for self)
	 * 
	 * @return Returns a more-detailed version of Coord's course data <br>
	 */
	public HashMap<String, CourseData> getCourseDetailsListForCoord(
			String coordId) throws EntityDoesNotExistException {

		Common.verifyNotNull(coordId, "coordinator ID");

		verifyCoordUsingOwnIdOrAbove(coordId);

		// TODO: using this method here may not be efficient as it retrieves
		// info not required
		HashMap<String, CourseData> courseList = getCourseListForCoord(coordId);
		ArrayList<EvaluationData> evaluationList = getEvaluationsListForCoord(coordId);
		for (EvaluationData ed : evaluationList) {
			CourseData courseSummary = courseList.get(ed.course);
			courseSummary.evaluations.add(ed);
		}
		return courseList;
	}

	/**
	 * Access level: Admin, Coord (for self)
	 * 
	 * @return Returns a less-detailed version of Coord's evaluations <br>
	 */
	public ArrayList<EvaluationData> getEvaluationsListForCoord(String coordId)
			throws EntityDoesNotExistException {

		Common.verifyNotNull(coordId, "coordinator ID");

		verifyCoordUsingOwnIdOrAbove(coordId);

		List<CourseData> courseList = CoursesStorage.inst().getDb()
				.getCourseListForCoordinator(coordId);

		if ((courseList.size() == 0) && (getCoord(coordId) == null)) {
			throw new EntityDoesNotExistException(
					"Coordinator does not exist :" + coordId);
		}

		ArrayList<EvaluationData> evaluationSummaryList = new ArrayList<EvaluationData>();

		for (CourseData cd : courseList) {
			List<EvaluationData> evaluationsSummaryForCourse = EvaluationsStorage
					.inst().getEvaluationsDb().getEvaluationsForCourse(cd.id);
			List<StudentData> students = getStudentListForCourse(cd.id);
			
			//calculate submission statistics for each evaluation
			for (EvaluationData evaluation : evaluationsSummaryForCourse) {
				evaluation.expectedTotal = students.size();
				
				HashMap<String, SubmissionData> submissions = getSubmissionsForEvaluation(cd.id, evaluation.name);
				evaluation.submittedTotal = countSubmittedStudents(submissions.values());
				
				evaluationSummaryList.add(evaluation);
			}
		}
		return evaluationSummaryList;
	}

	@SuppressWarnings("unused")
	private void ____COURSE_level_methods__________________________________() {
	}

	/**
	 * Access level: Coord and above
	 */
	public void createCourse(String coordId, String courseId, String courseName)
			throws EntityAlreadyExistsException, InvalidParametersException {

		Common.verifyNotNull(coordId, "coordinator ID");
		Common.verifyNotNull(courseId, "course ID");
		Common.verifyNotNull(courseName, "course name");

		verifyCoordUsingOwnIdOrAbove(coordId);

		// TODO: this validation should be done at a lower level
		Common.validateGoogleId(coordId);
		Common.validateCourseId(courseId);
		Common.validateCourseName(courseName);

		CourseData courseToAdd = new CourseData(courseId, courseName, coordId);
		CoursesStorage.inst().getDb().createCourse(courseToAdd);
	}

	/**
	 * AccessLevel : any registered user (because it is too expensive to check
	 * if a student is in the course)
	 */
	public CourseData getCourse(String courseId) {

		Common.verifyNotNull(courseId, "course ID");

		verifyRegisteredUserOrAbove();

		CourseData c = CoursesStorage.inst().getDb().getCourse(courseId);
		return c;
	}

	/**
	 * Returns a detailed version of course data, including evaluation data
	 * Access: course owner, student in course, admin
	 */
	public CourseData getCourseDetails(String courseId)
			throws EntityDoesNotExistException {

		Common.verifyNotNull(courseId, "course ID");

		verifyCourseOwnerOrStudentInCourse(courseId);

		// TODO: very inefficient. Should be optimized.
		CourseData course = getCourse(courseId);

		if (course == null) {
			throw new EntityDoesNotExistException("The course does not exist: "
					+ courseId);
		}
		HashMap<String, CourseData> courseList = getCourseDetailsListForCoord(course.coord);
		return courseList.get(courseId);
	}

	public void editCourse(CourseData course) throws NotImplementedException {
		throw new NotImplementedException("Not implemented because we do "
				+ "not allow editing courses");
	}

	/**
	 * Access: course owner and above
	 */
	public void deleteCourse(String courseId) {

		Common.verifyNotNull(courseId, "course ID");

		verifyCourseOwnerOrAbove(courseId);
		
		EvaluationsStorage.inst().deleteEvaluationsForCourse(courseId);
		CoursesStorage.inst().deleteCourse(courseId);
	}

	/**
	 * Access: course owner and above
	 */
	public List<StudentData> getStudentListForCourse(String courseId)
			throws EntityDoesNotExistException {

		Common.verifyNotNull(courseId, "course ID");

		verifyCourseOwnerOrAbove(courseId);

		List<StudentData> studentDataList = AccountsStorage.inst().getDb().getStudentListForCourse(
				courseId);

		if ((studentDataList.size() == 0) && (getCourse(courseId) == null)) {
			throw new EntityDoesNotExistException("Course does not exist :"
					+ courseId);
		}

		return studentDataList;
	}

	/**
	 * Access: course owner and above
	 * 
	 * @return Returns the list of emails sent. These can be used for
	 *         verification.
	 */
	public List<MimeMessage> sendRegistrationInviteForCourse(String courseId)
			throws InvalidParametersException {

		Common.verifyNotNull(courseId, "course ID");

		verifyCourseOwnerOrAbove(courseId);

		List<StudentData> studentDataList = AccountsStorage.inst().getDb()
				.getUnregisteredStudentListForCourse(courseId);

		ArrayList<MimeMessage> emailsSent = new ArrayList<MimeMessage>();

		for (StudentData s : studentDataList) {
			try {
				MimeMessage email = sendRegistrationInviteToStudent(courseId,
						s.email);
				emailsSent.add(email);
			} catch (EntityDoesNotExistException e) {
				Assumption.fail("Unexpected EntitiyDoesNotExistException thrown when sending registration email"
						+ Common.stackTraceToString(e));
			}
		}
		return emailsSent;
	}

	/**
	 * Enrolls new students in the course or modifies existing students. But it
	 * will not delete any students. It will not edit email address either. If
	 * an existing student was enrolled with a different email address, that
	 * student will be treated as a new student.<br>
	 * If there is an error in the enrollLines, there will be no changes to the
	 * datastore <br>
	 * Access: course owner and above
	 * 
	 * @return StudentData objects in the return value contains the status of
	 *         enrollment. It also includes data for other students in the
	 *         course that were not touched by the operation.
	 */
	public List<StudentData> enrollStudents(String enrollLines, String courseId)
			throws EnrollException, EntityDoesNotExistException {

		Common.verifyNotNull(courseId, "course ID");
		Common.verifyNotNull(enrollLines, "enrollment text");

		verifyCourseOwnerOrAbove(courseId);

		if (getCourse(courseId) == null) {
			throw new EntityDoesNotExistException("Course does not exist :"
					+ courseId);
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
				studentList.add(new StudentData(line, courseId)); // Note: Find out what is "line"
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

	/**
	 * Access: course owner, student in course, admin
	 * 
	 * @return The CourseData object that is returned will contain attributes
	 *         teams(type:TeamData) and loners(type:StudentData). We do not
	 *         expect any loners as the current system does not support students
	 *         without teams. This field is to be removed later. <br>
	 *         Access : course owner and above
	 */
	public CourseData getTeamsForCourse(String courseId)
			throws EntityDoesNotExistException {

		Common.verifyNotNull(courseId, "course ID");

		verifyCourseOwnerOrStudentInCourse(courseId);

		List<StudentData> students = getStudentListForCourse(courseId);
		CoursesStorage.sortByTeamName(students);

		CourseData course = getCourse(courseId);

		if (course == null) {
			throw new EntityDoesNotExistException("The course " + courseId
					+ " does not exist");
		}

		TeamData team = null;
		for (int i = 0; i < students.size(); i++) {

			StudentData s = students.get(i);

			// if loner
			if (s.team.equals("")) {
				course.loners.add(s);
				// first student of first team
			} else if (team == null) {
				team = new TeamData();
				team.name = s.team;
				team.students.add(s);
				// student in the same team as the previous student
			} else if (s.team.equals(team.name)) {
				team.students.add(s);
				// first student of subsequent teams (not the first team)
			} else {
				course.teams.add(team);
				team = new TeamData();
				team.name = s.team;
				team.students.add(s);
			}

			// if last iteration
			if (i == (students.size() - 1)) {
				course.teams.add(team);
			}
		}

		return course;
	}

	@SuppressWarnings("unused")
	private void ____STUDENT_level_methods__________________________________() {
	}

	/**
	 * Creates a student and adjust existing evaluations to accommodate the new
	 * student Access: course owner and above
	 */
	public void createStudent(StudentData studentData)
			throws EntityAlreadyExistsException, InvalidParametersException {

		Common.verifyNotNull(studentData, "student data");

		verifyCourseOwnerOrAbove(studentData.course);

		AccountsStorage.inst().getDb().createStudent(studentData);

		// adjust existing evaluations to accommodate new student
		EvaluationsStorage.inst().adjustSubmissionsForNewStudent(
				studentData.course, studentData.email,
				studentData.team);
	}

	/**
	 * Access: any registered user (to minimize cost of checking)
	 * 
	 * @return returns null if there is no such student.
	 */
	public StudentData getStudent(String courseId, String email) {

		Common.verifyNotNull(courseId, "course ID");
		Common.verifyNotNull(email, "student email");

		verifyRegisteredUserOrAbove();

		StudentData studentData = AccountsStorage.inst().getDb().getStudent(courseId, email);
		return studentData;
	}

	/**
	 * Access: coord of course and above.<br>
	 * All attributes except courseId be changed. Trying to change courseId will
	 * be treated as trying to edit a student in a different course.<br>
	 * Changing team name will not delete existing submissions under that team <br>
	 * Cascade logic:
	 *   Email changed-> changes email in all existing submissions <br>
	 *   Team changed-> creates new submissions for the new team, deletes 
	 *       submissions for previous team structure
	 */
	// TODO: rework this
	public void editStudent(String originalEmail, StudentData student)
			throws InvalidParametersException, EntityDoesNotExistException {

		Common.verifyNotNull(originalEmail, "student email");
		Common.verifyNotNull(student, "student object");

		verifyCourseOwnerOrAbove(student.course);

		StudentData originalStudent = AccountsStorage.inst().getDb().getStudent(
				student.course, originalEmail);

		if (originalStudent == null) {
			throw new EntityDoesNotExistException("Non-existent student "
					+ student.course + "/" + originalEmail);
		}
		String originalTeam = originalStudent.team;

		// TODO: make the implementation more defensive, e.g. duplicate email
		AccountsStorage.inst().getDb().editStudent(student.course, originalEmail,
				student.name, student.team, student.email, student.id,
				student.comments, student.profile);

		// cascade email change, if any
		if (!originalEmail.equals(student.email)) {
			EvaluationsStorage.inst().getSubmissionsDb().editStudentEmailForSubmissionsInCourse(student.course,
					originalEmail, student.email);
		}

		// adjust submissions if moving to a different team
		if (isTeamChanged(originalTeam, student.team)) {
			EvaluationsStorage.inst().adjustSubmissionsForChangingTeam(student.course, student.email, originalTeam, student.team);
		}
	}

	/**
	 * Access: course owner and above
	 */
	public void deleteStudent(String courseId, String studentEmail) {

		Common.verifyNotNull(courseId, "course ID");
		Common.verifyNotNull(studentEmail, "student email");

		verifyCourseOwnerOrAbove(courseId);

		AccountsStorage.inst().getDb().deleteStudent(courseId,studentEmail);
		EvaluationsStorage.inst().getSubmissionsDb().deleteAllSubmissionsForStudent(courseId,
				studentEmail);
	}

	/**
	 * Access: course owner and above
	 */
	public MimeMessage sendRegistrationInviteToStudent(String courseId,
			String studentEmail) throws EntityDoesNotExistException,
			InvalidParametersException {

		Common.verifyNotNull(courseId, "course ID");
		Common.verifyNotNull(studentEmail, "student email");

		verifyCourseOwnerOrAbove(courseId);

		CourseData course = CoursesStorage.inst().getDb().getCourse(courseId);
		StudentData studentData = AccountsStorage.inst().getDb().getStudent(courseId,
				studentEmail);
		if (studentData == null) {
			throw new EntityDoesNotExistException("Student [" + studentEmail
					+ "] does not exist in course [" + courseId + "]");
		}

		Emails emailMgr = new Emails();
		try {
			MimeMessage email = emailMgr.generateStudentCourseJoinEmail(
					course, studentData);
			emailMgr.sendEmail(email);
			return email;
		} catch (Exception e) {
			throw new RuntimeException("Unexpected error while sending email",
					e);
		}

	}

	/**
	 * Access: same student and admin only
	 * 
	 * @return Returns all StudentData objects associated with this Google ID.
	 *         Returns an empty list if no student has this Google ID.
	 */
	public ArrayList<StudentData> getStudentsWithId(String googleId) {

		Common.verifyNotNull(googleId, "Google ID");

		verifySameStudentOrAdmin(googleId);

		List<StudentData> students = AccountsStorage.inst().getDb().getStudentsWithGoogleId(googleId);
		ArrayList<StudentData> returnList = new ArrayList<StudentData>();
		for (StudentData s : students) {
			returnList.add(s);
		}
		return returnList;
	}

	/**
	 * Access: same student and admin only
	 * 
	 * @return Returns the StudentData object that has the given courseId and is
	 *         in given course. Returns null if no such student in the course.
	 */
	public StudentData getStudentInCourseForGoogleId(String courseId,
			String googleId) {
		// TODO: make more efficient?

		Common.verifyNotNull(courseId, "course ID");
		Common.verifyNotNull(googleId, "Google ID");

		verifySameStudentOrCourseOwnerOrAdmin(courseId, googleId);

		ArrayList<StudentData> studentList = getStudentsWithId(googleId);
		for (StudentData sd : studentList) {
			if (sd.course.equals(courseId)) {
				return sd;
			}
		}
		return null;
	}

	/**
	 * Access: owner of googleId
	 */
	public void joinCourse(String googleId, String key)
			throws JoinCourseException, InvalidParametersException {

		Common.verifyNotNull(googleId, "google ID");
		Common.verifyNotNull(key, "registration key");

		verifyOwnerOfId(googleId);

		AccountsStorage.inst().getDb().joinCourse(key, googleId);

	}

	/**
	 * Access: course owner and above
	 * 
	 * @return Returns registration key for a student in the given course.
	 */
	public String getKeyForStudent(String courseId, String email) {

		Common.verifyNotNull(courseId, "course ID");
		Common.verifyNotNull(email, "student email");

		verifyCourseOwnerOrAbove(courseId);

		StudentData studentData = AccountsStorage.inst().getDb().getStudent(courseId, email);

		if (studentData == null) {
			return null;
		}

		// TODO: this should be pushed to lower levels
		return studentData.key;
	}

	/**
	 * Access: student who owns the googleId, admin
	 * 
	 * @return Returns Courses associated with the student with the given Google
	 *         ID
	 * @throws EntityDoesNotExistException
	 *             Thrown if no such student.
	 */
	public List<CourseData> getCourseListForStudent(String googleId)
			throws EntityDoesNotExistException, InvalidParametersException {

		Common.verifyNotNull(googleId, "Google Id");

		verifySameStudentOrAdmin(googleId);

		if (getStudentsWithId(googleId).size() == 0) {
			throw new EntityDoesNotExistException("Student with Google ID "
					+ googleId + " does not exist");
		}

		return CoursesStorage.inst().getCourseListForStudent(googleId);
	}

	/**
	 * Access: any logged in user (to minimize cost of checking)
	 */
	public boolean hasStudentSubmittedEvaluation(String courseId,
			String evaluationName, String studentEmail)
			throws InvalidParametersException {

		Common.verifyNotNull(courseId, "course ID");
		Common.verifyNotNull(evaluationName, "evaluation name");
		Common.verifyNotNull(studentEmail, "student email");

		verifyLoggedInUserAndAbove();

		List<SubmissionData> submissions = null;
		try {
			submissions = getSubmissionsFromStudent(courseId, evaluationName,
					studentEmail);
		} catch (EntityDoesNotExistException e) {
			return false;
		}

		if (submissions == null) {
			return false;
		}

		for (SubmissionData sd : submissions) {
			if (sd.points != Common.POINTS_NOT_SUBMITTED) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Access: student who owns the googleId, admin
	 * 
	 * @return Returns details of courses the student is in. CourseData objects
	 *         returned contain details of evaluations too (except the ones
	 *         still AWAITING).
	 */
	public List<CourseData> getCourseDetailsListForStudent(String googleId)
			throws EntityDoesNotExistException, InvalidParametersException {

		Common.verifyNotNull(googleId, "Google ID");

		verifySameStudentOrAdmin(googleId);

		// Get the list of courses that this student is in
		List<CourseData> courseList = getCourseListForStudent(googleId);
		
		// For each course the student is in
		for (CourseData c : courseList) {
			// Get the list of evaluations for the course
			List<EvaluationData> evaluationDataList = EvaluationsStorage.inst().getEvaluationsDb()
					.getEvaluationsForCourse(c.id);
			
			// For the list of evaluations for this course
			for (EvaluationData ed : evaluationDataList) {
				// Add this evaluation to the course's list of evaluations.
				log.fine("Adding evaluation " + ed.name + " to course " + c.id);
				if (ed.getStatus() != EvalStatus.AWAITING) {
					c.evaluations.add(ed);
				}
			}
		}
		return courseList;
	}

	/**
	 * Access: owner of the course, owner of result (when PUBLISHED), admin
	 */
	public EvalResultData getEvaluationResultForStudent(String courseId,
			String evaluationName, String studentEmail)
			throws EntityDoesNotExistException, InvalidParametersException {

		Common.verifyNotNull(courseId, "course id");
		Common.verifyNotNull(evaluationName, "evaluation name");
		Common.verifyNotNull(studentEmail, "student email");

		verfyCourseOwner_OR_EmailOwnerAndPublished(courseId, evaluationName,
				studentEmail);

		StudentData student = getStudent(courseId, studentEmail);
		if (student == null) {
			throw new EntityDoesNotExistException("The student " + studentEmail
					+ " does not exist in course " + courseId);
		}
		// TODO: this is very inefficient as it calculates the results for the
		// whole class first
		EvaluationData courseResult = getEvaluationResult(courseId,
				evaluationName);
		TeamData teamData = courseResult.getTeamData(student.team);
		EvalResultData returnValue = null;

		for (StudentData sd : teamData.students) {
			if (sd.email.equals(student.email)) {
				returnValue = sd.result;
				break;
			}
		}

		for (StudentData sd : teamData.students) {
			returnValue.selfEvaluations.add(sd.result.getSelfEvaluation());
		}

		returnValue.sortIncomingByFeedbackAscending();
		return returnValue;
	}

	@SuppressWarnings("unused")
	private void ____EVALUATION_level_methods______________________________() {
	}

	/**
	 * Access: course owner and above
	 * 
	 * @throws InvalidParametersException
	 *             is thrown if any of the parameters puts the evaluation in an
	 *             invalid state (e.g., endTime is set before startTime).
	 *             However, setting start time to a past time is allowed.
	 */
	public void createEvaluation(EvaluationData evaluation)
			throws EntityAlreadyExistsException, InvalidParametersException {

		Common.verifyNotNull(evaluation, "evaluation");

		verifyCourseOwnerOrAbove(evaluation.course);

		EvaluationsStorage.inst().createEvaluation(evaluation);
	}

	

	/**
	 * Access: all registered users
	 * 
	 */
	public EvaluationData getEvaluation(String courseId, String evaluationName) {

		Common.verifyNotNull(courseId, "course ID");
		Common.verifyNotNull(evaluationName, "evaluation name");

		verifyRegisteredUserOrAbove();

		EvaluationData e = EvaluationsStorage.inst().getEvaluationsDb().getEvaluation(courseId,
				evaluationName);
		
		return e;
	}

	/**
	 * Can be used to change all fields exception "activated" field <br>
	 * Access: owner and above
	 * 
	 * @throws InvalidParametersException
	 *             if new values bring the evaluation to an invalid state.
	 */
	public void editEvaluation(String courseId, String evaluationName,
			String instructions, Date start, Date end, double timeZone,
			int gracePeriod, boolean p2pEndabled)
			throws EntityDoesNotExistException, InvalidParametersException {

		Common.verifyNotNull(courseId, "course ID");
		Common.verifyNotNull(evaluationName, "evaluation name");
		Common.verifyNotNull(start, "starting time");
		Common.verifyNotNull(end, "deadline");

		verifyCourseOwnerOrAbove(courseId);
		EvaluationData original = getEvaluation(courseId, evaluationName);

		if (original == null) {
			throw new EntityDoesNotExistException("Evaluation "
					+ evaluationName + " does not exist in course " + courseId);
		}

		EvaluationData evaluation = new EvaluationData();
		evaluation.course = courseId;
		evaluation.name = evaluationName;
		evaluation.instructions = instructions;
		evaluation.p2pEnabled = p2pEndabled;
		evaluation.startTime = start;
		evaluation.endTime = end;
		evaluation.gracePeriod = gracePeriod;
		evaluation.timeZone = timeZone;

		// this field cannot be changed via this method
		evaluation.activated = original.activated;
		evaluation.published = original.published;

		if (evaluation.isValid()) {
			EvaluationsStorage.inst().getEvaluationsDb().editEvaluation(evaluation);
		} else {
			throw new InvalidParametersException(evaluation.getInvalidStateInfo());
		}
	}

	

	/**
	 * Access: owner and above
	 */
	public void deleteEvaluation(String courseId, String evaluationName) {

		Common.verifyNotNull(courseId, "course ID");
		Common.verifyNotNull(evaluationName, "evaluation name");

		verifyCourseOwnerOrAbove(courseId);

		EvaluationsStorage.inst().deleteEvaluation(courseId, evaluationName);
	}

	/**
	 * Access: course owner and above
	 * 
	 * @throws InvalidParametersException
	 *             if the evaluation is not ready to be published.
	 */
	public void publishEvaluation(String courseId, String evaluationName)
			throws EntityDoesNotExistException, InvalidParametersException {

		Common.verifyNotNull(courseId, "course ID");
		Common.verifyNotNull(evaluationName, "evaluation name");

		verifyCourseOwnerOrAbove(courseId);

		EvaluationData evaluation = getEvaluation(courseId, evaluationName);

		verifyEvaluationExists(evaluation, courseId, evaluationName);

		if (evaluation.getStatus() != EvalStatus.CLOSED) {
			throw new InvalidParametersException(
					Common.ERRORCODE_PUBLISHED_BEFORE_CLOSING,
					"Cannot publish an evaluation unless it is CLOSED");
		}

		EvaluationsStorage.inst().getEvaluationsDb().setEvaluationPublishedStatus(courseId, evaluationName, true);
		sendEvaluationPublishedEmails(courseId, evaluationName);
	}

	/**
	 * Access: course owner and above
	 * 
	 * @throws InvalidParametersException
	 *             if the evaluation is not ready to be unpublished.
	 */
	public void unpublishEvaluation(String courseId, String evaluationName)
			throws EntityDoesNotExistException, InvalidParametersException {

		Common.verifyNotNull(courseId, "course ID");
		Common.verifyNotNull(evaluationName, "evaluation name");

		verifyCourseOwnerOrAbove(courseId);

		EvaluationData evaluation = getEvaluation(courseId, evaluationName);

		verifyEvaluationExists(evaluation, courseId, evaluationName);

		if (evaluation.getStatus() != EvalStatus.PUBLISHED) {
			throw new InvalidParametersException(
					Common.ERRORCODE_UNPUBLISHED_BEFORE_PUBLISHING,
					"Cannot unpublish an evaluation unless it is PUBLISHED");
		}

		EvaluationsStorage.inst().getEvaluationsDb().setEvaluationPublishedStatus(courseId, evaluationName, false);
	}

	/**
	 * Sends reminders to students who haven't submitted yet. Access: course
	 * owner and above
	 */
	public List<MimeMessage> sendReminderForEvaluation(String courseId,
			String evaluationName) throws EntityDoesNotExistException {

		Common.verifyNotNull(courseId, "course ID");
		Common.verifyNotNull(evaluationName, "evaluation name");

		verifyCourseOwnerOrAbove(courseId);

		EvaluationData evaluation = getEvaluation(courseId, evaluationName);

		verifyEvaluationExists(evaluation, courseId, evaluationName);

		// Filter out students who have submitted the evaluation
		List<StudentData> studentDataList = AccountsStorage.inst().getDb().getStudentListForCourse(courseId);
		
		List<StudentData> studentsToRemindList = new ArrayList<StudentData>();
		for (StudentData sd : studentDataList) {
			if (!EvaluationsStorage.inst().isEvaluationSubmitted(evaluation,
					sd.email)) {
				studentsToRemindList.add(sd);
			}
		}

		CourseData course = getCourse(courseId);

		List<MimeMessage> emails;

		Emails emailMgr = new Emails();
		try {
			emails = emailMgr.generateEvaluationReminderEmails(course,
					evaluation, studentsToRemindList);
			emailMgr.sendEmails(emails);
		} catch (Exception e) {
			throw new RuntimeException("Error while sending emails :", e);
		}

		return emails;
	}

	/**
	 * Access: course owner and above
	 */
	public EvaluationData getEvaluationResult(String courseId,
			String evaluationName) throws EntityDoesNotExistException {

		Common.verifyNotNull(courseId, "course ID");
		Common.verifyNotNull(evaluationName, "evaluation name");

		verifyCourseOwnerOrAbove(courseId);

		CourseData course = getTeamsForCourse(courseId);
		EvaluationData returnValue = getEvaluation(courseId, evaluationName);
		HashMap<String, SubmissionData> submissionDataList = getSubmissionsForEvaluation(
				courseId, evaluationName);
		returnValue.teams = course.teams;
		for (TeamData team : returnValue.teams) {
			for (StudentData student : team.students) {
				student.result = new EvalResultData();
				// TODO: refactor this method. May be have a return value?
				populateSubmissionsAndNames(submissionDataList, team, student);
			}

			TeamEvalResult teamResult = calculateTeamResult(team);
			team.result = teamResult;
			populateTeamResult(team, teamResult);

		}
		return returnValue;
	}

	/**
	 * @return Returns all submissions by a student for the given evaluation
	 *         Access: course owner, reviewer, admin
	 * 
	 */
	public List<SubmissionData> getSubmissionsFromStudent(String courseId,
			String evaluationName, String reviewerEmail)
			throws EntityDoesNotExistException, InvalidParametersException {

		Common.verifyNotNull(courseId, "course ID");
		Common.verifyNotNull(evaluationName, "evaluation name");
		Common.verifyNotNull(reviewerEmail, "student email");

		verifyReviewerOrCourseOwnerOrAdmin(courseId, reviewerEmail);

		List<SubmissionData> submissions = EvaluationsStorage.inst().getSubmissionsDb()
				.getSubmissionsFromEvaluationFromStudent(courseId, evaluationName,
						reviewerEmail);
		
		boolean isSubmissionsExist = (submissions.size() > 0 && 
			CoursesStorage.inst().isCourseExists(courseId) &&
			EvaluationsStorage.inst().isEvaluationExists(courseId,evaluationName) &&
			AccountsStorage.inst().isStudentExists(courseId, reviewerEmail));
		
		if (!isSubmissionsExist) {
			throw new EntityDoesNotExistException("Error getting submissions from student: "
												+ courseId + " / " + evaluationName
												+ ", reviewer: " + reviewerEmail);
		}
		
		StudentData student = getStudent(courseId, reviewerEmail);
		ArrayList<SubmissionData> returnList = new ArrayList<SubmissionData>();
		for (SubmissionData sd : submissions) {
			StudentData reviewee = getStudent(courseId, sd.reviewee);
			if (!isOrphanSubmission(student, reviewee, sd)) {
				sd.reviewerName = student.name;
				sd.revieweeName = reviewee.name;
				returnList.add(sd);
			}
		}
		return returnList;
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

	/**
	 * Access: course owner, reviewer (if OPEN), admin
	 */
	public void editSubmissions(List<SubmissionData> submissionDataList)
			throws EntityDoesNotExistException, InvalidParametersException {

		Common.verifyNotNull(submissionDataList, "submissions list");

		for (SubmissionData sd : submissionDataList) {
			verifySubmissionEditableForUser(sd);
			editSubmission(sd);
		}
	}

	public void deleteSubmission(SubmissionData submission)
			throws NotImplementedException {
		throw new NotImplementedException(
				"Not implemented because submissions "
						+ "are deleted automatically");
	}

	@SuppressWarnings("unused")
	private void ____helper_methods________________________________________() {
	}
	
	private boolean isTeamChanged(String originalTeam, String newTeam) {
		return (newTeam!=null)&&
				(originalTeam!=null)&&
				(!originalTeam.equals(newTeam));
	}

	/**
	 * Returns how many students have submitted at least one submission.
	 */
	private int countSubmittedStudents(
			Collection<SubmissionData> submissions) {
		int count = 0;
		List<String> emailsOfSubmittedStudents = new ArrayList<String>();
		for (SubmissionData s : submissions) {
			if (s.points != Common.POINTS_NOT_SUBMITTED
					&& !emailsOfSubmittedStudents.contains(s.reviewer)) {
				count++;
				emailsOfSubmittedStudents.add(s.reviewer);
			}
		}
		return count;
	}
	
	
	private List<MimeMessage> sendEvaluationPublishedEmails(String courseId,
			String evaluationName) throws EntityDoesNotExistException {
		List<MimeMessage> emailsSent;

		CourseData c = getCourse(courseId);
		EvaluationData e = getEvaluation(courseId, evaluationName);
		List<StudentData> students = getStudentListForCourse(courseId);

		Emails emailMgr = new Emails();
		try {
			emailsSent = emailMgr.generateEvaluationPublishedEmails(c, e,
					students);
			emailMgr.sendEmails(emailsSent);
		} catch (Exception ex) {
			throw new RuntimeException(
					"Unexpected error while sending emails ", ex);
		}
		return emailsSent;
	}

	private void verifyEvaluationExists(EvaluationData evaluation,
			String courseId, String evaluationName)
			throws EntityDoesNotExistException {
		if (evaluation == null) {
			throw new EntityDoesNotExistException(
					"There is no evaluation named '" + evaluationName
							+ "' under the course " + courseId);
		}
	}

	private void editSubmission(SubmissionData submission)
			throws EntityDoesNotExistException, InvalidParametersException {

		Common.verifyNotNull(submission, "submission");

		verifySubmissionEditableForUser(submission);

		if (getEvaluationStatus(submission.course, submission.evaluation) == EvalStatus.DOES_NOT_EXIST) {
			throw new EntityDoesNotExistException("The evaluation "
					+ submission.evaluation + " does not exist under course "
					+ submission.course);
		}

		EvaluationsStorage.inst().getSubmissionsDb().editSubmission(submission);
	}

	private EvalStatus getEvaluationStatus(String courseId,
			String evaluationName) {
		EvaluationData evaluation = getEvaluation(courseId, evaluationName);
		return evaluation == null ? EvalStatus.DOES_NOT_EXIST : evaluation
				.getStatus();
	}

	private StudentData enrollStudent(StudentData student) {
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
			log.severe("Exception thrown unexpectedly" + "\n" + Common.stackTraceToString(e));
		}
		student.updateStatus = updateStatus;
		return student;
	}

	/**
	 * Returns true if either of the three objects is null
	 * or if the team in submission is different from
	 * those in two students.
	 */
	private boolean isOrphanSubmission(StudentData reviewer,
			StudentData reviewee, SubmissionData submission) {
		if ((reviewer==null)||(reviewee==null)||(submission==null)) {
			return true;
		}
		if (!submission.team.equals(reviewer.team)) {
			return true;
		}
		if (!submission.team.equals(reviewee.team)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns submissions for the evaluation
	 */
	private HashMap<String, SubmissionData> getSubmissionsForEvaluation(
			String courseId, String evaluationName)
			throws EntityDoesNotExistException {
		if (getEvaluation(courseId, evaluationName) == null) {
			throw new EntityDoesNotExistException(
					"There is no evaluation named [" + evaluationName
							+ "] under the course [" + courseId + "]");
		}
		
		List<SubmissionData> submissionsList = EvaluationsStorage.inst()
				.getSubmissionsDb().getSubmissionsForEvaluation(courseId, evaluationName);
		
		HashMap<String, SubmissionData> submissionDataList = new HashMap<String, SubmissionData>();
		for (SubmissionData sd : submissionsList) {
			submissionDataList.put(sd.reviewer + "->" + sd.reviewee, sd);
		}
		return submissionDataList;
	}

	private TeamEvalResult calculateTeamResult(TeamData team) {

		Common.verifyNotNull(team, "team object");

		int teamSize = team.students.size();
		int[][] claimedFromStudents = new int[teamSize][teamSize];
		team.sortByStudentNameAscending();
		for (int i = 0; i < teamSize; i++) {
			StudentData studentData = team.students.get(i);
			studentData.result.sortOutgoingByStudentNameAscending();
			for (int j = 0; j < teamSize; j++) {
				SubmissionData submissionData = studentData.result.outgoing
						.get(j);
				claimedFromStudents[i][j] = submissionData.points;
			}

		}
		return new TeamEvalResult(claimedFromStudents);
	}

	private void populateTeamResult(TeamData team, TeamEvalResult teamResult) {
		team.sortByStudentNameAscending();
		int teamSize = team.students.size();
		for (int i = 0; i < teamSize; i++) {
			StudentData s = team.students.get(i);
			s.result.sortIncomingByStudentNameAscending();
			s.result.sortOutgoingByStudentNameAscending();
			s.result.claimedFromStudent = teamResult.claimed[i][i];
			s.result.claimedToCoord = teamResult.normalizedClaimed[i][i];
			s.result.perceivedToStudent = teamResult.denormalizedAveragePerceived[i][i];
			s.result.perceivedToCoord = teamResult.normalizedAveragePerceived[i];

			// populate incoming and outgoing
			for (int j = 0; j < teamSize; j++) {
				SubmissionData incomingSub = s.result.incoming.get(j);
				int normalizedIncoming = teamResult.denormalizedAveragePerceived[i][j];
				incomingSub.normalizedToStudent = normalizedIncoming;
				incomingSub.normalizedToCoord = teamResult.normalizedPeerContributionRatio[j][i];
				log.finer("Setting normalized incoming of " + s.name + " from "
						+ incomingSub.reviewerName + " to "
						+ normalizedIncoming);

				SubmissionData outgoingSub = s.result.outgoing.get(j);
				int normalizedOutgoing = teamResult.normalizedClaimed[i][j];
				outgoingSub.normalizedToStudent = Common.UNINITIALIZED_INT;
				outgoingSub.normalizedToCoord = normalizedOutgoing;
				log.finer("Setting normalized outgoing of " + s.name + " to "
						+ outgoingSub.revieweeName + " to "
						+ normalizedOutgoing);
			}
		}
	}

	private void populateSubmissionsAndNames(
			HashMap<String, SubmissionData> list, TeamData team,
			StudentData student) {
		for (StudentData peer : team.students) {

			// get incoming submission from peer
			String key = peer.email + "->" + student.email;
			SubmissionData submissionFromPeer = list.get(key);
			// this workaround is to cater for missing submissions in
			// legacy data.
			if (submissionFromPeer == null) {
				log.warning("Cannot find submission for" + key);
				submissionFromPeer = createEmptySubmission(peer.email,
						student.email);
			} else {
				// use a copy to prevent accidental overwriting of data
				submissionFromPeer = submissionFromPeer.getCopy();
			}

			// set names in incoming submission
			submissionFromPeer.revieweeName = student.name;
			submissionFromPeer.reviewerName = peer.name;

			// add incoming submission
			student.result.incoming.add(submissionFromPeer);

			// get outgoing submission to peer
			key = student.email + "->" + peer.email;
			SubmissionData submissionToPeer = list.get(key);

			// this workaround is to cater for missing submissions in
			// legacy data.
			if (submissionToPeer == null) {
				log.warning("Cannot find submission for" + key);
				submissionToPeer = createEmptySubmission(student.email,
						peer.email);
			} else {
				// use a copy to prevent accidental overwriting of data
				submissionToPeer = submissionToPeer.getCopy();
			}

			// set names in outgoing submission
			submissionToPeer.reviewerName = student.name;
			submissionToPeer.revieweeName = peer.name;

			// add outgoing submission
			student.result.outgoing.add(submissionToPeer);

		}
	}

	private SubmissionData createEmptySubmission(String reviewer,
			String reviewee) {
		SubmissionData s;
		s = new SubmissionData();
		s.reviewer = reviewer;
		s.reviewee = reviewee;
		s.points = Common.UNINITIALIZED_INT;
		s.justification = new Text("");
		s.p2pFeedback = new Text("");
		s.course = "";
		s.evaluation = "";
		return s;
	}

	protected SubmissionData getSubmission(String courseId,
			String evaluationName, String reviewerEmail, String revieweeEmail) {
		SubmissionData sd = EvaluationsStorage.inst().getSubmissionsDb().getSubmission(
				courseId, evaluationName, revieweeEmail, reviewerEmail);
		return sd;
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
		StudentData existingStudent = getStudent(student.course, student.email);
		if (existingStudent == null)
			return false;
		return student.isEnrollInfoSameAs(existingStudent);
	}

	private boolean isModificationToExistingStudent(StudentData student) {
		return AccountsStorage.inst().isStudentExists(student.course, student.email);
	}
	
	
	
	/**
	 * This method sends run-time error message to system support email
	 * @param req httpRequest that triggers the error
	 * @param error the error object
	 */
	public MimeMessage emailErrorReport(String path, String params, Throwable error) {
		String message = error.getMessage();
		String stackTrace = Common.stackTraceToString(error);
		
		//if the error doesn't contain a short description,
		//retrieve the first line of stack trace.
		//truncate stack trace at first "at" string		
		if(message == null) {
			int msgTruncateIndex = stackTrace.indexOf("at");
			if(msgTruncateIndex > 0) {
				message = stackTrace.substring(0, msgTruncateIndex);
			}else{
				message = "";
			}
		}
		MimeMessage email = null ;
		try {
			String version = BuildProperties.getAppVersion();
			email = new Emails().sendSystemErrorEmail(message, stackTrace, path, params, version);
			log.severe("Sent crash report: " + Emails.getEmailInfo(email));
		} catch (Exception e) {
			log.severe("Error in sending crash report: " + email.toString());
		}
		
		return email;
	}

}
