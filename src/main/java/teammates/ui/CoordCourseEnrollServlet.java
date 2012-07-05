package teammates.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.StudentData;
import teammates.logic.api.EnrollException;
import teammates.logic.api.EntityDoesNotExistException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Enroll Students action
 */
public class CoordCourseEnrollServlet extends
		ActionServlet<CoordCourseEnrollHelper> {

	@Override
	protected CoordCourseEnrollHelper instantiateHelper() {
		return new CoordCourseEnrollHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req,
			CoordCourseEnrollHelper helper) throws EntityDoesNotExistException {

		helper.courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentsInfo = req
				.getParameter(Common.PARAM_STUDENTS_ENROLLMENT_INFO);

		try {
			enrollAndProcessResultForDisplay(helper, studentsInfo);
		} catch (EnrollException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
		}
	}


	private void enrollAndProcessResultForDisplay(
			CoordCourseEnrollHelper helper, String studentsInfo)
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
		return Common.JSP_COORD_COURSE_ENROLL;
	}
}
