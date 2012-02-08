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

import teammates.exception.TeamFormingSessionExistsException;
import teammates.jdo.Course;
import teammates.jdo.Student;
import teammates.jdo.TeamFormingSession;

@SuppressWarnings("serial")
public class TeamFormingServlet extends HttpServlet {
	private HttpServletRequest req;
	private HttpServletResponse resp;

	// OPERATIONS	
	private static final String OPERATION_COORDINATOR_CREATETEAMFORMINGSESSION = "coordinator_createteamformingsession";
	private static final String OPERATION_COORDINATOR_GETTEAMFORMINGSESSIONLIST = "coordinator_getteamformingsessionlist";
	private static final String OPERATION_COORDINATOR_DELETETEAMFORMINGSESSION = "coordinator_deleteteamformingsession";
	private static final String OPERATION_COORDINATOR_REMINDSTUDENTS_TEAMFORMING = "coordinator_remindstudentsteamforming";
	private static final String OPERATION_COORDINATOR_EDITTEAMFORMINGSESSION = "coordinator_editteamformingsession";
	private static final String OPERATION_COORDINATOR_INFORMSTUDENTSOFTEAMFORMINGSESSIONCHANGES = "coordinator_informstudentsofteamformingsessionchanges";
	private static final String OPERATION_SHOW_TEAMFORMING = "coordinator_teamforming";
	
	// PARAMETERS
	private static final String COURSE_ID = "courseid";

	private static final String TEAMFORMING_ACTIVATED = "activated";
	private static final String TEAMFORMING_DEADLINE = "deadline";
	private static final String TEAMFORMING_DEADLINETIME = "deadlinetime";
	private static final String TEAMFORMING_GRACEPERIOD = "graceperiod";
	private static final String TEAMFORMING_INSTRUCTIONS = "instr";
	private static final String TEAMFORMING_START = "start";
	private static final String TEAMFORMING_STARTTIME = "starttime";
	private static final String TEAMFORMING_TIMEZONE = "timezone";	
	private static final String TEAMFORMING_PROFILETEMPLATE = "profile_template";

	// MESSAGES
	private static final String MSG_TEAMFORMINGSESSION_ADDED = "team forming session added";
	private static final String MSG_TEAMFORMINGSESSION_EXISTS = "team forming session exists";
	private static final String MSG_STATUS_OPENING = "<status>";
	private static final String MSG_STATUS_CLOSING = "</status>";	
	private static final String MSG_TEAMFORMINGSESSION_EDITED = "team forming session edited";
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
		
		else if (operation.equals(OPERATION_COORDINATOR_DELETETEAMFORMINGSESSION)) {
			coordinatorDeleteTeamFormingSession();
		}
		
		else if (operation.equals(OPERATION_COORDINATOR_EDITTEAMFORMINGSESSION)) {
			coordinatorEditTeamFormingSession();
		}

		else if (operation.equals(OPERATION_COORDINATOR_GETTEAMFORMINGSESSIONLIST)) {
			coordinatorGetTeamFormingSessionList();
		}
		
		else if (operation
				.equals(OPERATION_COORDINATOR_INFORMSTUDENTSOFTEAMFORMINGSESSIONCHANGES)) {
			coordinatorInformStudentsOfTeamFormingSessionChanges();
		}

		else if (operation.equals(OPERATION_COORDINATOR_REMINDSTUDENTS_TEAMFORMING)) {
			coordinatorRemindStudentsOfTeamForming();
		}
		
		else if (operation.equals(OPERATION_SHOW_TEAMFORMING)){
			coordinatorTeamForming();
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
	
	private void coordinatorTeamForming() throws IOException, ServletException {
		Accounts accounts = Accounts.inst();
		resp.getWriter().write(
				"<url><![CDATA[" + accounts.getLoginPage("/coordinator.jsp?teamforming")
						+ "]]></url>");
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

		Date start = Utils.convertToDate(startDate, startTime);
		Date deadline = Utils.convertToDate(deadlineDate, deadlineTime);

		// Add the team forming session		
		TeamForming teamForming = TeamForming.inst();
		
		try{
			teamForming
			.createTeamFormingSession(courseID, start, deadline, timeZone, gracePeriod, 
					instructions, profileTemplate);
			
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_TEAMFORMINGSESSION_ADDED + MSG_STATUS_CLOSING);
		}
		
		catch (TeamFormingSessionExistsException e){
			resp.getWriter().write(
					MSG_STATUS_OPENING + MSG_TEAMFORMINGSESSION_EXISTS + MSG_STATUS_CLOSING);
		}
	}
	
	private void coordinatorDeleteTeamFormingSession() {
		String courseID = req.getParameter(COURSE_ID);
		String deadlineDate = req.getParameter(TEAMFORMING_DEADLINE);
		int deadlineTime = Integer.parseInt(req
				.getParameter(TEAMFORMING_DEADLINETIME));
		int deadlineTimeHour = deadlineTime/100;
		int deadlineTimeMin = deadlineTime%100;		
		if(deadlineTimeMin>30)
			deadlineTimeHour++;
		deadlineTime = deadlineTimeHour;
		
		Date deadline = Utils.convertToDate(deadlineDate, deadlineTime);
		TeamForming teamForming = TeamForming.inst();
		teamForming.deleteTeamFormingSession(courseID, deadline);
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

		Date newStart = Utils.convertToDate(newStartDate, newStartTime);
		Date newDeadline = Utils.convertToDate(newDeadlineDate, newDeadlineTime);
		
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
	
	private void coordinatorGetTeamFormingSessionList() throws IOException {
		Accounts accounts = Accounts.inst();
		String googleID = accounts.getUser().getNickname().toLowerCase();
		
		Courses courses = Courses.inst();
		List<Course> courseList = courses.getCoordinatorCourseList(googleID);
		
		TeamForming teamForming = TeamForming.inst();
		List<TeamFormingSession> teamFormingSessionList = teamForming
				.getTeamFormingSessionList(courseList);		
		
//		List<EvaluationDetailsForCoordinator> evaluationDetailsList = new ArrayList<EvaluationDetailsForCoordinator>();
//
//		int numberOfCompletedEvaluations = 0;
//		int numberOfEvaluations = 0;
//
//		for (Evaluation e : evaluationList) {
//			if (courses.getCourse(e.getCourseID()).isArchived() != true) {
//				numberOfCompletedEvaluations = evaluations
//						.getNumberOfCompletedEvaluations(e.getCourseID(),
//								e.getName());
//				numberOfEvaluations = evaluations.getNumberOfEvaluations(
//						e.getCourseID(), e.getName());
//
//				evaluationDetailsList.add(new EvaluationDetailsForCoordinator(e
//						.getCourseID(), e.getName(), e.getInstructions(), e
//						.isCommentsEnabled(), e.getStart(), e.getDeadline(), e
//						.getTimeZone(), e.getGracePeriod(), e.isPublished(), e
//						.isActivated(), numberOfCompletedEvaluations,
//						numberOfEvaluations));
//			}
//		}
//	
		resp.getWriter().write(
				"<teamformingsessions>"
						+ parseCoordinatorTeamFormingSesssionListToXML(
								teamFormingSessionList).toString()
						+ "</teamformingsessions>");
	}
	
	private void coordinatorInformStudentsOfTeamFormingSessionChanges() {
		String courseID = req.getParameter(COURSE_ID);

		Courses courses = Courses.inst();
		List<Student> studentList = courses.getStudentList(courseID);

		//TODO: replace null with deadline if deadline is part of the primary key
		TeamForming teamForming = TeamForming.inst();
		teamForming.informStudentsOfChanges(studentList, courseID, null);
	}
	
	private void coordinatorRemindStudentsOfTeamForming() {
		String courseID = req.getParameter(COURSE_ID);

		Courses courses = Courses.inst();
		List<Student> studentList = courses.getStudentList(courseID);

		List<Student> studentsToRemindList = new ArrayList<Student>();

		for (Student s : studentList) {
			if (s.getTeamName().equals("")) {
				studentsToRemindList.add(s);
			}
		}

		//by kalpit
		//TODO: may have to change when getTeamFormingSession changes 
		Date dummyDeadline = null;
		TeamForming teamForming = TeamForming.inst();
		TeamFormingSession teamFormingSession = teamForming.getTeamFormingSession(courseID, dummyDeadline);
		
		Date deadline = teamFormingSession.getDeadline();

		teamForming.remindStudents(studentsToRemindList, courseID, deadline);
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
}
