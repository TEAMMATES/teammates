package teammates.ui.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.api.GateKeeper;

public class InstructorCourseEnrollSaveAction extends Action {
	protected static final Logger log = Utils.getLogger();
	
	
	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		String studentsInfo = getRequestParamValue(Const.ParamsNames.STUDENTS_ENROLLMENT_INFO);
		Assumption.assertNotNull(studentsInfo);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId), 
				logic.getCourse(courseId));
		
		studentsInfo = removeHeaderRowIfExist(studentsInfo);
		
		InstructorCourseEnrollResultPageData data = new InstructorCourseEnrollResultPageData(account);
		data.courseId = courseId;
		try {
			data.students = enrollAndProcessResultForDisplay(studentsInfo.trim(), courseId);
			statusToAdmin = "Students Enrolled in Course <span class=\"bold\">[" 
					+ courseId + "]:</span><br> - " + (studentsInfo).replace("\n", "<br> - ");
			
			return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL_RESULT, data);
			
		} catch (EnrollException | InvalidParametersException e) {
			setStatusForException(e);
			statusToAdmin += "<br>Enrollment string entered by user:<br>" + (studentsInfo).replace("\n", "<br>");
			
			InstructorCourseEnrollPageData d = new InstructorCourseEnrollPageData(account);
			d.courseId = courseId;
			d.enrollStudents = studentsInfo;
			
			return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, d);
		}
	}

	private List<StudentAttributes>[] enrollAndProcessResultForDisplay(String studentsInfo, String courseId)
			throws EnrollException, EntityDoesNotExistException, InvalidParametersException {
		List<StudentAttributes> students = logic.enrollStudents(studentsInfo, courseId);
		Collections.sort(students, new Comparator<StudentAttributes>() {
			@Override
			public int compare(StudentAttributes o1, StudentAttributes o2) {
				return (o1.updateStatus.numericRepresentation - o2.updateStatus.numericRepresentation);
			}
		});
		return separateStudents(students);

	}

	/**
	 * Separate the StudentData objects in the list into different categories based
	 * on their updateStatus. Each category is put into a separate list.<br>
	 * 
	 * Precondition:<br>
	 * * The list of StudentData objects passed in as argument has to be sorted in
	 * ascending order of their updateStatus first
	 * 
	 * @return An array of lists of StudentData objects in which each list contains
	 * student with the same updateStatus
	 */
	@SuppressWarnings("unchecked")
	private List<StudentAttributes>[] separateStudents(List<StudentAttributes> students) {
		// TODO: Refine and add unit testing for this method
		if (students == null)
			return new List[6];
		List<StudentAttributes>[] lists = new List[6];
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

	private String removeHeaderRowIfExist(String studentsInfo) {
		Assumption.assertNotNull(studentsInfo);
		
		String[] lines = studentsInfo.split(Const.EOL);
		
		if (isHeaderRow(lines[0])) {
			studentsInfo = studentsInfo.substring(lines[0].length() + Const.EOL.length());
		}
		
		return studentsInfo;
	}

	/**
	 * Check if a row is a header row by checking each column name
	 * Rules for a header row (case-insensitive):
	 * <br>-First column name is "team"
	 * <br>-Second column name contains the word "name"
	 * <br>-Third column name is the word "email"
	 */
	private boolean isHeaderRow(String row) {
		Assumption.assertNotNull(row);
		
		String[] fields = row.replace("|", "\t").split("\t");
		if (fields.length < 3) {
			return false;
		}
		
		String firstColumn = fields[0].trim();
		String secondColumn = fields[1].trim();
		String thirdColumn = fields[2].trim();
		
		if (firstColumn.equalsIgnoreCase("team") &&
				secondColumn.toLowerCase().contains("name") &&
				thirdColumn.equalsIgnoreCase("email")) {
			return true;
		} else {
			return false;
		}
	}

}
