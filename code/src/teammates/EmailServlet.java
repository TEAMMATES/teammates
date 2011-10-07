package teammates;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class EmailServlet extends HttpServlet 
{
	private HttpServletRequest req;
	private HttpServletResponse resp;
	
	// OPERATIONS
	private static final String INFORM_STUDENTSOFEVALUATIONCHANGES = "informstudentsofevaluationchanges";
	private static final String INFORM_STUDENTSOFPUBLISHEDEVALUATION = "informstudentspublishedevaluation";
	private static final String INFORM_STUDENTSOFEVALUATIONOPENING = "informstudentsofevaluationopening";
	private static final String REMIND_STUDENTS = "remindstudents";
	private static final String SEND_REGISTRATION_KEY = "sendregistrationkey";
	
	// PARAMETERS
	private static final String COORDINATOR_NAME = "coordinatorname";
	private static final String COORDINATOR_EMAIL = "coordinatoremail";
	
	private static final String COURSE_ID = "courseid";
	private static final String COURSE_NAME = "coursename";
	
	private static final String EVALUATION_DEADLINE = "deadline";
	private static final String EVALUATION_INSTRUCTIONS = "instr";
	private static final String EVALUATION_NAME = "evaluationname";
	private static final String EVALUATION_START = "start";
	
	private static final String STUDENT_EMAIL = "email";
	private static final String STUDENT_NAME = "name";
	private static final String STUDENT_REGKEY = "regkey";
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	{
		// Initialization
		this.req = req;
		this.resp = resp;
		
		this.resp.setContentType("text/xml"); 
		this.resp.setHeader("Cache-Control", "no-cache");
		
		// Processing
		String operation = this.req.getParameter("operation");
		
		if(operation.equals(SEND_REGISTRATION_KEY))
		{
			sendRegistrationKey();
		}
		
		else if(operation.equals(REMIND_STUDENTS))
		{
			remindStudents();
		}
		
		else if(operation.equals(INFORM_STUDENTSOFEVALUATIONCHANGES))
		{
			informStudentsOfEvaluationChanges();
		}
		
		else if(operation.equals(INFORM_STUDENTSOFEVALUATIONOPENING))
		{
			informStudentsOfEvaluationOpening();
		}
		
		else if(operation.equals(INFORM_STUDENTSOFPUBLISHEDEVALUATION))
		{
			informStudentsOfPublishedEvaluation();
		}
	}


	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{  
		doGet(req, resp);  
	} 
	
	private void informStudentsOfEvaluationChanges()
	{
		Emails emails = new Emails();
		
		String email = req.getParameter(STUDENT_EMAIL);
		String courseID = req.getParameter(COURSE_ID);
		String evaluationName = req.getParameter(EVALUATION_NAME);
		String studentName = req.getParameter(STUDENT_NAME);
		String instructions = req.getParameter(EVALUATION_INSTRUCTIONS);
		String start = req.getParameter(EVALUATION_START);
		String deadline = req.getParameter(EVALUATION_DEADLINE);
		
		emails.informStudentsOfEvaluationChanges(email, studentName, courseID, evaluationName,
				instructions, start, deadline);
	}
	
	private void informStudentsOfEvaluationOpening() 
	{
		Emails emails = new Emails();
		
		String email = req.getParameter(STUDENT_EMAIL);
		String courseID = req.getParameter(COURSE_ID);
		String evaluationName = req.getParameter(EVALUATION_NAME);
		String studentName = req.getParameter(STUDENT_NAME);
		
		
		emails.informStudentsOfEvaluationOpening(email, studentName, courseID, evaluationName);
	}
	
	private void informStudentsOfPublishedEvaluation() 
	{
		Emails emails = new Emails();
		
		String email = req.getParameter(STUDENT_EMAIL);
		String courseID = req.getParameter(COURSE_ID);
		String evaluationName = req.getParameter(EVALUATION_NAME);
		String studentName = req.getParameter(STUDENT_NAME);
		
		emails.informStudentsOfPublishedEvaluation(email, studentName, courseID, evaluationName);
		
	}

	private void remindStudents() 
	{
		Emails emails = new Emails();
		
		String email = req.getParameter(STUDENT_EMAIL);
		String courseID = req.getParameter(COURSE_ID);
		String evaluationName = req.getParameter(EVALUATION_NAME);
		String studentName = req.getParameter(STUDENT_NAME);
		String deadline = req.getParameter(EVALUATION_DEADLINE);

		emails.remindStudent(email, studentName, courseID, evaluationName, deadline);
		
	}
	
	private void sendRegistrationKey() 
	{
		Emails emails = new Emails();
		
		String studentName = req.getParameter(STUDENT_NAME);
		String courseID = req.getParameter(COURSE_ID);
		String courseName = req.getParameter(COURSE_NAME);
		String coordinatorName = req.getParameter(COORDINATOR_NAME);
		String studentEmail = req.getParameter(STUDENT_EMAIL);
		String registrationKey = req.getParameter(STUDENT_REGKEY);
		String coordinatorEmail =req.getParameter(COORDINATOR_EMAIL);
		emails.sendRegistrationKey(studentEmail, registrationKey, studentName,
				courseID, courseName, coordinatorName, coordinatorEmail);
	}


}
