package teammates.testing.junit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import teammates.Courses;
import teammates.Evaluations;
import teammates.jdo.Course;
import teammates.jdo.EnrollmentReport;
import teammates.jdo.Evaluation;
import teammates.jdo.Student;
import teammates.jdo.Submission;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

import com.google.appengine.api.datastore.Text;

/***
 * Base Test for JUnit
 * 
 * @author zds1989
 * @date 24 Mar 2012
 */
public class APITest {
	
	protected static Scenario scn = null;
	protected static PersistenceManager pm;
	

	public APITest() {
		
	}
	
	protected static Scenario setupBumpRatioScenarioInstance(String name, int index) {
		Scenario s = Scenario.scenarioForBumpRatioTest("target/test-classes/data/" + name + ".json", index);
		s.scrambleScenario();
		return s;
	}
	
	/***
	 * createCourse
	 * 
	 * @param courseID
	 * @param courseName
	 * @param googleID
	 */
	public void createCourse(String courseID, String courseName, String googleID) {
		Course c = new Course(courseID, courseName, googleID);
		pm.makePersistent(c);
	}
	
	/***
	 * 
	 * @param courseID
	 * @param students
	 */
	public void enrollStudents(String courseID, List<Student> students) {
		Courses courses = Courses.inst();
		Evaluations evaluations = Evaluations.inst();

		List<Student> currentStudentList = courses.getStudentList(courseID);
		
		if(evaluations.isEvaluationOngoing(courseID)) {
			for(Student s : students)
				for(Student cs : currentStudentList)
					if(s.getEmail().equals(cs.getEmail()) && !s.getTeamName().equals(cs.getTeamName()))
						s.setTeamName(cs.getTeamName());
		}
		List<EnrollmentReport> enrollmentReportList = new ArrayList<EnrollmentReport>();
		enrollmentReportList.addAll(courses.enrolStudents(students, courseID));
		
	}
	
	/***
	 * 
	 * @param eval
	 */
	public void openEvaluation(Evaluation eval) {
		Evaluations.inst().openEvaluation(eval.getCourseID(), eval.getName());
	}
	
	/***
	 * 
	 * @param eval
	 */
	public void closeEvaluation(Evaluation eval) {
		Evaluations.inst().closeEvaluation(eval.getCourseID(), eval.getName());
	}
	
	/***
	 * 
	 * @param eval
	 */
	public void publishEvaluation(Evaluation eval) {
		Courses courses = Courses.inst();
		List<Student> studentList = courses.getStudentList(eval.getCourseID());

		Evaluations.inst().publishEvaluation(eval.getCourseID(), eval.getName(), studentList);
	}
	
	public void unpublishEvaluation(Evaluation eval) {
		Evaluations.inst().unpublishEvaluation(eval.getCourseID(), eval.getName());
		
	}
	
	/***
	 * 
	 * @param students
	 * @param courseID
	 */
	public void studentsJoinCourse(List<Student> students, String courseID) {
		HashMap<String, Student> mapStudents = new HashMap<String, Student>();
		
		for(Student s: students)
			mapStudents.put(s.getEmail(), s);
		
		//Query all Students with CourseID received
		Query query = pm.newQuery(Student.class);
		query.setFilter("courseID == course_id");
		query.declareParameters("String course_id");
		@SuppressWarnings("unchecked")
		List<Student> dbStudents = (List<Student>) query.execute(courseID);
		
		for(Student dbStudent : dbStudents) {
			Student s = mapStudents.get(dbStudent.getEmail());
			if(s != null)
				dbStudent.setID(s.getID());
		}
		
		pm.makePersistentAll(dbStudents);
	}

	
	/***
	 * 
	 * @param courseID
	 * @param evalName
	 * @param submissinPoints
	 * @param students
	 */
	public void studentsSubmitDynamicFeedbacks(String courseID, String evalName, String[] submissionPoints, List<Student> students) {
		
		int studentIndex = 0;
		for (Student s : students) {
			String points = TMAPI.getSubmissionPoints(submissionPoints[studentIndex]);
			studentSubmitDynamicFeedbacks(courseID, evalName, points, s);
			studentIndex++;
		}
	}
	
	public void studentSubmitDynamicFeedbacks(String courseID, String evalName, String points, Student s) {
		String[] pointsArray = points.split(", ");

		Query studentQ = pm.newQuery(Student.class);
		studentQ.setFilter("courseID == course_id");
		studentQ.setFilter("teamName == team_name");
		studentQ.declareParameters("String course_id, String team_name");
		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) studentQ.execute(courseID, s.getTeamName());
	
		Query submissionQ = pm.newQuery(Submission.class);
		submissionQ.setFilter("courseID == course_id");
		submissionQ.setFilter("evaluationName == evaluation_name");
		submissionQ.setFilter("fromStudent == student_email");
		submissionQ.declareParameters("String course_id, String evaluation_name, String student_email");
		@SuppressWarnings("unchecked")
		List<Submission> submissionList = (List<Submission>) submissionQ.execute(courseID, evalName, s.getEmail());
		
		int position = 0;
		for (Submission submission : submissionList) {
			for (int i = 0; i < studentList.size(); i++) {
				if (submission.getToStudent().equalsIgnoreCase(studentList.get(i).getEmail()))
					position = i;
				int point = Integer.valueOf(pointsArray[position]);
				submission.setPoints(point);
				submission.setCommentsToStudent(new Text(String.format("This is a public comment from %s to %s.", s.getEmail(), submission.getToStudent())));
				submission.setJustification(new Text(String.format("This is a justification from %s to %s. ", s.getEmail(), submission.getToStudent())));
			}
		}
		
		System.out.println(pm.makePersistentAll(submissionList));
		
	}
	
	public void prepareSubmissionData(Scenario scn) {
		//prepare raw data
		Evaluation eval = new Evaluation(scn.evaluation.courseID, scn.evaluation.name, scn.evaluation.instructions, true, scn.evaluation.startTime, scn.evaluation.endTime, scn.evaluation.timezone, scn.evaluation.gracePeriod);
		List<Student> students = new ArrayList<Student>();
		for(int i = 0; i < scn.course.students.size(); i++) {
			String email = scn.course.students.get(i).email;
			String name = scn.course.students.get(i).name;
			String googleID = scn.course.students.get(i).google_id;
			String comments = scn.course.students.get(i).comments;
			String courseID = scn.course.students.get(i).courseID;
			String teamName = scn.course.students.get(i).teamName;
			students.add(new Student(email, name, googleID, comments, courseID, teamName));
		}
		
		//prepare datastore
		createCourse(scn.course.courseId, scn.course.courseName, scn.coordinator.username);
		enrollStudents(scn.course.courseId, students);
		createEvaluation(eval, students, scn.submissionPoints);
		
	}
	
	public void createEvaluation(Evaluation eval, List<Student> students, String[] submissionPoints) {
		
		try{
			//create evaluation
			pm.makePersistent(eval);
			
			//create submissions
			List<Submission> submissionList = new ArrayList<Submission>();
			Submission submission = null;
			int studentIndex = 0;
			for(Student sx: students) {
				String points = TMAPI.getSubmissionPoints(submissionPoints[studentIndex]);
				String[] pointsArray = points.split(", ");
				
				int position = 0;
				for(Student sy: students) {
					if(sx.getTeamName().equals(sy.getTeamName())) {
						submission = new Submission(sx.getEmail(), sy.getEmail(), eval.getCourseID(), eval.getName(), sx.getTeamName());
						submission.setCommentsToStudent(new Text(String.format("This is a public comment from %s to %s.", sx.getEmail(), sy.getEmail())));
						submission.setJustification(new Text(String.format("This is a justification for %s", sx.getEmail())));
						
						int point = Integer.valueOf(pointsArray[position++]);
						submission.setPoints(point);
						submissionList.add(submission);
					}
				}
				studentIndex++;
			}
			
			pm.makePersistentAll(submissionList);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/***
	 * helper function
	 * format of submissionPoints[0] = "Original: 100, 100, 100; ..."
	 * @param submissionPoints
	 * @return
	 */
	public int getTeamSizeFromSubmissionPoints(String[] submissionPoints) {
		return TMAPI.getSubmissionPoints(submissionPoints[0]).split(", ").length;
	}
	
	
	//debug functions--------------------------------------------------------------------------------------
	public void debug(String msg) {
		System.out.println("this is debug message:================" + msg);
	}
	
	public void printSubmission(Submission sub) {
		System.out.println("[submission] course: " + sub.getCourseID() 
			+ " evaluation: " + sub.getEvaluationName() 
			+ " point: " + sub.getPoints() 
			+ " from: " + sub.getFromStudent() 
			+ " to: " + sub.getToStudent()
			+ " comments: " + sub.getCommentsToStudent()
			+ " justification: " + sub.getJustification());
	}
	
}
