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
import teammates.datatransfer.StudentData;
import teammates.jsp.CoordCourseEnrollHelper;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Enroll Students action
 * @author Aldrian Obaja
 *
 */
public class CoordCourseEnrollServlet extends ActionServlet {
	
	protected void doPostAction(HttpServletRequest req, HttpServletResponse resp, Helper help)
			throws IOException, ServletException {
		CoordCourseEnrollHelper helper = new CoordCourseEnrollHelper(help);
		
		// Authenticate user
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect("unauthorized.jsp");
			return;
		}
		
		// Get parameters
		helper.courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentsInfo = req.getParameter(Common.PARAM_STUDENTS_ENROLLMENT_INFO);
		
		// Process action
		try {
			if(studentsInfo!=null){
				List<StudentData> students = helper.server.enrollStudents(studentsInfo, helper.courseID);
				Collections.sort(students,new Comparator<StudentData>(){
					@Override
					public int compare(StudentData o1, StudentData o2) {
						return getNum(o1.updateStatus).compareTo(getNum(o2.updateStatus));
					}
				});
				@SuppressWarnings("unchecked")
				List<StudentData>[] lists = (List<StudentData>[])new List[6];
				int prevIdx = 0;
				int nextIdx = 0;
				int id = 0;
				for(StudentData student: students){
					while(getNum(student.updateStatus)>id){
						lists[id] = students.subList(prevIdx, nextIdx);
						id++;
						prevIdx = nextIdx;
					}
					nextIdx++;
				}
				while(id<6){
					lists[id++] = students.subList(prevIdx, nextIdx);
					prevIdx = nextIdx;
				}
	
				helper.studentsError = lists[0];
				helper.studentsNew = lists[1];
				helper.studentsModified = lists[2];
				helper.studentsUnmodified = lists[3];
				helper.studentsOld = lists[4];
				helper.studentsUnknown = lists[5];
				
				if(helper.studentsError!=null) helper.isResult = true;
			}
			
		} catch (EnrollException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
		}
		
		// Goto display page
		if(helper.nextUrl==null) helper.nextUrl = "coordCourseEnroll.jsp";
		helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_USER_ID, helper.requestedUser);
		
		if(helper.nextUrl.startsWith("coordCourseEnroll.jsp")){
			req.setAttribute("helper", helper);
			req.getRequestDispatcher(helper.nextUrl).forward(req, resp);
		} else {
			resp.sendRedirect(helper.nextUrl);
		}
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
