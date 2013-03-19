package teammates.ui.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import teammates.common.BuildProperties;
import teammates.common.Common;
import teammates.common.datatransfer.InstructorData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.SubmissionData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.AccountsDb;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class AdminHomeServlet extends ActionServlet<AdminHomeHelper> {

	@Override
	protected AdminHomeHelper instantiateHelper() {
		return new AdminHomeHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req, AdminHomeHelper helper) {
		String url = getRequestedURL(req);
		
		helper.instructorId = req.getParameter(Common.PARAM_INSTRUCTOR_ID);
		helper.instructorName = req.getParameter(Common.PARAM_INSTRUCTOR_NAME);
		helper.instructorEmail = req.getParameter(Common.PARAM_INSTRUCTOR_EMAIL);
		helper.instructorInstitution = req.getParameter(Common.PARAM_INSTRUCTOR_INSTITUTION);
		String importSampleData = req
				.getParameter(Common.PARAM_INSTRUCTOR_IMPORT_SAMPLE);

		try {
			if (helper.instructorId != null && helper.instructorName != null && helper.instructorEmail != null && helper.instructorInstitution != null) {
				helper.instructorId = helper.instructorId.trim();
				helper.instructorName = helper.instructorName.trim();
				helper.instructorEmail = helper.instructorEmail.trim();
				helper.instructorInstitution = helper.instructorInstitution.trim();
				
				AccountsDb accounts = new AccountsDb();
				if (accounts.isAccountExists(helper.instructorId)) {
					helper.error = true;
					helper.statusMessage = "The Google ID "
							+ helper.instructorId
							+ " has been already registered";
					activityLogEntry = instantiateActivityLogEntry(
							Common.ADMIN_HOME_SERVLET,
							Common.ADMIN_HOME_SERVLET_ID_ALREADY_REGISTERED,
							false, helper, url, null);
				} else {
					helper.server.createAccount(helper.instructorId,
							helper.instructorName, true,
							helper.instructorEmail,
							helper.instructorInstitution);
					helper.statusMessage = "Instructor "
							+ helper.instructorName
							+ " has been successfully created";

					ArrayList<Object> data = new ArrayList<Object>();
					data.add(helper.instructorId);
					data.add(helper.instructorName);
					data.add(helper.instructorEmail);
					activityLogEntry = instantiateActivityLogEntry(
							Common.ADMIN_HOME_SERVLET,
							Common.ADMIN_HOME_SERVLET_CREATE_INSTRUCTOR, false,
							helper, url, null);
				}
			} else {
				activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_HOME_SERVLET, Common.ADMIN_HOME_SERVLET_PAGE_LOAD,
						false, helper, url, null);
			}

			if (importSampleData != null) {
				importDemoData(helper);
			}
		} catch (Exception e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(e.getClass() + ": " + e.getMessage());
			activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_HOME_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
					false, helper, url, data);
		}
	}

	private void importDemoData(AdminHomeHelper helper) throws EntityAlreadyExistsException,
			InvalidParametersException, EntityDoesNotExistException{
		String jsonString;
		String courseId = helper.instructorId.concat("-demo").replace("@", "-at-");
		if (courseId.length() > 20) {
			courseId = courseId.substring(courseId.length() - 20);
		}
		jsonString = Common.readStream(BuildProperties.class.getClassLoader()
				.getResourceAsStream("InstructorSampleData.json"));
		
		// replace email
		jsonString = jsonString.replaceAll("teammates.demo.instructor@demo.course",
				helper.instructorEmail);
		// replace name
		jsonString = jsonString.replaceAll("Demo_Instructor", helper.instructorName);
		// replace id
		jsonString = jsonString.replaceAll("teammates.demo.instructor", helper.instructorId);
		// replace course
		jsonString = jsonString.replaceAll("demo.course", courseId);

		// update evaluation time
		Calendar c = Calendar.getInstance();
		c.set(Calendar.AM_PM, Calendar.PM);
		c.set(Calendar.HOUR, 11);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
		;

		jsonString = jsonString.replace("2013-04-01 11:59 PM",
				formatter.format(c.getTime()));

		Gson gson = Common.getTeammatesGson();
		DataBundle data = gson.fromJson(jsonString, DataBundle.class);
		persist(data.instructors, helper);
		persist(data.courses, helper);
		persist(data.students, helper);
		persist(data.evaluations, helper);
		Common.waitBriefly();

		persist(data.submissions, helper);
	}

	private static void persist(@SuppressWarnings("rawtypes") HashMap map,
			AdminHomeHelper helper) throws EntityAlreadyExistsException,
			InvalidParametersException, EntityDoesNotExistException {
		DataBundle bundle = new DataBundle();
		@SuppressWarnings("unchecked")
		Set<String> set = map.keySet();
		@SuppressWarnings("rawtypes")
		Iterator itr = set.iterator();
		List<SubmissionData> submissionDataList = new LinkedList<SubmissionData>();
		String type = "";
		while (itr.hasNext()) {
			String key = (String) itr.next();
			Object obj = map.get(key);

			if (obj instanceof InstructorData) {
				type = "InstructorData";
				InstructorData instructorData = (InstructorData) obj;
				// skip
			} else if (obj instanceof CourseData) {
				type = "CourseData";
				CourseData courseData = (CourseData) obj;
				helper.server.createCourse(helper.instructorId, courseData.id,
						courseData.name);

			} else if (obj instanceof StudentData) {
				type = "StudentData";
				StudentData studentData = (StudentData) obj;
				helper.server.createStudent(studentData);
			} else if (obj instanceof EvaluationData) {
				type = "EvaluationData";
				EvaluationData evaluationData = (EvaluationData) obj;
				helper.server.createEvaluation(evaluationData);
			} else if (obj instanceof SubmissionData) {
				type = "SubmissionData";
				SubmissionData submissionData = (SubmissionData) obj;
				submissionDataList.add(submissionData);
			}
			itr.remove();
			helper.server.editSubmissions(submissionDataList);
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_HOME;
	}

	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		
		String message;
		
		if(action.equals(Common.ADMIN_HOME_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else if (action.equals(Common.ADMIN_HOME_SERVLET_CREATE_INSTRUCTOR)){
			message = generateCreateInstructorMessage(servletName, action, data);
		} else if (action
				.equals(Common.ADMIN_HOME_SERVLET_ID_ALREADY_REGISTERED)) {
			message = generateIdAlreadyRegisteredMessage(servletName, action,
					data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
			
		return message;
	}

	private String generateIdAlreadyRegisteredMessage(String servletName,
			String action, ArrayList<Object> data) {
		String message;

		message = "Google Id has already been registered";

		return message;
	}
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		message = "Admin Home Page Load";
		
		return message;
	}
	
	
	private String generateCreateInstructorMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "A New Instructor <span class=\"bold\">" + (String)data.get(1) + "</span> has been created.<br>";
			message += "<span class=\"bold\">Id: </span>" + (String)data.get(0) + "<br>";
			message += "<span class=\"bold\">Email: </span>" + (String)data.get(2);
		} catch (NullPointerException e){
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
