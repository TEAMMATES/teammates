package teammates.logic.core;

import static teammates.common.util.Const.EOL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Utils;
import teammates.storage.api.CoursesDb;

/**
 * Handles  operations related to courses.
 */
public class CoursesLogic {
	/* Explanation: Most methods in the API of this class doesn't have header 
	 *  comments because it sits behind the API of the logic class. 
	 *  Those who use this class is expected to be familiar with the its code 
	 *  and Logic's code. Hence, no need for header comments.
	 */ 
	
	//TODO: There's no need for this class to be a Singleton.
	private static CoursesLogic instance = null;
	
	private static final Logger log = Utils.getLogger();
	
	/* Explanation: This class depends on CoursesDb class but no other *Db classes.
	 * That is because reading/writing entities from/to the datastore is the 
	 * responsibility of the matching *Logic class.
	 * However, this class can talk to other *Logic classes. That is because
	 * the logic related to one entity type can involve the logic related to
	 * other entity types.
	 */

	private static final CoursesDb coursesDb = new CoursesDb();
	
	private static final StudentsLogic studentsLogic = StudentsLogic.inst();
	private static final EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
	private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
	private static final AccountsLogic accountsLogic = AccountsLogic.inst();
	private static final FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();

	
	public static CoursesLogic inst() {
		if (instance == null)
			instance = new CoursesLogic();
		return instance;
	}

	public void createCourse(String courseId, String courseName) 
			throws InvalidParametersException, EntityAlreadyExistsException {
		
		CourseAttributes courseToAdd = new CourseAttributes(courseId, courseName);
		coursesDb.createEntity(courseToAdd);
	}
	
	/**
	 * Creates a Course object and an Instructor object for the Course.
	 */
	public void createCourseAndInstructor(String instructorGoogleId, String courseId, String courseName) 
			throws InvalidParametersException, EntityAlreadyExistsException {
		
		AccountAttributes courseCreator = accountsLogic.getAccount(instructorGoogleId);
		Assumption.assertNotNull(
				"Trying to create a course for a non-existent instructor :"+ instructorGoogleId, 
				courseCreator);
		Assumption.assertTrue(
				"Trying to create a course for a person who doesn't have instructor privileges :"+ instructorGoogleId, 
				courseCreator.isInstructor);
		
		createCourse(courseId, courseName);
		
		InstructorAttributes instructor = new InstructorAttributes();
		instructor.googleId = instructorGoogleId;
		instructor.courseId = courseId;
		instructor.email = courseCreator.email;
		instructor.name = courseCreator.name;
		
		try {
			instructorsLogic.createInstructor(instructor);
		} catch (Exception e) {
			//roll back the transaction
			coursesDb.deleteCourse(courseId);
			String errorMessage = "Unexpected exception while trying to create instructor for a new course "+ EOL 
					+ instructor.toString() + EOL
					+ TeammatesException.toStringWithStackTrace(e);
			Assumption.fail(errorMessage);
		}
	}

	public CourseAttributes getCourse(String courseId) {
		return coursesDb.getCourse(courseId);
	}

	public boolean isCoursePresent(String courseId) {
		return coursesDb.getCourse(courseId) != null;
	}
	
	public boolean isSampleCourse(String courseId) {
		return courseId.matches(FieldValidator.REGEX_SAMPLE_COURSE_ID);
	}
	
	public void verifyCourseIsPresent(String courseId) throws EntityDoesNotExistException{
		if (!isCoursePresent(courseId)){
			throw new EntityDoesNotExistException("Course does not exist :"+courseId);
		}
	}

	public CourseDetailsBundle getCourseDetails(String courseId) 
			throws EntityDoesNotExistException {
		CourseDetailsBundle courseSummary = getCourseSummary(courseId);

		ArrayList<EvaluationDetailsBundle> evaluationList = 
				evaluationsLogic.getEvaluationsDetailsForCourse(courseSummary.course.id);
		
		for (EvaluationDetailsBundle edd : evaluationList) {
			courseSummary.evaluations.add(edd);
		}

		return courseSummary;
	}

	public List<CourseDetailsBundle> getCourseDetailsListForStudent(
			String googleId) throws EntityDoesNotExistException {
		
		List<CourseAttributes> courseList = getCoursesForStudentAccount(googleId);
		List<CourseDetailsBundle> courseDetailsList = new ArrayList<CourseDetailsBundle>();
		
		for (CourseAttributes c : courseList) {

			StudentAttributes s = studentsLogic.getStudentForGoogleId(c.id, googleId);
			
			if (s == null) {
				//TODO Remove excessive logging after the reason why s can be null is found
				String logMsg = "Student is null in CoursesLogic.getCourseDetailsListForStudent(String googleId)"
						+ "<br/> Student Google ID: " + googleId
						+ "<br/> Course: " + c.id
						+ "<br/> All Courses Retrieved using the Google ID:";
				for (CourseAttributes course : courseList) {
					logMsg += "<br/>" + course.id;
				}
				log.severe(logMsg);
				
				//TODO Failing might not be the best course of action here. 
				//Maybe throw a custom exception and tell user to wait due to eventual consistency?
				Assumption.assertNotNull("Student should not be null at this point.", s);
			}
			
			List<EvaluationAttributes> evaluationDataList = evaluationsLogic
					.getEvaluationsForCourse(c.id);			
			List<FeedbackSessionAttributes> feedbackSessionList = 
					feedbackSessionsLogic.getFeedbackSessionsForUserInCourse(c.id, s.email);
			
			CourseDetailsBundle cdd = new CourseDetailsBundle(c);
			
			for (EvaluationAttributes ed : evaluationDataList) {
				EvaluationDetailsBundle edd = new EvaluationDetailsBundle(ed);
				log.fine("Adding evaluation " + ed.name + " to course " + c.id);
				if (ed.getStatus() != EvalStatus.AWAITING) {
					cdd.evaluations.add(edd);
				}
			}
			for (FeedbackSessionAttributes fs : feedbackSessionList) {
				cdd.feedbackSessions.add(new FeedbackSessionDetailsBundle(fs));
			}
			
			courseDetailsList.add(cdd);
		}
		return courseDetailsList;
	}

	public CourseDetailsBundle getTeamsForCourse(String courseId) 
			throws EntityDoesNotExistException {
		//TODO: change the return type to List<TeamDetailsBundle>
		
		List<StudentAttributes> students = studentsLogic.getStudentsForCourse(courseId);
		StudentAttributes.sortByTeamName(students);
	
		CourseAttributes course = getCourse(courseId);
	
		if (course == null) {
			throw new EntityDoesNotExistException("The course " + courseId
					+ " does not exist");
		}
		
		CourseDetailsBundle cdd = new CourseDetailsBundle(course);
	
		TeamDetailsBundle team = null;
		for (int i = 0; i < students.size(); i++) {
	
			StudentAttributes s = students.get(i);
	
			// if loner
			if (s.team.equals("")) {
				cdd.loners.add(s);
				// first student of first team
			} else if (team == null) {
				team = new TeamDetailsBundle();
				team.name = s.team;
				team.students.add(s);
				// student in the same team as the previous student
			} else if (s.team.equals(team.name)) {
				team.students.add(s);
				// first student of subsequent teams (not the first team)
			} else {
				cdd.teams.add(team);
				team = new TeamDetailsBundle();
				team.name = s.team;
				team.students.add(s);
			}
	
			// if last iteration
			if (i == (students.size() - 1)) {
				cdd.teams.add(team);
			}
		}
	
		return cdd;
	}

	public int getNumberOfTeams(String courseID) throws EntityDoesNotExistException {

		List<StudentAttributes> studentDataList = 
				studentsLogic.getStudentsForCourse(courseID);

		List<String> teamNameList = new ArrayList<String>();

		for (StudentAttributes sd : studentDataList) {
			if (!teamNameList.contains(sd.team)) {
				teamNameList.add(sd.team);
			}
		}

		return teamNameList.size();
	}

	public int getTotalEnrolledInCourse(String courseId) throws EntityDoesNotExistException {
		return studentsLogic.getStudentsForCourse(courseId).size();
	}

	public int getTotalUnregisteredInCourse(String courseID) {
		return studentsLogic.getUnregisteredStudentsForCourse(courseID).size();
	}


	public CourseDetailsBundle getCourseSummary(String courseId)
			throws EntityDoesNotExistException {
		CourseAttributes cd = coursesDb.getCourse(courseId);

		if (cd == null) {
			throw new EntityDoesNotExistException("The course does not exist: "
					+ courseId);
		}

		CourseDetailsBundle cdd = getTeamsForCourse(courseId);
		cdd.stats.teamsTotal = getNumberOfTeams(cd.id);
		cdd.stats.studentsTotal = getTotalEnrolledInCourse(cd.id);
		cdd.stats.unregisteredTotal = getTotalUnregisteredInCourse(cd.id);
		return cdd;
	}
	
	public CourseSummaryBundle getCourseSummaryWithoutStats(String courseId)
			throws EntityDoesNotExistException {
		CourseAttributes cd = coursesDb.getCourse(courseId);

		if (cd == null) {
			throw new EntityDoesNotExistException("The course does not exist: "
					+ courseId);
		}

		CourseSummaryBundle cdd = new CourseSummaryBundle(cd);
		return cdd;
	}
	
	public List<CourseAttributes> getCoursesForStudentAccount(String googleId) throws EntityDoesNotExistException {
		
		List<StudentAttributes> studentDataList = studentsLogic.getStudentsForGoogleId(googleId);
		
		if (studentDataList.size() == 0) {
			throw new EntityDoesNotExistException("Student with Google ID "
					+ googleId + " does not exist");
		}
		
		ArrayList<CourseAttributes> courseList = new ArrayList<CourseAttributes>();

		for (StudentAttributes s : studentDataList) {
			CourseAttributes course = coursesDb.getCourse(s.course);
			if(course==null){
				log.warning(
						"Course was deleted but the Student still exists :"+Const.EOL 
						+ s.toString());
			}else{
				courseList.add(course);
			}
		}
		return courseList;
	}
	
	public HashMap<String, CourseDetailsBundle> getCourseSummariesForInstructor(String googleId) {
		
		List<InstructorAttributes> instructorAttributesList = instructorsLogic.getInstructorsForGoogleId(googleId);
		
		HashMap<String, CourseDetailsBundle> courseSummaryList = new HashMap<String, CourseDetailsBundle>();
		
		for (InstructorAttributes ia : instructorAttributesList) {
			CourseAttributes course = coursesDb.getCourse(ia.courseId);
			
			try {
				courseSummaryList.put(course.id, getCourseSummary(course.id));
			} catch (EntityDoesNotExistException e) {
				log.warning("Course was deleted but the Instructor still exists: "+Const.EOL 
						+ ia.toString());
			}
		}
		
		return courseSummaryList;
	}

	public HashMap<String, CourseDetailsBundle> getCoursesDetailsForInstructor(
			String instructorId) throws EntityDoesNotExistException {
		
		HashMap<String, CourseDetailsBundle> courseList = 
				getCourseSummariesForInstructor(instructorId);
		
		ArrayList<EvaluationDetailsBundle> evaluationList = 
				evaluationsLogic.getEvaluationsDetailsForInstructor(instructorId);
		List<FeedbackSessionDetailsBundle> feedbackSessionList = 
				feedbackSessionsLogic.getFeedbackSessionDetailsForInstructor(instructorId);
		
		for (EvaluationDetailsBundle edd : evaluationList) {
			CourseDetailsBundle courseSummary = courseList.get(edd.evaluation.courseId);
			courseSummary.evaluations.add(edd);
		}
		for (FeedbackSessionDetailsBundle fsb : feedbackSessionList) {
			CourseDetailsBundle courseSummary = courseList.get(fsb.feedbackSession.courseId);
			courseSummary.feedbackSessions.add(fsb);
		}
		return courseList;
	}
	
	public HashMap<String, CourseSummaryBundle> getCoursesSummaryWithoutStatsForInstructor(
			String instructorId) throws EntityDoesNotExistException {
		
		HashMap<String, CourseSummaryBundle> courseList = 
				getCourseSummaryWithoutStatsForInstructor(instructorId);
		
		ArrayList<EvaluationAttributes> evaluationList = 
				evaluationsLogic.getEvaluationsListForInstructor(instructorId);
		List<FeedbackSessionAttributes> feedbackSessionList = 
				feedbackSessionsLogic.getFeedbackSessionsListForInstructor(instructorId);
		
		for (EvaluationAttributes edd : evaluationList) {
			CourseSummaryBundle courseSummary = courseList.get(edd.courseId);
			courseSummary.evaluations.add(edd);
		}
		for (FeedbackSessionAttributes fsb : feedbackSessionList) {
			CourseSummaryBundle courseSummary = courseList.get(fsb.courseId);
			courseSummary.feedbackSessions.add(fsb);
		}
		return courseList;
	}

	public void deleteCourseCascade(String courseId) {
		evaluationsLogic.deleteEvaluationsForCourse(courseId);
		studentsLogic.deleteStudentsForCourse(courseId);
		instructorsLogic.deleteInstructorsForCourse(courseId);
		feedbackSessionsLogic.deleteFeedbackSessionsForCourse(courseId);
		coursesDb.deleteCourse(courseId);
	}
	
	private HashMap<String, CourseSummaryBundle> getCourseSummaryWithoutStatsForInstructor(String googleId) {
		
		List<InstructorAttributes> instructorAttributesList = instructorsLogic.getInstructorsForGoogleId(googleId);
		
		HashMap<String, CourseSummaryBundle> courseSummaryList = new HashMap<String, CourseSummaryBundle>();
		
		for (InstructorAttributes ia : instructorAttributesList) {
			CourseAttributes course = coursesDb.getCourse(ia.courseId);
			
			try {
				courseSummaryList.put(course.id, getCourseSummaryWithoutStats(course.id));
			} catch (EntityDoesNotExistException e) {
				log.warning("Course was deleted but the Instructor still exists: "+Const.EOL 
						+ ia.toString());
			}
		}
		
		return courseSummaryList;
	}

}
