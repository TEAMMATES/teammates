package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;


/**
 * Generate list of institutes and number of users per institute.
 */
public class StatisticsPerInstitute extends RemoteApiClient {
    
    //TODO: remove pm and use Datastore.initialize(); as done in GenerateFeedbackReport
    protected static final PersistenceManager pm = JDOHelper
            .getPersistenceManagerFactory("transactions-optional")
            .getPersistenceManager();
    
    private static final int INSTRUCTOR_INDEX = 0;
    private static final int STUDENT_INDEX = 1;
    private static final String UNKNOWN_INSTITUTE = "Unkonwn Institute";
    
    private HashMap<String, String> courseIdToInstituteMap = new HashMap<String, String>();
    
    public static void main(String[] args) throws IOException {
        StatisticsPerInstitute statistics = new StatisticsPerInstitute();
        statistics.doOperationRemotely();
    }
    
    
    @SuppressWarnings("unchecked")
    protected void doOperation() {
        
        String q = "SELECT FROM " + Student.class.getName();
        List<Student> allStudents = (List<Student>) pm.newQuery(q).execute();
        
        q = "SELECT FROM " + Instructor.class.getName();
        List<Instructor> allInstructors = (List<Instructor>) pm.newQuery(q).execute();
        
        
        List<InstituteStats> statsPerInstituteList = generateStatsPerInstitute(allStudents, allInstructors);
        String statsForUniqueStudentEmail =  generateUniqueStudentEmailStatsInWholeSystem(allStudents);
        String statsForUniqueInstructorEmail = generateUniqueInstructorEmailStatsInWholeSystem(allInstructors);
        
        print(statsPerInstituteList);
        System.out.println("\n\n" + "***************************************************" + "\n\n");
        System.out.println(statsForUniqueStudentEmail);
        
        System.out.println("\n\n" + "***************************************************" + "\n\n");
        System.out.println(statsForUniqueInstructorEmail);
    }
    
    
    @SuppressWarnings("unchecked")
    private String generateUniqueInstructorEmailStatsInWholeSystem(List<Instructor> allInstructors){
       
        HashSet<String> set = new HashSet<String>();
        int totalRealInstructor = 0;
        
        for(Instructor i: allInstructors){
            if(!isTestingInstructorData(i)){
                set.add(i.getEmail().toLowerCase());
                totalRealInstructor ++;
            }
            
        }
        
        String result = "===============Unique Instructor Emails===============\n"
                        + "Format=> Total Unique Emails [Total Emails]\n"
                        + "===================================================\n"
                        + set.size() + " [ " + totalRealInstructor + " ]\n";
        return result;
    }
    
    
    private boolean isTestingInstructorData(Instructor instructor){
        boolean isTestingData = false;
        
        if(instructor.getEmail().toLowerCase().endsWith(".tmt")){
            isTestingData = true;
        }       
        
        if(getInstituteForInstructor(instructor).contains("TEAMMATES Test Institute")){
            isTestingData = true;
        } 
        
        return isTestingData;
    }
    
    
    
    
    @SuppressWarnings("unchecked")
    private String generateUniqueStudentEmailStatsInWholeSystem(List<Student> allStudents){
        
        HashSet<String> set = new HashSet<String>();
        int totalRealStudent = 0;
        
        for(Student s: allStudents){
            if(!isTestingStudentData(s)){
                set.add(s.getEmail().toLowerCase());
                totalRealStudent ++;
            }
            
        }
        
        String result = "===============Unique Student Emails===============\n"
                        + "Format=> Total Unique Emails [Total Emails]\n"
                        + "===================================================\n"
                        + set.size() + " [ " + totalRealStudent + " ]\n";
        return result;
    }
    
    private boolean isTestingStudentData(Student student){
        boolean isTestingData = false;
        
        if(student.getEmail().toLowerCase().endsWith(".tmt")){
            isTestingData = true;
        }       
        
        if(getInstituteForStudent(student).contains("TEAMMATES Test Institute")){
            isTestingData = true;
        } 
        
        return isTestingData;
    }
    
    @SuppressWarnings("unchecked")
    private List<InstituteStats> generateStatsPerInstitute(List<Student> allStudents, List<Instructor> allInstructors){
        HashMap<String, HashMap<Integer, Integer>> institutes = new HashMap<String, HashMap<Integer, Integer>>();

        for (Instructor instructor : allInstructors){
            
            if(isTestingInstructorData(instructor)){               
                continue;
            }
            
            String institute = getInstituteForInstructor(instructor);
            
            if(!institutes.containsKey(institute)){               
                institutes.put(institute,
                               new HashMap<Integer, Integer>());
                institutes.get(institute).put(INSTRUCTOR_INDEX, 0);
                institutes.get(institute).put(STUDENT_INDEX, 0);
            }
            
            institutes.get(institute).put(INSTRUCTOR_INDEX,
                                          institutes.get(institute).get(INSTRUCTOR_INDEX) + 1);
        }

       
        
        for(Student student : allStudents){
            
            if(isTestingStudentData(student)){
                continue;
            }
            
            String institute = getInstituteForStudent(student);
            
            if(!institutes.containsKey(institute)){               
                institutes.put(institute,
                        new HashMap<Integer, Integer>());
                institutes.get(institute).put(INSTRUCTOR_INDEX, 0);
                institutes.get(institute).put(STUDENT_INDEX, 0);
            }
            
            institutes.get(institute).put(STUDENT_INDEX,
                                          institutes.get(institute).get(STUDENT_INDEX) + 1);
                                          
          
        }
                
        List<InstituteStats> statList = convertToList(institutes);
        sortByTotalStudentsDescending(statList);
        return statList;
    }
    
    @SuppressWarnings("unchecked")
    private String getInstituteForStudent(Student student){
        
        String institute = courseIdToInstituteMap.get(student.getCourseId());
        
        if(institute != null){
            return institute;
        } else{
            institute = UNKNOWN_INSTITUTE;
        }
        
        
        Query q = pm.newQuery(Instructor.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseId == courseIdParam");
        List<Instructor> instructorList = (List<Instructor>) q.execute(student.getCourseId());        
        
        institute = getInstituteForInstructors(instructorList);
        
        courseIdToInstituteMap.put(student.getCourseId(), institute);
        
        return institute;
        
    }
    
    private String getInstituteForInstructors(List<Instructor> instructorList){
        String institute = UNKNOWN_INSTITUTE;
        
        for(Instructor instructor : instructorList){
           
            if(getInstituteForInstructor(instructor) != null){
                institute = getInstituteForInstructor(instructor);
                break;
            }
           
        }
                
        return institute;
    }
    
    private String getInstituteForInstructor(Instructor instructor){
        
        String institute = UNKNOWN_INSTITUTE;
        
        if(instructor.getGoogleId() == null){
            return institute;
        }
        
        Account account = getAccountEntity(instructor.getGoogleId());
        if(account != null) {
            return account.getInstitute();
            
        } else {
            return institute;
        }
    }
    
    
    private Account getAccountEntity(String googleId) {
        
        try {
            Key key = KeyFactory.createKey(Account.class.getSimpleName(), googleId);
            Account account = pm.getObjectById(Account.class, key);
            
            if (JDOHelper.isDeleted(account)) {
                return null;
            } 
            
            return account;
            
        } catch (IllegalArgumentException iae){
            return null;            
        } catch(JDOObjectNotFoundException je) {
            return null;
        }
    }
    
    
    private boolean isTestingAccount(Account account){
        boolean isTestingAccount = false;
        
        if(account.getInstitute() != null && account.getInstitute().contains("TEAMMATES Test Institute")){
            isTestingAccount = true;
        }
        if(account.getEmail() != null && account.getEmail().toLowerCase().endsWith(".tmt")){
            isTestingAccount = true;
        }
        return isTestingAccount;
    }
    
    private void print(List<InstituteStats> statList) {
        System.out.println("===============Stats Per Institute=================");
        System.out.println("Format=> Instructors + Students = Total [Institute]");
        System.out.println("===================================================");
        int i = 0;
        int runningTotal = 0;
        for (InstituteStats stats : statList) {
            i++;
            int numInstructors = stats.instructorTotal;
            int numStudents = stats.studentTotal;
            int total = numInstructors + numStudents;
            runningTotal += total; 
            System.out.println(
                    "["+i+"]" + numInstructors + "+" + numStudents + "=" 
                            + total    + "{" + runningTotal + "}\t[" + stats.name + "]");
        }
        
    }

    private List<InstituteStats> convertToList(
            HashMap<String, HashMap<Integer, Integer>> institutes) {
        List<InstituteStats> list = new ArrayList<InstituteStats>();
        for (String insName : institutes.keySet()) {
            InstituteStats insStat = new InstituteStats();
            insStat.name = insName;
            insStat.studentTotal = institutes.get(insName).get(STUDENT_INDEX);
            insStat.instructorTotal = institutes.get(insName).get(INSTRUCTOR_INDEX);
            list.add(insStat);
        }
        return list;
    }
    
    private void sortByTotalStudentsDescending(List<InstituteStats> list){
        Collections.sort(list, new Comparator<InstituteStats>() {
            public int compare(InstituteStats inst1, InstituteStats inst2) {
                //the two objects are swapped, to sort in descending order
                return (new Integer(inst2.studentTotal).compareTo(new Integer(inst1.studentTotal)));
            }
        });
    }
    
    class InstituteStats{
        String name;
        int studentTotal;
        int instructorTotal;
        
        
    }
    
}
