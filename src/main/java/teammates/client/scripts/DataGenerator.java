package teammates.client.scripts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import teammates.test.driver.TestProperties;
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
    
    public static final Integer NUM_OF_COURSES = 5;
    public static final Integer NUM_OF_STUDENTS = 1000;
    
    public static final Integer MIN_NUM_OF_INSTRUCTOR_PER_COURSES = 1;
    public static final Integer MAX_NUM_OF_INSTRUCTOR_PER_COURSES = 3;
    
    public static final Integer MIN_NUM_OF_STUDENTS_PER_COURSE = 50;
    public static final Integer AVERAGE_NUM_OF_STUDENTS_PER_COURSE = 150;
    public static final Integer STANDARD_DEVIATION_STUDENT_PER_COURSE = 100;
    public static final Integer MAX_NUM_OF_STUDENTS_PER_COURSE = 250;
    
    public static final Integer MAX_TEAM_SIZE = 5;
    public static final Integer MIN_TEAM_SIZE = 3;
    
    public static final Integer MIN_ACTIVE_EVALUATION_PER_COURSE =0;
    public static final Integer MAX_ACTIVE_EVALUATION_PER_COURSE =0;

    public static final String START_TIME = "2012-04-01 11:59 PM UTC";
    public static final String END_TIME_PASSED = "2012-07-30 11:59 PM UTC";
    public static final String END_TIME_NOT_PASSED = "2013-012-30 11:59 PM UTC";

    public static ArrayList<String> courses = new ArrayList<String>();
    public static HashMap<String, String> instructors = new HashMap<String, String>();
    public static ArrayList<String> studentEmails = new ArrayList<String>();
    public static ArrayList<String> students = new ArrayList<String>();
    public static ArrayList<ArrayList<String>> teams = new ArrayList<ArrayList<String>>();
    
    public static Random random = new Random();

    public static void main(String[] args) throws IOException {
        String data = generateData();
        writeDataToFile(data,TestProperties.TEST_DATA_FOLDER +"/"+ FILE_NAME);
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
        
        // Create courses
        for (int i = 0; i < NUM_OF_COURSES; i++) {
            String courseName = "Course"+i;
            courses.add(courseName);
            generateDataForCourse(courseName);
        }
        System.out.println("Done generating data!");
        
        //Create output string
        String data = output();
        return data;
    }
    
    /**
     * Randomly create courses, students and evaluations for a particular instructor
     * @param instructorName
     */
    public static void generateDataForCourse(String courseName) {
        //number of courses for this particular instructor
        long numOfInstr = Math.round(random.nextInt(MAX_NUM_OF_INSTRUCTOR_PER_COURSES - MIN_NUM_OF_INSTRUCTOR_PER_COURSES+1)
                +MIN_NUM_OF_INSTRUCTOR_PER_COURSES);
        
        for (int j =0 ; j < numOfInstr; j ++) {
            // Add an Instructor
            String instrName = "Instr"+j + "_of_"+courseName;
            instructors.put(instrName, courseName);

        }
        
        // Add students to this course
        generateStudentsDataForCourse(courseName);
       
    }
    
    /**
     * Randomly create students for a particular course
     * @param courseName
     */
    public static void generateStudentsDataForCourse(String courseName) {
        // randomly get a number for student size for this course
        long numOfStudent = getDeviatedNumberOfStudentInCourse();
        //=====================================================================
        
        // randomly pick student indexes from global list to be put into this course
        Set<Integer> studentIndexs = new HashSet<Integer>();
        while (studentIndexs.size() < numOfStudent) {
            studentIndexs.add(random.nextInt(NUM_OF_STUDENTS));
        }
        
        ArrayList<String> studentEmailInCourse = new ArrayList<String>();
        for (Integer integer : studentIndexs) {
            studentEmailInCourse.add(studentEmails.get(integer));
        }
        //=====================================================================
        
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
        output += allAccounts() + "\n\n";
        output += allCourses() + "\n\n";
        output += allInstructors() + "\n\n";
        output += allStudents() + "\n\n";
        output += "}";
        
        System.out.println("Finish writing to file !");
        return output;
    }
    
    public static String allAccounts() {
        String output = "\"accounts\":{\n";
        for (String email : studentEmails) {
            email = email.split("@")[0];
            output+="\t"+account(email);
            output+=",\n";
        }
        output = output.substring(0,output.length()-2);
        output+= "\n},";
        return output;
    }
    
    /**
     * @return Json string presentation for all instructors
     */
    public static String allInstructors() {
        String output = "\"instructors\":{\n";
        for (String instructor : instructors.keySet()) {
            String course = PREFIX + instructors.get(instructor);
            instructor = PREFIX + instructor;
            output+="\t"+instructor(instructor,"googleIdOf_"+instructor,"courseIdOf_"+course,"nameOf_"+instructor, "emailOf_"+instructor+"@gmail.com");
            output+=",\n";
        }
        output = output.substring(0,output.length()-2);
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
            output+="\t"+course(course,"courseIdOf_"+course, "nameOf_"+course);
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
                    "Team "+team, email.split("@")[0], "comment", "courseIdOf_"+course, "profile");
            if(i!=students.size()-1)
                output+=",\n";
        }
        output+= "\n},";
        return output;
    }
    

    
    public static String account(String acc) {
        String result = "\""+acc+"\":{";
        result += "\"googleId\":\""+acc+"\",";
        result += "\"name\":\""+acc+"\",";
        result += "\"email\":\""+acc+"@gmail.com\",";
        result += "\"institute\":\"\"";
          result += "}";
          return result;
    }
    
    /**
     * @return Json string presentation for a instructor entity
     */
    public static String instructor (String objName, String googleId, String courseId, String name, String email) {
        String result = "\""+objName+"\":{";
        result += "\"googleId\":\""+googleId+"\",";
        result += "\"courseId\":\""+courseId+"\",";
        result += "\"name\":\""+name+"\",";
        result += "\"email\":\""+email+"\"";
          result += "}";
          return result;
    }
    
    /**
     * @return Json string presentation for a course entity
     */
    public static String course (String objName,String id, String name){
        String result = "\""+objName+"\":{";
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
    public static int getDeviatedNumberOfStudentInCourse() {
        int num = 0;
        do {
            num = (int) Math.floor(random.nextGaussian()*(STANDARD_DEVIATION_STUDENT_PER_COURSE) + AVERAGE_NUM_OF_STUDENTS_PER_COURSE);
        } while (num > MAX_NUM_OF_STUDENTS_PER_COURSE || num < MIN_NUM_OF_STUDENTS_PER_COURSE);
        return num;
    }

}
