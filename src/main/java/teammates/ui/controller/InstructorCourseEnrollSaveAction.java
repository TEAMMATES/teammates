package teammates.ui.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.logic.GateKeeper;

public class InstructorCourseEnrollSaveAction extends Action {
	protected static final Logger log = Config.getLogger();
	
	
	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		
		String courseId = getRequestParam(Config.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		String studentsInfo = getRequestParam(Config.PARAM_STUDENTS_ENROLLMENT_INFO);
		Assumption.assertNotNull(studentsInfo);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId), 
				logic.getCourse(courseId));
		
		InstructorCourseEnrollResultPageData data = new InstructorCourseEnrollResultPageData(account);
		data.courseId = courseId;
		try {
			data.students = enrollAndProcessResultForDisplay(studentsInfo, courseId);
			statusToAdmin = "Students Enrolled in Course <span class=\"bold\">[" 
					+ courseId + "]:</span><br> - " + (studentsInfo).replace("\n", "<br> - ");
			
		} catch (EnrollException e) {
			statusToUser.add(e.getMessage());
			isError = true;
			statusToAdmin = e.getMessage();
		}
		
		return createShowPageResult(Config.JSP_INSTRUCTOR_COURSE_ENROLL_RESULT, data);
	}

	
	private List<StudentAttributes>[] enrollAndProcessResultForDisplay(String studentsInfo, String courseId)
			throws EnrollException, EntityDoesNotExistException {
		List<StudentAttributes> students = logic.enrollStudents(studentsInfo, courseId);
		Collections.sort(students, new Comparator<StudentAttributes>() {
			@Override
			public int compare(StudentAttributes o1, StudentAttributes o2) {
				return (o1.updateStatus.numericRepresentation - o2.updateStatus.numericRepresentation);
			}
		});
		return separateStudents(students);

	}

	@SuppressWarnings("unchecked")
	private List<StudentAttributes>[] separateStudents(List<StudentAttributes> students) {
		if (students == null)
			return (List<StudentAttributes>[]) new List[6];
		List<StudentAttributes>[] lists = (List<StudentAttributes>[]) new List[6];
		int prevIdx = 0;
		int nextIdx = 0;
		int id = 0;
		for (StudentAttributes student : students) {
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
			StudentAttributes.sortByNameAndThenByEmail(lists[id - 1]);
			prevIdx = nextIdx;
		}
		return lists;
	}


}
