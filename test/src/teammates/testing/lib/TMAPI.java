package teammates.testing.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.testing.Config;
import teammates.testing.object.Course;
import teammates.testing.object.Evaluation;
import teammates.testing.object.Student;
import teammates.testing.object.Submission;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This class use REST protocol to interact directly with Teammates software to
 * make changes.
 * 
 * URL to interact: http://<teammates_url>/api
 * 
 * @author huy, wangsha
 * 
 */
public class TMAPI {

	private static Gson gson = new Gson();

	/**
	 * Clean up all tables. Except for Coordinator table.
	 */
	public static void cleanup() {
		 // Check appengine the test is running on, disable total cleanup for live server
        // wangsha
        // September 8
        cleanupByCoordinator();

		/*
        if(Config.TEAMMATES_APP.equalsIgnoreCase(Config.TEAMMATES_LIVE_SITE)) {
                System.out.println("Total cleanup disabled for live site, redirect to total clean up by coordinator.");
                cleanupByCoordinator();
        }else{
                System.out.println("Perform total cleanning up ");
                HashMap<String, Object> params = createParamMap("cleanup");
                String paramsString = buildParamsString(params);

                makePOSTRequest(paramsString);
        }*/
       
	}
	
	 /**
     * Clean up everything related to the coordinator
     * 
     * @author wangsha
     * @date Sep 8, 2011
     */
    public static void cleanupByCoordinator() {
    	System.out.println("Clean up by coordinator");
            HashMap<String, Object> params = createParamMap("cleanup_by_coordinator");
            params.put("coordinator_id", Config.TEAMMATES_COORD_ID);
            String paramsString = buildParamsString(params);
            makePOSTRequest(paramsString);
    }

	/**
	 * Clean up everything related to courseID.
	 * 
	 * @param courseId
	 * @return
	 */
	public static void cleanupCourse(String courseId) {
		System.out.println("Cleaning up course " + courseId);
		HashMap<String, Object> params = createParamMap("cleanup_course");
		params.put("course_id", courseId);
		String paramsString = buildParamsString(params);

		makePOSTRequest(paramsString);

	}

	/**
	 * Create new course
	 */
	public static void createCourse(Course course) {
		System.out.println("Creating course.");

		HashMap<String, Object> params = createParamMap("course_add");
		params.put("google_id", "teammates.coord");
		params.put("course", course.toJSON());
		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);
	}

	public static void createEvaluation(Evaluation eval) {
		System.out.println("Creating evaluation.");
		HashMap<String, Object> params = createParamMap("evaluation_add");
		params.put("evaluation", eval.toJSON());
		String paramsString = buildParamsString(params);
		// System.out.println(eval.toJSON());
		makePOSTRequest(paramsString);

	}

	public static void openEvaluation(String courseID, String evalName) {
		System.out.println("Opening evaluation.");

		HashMap<String, Object> params = createParamMap("evaluation_open");
		params.put("course_id", courseID);
		params.put("evaluation_name", evalName);
		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);
	}

	public static void closeEvaluation(String courseID, String evalName) {
		System.out.println("Closing evaluation.");

		HashMap<String, Object> params = createParamMap("evaluation_close");
		params.put("course_id", courseID);
		params.put("evaluation_name", evalName);
		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);
	}

	public static void enrollStudents(String courseId, List<Student> students) {
		System.out.println("Enrolling students.");

		HashMap<String, Object> params = createParamMap("enroll_students");

		Type listType = new TypeToken<List<Student>>() {
		}.getType();
		String json_students = gson.toJson(students, listType);
		params.put("course_id", courseId);
		params.put("students", json_students);

		// System.out.println(json_students);

		makePOSTRequest(buildParamsString(params));
	}

	public static void registerStudents(String courseId, List<Student> students) {
		System.out.println("Register students.");

		HashMap<String, Object> params = createParamMap("register_students");

		Type listType = new TypeToken<List<Student>>() {
		}.getType();
		String json_students = gson.toJson(students, listType);
		params.put("course_id", courseId);
		params.put("students", json_students);

		// System.out.println(json_students);

		makePOSTRequest(buildParamsString(params));

	}

	public static void publishEvaluation(String courseID, String evalName) {
		HashMap<String, Object> params = createParamMap("evaluation_publish");
		params.put("course_id", courseID);
		params.put("evaluation_name", evalName);
		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);
	}

	public static void unpublishEvaluation(String courseID, String evalName) {
		HashMap<String, Object> params = createParamMap("evaluation_unpublish");
		params.put("course_id", courseID);
		params.put("evaluation_name", evalName);
		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);
	}

	public static void remindStudents(ArrayList<Student> students) {
		// TODO Auto-generated method stub
	}

	public static void submitEvaluation(ArrayList<Submission> submissions) {
		
	}

	/**
	 * HAVE NOT TESTED
	 */
	public static void studentsJoinCourse(List<Student> students,
			String courseId) {
		// Go into database and fill feedback from this student.
		System.out.println("Joining course for students.");

		HashMap<String, Object> params = createParamMap("students_join_course");
		params.put("course_id", courseId);
		Type listType = new TypeToken<List<Student>>() {
		}.getType();
		params.put("students", gson.toJson(students, listType));

		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);
	}

	/**
	 * Submit feedbacks for a particular student
	 * 
	 * @prerequisite Evaluation must be open.
	 */
	public static void studentSubmitFeedbacks(Student student, String courseId,
			String evaluationName) {
		// Go into database and fill feedback from this student.
		System.out.println("Submitting feedback for student " + student.email);

		HashMap<String, Object> params = createParamMap("student_submit_feedbacks");
		params.put("course_id", courseId);
		params.put("evaluation_name", evaluationName);
		params.put("student_email", student.email);

		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);
	}

	public static void studentsSubmitFeedbacks(List<Student> students,
			String courseId, String evaluationName) {
		for (Student s : students) {
			studentSubmitFeedbacks(s, courseId, evaluationName);
		}
	}

	/**
	 * Mail Stress Testing
	 * 
	 * @param account
	 * @param size
	 * @author wangsha
	 */
	public static void mailStressTesting(String account, int size) {
		System.out.println("Mail testing " + account + ", size:" + size);

		HashMap<String, Object> params = createParamMap("email_stress_testing");
		params.put("account", account);
		params.put("size", size);
		String paramsString = buildParamsString(params);
		makePOSTRequest(paramsString);

	}

	/*
	 * public static void firstStudentDidNotSubmitFeedbacks(List<Student>
	 * students, String courseId, String evaluationName) { students.remove(0);
	 * for (Student s : students) { studentSubmitFeedbacks(s, courseId,
	 * evaluationName); } }
	 */

	// ---------------------------------
	// PRIVATE HELPER FUNCTIONS
	// ---------------------------------

	/**
	 * Take a map and convert it to url-friendly querystring
	 */
	private static String buildParamsString(HashMap<String, Object> map) {
		try {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, Object> e : map.entrySet()) {

				sb.append(URLEncoder.encode(e.getKey(), "UTF-8") + "="
						+ URLEncoder.encode(e.getValue().toString(), "UTF-8")
						+ "&");
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return "";
		}
	}

	private static HashMap<String, Object> createParamMap(String string) {
		HashMap<String, Object> ans = new HashMap<String, Object>();
		ans.put("action", string);
		
		// API Authentication
		ans.put("tm_auth", Config.API_AUTH_CODE);

		return ans;
	}

	private static String makePOSTRequest(String data) {
		try {
			// http://teammates/api
			URL url = new URL(Config.TEAMMATES_URL + "api");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			wr.close();
			rd.close();

			return sb.toString();

		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
}
