package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.StudentData;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityDoesNotExistException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Enroll Students action
 */
public class InstructorCourseEnrollServlet extends
		ActionServlet<InstructorCourseEnrollHelper> {

	@Override
	protected InstructorCourseEnrollHelper instantiateHelper() {
		return new InstructorCourseEnrollHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req,
			InstructorCourseEnrollHelper helper) throws EntityDoesNotExistException {
		String url = getRequestedURL(req);
        
		helper.courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentsInfo = req.getParameter(Common.PARAM_STUDENTS_ENROLLMENT_INFO);
		
		try {
			enrollAndProcessResultForDisplay(helper, studentsInfo); 
		} catch (EnrollException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
			
			ArrayList<Object> data = new ArrayList<Object>();
	        data.add(helper.statusMessage);	                        
	        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_ENROLL_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
	        		true, helper, url, data);
		}
		
		if (studentsInfo == null){
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(helper.courseID);
			activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_ENROLL_SERVLET, Common.INSTRUCTOR_COURSE_ENROLL_SERVLET_PAGE_LOAD,
					true, helper, url, data);
		} else {
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(helper.courseID);
			data.add(studentsInfo);
			activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_ENROLL_SERVLET, Common.INSTRUCTOR_COURSE_ENROLL_SERVLET_ENROLL_STUDENTS,
					true, helper, url, data);
		}
	}


	private void enrollAndProcessResultForDisplay(
			InstructorCourseEnrollHelper helper, String studentsInfo)
			throws EnrollException, EntityDoesNotExistException {
		if (studentsInfo == null)
			return;
		List<StudentData> students = helper.server.enrollStudents(studentsInfo,
				helper.courseID);
		Collections.sort(students, new Comparator<StudentData>() {
			@Override
			public int compare(StudentData o1, StudentData o2) {
				return (o1.updateStatus.numericRepresentation - o2.updateStatus.numericRepresentation);
			}
		});
		helper.students = separateStudents(students);

		if (helper.students[0] != null)
			helper.isResult = true;
	}

	@SuppressWarnings("unchecked")
	private List<StudentData>[] separateStudents(List<StudentData> students) {
		if (students == null)
			return (List<StudentData>[]) new List[6];
		List<StudentData>[] lists = (List<StudentData>[]) new List[6];
		int prevIdx = 0;
		int nextIdx = 0;
		int id = 0;
		for (StudentData student : students) {
			if (student.comments == null)
				student.comments = "";
			if (student.team == null)
				student.team = "";
			while (student.updateStatus.numericRepresentation > id) {
				lists[id] = students.subList(prevIdx, nextIdx);
				id++;
				prevIdx = nextIdx;
			}
			nextIdx++;
		}
		while (id < 6) {
			lists[id++] = students.subList(prevIdx, nextIdx);
			sortStudents(lists[id - 1]);
			prevIdx = nextIdx;
		}
		return lists;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_COURSE_ENROLL;
	}


	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.INSTRUCTOR_COURSE_ENROLL_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else if (action.equals(Common.INSTRUCTOR_COURSE_ENROLL_SERVLET_ENROLL_STUDENTS)){
			message = generateEnrollStudentsMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "instructorCourseEnroll Page Load<br>";
			message += "Enrollment for Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>";
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
	
	private String generateEnrollStudentsMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "Students Enrolled in Course <span class=\"bold\">[" + (String)data.get(0) + "]:</span><br> - " + ((String)data.get(1)).replace("\n", "<br> - ");
		} catch (NullPointerException e){
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
