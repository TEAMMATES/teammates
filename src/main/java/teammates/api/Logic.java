package teammates.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import teammates.TeamEvalResult;
import teammates.datatransfer.CoordData;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.DataBundle;
import teammates.datatransfer.EvalResultData;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.StudentActionData;
import teammates.datatransfer.StudentData;
import teammates.datatransfer.SubmissionData;
import teammates.datatransfer.TeamData;
import teammates.datatransfer.TeamProfileData;
import teammates.datatransfer.TfsData;
import teammates.datatransfer.UserData;
import teammates.datatransfer.EvaluationData.EvalStatus;
import teammates.datatransfer.StudentData.UpdateStatus;
import teammates.exception.GoogleIDExistsInCourseException;
import teammates.exception.RegistrationKeyInvalidException;
import teammates.exception.RegistrationKeyTakenException;
import teammates.jdo.CourseSummaryForCoordinator;
import teammates.jdo.EvaluationDetailsForCoordinator;
import teammates.manager.Accounts;
import teammates.manager.Courses;
import teammates.manager.Evaluations;
import teammates.manager.TeamForming;
import teammates.persistent.Coordinator;
import teammates.persistent.Course;
import teammates.persistent.Evaluation;
import teammates.persistent.Student;
import teammates.persistent.Submission;
import teammates.persistent.TeamFormingLog;
import teammates.persistent.TeamFormingSession;
import teammates.persistent.TeamProfile;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.gson.Gson;

public class Logic {
	
	private static Logger log = Common.getLogger();
	
	
	protected void createSubmissions(List<SubmissionData> submissionDataList) {
		ArrayList<Submission> submissions = new ArrayList<Submission>();
		for (SubmissionData sd : submissionDataList) {
			submissions.add(sd.toSubmission());
		}
		Evaluations.inst().editSubmissions(submissions);
	}

	

	@SuppressWarnings("unused")
	private void ____SYSTEM_level_methods__________________________________() {
	}

	public static String getLoginUrl(String redirectUrl) {
		Accounts accounts = Accounts.inst();
		return accounts.getLoginPage(redirectUrl);
	}

	public static String getLogoutUrl(String redirectUrl) {
		Accounts accounts = Accounts.inst();
		return accounts.getLogoutPage(redirectUrl);
	}

	public static boolean isUserLoggedIn() {
		Accounts accounts = Accounts.inst();
		return (accounts.getUser() != null);
	}

	@Deprecated
	public String getUserId() {
		Accounts accounts = Accounts.inst();
		return accounts.getUser().getNickname();
	}

	public UserData getLoggedInUser() {
		Accounts accounts = Accounts.inst();
		User user = accounts.getUser();
		if (user == null) {
			return null;
		}

		UserData userData = new UserData(user.getNickname());

		// TODO: make more efficient?
		if (accounts.isAdministrator()) {
			userData.isAdmin = true;
		}
		if (accounts.isCoordinator()) {
			userData.isCoord = true;
		}

		if (accounts.isStudent(user.getNickname())) {
			userData.isStudent = true;
		}
		return userData;
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

	// TODO: return ArrayList instead?
	// That's better probably ~Aldrian~
	public HashMap<String, CourseData> getCourseListForCoord(String coordId)
			throws EntityDoesNotExistException {
		if (coordId == null)
			return null;
		HashMap<String, CourseSummaryForCoordinator> courseSummaryListForCoord = Courses
				.inst().getCourseSummaryListForCoord(coordId);
		if (courseSummaryListForCoord.size() == 0) {
			if (getCoord(coordId) == null) {
				throw new EntityDoesNotExistException(
						"Coordinator does not exist :" + coordId);
			}
		}
		HashMap<String, CourseData> returnList = new HashMap<String, CourseData>();
		for (CourseSummaryForCoordinator csfc : courseSummaryListForCoord
				.values()) {
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
			String coordId) throws EntityDoesNotExistException {
		if (coordId == null) {
			return null;
		}
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

	public ArrayList<EvaluationData> getEvaluationsListForCoord(String coordId)
			throws EntityDoesNotExistException {

		if (coordId == null) {
			return null;
		}

		List<Course> courseList = Courses.inst().getCoordinatorCourseList(
				coordId);
		if ((courseList.size() == 0) && (getCoord(coordId) == null)) {
			throw new EntityDoesNotExistException(
					"Coordinator does not exist :" + coordId);
		}
		ArrayList<EvaluationData> evaluationDetailsList = new ArrayList<EvaluationData>();

		for (Course c : courseList) {
			ArrayList<EvaluationDetailsForCoordinator> evaluationsSummaryForCourse = Evaluations
					.inst().getEvaluationsSummaryForCourse(c.getID());
			for (EvaluationDetailsForCoordinator edfc : evaluationsSummaryForCourse) {
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

	public List<TfsData> getTfsListForCoord(String coordId)
			throws EntityDoesNotExistException {
		if (coordId == null) {
			return null;
		}
		List<Course> courseList = Courses.inst().getCoordinatorCourseList(
				coordId);
		if ((courseList.size() == 0) && (getCoord(coordId) == null)) {
			throw new EntityDoesNotExistException(
					"Coordinator does not exist :" + coordId);
		}
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

	public CourseData getCourseDetails(String courseId)
			throws EntityDoesNotExistException {
		// TODO: very inefficient. Should be optimized.
		
		CourseData course = getCourse(courseId);
		if(course==null){
			throw new EntityDoesNotExistException("The course does not exist: "+courseId);
		}
		HashMap<String, CourseData> courseList = getCourseDetailsListForCoord(course.coord);
		return courseList.get(courseId);
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

	public List<StudentData> getStudentListForCourse(String courseId)
			throws EntityDoesNotExistException {
		if (courseId == null) {
			return null;
		}
		List<Student> studentList = Courses.inst().getStudentList(courseId);

		if ((studentList.size() == 0) && (getCourse(courseId) == null)) {
			throw new EntityDoesNotExistException("Course does not exist :"
					+ courseId);
		}

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
			try {
				sendRegistrationInviteToStudent(courseId, s.getEmail());
			} catch (EntityDoesNotExistException e) {
				log.severe("Unexpected exception");
				e.printStackTrace();
			}
		}
	}

	public List<StudentData> enrollStudents(String enrollLines, String courseId)
			throws EnrollException, EntityDoesNotExistException {
		if (enrollLines == null || courseId == null) {
			throw new EnrollException(Common.ERRORCODE_NULL_PARAMETER,
					(enrollLines == null ? "Enroll text" : "Course ID")
							+ " cannot be null");
		}
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

		// TODO: adjust team profiles
		return returnList;
	}

	/**
	 * 
	 * @param courseId
	 * @return The CourseData object that is returned will contain attributes
	 *         teams(type:TeamData) and loners(type:StudentData)
	 * @throws EntityDoesNotExistException
	 *             if the course does not exist
	 */
	public CourseData getTeamsForCourse(String courseId)
			throws EntityDoesNotExistException {
		if (courseId == null) {
			return null;
		}
		List<StudentData> students = getStudentListForCourse(courseId);
		Courses.sortByTeamName(students);

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
				team.profile = getTeamProfile(courseId, team.name);
				team.students.add(s);
				// student in the same team as the previous student
			} else if (s.team.equals(team.name)) {
				team.students.add(s);
				// first student of subsequent teams (not the first team)
			} else {
				course.teams.add(team);
				team = new TeamData();
				team.name = s.team;
				team.profile = getTeamProfile(courseId, team.name);
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

	public void createStudent(StudentData studentData)
			throws EntityAlreadyExistsException, InvalidParametersException {
		if (studentData == null) {
			throw new InvalidParametersException("Student cannot be null");
		}
		Student student = new Student(studentData);
		// TODO: this if for backward compatibility with old system. Old system
		// considers "" as unregistered. It should be changed to consider
		// null as unregistered.
		if (student.getID() == null) {
			student.setID("");
		}
		if (student.getComments() == null) {
			student.setComments("");
		}
		if (student.getTeamName() == null) {
			student.setTeamName("");
		}
		Courses.inst().createStudent(student);
	}

	public StudentData getStudent(String courseId, String email) {
		if (courseId == null || email == null) {
			return null;
		}
		Student student = Accounts.inst().getStudent(courseId, email);
		return (student == null ? null : new StudentData(student));
	}

	/**
	 * All attributes except courseId be changed. Trying to change courseId will
	 * be treated as trying to edit a student in a different course.<br>
	 * Changing team name will not delete existing team profile even if there
	 * are no more members in the team. This can cause orphan team profiles but
	 * the effect is considered insignificant and not worth the effort required
	 * to avoid it. A side benefit of this strategy is the team can reclaim the
	 * profile by changing the team name back to the original one. But note that
	 * orphaned team profiles can be inherited by others if another team adopts
	 * the team name previously discarded by a team.
	 * 
	 * @param originalEmail
	 * @param student
	 * @throws InvalidParametersException
	 * @throws EntityDoesNotExistException
	 */
	public void editStudent(String originalEmail, StudentData student)
			throws InvalidParametersException, EntityDoesNotExistException {
		// TODO: make the implementation more defensive
		String newTeamName = student.team;
		Courses.inst().editStudent(student.course, originalEmail, student.name,
				student.team, student.email, student.id, student.comments,
				student.profile);
	}

	public void deleteStudent(String courseId, String studentEmail) {
		Courses.inst().deleteStudent(courseId, studentEmail);
		Evaluations.inst().deleteSubmissionsForStudent(courseId, studentEmail);
		TeamForming.inst().deleteLogsForStudent(courseId, studentEmail);
		// TODO:delete team profile, if the last member
	}

	// TODO: make this private
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
			String studentEmail) throws EntityDoesNotExistException,
			InvalidParametersException {

		if ((courseId == null) || (studentEmail == null)) {
			throw new InvalidParametersException(
					"Course ID and Student email cannot be null");
		}
		Course course = Courses.inst().getCourse(courseId);
		Student student = Courses.inst().getStudentWithEmail(courseId,
				studentEmail);
		if (student == null) {
			throw new EntityDoesNotExistException("Student [" + studentEmail
					+ "] does not exist in course [" + courseId + "]");
		}
		Coordinator coord = Accounts.inst().getCoordinator(
				course.getCoordinatorID());
		List<Student> studentList = new ArrayList<Student>();
		studentList.add(student);

		// TODO: this need not be a batch processing method
		Courses.inst().sendRegistrationKeys(studentList, courseId,
				course.getName(), coord.getName(), coord.getEmail());

	}

	public ArrayList<StudentData> getStudentsWithId(String googleId) {
		List<Student> students = Accounts.inst().getStudentWithID(googleId);
		if (students == null) {
			return null;
		}
		ArrayList<StudentData> returnList = new ArrayList<StudentData>();
		for (Student s : students) {
			returnList.add(new StudentData(s));
		}
		return returnList;
	}
	
	public StudentData getStudentInCourseForGoogleId(String courseId, String googleId) {
		//TODO: make more efficient?
		ArrayList<StudentData> studentList = getStudentsWithId(googleId);
		for(StudentData sd: studentList){
			if(sd.course.equals(courseId)){
				return sd;
			}
		}
		return null;
	}

	public void joinCourse(String googleId, String key)
			throws JoinCourseException, InvalidParametersException {
		if ((googleId == null) || (key == null)) {
			throw new InvalidParametersException(
					"GoogleId or key cannot be null");
		}
		try {
			Courses.inst().joinCourse(key, googleId);
		} catch (RegistrationKeyInvalidException e) {
			throw new JoinCourseException(Common.ERRORCODE_INVALID_KEY,
					"Invalid key :" + key);
		} catch (GoogleIDExistsInCourseException e) {
			throw new JoinCourseException(Common.ERRORCODE_ALREADY_JOINED,
					googleId + " is already joined this course");
		} catch (RegistrationKeyTakenException e) {
			throw new JoinCourseException(
					Common.ERRORCODE_KEY_BELONGS_TO_DIFFERENT_USER, googleId
							+ " belongs to a different user");
		}

	}

	public String getKeyForStudent(String courseId, String email) {
		if ((courseId == null) || (email == null)) {
			return null;
		}
		Student student = Accounts.inst().getStudent(courseId, email);

		if(student == null ){
			return null;
		}
		
		long keyLong = Long.parseLong(student.getRegistrationKey().toString());
		return KeyFactory.createKeyString(Student.class.getSimpleName(),
				keyLong);
	}

	public List<CourseData> getCourseListForStudent(String googleId)
			throws EntityDoesNotExistException, InvalidParametersException {

		Common.verifyNotNull(googleId, "Google Id");

		if (getStudentsWithId(googleId) == null) {
			throw new EntityDoesNotExistException("Student with Google ID "
					+ googleId + " does not exist");
		}

		return Courses.inst().getCourseListForStudent(googleId);
	}
	
	public boolean hasStudentSubmittedEvaluation(String courseId, String evaluationName,
			String studentEmail) throws InvalidParametersException {
		Common.verifyNotNull(courseId, "course ID");
		Common.verifyNotNull(evaluationName, "evaluation name");
		Common.verifyNotNull(studentEmail, "student email");
		
		List<SubmissionData> submissions = null;
		try {
			submissions = getSubmissionsFromStudent(courseId, evaluationName, studentEmail);
		} catch (EntityDoesNotExistException e) {
			return false;
		}
		
		if(submissions==null){
			return false;
		}
		
		for(SubmissionData sd: submissions){
			if(sd.points!=Common.POINTS_NOT_SUBMITTED){
				return true;
			}
		}
		return false;
	}

	public List<CourseData> getCourseDetailsListForStudent(String googleId)
			throws EntityDoesNotExistException, InvalidParametersException {
		List<CourseData> courseList = getCourseListForStudent(googleId);
		for (CourseData c : courseList) {
			List<Evaluation> evaluationList = Evaluations.inst()
					.getEvaluationList(c.id);
			for (Evaluation e : evaluationList) {
				EvaluationData ed = new EvaluationData(e);
				log.fine("Adding evaluation " + ed.name + " to course " + c.id);
				if (ed.getStatus() != EvalStatus.AWAITING) {
					c.evaluations.add(ed);
				}
			}
		}
		return courseList;
	}

	public EvalResultData getEvaluationResultForStudent(String courseId,
			String evaluationName, String studentEmail)
			throws EntityDoesNotExistException, InvalidParametersException {

		Common.verifyNotNull(courseId, "course id");
		Common.verifyNotNull(evaluationName, "evaluation name");
		Common.verifyNotNull(studentEmail, "student email");

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
	 * Note: 
	 * @throws EntityAlreadyExistsException
	 * @throws InvalidParametersException is thrown if any of the parameters  
	 * puts the evaluation in an invalid state (e.g., endTime is set before
	 * startTime). However, setting start time to a past time is allowed.
	 */
	public void createEvaluation(EvaluationData evaluation)
			throws EntityAlreadyExistsException, InvalidParametersException {
		Common.verifyNotNull(evaluation, "evaluation");
		evaluation.validate();
		Evaluations.inst().addEvaluation(evaluation.toEvaluation());
	}

	public EvaluationData getEvaluation(String courseId, String evaluationName) {
		Evaluation e = Evaluations.inst().getEvaluation(courseId,
				evaluationName);
		return (e == null ? null : new EvaluationData(e));
	}

	public void editEvaluation(EvaluationData evaluation)
			throws EntityDoesNotExistException, InvalidParametersException {
		Common.verifyNotNull(evaluation, "evaluation");
		evaluation.validate();
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

	public void unpublishEvaluation(String courseId, String evaluationName)
			throws EntityDoesNotExistException {
		Evaluations.inst().unpublishEvaluation(courseId, evaluationName);
	}

	/**
	 * 
	 * @param courseId
	 * @param evaluationName
	 * @return Returns null if any of the parameters is null.
	 * @throws EntityDoesNotExistException
	 *             if the course or the evaluation does not exists.
	 */
	public EvaluationData getEvaluationResult(String courseId,
			String evaluationName) throws EntityDoesNotExistException {
		if ((courseId == null) || (evaluationName == null)) {
			return null;
		}
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

	

	public List<SubmissionData> getSubmissionsFromStudent(String courseId,
			String evaluationName, String reviewerEmail)
			throws EntityDoesNotExistException, InvalidParametersException {
		Common.verifyNotNull(courseId, "course ID");
		Common.verifyNotNull(evaluationName, "evaluation name");
		Common.verifyNotNull(reviewerEmail, "student email");
		List<Submission> submissions = Evaluations.inst()
				.getSubmissionFromStudentList(courseId, evaluationName,
						reviewerEmail);
		if (submissions.size() == 0) {
			Courses.inst().verifyCourseExists(courseId);
			Evaluations.inst().verifyEvaluationExists(courseId, evaluationName);
			Accounts.inst().verifyStudentExists(courseId, reviewerEmail);
		}
		StudentData student = getStudent(courseId, reviewerEmail);
		ArrayList<SubmissionData> returnList = new ArrayList<SubmissionData>();
		for (Submission s : submissions) {
			StudentData reviewee = getStudent(courseId, s.getToStudent());
			if (!isOrphanSubmission(student, reviewee, s)) {
				SubmissionData sd = new SubmissionData(s);
				sd.reviewerName = student.name;
				sd.revieweeName = reviewee.name;
				returnList.add(sd);
			}
		}
		return returnList;
	}
	
	public void sendReminderForEvaluation(String courseId, String evaluationName) {
		
		List<Student> studentList = Courses.inst().getStudentList(courseId);

		// Filter out students who have submitted the evaluation
		Evaluations evaluations = Evaluations.inst();
		Evaluation evaluation = evaluations.getEvaluation(courseId, evaluationName);

		if (evaluation == null) {
			//TODO: throw exception
			return;
		}

		List<Student> studentsToRemindList = new ArrayList<Student>();

		for (Student s : studentList) {
			if (!evaluations.isEvaluationSubmitted(evaluation, s.getEmail())) {
				studentsToRemindList.add(s);
			}
		}

		Date deadline = evaluation.getDeadline();

		evaluations.remindStudents(studentsToRemindList, courseId, evaluationName, deadline);
		
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



	public void editSubmissions(List<SubmissionData> submissionDataList) 
			throws EntityDoesNotExistException, InvalidParametersException{
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

	// only for TMAPI
	public void createTfs(TfsData tfs) throws EntityAlreadyExistsException,
			InvalidParametersException {
		TeamForming.inst().createTeamFormingSession(tfs.toTfs());
	}

	public TfsData getTfs(String courseId) {
		TeamFormingSession tfs = TeamForming.inst().getTeamFormingSession(
				courseId);
		return (tfs == null ? null : new TfsData(tfs));
	}

	public void editTfs(TfsData tfs) throws EntityDoesNotExistException,
			InvalidParametersException {
		TeamForming.inst().editTeamFormingSession(tfs.course, tfs.startTime,
				tfs.endTime, tfs.gracePeriod, tfs.instructions,
				tfs.profileTemplate);
	}

	public void deleteTfs(String courseId) {
		TeamForming.inst().deleteTeamFormingSession(courseId);
	}

	public void renameTeam(String courseId, String originalTeamName,
			String newTeamName) throws EntityDoesNotExistException,
			InvalidParametersException {
		TeamForming.inst().editStudentsTeam(courseId, originalTeamName,
				newTeamName);
	}

	@SuppressWarnings("unused")
	private void ____TEAM_PROFILE_level_methods_____________________________() {
	}

	// only for TMAPI
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
			throws EntityDoesNotExistException, InvalidParametersException {
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

	// only for TMAPI
	public void createStudentAction(StudentActionData studentAction)
			throws InvalidParametersException {
		TeamForming.inst().createTeamFormingLogEntry(
				studentAction.toTeamFormingLog());
	}

	/**
	 * 
	 * @param courseId
	 * @return
	 * @throws EntityDoesNotExistException
	 *             if the course does not exist
	 */
	public List<StudentActionData> getStudentActions(String courseId)
			throws EntityDoesNotExistException {
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
	
	private boolean isOrphanSubmission(StudentData reviewer,
			StudentData reviewee, Submission submission) {
		if (!submission.getTeamName().equals(reviewer.team)) {
			return true;
		}
		if (!submission.getTeamName().equals(reviewee.team)) {
			return true;
		}
		return false;
	}

	private HashMap<String, SubmissionData> getSubmissionsForEvaluation (
			String courseId, String evaluationName)
			throws EntityDoesNotExistException {
		if (getEvaluation(courseId, evaluationName) == null) {
			throw new EntityDoesNotExistException(
					"There is no evaluation named [" + evaluationName
							+ "] under the course [" + courseId + "]");
		}
		// create SubmissionData Hashmap
		List<Submission> submissionsList = Evaluations.inst()
				.getSubmissionList(courseId, evaluationName);
		HashMap<String, SubmissionData> submissionDataList = new HashMap<String, SubmissionData>();
		for (Submission s : submissionsList) {
			SubmissionData sd = new SubmissionData(s);
			submissionDataList.put(sd.reviewer + "->" + sd.reviewee, sd);
		}
		return submissionDataList;
	}

	private TeamEvalResult calculateTeamResult(TeamData team) {
		if (team == null) {
			return null;
		}
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
			s.result.claimedFromStudent = teamResult.claimedToStudents[i][i];
			s.result.claimedToCoord = teamResult.claimedToCoord[i][i];
			s.result.perceivedToStudent = teamResult.perceivedToStudents[i][i];
			s.result.perceivedToCoord = teamResult.perceivedToCoord[i];
			
			//populate incoming and outgoing
			for (int j = 0; j < teamSize; j++) {
				SubmissionData incomingSub = s.result.incoming.get(j);
				int normalizedIncoming = teamResult.perceivedToStudents[i][j];
				incomingSub.normalized = normalizedIncoming;
				incomingSub.normalizedToCoord = teamResult.unbiased[j][i];
				log.finer("Setting normalized incoming of " + s.name + " from "
						+ incomingSub.reviewerName + " to "
						+ normalizedIncoming);

				SubmissionData outgoingSub = s.result.outgoing.get(j);
				int normalizedOutgoing = teamResult.claimedToCoord[i][j];
				outgoingSub.normalized = Common.UNINITIALIZED_INT;
				outgoingSub.normalizedToCoord = normalizedOutgoing;
				log.fine("Setting normalized outgoing of " + s.name + " to "
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
			SubmissionData submissionFromPeer = list.get(
					peer.email + "->" + student.email).getCopy();

			// set names in incoming submission
			submissionFromPeer.revieweeName = student.name;
			submissionFromPeer.reviewerName = peer.name;

			// add incoming submission
			student.result.incoming.add(submissionFromPeer);

			// get outgoing submission to peer
			SubmissionData submissionToPeer = list.get(
					student.email + "->" + peer.email).getCopy();

			// set names in outgoing submission
			submissionToPeer.reviewerName = student.name;
			submissionToPeer.revieweeName = peer.name;

			// add outgoing submission
			student.result.outgoing.add(submissionToPeer);

		}
	}

	protected SubmissionData getSubmission(String courseId, String evaluationName,
			String reviewerEmail, String revieweeEmail) {
		Submission submission = Evaluations.inst().getSubmission(courseId,
				evaluationName, reviewerEmail, revieweeEmail);
		return (submission == null ? null : new SubmissionData(submission));
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
		return getStudent(student.course, student.email) != null;
	}

	

}
