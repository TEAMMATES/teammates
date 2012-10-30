package teammates.test.scripts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import teammates.common.Common;
/**
 * Class that create a json data file to be used with ImportData script
 * The result file will be saved in src/test/resources/data/ folder.
 * 
 * @James: This scripts does not use any teamamtes's data structures or json framework for some reasons:
 * 
 * First - For 5000 or more students, it will consume a lot of memory. Need to store only id
 * of the objects to save memory.
 * 
 * Second - To make this script portable, if you remove the teammates.common.Common import, and define
 * the path yourself, this become a totally portable script.(this speeds up the process of creating data)
 * 
 * Third - The format of data bundle is json but quite "strange", no relationships, no arrays.
 * 
 */

public class DataGenerator {
	// Name of the result file, please do not override existing file
	public static final String FILE_NAME = "ResultFileName.json";
	// Prefix used in all entities
	public static final String PREFIX = "D1_";
	
	public static final Integer NUM_OF_COORDINATORS = 1;
	public static final Integer NUM_OF_STUDENTS = 1000;
	
	public static final Integer MIN_NUM_OF_COURSES_PER_COORD =0;
	public static final Integer MAX_NUM_OF_COURSES_PER_COORD =5;
	
	public static final Integer MIN_NUM_OF_STUDENTS_PER_COURSE =30;
	public static final Integer AVERAGE_NUM_OF_STUDENTS_PER_COURSE = 100;
	public static final Integer STANDARD_DEVIATION_STUDENT_PER_COURSE = 100;
	public static final Integer MAX_NUM_OF_STUDENTS_PER_COURSE =300;
	
	public static final Integer MAX_TEAM_SIZE = 5;
	public static final Integer MIN_TEAM_SIZE = 3;
	
	public static final Integer MIN_ACTIVE_EVALUATION_PER_COURSE =0;
	public static final Integer MAX_ACTIVE_EVALUATION_PER_COURSE =1;

	public static final String START_TIME = "2012-04-01 11:59 PM";
	public static final String END_TIME_PASSED = "2012-07-30 11:59 PM";
	public static final String END_TIME_NOT_PASSED = "2013-012-30 11:59 PM";
	
	public static ArrayList<String> coords = new ArrayList<String>();
	public static ArrayList<String> courses = new ArrayList<String>();
	public static ArrayList<String> studentEmails = new ArrayList<String>();
	public static ArrayList<String> students = new ArrayList<String>();
	public static ArrayList<String> evaluations = new ArrayList<String>();
	public static ArrayList<ArrayList<String>> teams = new ArrayList<ArrayList<String>>();
	public static ArrayList<Dictionary<String, String>> submissions = new ArrayList<Dictionary<String, String>>();
	
	public static Random random = new Random();

	public static void main(String[] args) throws IOException {
		String data = generateData();
		writeDataToFile(data,Common.TEST_DATA_FOLDER +"/"+ FILE_NAME);
	}
	
	/**
	 * Write data to file, create new file if necessary
	 * 
	 * @param data - Data string to write
	 * @param filePath - path to file
	 * @throws IOException
	 */
	public static void writeDataToFile(String data,String filePath) throws IOException {
		File f;
		f=new File(filePath);
		// Create file if it does not exist
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//get the file writer
		BufferedWriter out;
		FileWriter fstream = new FileWriter(filePath);
		out = new BufferedWriter(fstream);
		out.write(data);
		out.close();
		
	}

	/**
	 * Create data
	 * 
	 */
	public static String generateData() {
		System.out.println("Start generating data!");
		//Create students
		for (int i = 0; i< NUM_OF_STUDENTS; i ++) {
			studentEmails.add(PREFIX+"Stu"+i+"Email@gmail.com");
		}
		
		//Create coordinators
		for (int i = 0; i < NUM_OF_COORDINATORS; i++) {
			String coordName = "Coo"+i;
			coords.add(coordName);
			generateDataForCoord(coordName);
		}
		System.out.println("Done gererating data!");
		
		//Create output string
		String data = output();
		return data;
	}
	
	/**
	 * Randomly create courses, students and evaluations for a particular coordinator
	 * @param coordName
	 */
	public static void generateDataForCoord(String coordName) {
		//number of courses for this particular coordinator
		long numOfCourse = Math.round(random.nextInt(MAX_NUM_OF_COURSES_PER_COORD - MIN_NUM_OF_COURSES_PER_COORD+1)
				+MIN_NUM_OF_COURSES_PER_COORD);
		for (int j =0 ; j < numOfCourse; j ++)
		{
			// Add a course
			String courseName = "Cou"+j + "_of_"+coordName;
			courses.add(courseName);
			
			// Add students to this course
			generateStudentsDataForCourse(courseName);
			
			// Add evaluation for this course
			Integer numerOfActiveEval = (int) Math.round(random.nextInt
					(MAX_ACTIVE_EVALUATION_PER_COURSE - MIN_ACTIVE_EVALUATION_PER_COURSE+1)+MIN_ACTIVE_EVALUATION_PER_COURSE);
			
			for (int n = 0; n < numerOfActiveEval ; n ++)
			{
				String eval = "Eval"+n+"_in_"+courseName;
				evaluations.add(eval);	
			}
		}
	}
	
	/**
	 * Randomly create students for a particular course
	 * @param courseName
	 */
	public static void generateStudentsDataForCourse(String courseName) {
		//randomly get a size of this course
		long numOfStudent = numberOfStudentInCourse();
		
		//randomly pick student from list
		Set<Integer> studentIndexs = new HashSet<Integer>();
		while (studentIndexs.size() < numOfStudent) {
			studentIndexs.add(random.nextInt(NUM_OF_STUDENTS));
		}
		
		ArrayList<String> studentEmailInCourse = new ArrayList<String>();
		for (Integer integer : studentIndexs) {
			studentEmailInCourse.add(studentEmails.get(integer));
		}
		
		//Add teams
		int teamCount = 1;
		while(studentEmailInCourse.size() >0) {
			long teamSize = Math.round(random.nextInt(MAX_TEAM_SIZE - MIN_TEAM_SIZE +1)+MIN_TEAM_SIZE);
			ArrayList<String> team = new ArrayList<String>();
			for(int k = 0; studentEmailInCourse.size() >0 && k < teamSize ; k ++) {
				
				String email =studentEmailInCourse.remove(0);
				
				//add to team, add to students;
				String studentIndex = email.split("Email@gmail.com")[0].split("Stu")[1];
				String studentID =PREFIX+"Stu"+studentIndex+"Team"+teamCount +"_in_"+courseName;
				
				students.add(studentID);
				team.add(studentID);
			}

			teamCount ++;
			teams.add(team);
		}
	}
	
	
	

	/**
	 * @return json string presenting the databundle 
	 */
	public static String output () {
		System.out.println("Start writing to file !");
		String output = "{\n";
		output += allCoords() + "\n\n";
		output += allCourses() + "\n\n";
		output += allStudents() + "\n\n";
		output += allEvaluations() + "\n\n";
		output += allSubmissions() + "\n\n}";
		
		System.out.println("Finish writing to file !");
		return output;
	}
	
	
	/**
	 * @return Json string presentation for all coordinators
	 */
	public static String allCoords() {
		String output = "\"coords\":{\n";
		for (int i = 0; i < coords.size(); i++) {
			String coord = PREFIX+coords.get(i);
			output+="\t"+coord(coord,"idOf_"+coord,"nameOf_"+coord, "emailOf_"+coord+"@gmail.com");
			if(i!=coords.size()-1)
				output+=",\n";
		}
		output+= "\n},";
		return output;
	}
	
	/**
	 * @return Json string presentation for all courses
	 */
	public static String allCourses(){
		String output = "\"courses\":{\n";
		for (int i = 0; i < courses.size(); i++) {
			
			String course = PREFIX+courses.get(i);
			String coord = PREFIX+course.split("_of_")[1];
			output+="\t"+course(course,"idOf_"+coord,"idOf_"+course, "nameOf_"+course);
			if(i!=courses.size()-1)
				output+=",\n";
		}
		output+= "\n},";
		return output;
	}
	
	/**
	 * @return Json string presentation for all students
	 */
	public static String allStudents(){
		String output = "\"students\":{\n";
		for (int i = 0; i < students.size(); i++) {
			String student = students.get(i);
			String index = student.split("Stu")[1].split("Team")[0];
			String team  = student.split("Team")[1].split("_")[0];
			String course = PREFIX+student.split("_in_")[1];
			String email = studentEmails.get(Integer.parseInt(index));
			output+="\t"+student(student, email, "Student "+index+ " in " +course,
					"Team "+team, email.split("@")[0], "comment", "idOf_"+course, "profile");
			if(i!=students.size()-1)
				output+=",\n";
		}
		output+= "\n},";
		return output;
	}
	/**
	 * @return Json string presentation for all evaluations
	 */
	public static String allEvaluations(){
		String output = "\"evaluations\":{\n";
		for (int i = 0; i < evaluations.size(); i++) {
			String eval = PREFIX+evaluations.get(i);
			String course = PREFIX+eval.split("_in_")[1];
			output+="\t"+evaluation("idOf_"+eval, "idOf_"+course, eval, 
					"instructions for "+eval, 10, true,
					START_TIME, END_TIME_NOT_PASSED, 8.0, true, false);
			if(i!=evaluations.size()-1)
				output+=",\n";
		}
		output+= "\n},";
		return output;
	}
	
	/**
	 * @return Json string presentation for all Submissions
	 */
	public static String allSubmissions(){
		String output = "\"submissions\":{\n";
		for (String eval : evaluations) {
			String courseOfEval = eval.split("_in_")[1];
			for (ArrayList<String> team : teams) {
				if (team.size() == 0) // Ignore if team's size is 0
					continue;
				String courseOfTeam = team.get(0).split("_in_")[1];
				if (courseOfEval.equals(courseOfTeam)){
					output += submissionForTeam(courseOfEval,eval,team);
				}
			}
		}
		output = output.substring(0,output.length() -2); //remove the last comma
		output+= "\n}";
		return output;
	}

	
	/**
	 *  Json string presentation for submissions for a particular team
	 * @param course - name of the course
	 * @param eval - name of the eval
	 * @param team - arrayList of student in the team 
	 * @return
	 */
	public static String submissionForTeam(String course,String eval,ArrayList<String> team) {
		String output = "";
		Integer subCount =0;
		while(team.size() >0){
			String stu = team.get(0);
			String teamIndex = stu.split("Team")[1].split("_in_")[0];
			String stuEmail = emailFromStudentId(stu);
			for (int i = 0 ; i < team.size() ; i++){
				String other = team.get(i);
				String otherEmail = emailFromStudentId(other);
				output += "\t"+submissions(PREFIX+"Sub"+subCount+"Team"+teamIndex+eval, stuEmail, otherEmail,
						"idOf_"+PREFIX+course,PREFIX+eval, 10, "justification", "p2p", "Team "+teamIndex);
				output += ",\n";
				subCount++;
				if (!stuEmail.equals(otherEmail)){
					output += "\t"+submissions(PREFIX+"Sub"+subCount+"Team"+teamIndex+eval, otherEmail, stuEmail,
							"idOf_"+PREFIX+course,PREFIX+eval, 10, "justification", "p2p", "Team "+teamIndex);
					output += ",\n";
					subCount++;
				}
			}
			team.remove(0);
		}
		return output;
	}
	

	/**
	 * @return Json string presentation for a coordinator entity
	 */
	public static String coord (String objName, String id,String name, String email) {
		String result = "\""+objName+"\":{";
		result += "\"id\":\""+id+"\",";
		result += "\"name\":\""+name+"\",";
		result += "\"email\":\""+email+"\"";
	  	result += "}";
	  	return result;
	}
	
	/**
	 * @return Json string presentation for a course entity
	 */
	public static String course (String objName, String coord,String id, String name){
		String result = "\""+objName+"\":{";
		result += "\"coord\":\""+coord+"\",";
		result += "\"id\":\""+id+"\",";
		result += "\"name\":\""+name+"\"";
	  	result += "}";
	  	return result;
	}
	
	/**
	 * @return Json string presentation for a student entity
	 */
	public static String student (String objName, String email,String name, 
			String team,String id, String comments , String course , String profile) {
		String result = "\""+objName+"\":{";
		result += "\"email\":\""+email+"\",";
		result += "\"name\":\""+name+"\",";
		result += "\"team\":\""+team+"\",";
		result += "\"id\":\""+id+"\",";
		result += "\"comments\":\""+comments+"\",";
		result += "\"course\":\""+course+"\",";
		result += "\"profile\":{\"value\": \""+name+"\"}";
	  	result += "}";
	  	return result;
	}
	
	/**
	 * @return Json string presentation for a evaluation entity
	 */
	public static String evaluation(String objName,String course, String name,String instructions,
			Integer gracePeriod,Boolean p2pEnabled, String startTime,String endTime,
			Double timeZone, Boolean activated, Boolean published) {
		String result = "\""+objName+"\":{";
		result += "\"course\":\""+course+"\",";
		result += "\"name\":\""+name+"\",";
		result += "\"instructions\":\""+instructions+"\",";
		result += "\"gracePeriod\":"+gracePeriod+",";
		result += "\"p2pEnabled\":"+p2pEnabled+",";
		result += "\"startTime\":\""+startTime+"\",";
		result += "\"endTime\":\""+endTime+"\",";
		result += "\"startTime\":\""+startTime+"\",";
		result += "\"timeZone\":"+timeZone+",";
		result += "\"activated\":"+activated+",";
		result += "\"published\":"+published+"";
	  	result += "}";
	  	return result;
	}
	/**
	 * @return Json string presentation for a submission entity
	 */
	public static String submissions(String objName,String reviewer,String reviewee,String course,String evaluation,
			Integer points,String justification,String p2pFeedback,String team) {
		String result = "\""+objName+"\":{";
		result += "\"reviewer\":\""+reviewer+"\",";
		result += "\"reviewee\":\""+reviewee+"\",";
		result += "\"course\":\""+course+"\",";
		result += "\"evaluation\":\""+evaluation+"\",";
		result += "\"points\":"+points+",";
		result += "\"justification\":{\"value\":\""+justification+"\"},";
		result += "\"p2pFeedback\":{\"value\":\""+p2pFeedback+"\"},";
		result += "\"team\":\""+team+"\"";
	  	result += "}";
	  	return result;
	}
	
	/*helper methods*/
	
	
	/**
	 * @param id - id of student
	 * @return email of that student
	 */
	public static String emailFromStudentId(String id) {
		String index = id.split("Team")[0].split("Stu")[1];
		return PREFIX+"Stu"+index+"Email@gmail.com";
	}
	
	/**
	 * @return a random number of student in course
	 */
	public static int numberOfStudentInCourse() {
		int num = 0;
		do {
			num = (int) Math.floor(random.nextGaussian()*(STANDARD_DEVIATION_STUDENT_PER_COURSE) + AVERAGE_NUM_OF_STUDENTS_PER_COURSE);
		} while (num > MAX_NUM_OF_STUDENTS_PER_COURSE || num < MIN_NUM_OF_STUDENTS_PER_COURSE);
		return num;
	}

}
