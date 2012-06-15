package teammates.servlet;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EnrollException;
import teammates.api.EntityDoesNotExistException;
import teammates.datatransfer.StudentData;
import teammates.jsp.CoordCourseEnrollHelper;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Enroll Students action
 * @author Aldrian Obaja
 *
 */
public class CoordCourseEnrollServlet extends ActionServlet<CoordCourseEnrollHelper> {
	
	private static final String DISPLAY_URL = Common.JSP_COORD_COURSE_ENROLL;

	@Override
	protected CoordCourseEnrollHelper instantiateHelper() {
		return new CoordCourseEnrollHelper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, CoordCourseEnrollHelper helper) throws IOException {
		// Authenticate user
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, CoordCourseEnrollHelper helper)
			throws EntityDoesNotExistException{
		// Get parameters
		helper.courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentsInfo = req.getParameter(Common.PARAM_STUDENTS_ENROLLMENT_INFO);
		
		// Process action
		try {
			processEnrollmentForDisplay(helper, studentsInfo);
		} catch (EnrollException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
		}
	}

	@Override
	protected void doCreateResponse(HttpServletRequest req,
			HttpServletResponse resp,
			CoordCourseEnrollHelper helper) throws ServletException, IOException {
		if(helper.nextUrl==null) helper.nextUrl = DISPLAY_URL;
		
		if(helper.nextUrl.startsWith(DISPLAY_URL)){
			// Goto display page
			req.setAttribute("helper", helper);
			req.getRequestDispatcher(helper.nextUrl).forward(req, resp);
		} else {
			// Goto next page
			helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_USER_ID, helper.requestedUser);
			resp.sendRedirect(helper.nextUrl);
		}
	}

	private void processEnrollmentForDisplay(CoordCourseEnrollHelper helper,
			String studentsInfo) throws EnrollException, EntityDoesNotExistException {
		if(studentsInfo==null) return;
		List<StudentData> students = helper.server.enrollStudents(studentsInfo, helper.courseID);
		Collections.sort(students,new Comparator<StudentData>(){
			@Override
			public int compare(StudentData o1, StudentData o2) {
				return getNum(o1.updateStatus).compareTo(getNum(o2.updateStatus));
			}
		});
		helper.students = separateStudents(students);
		
		if(helper.students[0]!=null) helper.isResult = true;
	}

	@SuppressWarnings("unchecked")
	private List<StudentData>[] separateStudents(List<StudentData> students) {
		if(students==null) return (List<StudentData>[])new List[6];
		List<StudentData>[] lists = (List<StudentData>[])new List[6];
		int prevIdx = 0;
		int nextIdx = 0;
		int id = 0;
		for(StudentData student: students){
			if(student.comments==null) student.comments = "";
			if(student.team==null) student.team = "";
			while(getNum(student.updateStatus)>id){
				lists[id] = students.subList(prevIdx, nextIdx);
				id++;
				prevIdx = nextIdx;
			}
			nextIdx++;
		}
		while(id<6){
			lists[id++] = students.subList(prevIdx, nextIdx);
			sortStudents(lists[id-1]);
			prevIdx = nextIdx;
		}
		return lists;
	}
	
	private Integer getNum(StudentData.UpdateStatus en){
		switch(en){
		case ERROR: return 0;
		case NEW: return 1;
		case MODIFIED: return 2;
		case UNMODIFIED: return 3;
		case NOT_IN_ENROLL_LIST: return 4;
		default: return 5;
		}
	}
}
