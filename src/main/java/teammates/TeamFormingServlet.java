package teammates;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.exception.EntityAlreadyExistsException;
import teammates.exception.EntityDoesNotExistException;
import teammates.jdo.CourseDetailsForStudent;
import teammates.manager.Accounts;
import teammates.manager.Courses;
import teammates.manager.TeamForming;
import teammates.persistent.Course;
import teammates.persistent.Student;
import teammates.persistent.TeamFormingLog;
import teammates.persistent.TeamFormingSession;
import teammates.persistent.TeamProfile;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;

@SuppressWarnings("serial")
public class TeamFormingServlet extends HttpServlet {
	private HttpServletRequest req;
	private HttpServletResponse resp;

	// OPERATIONS	
	private static final String OPERATION_COORDINATOR_CHECKTEAMEXISTS = "coordinator_checkteamexists";
	private static final String OPERATION_COORDINATOR_CREATETEAMFORMINGSESSION = "coordinator_createteamformingsession";
	private static final String OPERATION_COORDINATOR_CREATETEAMPROFILE = "coordinator_createteamprofile";
	private static final String OPERATION_COORDINATOR_GETSTUDENTSOFCOURSETEAM = "coordinator_getstudentsofcourseteam";
	private static final String OPERATION_COORDINATOR_GETSTUDENTSWITHOUTTEAM = "coordinator_getstudentswithoutteam";
	private static final String OPERATION_COORDINATOR_GETTEAMSOFCOURSE = "coordinator_getteamsofcourse";
	private static final String OPERATION_COORDINATOR_GETTEAMDETAIL = "coordinator_getteamdetail";
	private static final String OPERATION_COORDINATOR_GETTEAMFORMINGSESSION = "coordinator_getteamformingsession";
	private static final String OPERATION_COORDINATOR_GETTEAMFORMINGSESSIONLIST = "coordinator_getteamformingsessionlist";
	private static final String OPERATION_COORDINATOR_GETTEAMFORMINGSESSIONLOG = "coordinator_getteamformingsessionlog";
	private static final String OPERATION_COORDINATOR_DELETETEAMFORMINGSESSION = "coordinator_deleteteamformingsession";
	private static final String OPERATION_COORDINATOR_DELETETEAMPROFILES = "coordinator_deleteteamprofiles";
	private static final String OPERATION_COORDINATOR_DELETETEAMPROFILE = "coordinator_deleteteamprofile";
	private static final String OPERATION_COORDINATOR_REMINDSTUDENTS_TEAMFORMING = "coordinator_remindstudentsteamforming";
	private static final String OPERATION_COORDINATOR_EDITSTUDENTTEAM = "coordinator_editstudentteam";
	private static final String OPERATION_COORDINATOR_EDITTEAMFORMINGSESSION = "coordinator_editteamformingsession";
	private static final String OPERATION_COORDINATOR_EDITTEAMPROFILE = "coordinator_editteamprofile";
	private static final String OPERATION_COORDINATOR_INFORMSTUDENTSOFTEAMFORMINGSESSIONCHANGES = "coordinator_informstudentsofteamformingsessionchanges";
	private static final String OPERATION_CREATETEAMWITHSTUDENT = "createteamwithstudent";
	private static final String OPERATION_COORDINATOR_PUBLISHTEAMFORMINGRESULTS = "coordinator_publishteamformingresults";
	
	private static final String OPERATION_ADDSTUDENTTOTEAM = "addstudenttoteam";
	private static final String OPERATION_EDITSTUDENTTEAM = "editstudentteam";
	private static final String OPERATION_ENTERLOG = "enterlog";
	private static final String OPERATION_GETCURRENTUSER = "getcurrentuser";
	private static final String OPERATION_GETSTUDENTTEAMNAME = "getstudentteamname";
	private static final String OPERATION_JOINTEAM = "jointeam";
	private static final String OPERATION_LEAVETEAM = "leaveteam";
	private static final String OPERATION_SHOW_TEAMFORMING = "coordinator_teamforming";
	private static final String OPERATION_STUDENT_EDITPROFILE = "student_editprofile";
	private static final String OPERATION_STUDENT_EDITTEAMPROFILE = "student_editteamprofile";
	private static final String OPERATION_STUDENT_GETCOURSESTUDENTDETAIL = "student_getcoursestudentdetail";
	
	// PARAMETERS
	private static final String COURSE_ID = "courseid";
	private static final String COURSE_COORDINATORNAME = "coordinatorname";
	private static final String COURSE_NAME = "coursename";
	private static final String EMAIL = "email";
	private static final String NICKNAME = "nickname";
	private static final String STUDENT_ID = "studentid";
	private static final String STUDENT_EMAIL = "studentemail";
	private static final String STUDENT_NAME = "studentname";
	private static final String STUDENT_PROFILE_DETAIL = "studentprofiledetail";
	private static final String STUDENT_PROFILE_SUMMARY = "studentprofilesummary";
	private static final String STUDENT_TEAMMATE = "teammate";
	private static final String STUDENT_TEAMMATES = "teammates";
	private static final String STUDENT_TEAMNAME = "studentteamname";

	private static final String TEAMFORMING_ACTIVATED = "activated";
	private static final String TEAMFORMING_DEADLINE = "deadline";
	private static final String TEAMFORMING_DEADLINETIME = "deadlinetime";
	private static final String TEAMFORMING_GRACEPERIOD = "graceperiod";
	private static final String TEAMFORMING_INSTRUCTIONS = "instr";
	private static final String TEAMFORMING_START = "start";
	private static final String TEAMFORMING_STARTTIME = "starttime";
	private static final String TEAMFORMING_TIMEZONE = "timezone";	
	private static final String TEAMFORMING_PROFILETEMPLATE = "profile_template";
	private static final String TEAM_NAME = "teamName";
	private static final String TEAM_PROFILE = "teamProfile";	
	private static final String TEAM = "team";
	private static final String TIME = "time";
	private static final String MESSAGE = "message";

	// MESSAGES
	private static final String MSG_STUDENTPROFILE_SAVED = "student profile saved";
	private static final String MSG_STUDENTJOINTEAM = "student has joined the team";
	private static final String MSG_TEAMCREATEDWITHSTUDENT = "team has been created with the student";
	private static final String MSG_TEAMFORMINGSESSION_ADDED = "team forming session added";
	private static final String MSG_TEAMFORMINGSESSION_EXISTS = "team forming session exists";
	private static final String MSG_TEAMPROFILE_SAVED = "team profile saved";
	private static final String MSG_TEAMPROFILE_EXISTS = "team profile exists";
	private static final String MSG_STATUS_OPENING = "<status>";
	private static final String MSG_STATUS_CLOSING = "</status>";
	private static final String MSG_STUDENTADDEDTOTEAM = "student is added to the team";
	private static final String MSG_TEAMFORMINGSESSION_EDITED = "team forming session edited";
	private static final String MSG_TEAMFORMINGSESSION_NULL = "team forming session null";
	private static final String MSG_TEAMFORMINGSESSION_REMAINED = "team forming session remained";

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
		
		if(operation.equals(OPERATION_COORDINATOR_CREATETEAMFORMINGSESSION)){
			coordinatorCreateTeamFormingSession();
		}
		
		else if (operation.equals(OPERATION_COORDINATOR_CHECKTEAMEXISTS)) {
			coordinatorCheckTeamExists();
		}
		
		else if (operation.equals(OPERATION_COORDINATOR_CREATETEAMPROFILE)) {
			coordinatorCreateTeamProfile();
		}
		
		else if (operation.equals(OPERATION_COORDINATOR_DELETETEAMFORMINGSESSION)) {
			coordinatorDeleteTeamFormingSession();
		}
		
		else if (operation.equals(OPERATION_COORDINATOR_DELETETEAMPROFILES)) {
			coordinatorDeleteTeamProfiles();
		}
		
		else if (operation.equals(OPERATION_COORDINATOR_DELETETEAMPROFILE)) {
			try {
				coordinatorDeleteTeamProfile();
			} catch (EntityDoesNotExistException e) {
				e.printStackTrace();
			}
		}
		
		else if (operation.equals(OPERATION_COORDINATOR_EDITSTUDENTTEAM)) {
			coordinatorEditStudentTeam();
		}
		
		else if (operation.equals(OPERATION_ADDSTUDENTTOTEAM)) {
			addStudentToTeam();
		}
		
		else if (operation.equals(OPERATION_CREATETEAMWITHSTUDENT)) {
			createTeamWithStudent();
		}
		
		else if (operation.equals(OPERATION_EDITSTUDENTTEAM)) {
			coordinatorEditStudentTeam();
		}
		
		else if (operation.equals(OPERATION_COORDINATOR_EDITTEAMFORMINGSESSION)) {
			coordinatorEditTeamFormingSession();
		}
		
		else if (operation.equals(OPERATION_COORDINATOR_EDITTEAMPROFILE)) {
			try {
				coordinatorEditTeamProfile();
			} catch (EntityDoesNotExistException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if (operation.equals(OPERATION_COORDINATOR_GETSTUDENTSOFCOURSETEAM)) {
			coordinatorGetStudentsOfCourseTeam();
		}
		
		else if (operation.equals(OPERATION_COORDINATOR_GETSTUDENTSWITHOUTTEAM)) {
			coordinatorGetStudentsWithoutTeam();
		}
		
		else if (operation.equals(OPERATION_COORDINATOR_GETTEAMSOFCOURSE)) {
			coordinatorGetTeamsOfCourse();
		}
		
		else if (operation.equals(OPERATION_COORDINATOR_GETTEAMDETAIL)) {
			coordinatorGetTeamDetail();
		}
		
		else if (operation.equals(OPERATION_COORDINATOR_GETTEAMFORMINGSESSION)) {
			coordinatorGetTeamFormingSession();
		}
		
		else if (operation.equals(OPERATION_COORDINATOR_GETTEAMFORMINGSESSIONLIST)) {
			coordinatorGetTeamFormingSessionList();
		}
		
		else if (operation.equals(OPERATION_COORDINATOR_GETTEAMFORMINGSESSIONLOG)) {
			coordinatorGetTeamFormingSessionLog();
		}
		
		else if (operation
				.equals(OPERATION_COORDINATOR_INFORMSTUDENTSOFTEAMFORMINGSESSIONCHANGES)) {
			coordinatorInformStudentsOfTeamFormingSessionChanges();
		}
		
		else if (operation.equals(OPERATION_COORDINATOR_PUBLISHTEAMFORMINGRESULTS)) {
			coordinatorPublishTeamFormingResults();
		}

		else if (operation.equals(OPERATION_COORDINATOR_REMINDSTUDENTS_TEAMFORMING)) {
			coordinatorRemindStudentsOfTeamForming();
		}
		
		else if (operation.equals(OPERATION_ENTERLOG)) {
			enterTeamFormingLog();
		}
		
		else if (operation.equals(OPERATION_GETCURRENTUSER)) {
			getCurrentUser();
		}
		
		else if (operation.equals(OPERATION_GETSTUDENTTEAMNAME)) {
			getStudentTeamName();
		}
		
		else if (operation.equals(OPERATION_JOINTEAM)) {
			studentJoinTeam();
		}
		
		else if (operation.equals(OPERATION_LEAVETEAM)){
			studentJoinTeam();
		}
		
		else if (operation.equals(OPERATION_SHOW_TEAMFORMING)){
			coordinatorTeamForming();
		}
		
		else if (operation.equals(OPERATION_STUDENT_EDITPROFILE)){
			studentEditProfile();
		}
		
		else if (operation.equals(OPERATION_STUDENT_EDITTEAMPROFILE)){
			try {
				coordinatorEditTeamProfile();
			} catch (EntityDoesNotExistException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if (operation.equals(OPERATION_STUDENT_GETCOURSESTUDENTDETAIL)){
			studentGetCourseStudentDetail();
		}
		
		else {
			System.out.println("unknown command");
		}
		// Clean-up
		this.resp.flushBuffer();
	}
	
//	public void doGet(HttpServletRequest req, HttpServletResponse resp)
//			throws IOException, ServletException {
//		
//		this.resp = resp;
//		this.req = req;
//		
//		resp.setContentType("text/html");
//		resp.getWriter().println("<html><body>Hello there!</body></html>");
//		resp.flushBuffer();
//	}
	
	private void addStudentToTeam() throws IOException {
		String courseId = req.getParameter("courseId");
		String teamName = req.getParameter("teamName");
		String newStudentEmail = req.getParameter("email");
		
		// Add the team forming session		
		TeamForming teamForming = TeamForming.inst();
		teamForming.addStudentToTeam(courseId, teamName, newStudentEmail);
		
		resp.getWriter().write(
				MSG_STATUS_OPENING + MSG_STUDENTADDEDTOTEAM + MSG_STATUS_CLOSING);
	}
	
	private void createTeamWithStudent() throws IOException {
		String courseId = req.getParameter("courseId");
		String courseName = req.getParameter("courseName");
		String newStudentEmail = req.getParameter("studentToAddEmail");
		String currentStudentEmail = req.getParameter("currentStudentEmail");
		String currentStudentNickName = req.getParameter("currentStudentNickname");
		
		// Add the team forming session		
		TeamForming teamForming = TeamForming.inst();
		try{
		teamForming.createTeamWithStudent(courseId, courseName, newStudentEmail, currentStudentEmail, currentStudentNickName);
		}
		catch (EntityAlreadyExistsException e){
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_TEAMPROFILE_EXISTS + MSG_STATUS_CLOSING);
		}
		
		resp.getWriter().write(
				MSG_STATUS_OPENING + MSG_TEAMCREATEDWITHSTUDENT + MSG_STATUS_CLOSING);		
	}
	
	private void coordinatorTeamForming() throws IOException, ServletException {
		Accounts accounts = Accounts.inst();
		resp.getWriter().write(
				"<url><![CDATA[" + accounts.getLoginPage("/coordinator.jsp?teamforming")
						+ "]]></url>");
	}
	
	private void coordinatorCheckTeamExists() throws IOException {
		String courseID = req.getParameter("courseId");
		String teamName = req.getParameter("teamName");
		
		TeamForming teamForming = TeamForming.inst();
		int newTeamNameExists = 0;
		List<TeamProfile> teamProfiles = teamForming.getTeamProfiles(courseID);
		
		for(int i=0;i<teamProfiles.size(); i++)
		{
			if(teamProfiles.get(i).getTeamName().equalsIgnoreCase(teamName.trim()))
				newTeamNameExists = 1;
		}
		if (newTeamNameExists==1)
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_TEAMPROFILE_EXISTS + MSG_STATUS_CLOSING);
		else
			resp.getWriter().write(
				MSG_STATUS_OPENING + MSG_TEAMPROFILE_SAVED + MSG_STATUS_CLOSING);
	}
	
	private void coordinatorCreateTeamFormingSession() throws IOException {
		String courseID = req.getParameter(COURSE_ID);
		String startDate = req.getParameter(TEAMFORMING_START);
		int startTime = Integer.parseInt(req
				.getParameter(TEAMFORMING_STARTTIME));
		String deadlineDate = req.getParameter(TEAMFORMING_DEADLINE);
		int deadlineTime = Integer.parseInt(req
				.getParameter(TEAMFORMING_DEADLINETIME));
		double timeZone = Double.parseDouble(req
				.getParameter(TEAMFORMING_TIMEZONE));
		int gracePeriod = Integer.parseInt(req
				.getParameter(TEAMFORMING_GRACEPERIOD));
		String instructions = req.getParameter(TEAMFORMING_INSTRUCTIONS);
		String profileTemplate = req.getParameter(TEAMFORMING_PROFILETEMPLATE);
		
		Date start = Common.convertToDate(startDate, startTime);
		Date deadline = Common.convertToDate(deadlineDate, deadlineTime);
		
		// Add the team forming session		
		TeamForming teamForming = TeamForming.inst();
		
		try{
			teamForming
			.createTeamFormingSession(courseID, start, deadline, timeZone, gracePeriod, 
					instructions, profileTemplate);
			
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_TEAMFORMINGSESSION_ADDED + MSG_STATUS_CLOSING);
		}
		
		catch (EntityAlreadyExistsException e){
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_TEAMFORMINGSESSION_EXISTS + MSG_STATUS_CLOSING);
		}
	}
	
	private void coordinatorCreateTeamProfile() throws IOException {
		String courseId = req.getParameter("courseId");
		String courseName = req.getParameter("courseName");
		String teamName = req.getParameter(TEAM_NAME);
		Text teamProfile = new Text(req.getParameter(TEAM_PROFILE));
		
		// Add the team forming session		
		TeamForming teamForming = TeamForming.inst();
		
		try{
			teamForming.createTeamProfile(courseId, courseName, teamName, teamProfile);
			
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_TEAMPROFILE_SAVED + MSG_STATUS_CLOSING);
		}
		
		catch (EntityAlreadyExistsException e){
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_TEAMPROFILE_EXISTS + MSG_STATUS_CLOSING);
		}
	}
	
	private void coordinatorDeleteTeamFormingSession() {
		String courseID = req.getParameter(COURSE_ID);
		TeamForming teamForming = TeamForming.inst();
		teamForming.deleteTeamFormingSession(courseID);
		
		if(teamForming.getTeamFormingLogList(courseID)!=null)
			teamForming.deleteTeamFormingLog(courseID);
	}
	
	private void coordinatorDeleteTeamProfiles() {
		String courseID = req.getParameter(COURSE_ID);
		TeamForming teamForming = TeamForming.inst();
		teamForming.deleteTeamProfiles(courseID);
	}
	
	private void coordinatorDeleteTeamProfile() throws EntityDoesNotExistException {
		String courseID = req.getParameter(COURSE_ID);
		String teamName = req.getParameter(TEAM_NAME);
		TeamForming teamForming = TeamForming.inst();
		teamForming.deleteTeamProfile(courseID, teamName);
	}
	
	private void coordinatorEditStudentTeam() throws IOException {
		String courseId = req.getParameter("courseId");
		String teamName = req.getParameter("oldteamname");
		String newTeamName = req.getParameter(TEAM_NAME);
		
		// Add the team forming session		
		TeamForming teamForming = TeamForming.inst();
		
		//update student teamname info
		teamForming.editStudentsTeam(courseId, teamName, newTeamName);
	}
	
	private void coordinatorEditTeamFormingSession() throws IOException {
		String courseID = req.getParameter(COURSE_ID);
		String newStartDate = req.getParameter(TEAMFORMING_START);
		int newStartTime = Integer.parseInt(req
				.getParameter(TEAMFORMING_STARTTIME));
		String newDeadlineDate = req.getParameter(TEAMFORMING_DEADLINE);
		int newDeadlineTime = Integer.parseInt(req
				.getParameter(TEAMFORMING_DEADLINETIME));
		int newGracePeriod = Integer.parseInt(req
				.getParameter(TEAMFORMING_GRACEPERIOD));
		String newInstructions = req.getParameter(TEAMFORMING_INSTRUCTIONS);
		String newProfileTemplate = req.getParameter(TEAMFORMING_PROFILETEMPLATE);

		Date newStart = Common.convertToDate(newStartDate, newStartTime);
		Date newDeadline = Common.convertToDate(newDeadlineDate, newDeadlineTime);
		
		TeamForming teamForming = TeamForming.inst();

		boolean edited = teamForming.editTeamFormingSession(courseID, newStart,
			newDeadline, newGracePeriod, newInstructions, newProfileTemplate);

		if (edited) {
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_TEAMFORMINGSESSION_EDITED
							+ MSG_STATUS_CLOSING);
		}

		else {
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_TEAMFORMINGSESSION_REMAINED
							+ MSG_STATUS_CLOSING);
		}
	}
	
	private void coordinatorEditTeamProfile() throws IOException, EntityDoesNotExistException {
		String courseId = req.getParameter("courseId");
		String courseName = req.getParameter("courseName");
		String teamName = req.getParameter("oldteamname");
		String newTeamName = req.getParameter(TEAM_NAME);
		Text newTeamProfile = new Text(req.getParameter(TEAM_PROFILE));
		
		// Add the team forming session		
		TeamForming teamForming = TeamForming.inst();

		boolean edited = teamForming
				.editTeamProfile(courseId, courseName, teamName, newTeamName, newTeamProfile);

		if (edited) {
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_TEAMPROFILE_SAVED + MSG_STATUS_CLOSING);
		}

		else {
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_TEAMPROFILE_EXISTS + MSG_STATUS_CLOSING);
		}
	}
	
	private void coordinatorGetStudentsOfCourseTeam() throws IOException {
		String courseId = req.getParameter("courseId");
		String teamName = req.getParameter(TEAM_NAME);
		
		// Add the team forming session		
		TeamForming teamForming = TeamForming.inst();
		List<Student> students = teamForming.getStudentsOfCourseTeam(courseId, teamName);
		
		resp.getWriter().write(
				"<students>"
						+ parseCoordinatorGetStudentsOfCourseTeamToXML(students).toString()
						+ "</students>");
	}
	
	private void coordinatorGetStudentsWithoutTeam()throws IOException {
		String courseId = req.getParameter("courseId");
		
		// Add the team forming session		
		TeamForming teamForming = TeamForming.inst();
		List<Student> students = teamForming.getStudentsOfCourseTeam(courseId, "");

		resp.getWriter().write(
				"<students>"
						+ parseCoordinatorGetStudentsWithoutTeamToXML(students).toString()
						+ "</students>");
	}
	
	private void coordinatorGetTeamFormingSession() throws IOException {
		String courseID = req.getParameter("courseid");
		Date dummyDeadline = null;
		
		TeamForming teamForming = TeamForming.inst();
		TeamFormingSession teamFormingSession = teamForming.getTeamFormingSession(courseID, dummyDeadline);
		
		if(teamFormingSession!=null){
		resp.getWriter().write(
				"<teamformingsession>"
						+ parseCoordinatorTeamFormingSesssionToXML(
								teamFormingSession).toString()
						+ "</teamformingsession>");
		}
		else
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_TEAMFORMINGSESSION_NULL + MSG_STATUS_CLOSING);
	}
	
	private void coordinatorGetTeamFormingSessionLog() throws IOException {
		String courseID = req.getParameter(COURSE_ID);
		
		TeamForming teamForming = TeamForming.inst();
		List<TeamFormingLog> teamFormingLog = teamForming
				.getTeamFormingSessionLog(courseID);	
		
		resp.getWriter().write(
				"<teamforminglogs>"
						+ parseCoordinatorTeamFormingSesssionLogToXML(
								teamFormingLog).toString()
						+ "</teamforminglogs>");
	}
	
	private void coordinatorGetTeamFormingSessionList() throws IOException {
		Accounts accounts = Accounts.inst();
		String googleID = accounts.getUser().getNickname().toLowerCase();
		
		Courses courses = Courses.inst();
		List<Course> courseList = courses.getCoordinatorCourseList(googleID);
		
		TeamForming teamForming = TeamForming.inst();
		List<TeamFormingSession> teamFormingSessionList = teamForming
				.getTeamFormingSessionList(courseList);		
			
		resp.getWriter().write(
				"<teamformingsessions>"
						+ parseCoordinatorTeamFormingSesssionListToXML(
								teamFormingSessionList).toString()
						+ "</teamformingsessions>");
	}
	
	private void coordinatorGetTeamDetail() throws IOException {
		String courseId = req.getParameter("courseId");
		String teamName = req.getParameter(TEAM_NAME);
		
		// Add the team forming session		
		TeamForming teamForming = TeamForming.inst();
		TeamProfile teamDetail = teamForming.getTeamProfile(courseId, teamName);
		
		resp.getWriter().write(
				"<teamdetail>"
						+ parseCoordinatorTeamDetailToXML(teamDetail).toString()
						+ "</teamdetail>");
	}
	
	private void coordinatorGetTeamsOfCourse() throws IOException {
		String courseId = req.getParameter("courseId");
		int found=0;
		
		// Add the team forming session		
		TeamForming teamForming = TeamForming.inst();
		List<String> teams = teamForming.getTeamsOfCourse(courseId);
		List<String> teamsCopy = new ArrayList<String>();
		
		if(teams!=null){
			for(int i=0; i<teams.size(); i++)
			{
				for(int j=0; j<teamsCopy.size(); j++)
				{
					if(teamsCopy.get(j).equals(teams.get(i)))
						found = 1;
				}
				if(found==0)
					teamsCopy.add(teams.get(i));
				found=0;
			}
		}
		
		resp.getWriter().write(
				"<teams>"
						+ parseCoordinatorGetTeamsOfCourseToXML(
								teamsCopy).toString()
						+ "</teams>");
	}
	
	private void coordinatorInformStudentsOfTeamFormingSessionChanges() {
		String courseID = req.getParameter(COURSE_ID);

		Courses courses = Courses.inst();
		List<Student> studentList = courses.getStudentList(courseID);

		TeamForming teamForming = TeamForming.inst();
		TeamFormingSession teamFormingSession = teamForming.getTeamFormingSession(courseID, null);		
		
		Date deadline = teamFormingSession.getDeadline();
		teamForming.informStudentsOfChanges(studentList, courseID, deadline);
	}
	
	private void coordinatorPublishTeamFormingResults() {
		String courseID = req.getParameter(COURSE_ID);

		Courses courses = Courses.inst();
		List<Student> studentList = courses.getStudentList(courseID);

		TeamForming teamForming = TeamForming.inst();
		teamForming.publishTeamFormingResults(studentList, courseID);
	}
	
	private void coordinatorRemindStudentsOfTeamForming() {
		String courseID = req.getParameter(COURSE_ID);

		Courses courses = Courses.inst();
		List<Student> studentList = courses.getStudentList(courseID);

		List<Student> studentsToRemindList = new ArrayList<Student>();

		for (Student s : studentList) {
			String t = s.getTeamName();
			t.replaceAll("\\s+", " ");
			if(t.equals("")||t.equals(" "))
				studentsToRemindList.add(s);
		}

		Date dummyDeadline = null;
		TeamForming teamForming = TeamForming.inst();
		TeamFormingSession teamFormingSession = teamForming.getTeamFormingSession(courseID, dummyDeadline);
		
		Date deadline = teamFormingSession.getDeadline();

		teamForming.remindStudents(studentsToRemindList, courseID, deadline);
	}
	
	private void enterTeamFormingLog() {
		String courseID = req.getParameter(COURSE_ID);
		String nowDate = req.getParameter("nowdate");
		int nowTime = Integer.parseInt(req.getParameter("nowtime"));
		String studentName = req.getParameter("name");
		String studentEmail = req.getParameter("email");
		Text message = new Text(req.getParameter("message"));
		
		Date now = Common.convertToExactDateTime(nowDate, nowTime);
		
		TeamForming teamForming = TeamForming.inst();
		teamForming.enterTeamFormingLog(courseID, now, studentName, studentEmail, message);
	}
	
	private void getCurrentUser() throws IOException{
		Accounts accounts = Accounts.inst();
		User currentUser = accounts.getUser();
		resp.getWriter().write(
				"<currentuser>"
						+ parseCurrentUserToXML(currentUser).toString()
						+ "</currentuser>");
	}
	
	private void getStudentTeamName() throws IOException{
		String courseID = req.getParameter("courseId");
		String email = req.getParameter("email");
		
		TeamForming teamForming = TeamForming.inst();
		Student currentStudent = teamForming.getStudent(courseID, email);
		
		resp.getWriter().write(
				"<student>" + parseStudentTeamNameToXML(currentStudent).toString()
						+ "</student>");
	}
	
	private StringBuffer parseCurrentUserToXML(User currentUser){
		StringBuffer sb = new StringBuffer();
		sb.append("<" + EMAIL + "><![CDATA[" + currentUser.getEmail()
				+ "]]></" + EMAIL + ">");
		sb.append("<" + NICKNAME + "><![CDATA[" + currentUser.getNickname()
				+ "]]></" + NICKNAME + ">");
		return sb;
	}
	
	private StringBuffer parseCoordinatorGetStudentsWithoutTeamToXML(List<Student> students){
		StringBuffer sb = new StringBuffer();
		for(int loop=0; loop<students.size(); loop++){
			sb.append("<student>");
			sb.append("<" + STUDENT_ID + "><![CDATA[" + students.get(loop).getID()
					+ "]]></" + STUDENT_ID + ">");
			sb.append("<" + STUDENT_NAME + "><![CDATA[" + students.get(loop).getName()
					+ "]]></" + STUDENT_NAME + ">");
			sb.append("<" + STUDENT_EMAIL + "><![CDATA[" + students.get(loop).getEmail()
					+ "]]></" + STUDENT_EMAIL + ">");
			sb.append("<" + STUDENT_PROFILE_SUMMARY + "><![CDATA[" + students.get(loop).getProfileSummary()
					+ "]]></" + STUDENT_PROFILE_SUMMARY + ">");
			if(students.get(loop).getProfileDetail()==null)
			sb.append("<" + STUDENT_PROFILE_DETAIL + "><![CDATA[" + students.get(loop).getProfileDetail()
					+ "]]></" + STUDENT_PROFILE_DETAIL + ">");
			else
			sb.append("<" + STUDENT_PROFILE_DETAIL + "><![CDATA[" + students.get(loop).getProfileDetail().getValue()
						+ "]]></" + STUDENT_PROFILE_DETAIL + ">");
			sb.append("</student>");
		}
		return sb;
	}
	
	private StringBuffer parseCoordinatorGetStudentsOfCourseTeamToXML(List<Student> students){
		StringBuffer sb = new StringBuffer();
		for(int loop=0; loop<students.size(); loop++){
			sb.append("<student>");
			sb.append("<" + COURSE_ID + "><![CDATA[" + students.get(loop).getCourseID()
					+ "]]></" + COURSE_ID + ">");
			sb.append("<" + STUDENT_ID + "><![CDATA[" + students.get(loop).getID()
					+ "]]></" + STUDENT_ID + ">");
			sb.append("<" + STUDENT_NAME + "><![CDATA[" + students.get(loop).getName()
					+ "]]></" + STUDENT_NAME + ">");
			sb.append("<" + STUDENT_EMAIL + "><![CDATA[" + students.get(loop).getEmail()
					+ "]]></" + STUDENT_EMAIL + ">");
			sb.append("<" + STUDENT_PROFILE_SUMMARY + "><![CDATA[" + students.get(loop).getProfileSummary()
					+ "]]></" + STUDENT_PROFILE_SUMMARY + ">");
			if(students.get(loop).getProfileDetail()==null)
				sb.append("<" + STUDENT_PROFILE_DETAIL + "><![CDATA[" + students.get(loop).getProfileDetail()
					+ "]]></" + STUDENT_PROFILE_DETAIL + ">");
			else
				sb.append("<" + STUDENT_PROFILE_DETAIL + "><![CDATA[" + students.get(loop).getProfileDetail().getValue()
						+ "]]></" + STUDENT_PROFILE_DETAIL + ">");
			sb.append("</student>");
		}
		return sb;		
	}
	
	private StringBuffer parseCoordinatorGetTeamsOfCourseToXML(List<String> teams){
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<teams.size(); i++)
			sb.append("<" + TEAM + "><![CDATA[" + teams.get(i)
					+ "]]></" + TEAM + ">");
		return sb;
	}
	
	private StringBuffer parseCoordinatorTeamDetailToXML(TeamProfile teamDetail) {
		StringBuffer sb = new StringBuffer();
		if(teamDetail!=null){
			sb.append("<" + COURSE_ID + "><![CDATA[" + teamDetail.getCourseID()
					+ "]]></" + COURSE_ID + ">");
			sb.append("<" + COURSE_NAME + "><![CDATA[" + teamDetail.getCourseName()
					+ "]]></" + COURSE_NAME + ">");
			sb.append("<" + TEAM_NAME + "><![CDATA[" + teamDetail.getTeamName()
					+ "]]></" + TEAM_NAME + ">");
			sb.append("<" + TEAM_PROFILE + "><![CDATA[" + teamDetail.getTeamProfile().getValue()
					+ "]]></" + TEAM_PROFILE + ">");
		}
		return sb;
	}

	private StringBuffer parseCoordinatorTeamFormingSesssionToXML(TeamFormingSession teamFormingSession) {
		StringBuffer sb = new StringBuffer();
		sb.append("<" + COURSE_ID + "><![CDATA[" + teamFormingSession.getCourseID()
				+ "]]></" + COURSE_ID + ">");
		sb.append("<" + TEAMFORMING_START + "><![CDATA["
				+ DateFormat.getDateTimeInstance().format(teamFormingSession.getStart())
				+ "]]></" + TEAMFORMING_START + ">");
		sb.append("<" + TEAMFORMING_DEADLINE + "><![CDATA["
				+ DateFormat.getDateTimeInstance().format(teamFormingSession.getDeadline())
				+ "]]></" + TEAMFORMING_DEADLINE + ">");
		sb.append("<" + TEAMFORMING_TIMEZONE + "><![CDATA["
				+ teamFormingSession.getTimeZone() + "]]></" + TEAMFORMING_TIMEZONE + ">");
		sb.append("<" + TEAMFORMING_GRACEPERIOD + "><![CDATA["
				+ teamFormingSession.getGracePeriod() + "]]></" + TEAMFORMING_GRACEPERIOD
				+ ">");
		sb.append("<" + TEAMFORMING_ACTIVATED + "><![CDATA["
				+ teamFormingSession.isActivated() + "]]></" + TEAMFORMING_ACTIVATED + ">");
		sb.append("<" + TEAMFORMING_INSTRUCTIONS + "><![CDATA["
				+ teamFormingSession.getInstructions() + "]]></" + TEAMFORMING_INSTRUCTIONS
				+ ">");
		sb.append("<" + TEAMFORMING_PROFILETEMPLATE + "><![CDATA["
				+ teamFormingSession.getProfileTemplate() + "]]></" + TEAMFORMING_PROFILETEMPLATE
				+ ">");
		return sb;
	}
	
	private StringBuffer parseCoordinatorTeamFormingSesssionListToXML(
			List<TeamFormingSession> teamFormingSessionList) {
		StringBuffer sb = new StringBuffer();
		for (TeamFormingSession e : teamFormingSessionList) {
			sb.append("<teamformingsession>");

			sb.append("<" + COURSE_ID + "><![CDATA[" + e.getCourseID()
					+ "]]></" + COURSE_ID + ">");
			sb.append("<" + TEAMFORMING_START + "><![CDATA["
					+ DateFormat.getDateTimeInstance().format(e.getStart())
					+ "]]></" + TEAMFORMING_START + ">");
			sb.append("<" + TEAMFORMING_DEADLINE + "><![CDATA["
					+ DateFormat.getDateTimeInstance().format(e.getDeadline())
					+ "]]></" + TEAMFORMING_DEADLINE + ">");
			sb.append("<" + TEAMFORMING_TIMEZONE + "><![CDATA["
					+ e.getTimeZone() + "]]></" + TEAMFORMING_TIMEZONE + ">");
			sb.append("<" + TEAMFORMING_GRACEPERIOD + "><![CDATA["
					+ e.getGracePeriod() + "]]></" + TEAMFORMING_GRACEPERIOD
					+ ">");
			sb.append("<" + TEAMFORMING_ACTIVATED + "><![CDATA["
					+ e.isActivated() + "]]></" + TEAMFORMING_ACTIVATED + ">");
			sb.append("<" + TEAMFORMING_INSTRUCTIONS + "><![CDATA["
					+ e.getInstructions() + "]]></" + TEAMFORMING_INSTRUCTIONS
					+ ">");
			sb.append("<" + TEAMFORMING_PROFILETEMPLATE + "><![CDATA["
					+ e.getProfileTemplate() + "]]></" + TEAMFORMING_PROFILETEMPLATE
					+ ">");

			sb.append("</teamformingsession>");
		}
		return sb;
	}
	
	private StringBuffer parseCoordinatorTeamFormingSesssionLogToXML(
			List<TeamFormingLog> teamFormingLog) {
		StringBuffer sb = new StringBuffer();
		for (TeamFormingLog e : teamFormingLog) {
			sb.append("<teamforminglog>");

			sb.append("<" + COURSE_ID + "><![CDATA[" + e.getCourseID()
					+ "]]></" + COURSE_ID + ">");
			sb.append("<" + STUDENT_EMAIL + "><![CDATA["
					+ e.getStudentEmail() + "]]></" + STUDENT_EMAIL + ">");
			sb.append("<" + STUDENT_NAME + "><![CDATA["
					+ e.getStudentName() + "]]></" + STUDENT_NAME + ">");
			sb.append("<" + MESSAGE + "><![CDATA["
					+ e.getMessage().getValue() + "]]></" + MESSAGE
					+ ">");
			sb.append("<" + TIME + "><![CDATA["
					+ DateFormat.getDateTimeInstance().format(e.getTime())
					+ "]]></" + TIME + ">");

			sb.append("</teamforminglog>");
		}
		return sb;

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
		sb.append("<" + STUDENT_PROFILE_SUMMARY + "><![CDATA["
				+ courseDetails.getProfileSummary() + "]]></" + STUDENT_PROFILE_SUMMARY
				+ ">");
		if(courseDetails.getProfileDetail()==null)
			sb.append("<" + STUDENT_PROFILE_DETAIL + "><![CDATA["
					+ courseDetails.getProfileDetail() + "]]></" + STUDENT_PROFILE_DETAIL
					+ ">");
		else
			sb.append("<" + STUDENT_PROFILE_DETAIL + "><![CDATA["
				+ courseDetails.getProfileDetail().getValue() + "]]></" + STUDENT_PROFILE_DETAIL
				+ ">");
		
		sb.append("</coursedetails>");

		return sb;
	}
	
	private StringBuffer parseStudentTeamNameToXML(Student currentStudent){
		StringBuffer sb = new StringBuffer();
		sb.append("<" + TEAM_NAME + "><![CDATA[" + currentStudent.getTeamName()
				+ "]]></" + TEAM_NAME + ">");
		if(currentStudent.getProfileDetail()==null)
			sb.append("<" + STUDENT_PROFILE_DETAIL + "><![CDATA[" + currentStudent.getProfileDetail()
					+ "]]></" + STUDENT_PROFILE_DETAIL + ">");
		else
			sb.append("<" + STUDENT_PROFILE_DETAIL + "><![CDATA[" + currentStudent.getProfileDetail().getValue()
				+ "]]></" + STUDENT_PROFILE_DETAIL + ">");
		sb.append("<" + STUDENT_NAME + "><![CDATA[" + currentStudent.getName()
				+ "]]></" + STUDENT_NAME + ">");
		return sb;
	}
	
	private void studentEditProfile() throws IOException {
		String courseId = req.getParameter("courseId");
		String studentEmail = req.getParameter("studentEmail");
		String profileSummary = req.getParameter("profileSummary");
		Text profileDetail = new Text(req.getParameter("profileDetail")); 
		
		// Add the team forming session		
		TeamForming teamForming = TeamForming.inst();

		boolean edited = teamForming
				.editStudentProfile(courseId, studentEmail, profileSummary, profileDetail);
		
		if (edited) {
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_STUDENTPROFILE_SAVED + MSG_STATUS_CLOSING);
		}		
	}
	
	private void studentGetCourseStudentDetail() throws IOException {
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
		String studentProfileSummary = student.getProfileSummary();
		Text studentProfileDetail = student.getProfileDetail();
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
				studentEmail, teammateList, studentProfileSummary, studentProfileDetail);

		resp.getWriter().write(
				parseCourseDetailsForStudentToXML(courseDetails).toString());
	}
	
	private void studentJoinTeam() throws IOException {
		String courseId = req.getParameter("courseId");
		String teamName = req.getParameter("teamName");
		String email = req.getParameter("email");
		
		// Add the team forming session		
		TeamForming teamForming = TeamForming.inst();
		teamForming.addStudentToTeam(courseId, teamName, email);
		
		resp.getWriter().write(
				MSG_STATUS_OPENING + MSG_STUDENTJOINTEAM + MSG_STATUS_CLOSING);
	}
}
