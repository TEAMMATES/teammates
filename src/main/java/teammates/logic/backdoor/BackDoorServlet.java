package teammates.logic.backdoor;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Course; //TODO: remove this dependency

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

		String action = req.getParameter(PARAMETER_BACKDOOR_OPERATION);
		log.info(action);

		String returnValue;

		String auth = req.getParameter(PARAMETER_BACKDOOR_KEY);
		if (!auth.equals(Common.BACKDOOR_KEY)) {
			returnValue = "Not authorized to access Backdoor Services";

		} else {
			try {
				returnValue = executeBackendAction(req, action);
			} catch (Exception e) {
				returnValue = Common.BACKEND_STATUS_FAILURE
						+ Common.stackTraceToString(e);
			} catch (AssertionError ae) {
				returnValue = Common.BACKEND_STATUS_FAILURE
						+ " Assertion error " + ae.getMessage();
			}
		}

		// TODO: Change to JSON/XML
		resp.setContentType("text/plain");
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
		} else {
			throw new Exception("Unknown command: " + action);
		}
		return Common.BACKEND_STATUS_SUCCESS;
	}

	// TODO: move to BackDoorLogic
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