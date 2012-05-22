package teammates.testing.junit;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import teammates.Courses;
import teammates.Evaluations;
import teammates.exception.InvalidParametersException;
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
		s.randomizeCourseId();
		return s;
	}
	
	/***
	 * createCourse
	 * 
	 * @param courseID
	 * @param courseName
	 * @param googleID
	 * @throws InvalidParametersException 
	 */
	public void createCourse(String courseID, String courseName, String googleID) throws InvalidParametersException {
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
	
	public void prepareSubmissionData(Scenario scn) throws InvalidParametersException {
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
	
	public List<Integer> getPointListFromServerResponse(String data){
		List<Integer> pointList = new ArrayList<Integer>();
		try{
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    InputSource is = new InputSource();
		    is.setCharacterStream(new StringReader(data));

		    Document doc = db.parse(is);
		    NodeList nodes = doc.getElementsByTagName("points");
		    
		    
		    for(int i = 0; i < nodes.getLength(); i++){
		    	Element element = (Element) nodes.item(i);
		    	int point = Math.round(Float.parseFloat(getCharacterDataFromElement(element)));
		    	pointList.add(point);
		    	System.out.println("point: " + getCharacterDataFromElement(element));
		    }
		    
		    nodes = doc.getElementsByTagName("fromemail");
		    for(int i = 0; i < nodes.getLength(); i++){
		    	Element element = (Element) nodes.item(i);
		    	System.out.println("email from: " + getCharacterDataFromElement(element));
		    }
		    
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return pointList;
	}
	
	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}
	
	public String[] prepareSubmissionPoints(String submission) {
		String[] points = TMAPI.getSubmissionPoints(submission).split(", ");
		boolean isValid = false;
		for(int i = 0; i < points.length; i++){
			if(Integer.parseInt(points[i]) != 0){
				isValid = true;
				break;
			}
		}
		
		if(!isValid){
			for(int i = 0; i < points.length; i++){
				points[i] = "100";
			}
		}
		return points;
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
