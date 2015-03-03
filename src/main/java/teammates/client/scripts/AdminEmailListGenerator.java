package teammates.client.scripts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;

public class AdminEmailListGenerator extends RemoteApiClient {
    
    private static enum StudentStatus{REG, UNREG, ALL};
    private static enum InstructorStatus{REG, UNREG, ALL};
    
    private EmailListConfig emailListConfig = new EmailListConfig();
    private HashMap<String, Date> CourseIdToCreatedDateMap = new HashMap<String, Date>();
    
    protected static final PersistenceManager pm = JDOHelper
                                                   .getPersistenceManagerFactory("transactions-optional")
                                                   .getPersistenceManager();
    
    public static void main(String[] args) throws IOException {
        AdminEmailListGenerator adminEmailListGenerator = new AdminEmailListGenerator();
        adminEmailListGenerator.doOperationRemotely();
    }

    @SuppressWarnings("unchecked")
    protected void doOperation() {
        
        getInstructorEmailConfiguration();
        getStudentEmailConfiguration();     
        printToFile();  
        
        System.out.print("\n\nstudent : " + emailListConfig.student + "\n");       
        if( emailListConfig.studentCreatedDateRangeStart !=null){
            System.out.print("student start : " + emailListConfig.studentCreatedDateRangeStart.toString() + "\n");
        }
        
        if( emailListConfig.studentCreatedDateRangeEnd !=null){
            System.out.print("student end : " + emailListConfig.studentCreatedDateRangeEnd.toString() + "\n");
        }
        System.out.print("instructor : " + emailListConfig.instructor + "\n");
        
        if( emailListConfig.instructorCreatedDateRangeStart !=null){
            System.out.print("instructor start : " + emailListConfig.instructorCreatedDateRangeStart.toString() + "\n");
        } 
        
        if( emailListConfig.instructorCreatedDateRangeEnd !=null){
            System.out.print("instructor end : " + emailListConfig.instructorCreatedDateRangeEnd.toString() + "\n");
        }
    }
    
    private void getInstructorEmailConfiguration(){
        int needInstructor = getUserInputForCommand("send to instructor ? 1.Yes 2.No", 2);
        
        if(needInstructor == 1){
            emailListConfig.instructor = true;
            
            int regStatus = getUserInputForCommand("1.Registered Only 2.Unregistered Only 3.All", 3);
            switch (regStatus){
                case 1:
                    emailListConfig.instructorStatus = InstructorStatus.REG;
                    break;
                case 2:
                    emailListConfig.instructorStatus = InstructorStatus.UNREG;
                    break;
                case 3:
                    emailListConfig.instructorStatus = InstructorStatus.ALL;
                    break;
                default :
                    break;  
            };
            
            int dateRangeStatus = getUserInputForCommand("[Instructor]Created Date Range Option : \n" +
                                                            "1. All time\n" +
                                                            "2. After a specific date : [your input] ~ now \n" +
                                                            "3. Before a specific date : ~ [your input] \n" + 
                                                            "4. Within an interval : [your input start] ~ [your input end]", 4);
           
                
            switch (dateRangeStatus){
            case 1:
                break;
            case 2:
                getInputDate(true, true);
                break;
            case 3:
                getInputDate(false, true);
                break;
            case 4:
            default: 
                getInputDate(true, true);
                getInputDate(false, true);
                break;
        }
        }    
    }
    
    private void getStudentEmailConfiguration(){
        int needStudent = getUserInputForCommand("send to student ? 1.Yes 2.No", 2);
        
        if(needStudent == 1){
            emailListConfig.student = true;
            
            int regStatus = getUserInputForCommand("1.Registered Only 2.Unregistered Only 3.All", 3);
            switch (regStatus){
                case 1:
                    emailListConfig.studentStatus = StudentStatus.REG;
                    break;
                case 2:
                    emailListConfig.studentStatus = StudentStatus.UNREG;
                    break;
                case 3:
                    emailListConfig.studentStatus = StudentStatus.ALL;
                    break;
                default :
                    break;  
            }; 
            
            
            int dateRangeStatus = getUserInputForCommand("[Student]Created Date Range Option : \n" +
                                                            "1. All time\n" +
                                                            "2. After a specific date : [your input] ~ now \n" +
                                                            "3. Before a specific date : ~ [your input] \n" + 
                                                            "4. Within an interval : [your input start] ~ [your input end]", 4);

            
            switch (dateRangeStatus){
                case 1:
                    break;
                case 2:
                    getInputDate(true, false);
                    break;
                case 3:
                    getInputDate(false, false);
                    break;
                case 4:
                default: 
                    getInputDate(true, false);
                    getInputDate(false, false);
                    break;
            }
            
        }     
       
    }
    
    
    private void getInputDate(boolean isStart, boolean isInstructor){
        
        String cmdStr = isStart ? "start" : "end";
        int year;
        int month;
        int day;
        
        boolean isDateValid = false;
        
        do{

            System.out.print("****** Set " + cmdStr + " date: ******* \n");
            
            
            year = getUserInputForCommand("Year ?", Integer.MAX_VALUE);
            
            
            
            month = getUserInputForCommand("Month ? \n" +
                                                  "1. Jan " + 
                                                  "2. Feb " + 
                                                  "3. Mar " + 
                                                  "4. Apr " + 
                                                  "5. May " + 
                                                  "6. Jun " + 
                                                  "7. Jul " + 
                                                  "8. Aug " + 
                                                  "9. Sep " + 
                                                  "10. Oct " + 
                                                  "11. Nov " + 
                                                  "12. Dec ", 12);
            
            day = getUserInputForCommand("Day ?", 31);   
            
            isDateValid = isValidDate(day, month, year);
            
            
        }while(!isDateValid);     
      
        Date date = getDate(day, month, year);
        
        System.out.print(date.toGMTString()+ "\n");
        
        if(isInstructor){
            if(isStart){
                
               emailListConfig.instructorCreatedDateRangeStart = date;
            } else {
                emailListConfig.instructorCreatedDateRangeEnd = date;
            }
        } else {
            
            if(isStart){
                emailListConfig.studentCreatedDateRangeStart = date;
            } else {
                emailListConfig.studentCreatedDateRangeEnd = date;
            }
        }
        
    }
    
   
    
    private int getUserInputForCommand(String cmd, int upperLimit){
        
        int validatedUserInput = 0;
        boolean isUserInputValid = false;  
        do{
            System.out.print("******************************\n" + 
                             cmd + "\n" + 
                             "******************************\n"); 
            Scanner reader = new Scanner(System.in);
            String input = reader.nextLine();
            try{
                int userInput = Integer.parseInt(input);
                if(userInput > 0 && userInput <= upperLimit){
                    isUserInputValid = true;
                    validatedUserInput = userInput;
                } else {
                    isUserInputValid = false;
                }
                
            } catch(NumberFormatException e) {
                isUserInputValid = false;
            }
        } while (!isUserInputValid);
        
        return validatedUserInput;
    }
    
    private void printToFile() {     
        
        HashSet<String> studentEmailSet = new HashSet<String>();
        HashSet<String> instructorEmailSet = new HashSet<String>();
        
        if(!emailListConfig.student && !emailListConfig.instructor){
            System.out.print("No email list to be generated. Exiting now..\n\n");
            return;
        }
        
        try {

//            File statText = new File("C:\\Users\\Mo\\Desktop\\" + this.getCurrentDateForDisplay() + ".txt");
            File statText = new File("C:\\Users\\Mo\\Desktop\\" + "aaa" + ".txt");
            FileOutputStream is = new FileOutputStream(statText);
            OutputStreamWriter osw = new OutputStreamWriter(is);    
            Writer w = new BufferedWriter(osw);
            
            if(emailListConfig.student){
                String q = "SELECT FROM " + Student.class.getName();
                List<Student> allStudents = (List<Student>) pm.newQuery(q).execute();

                for(Student student : allStudents){
                    if((student.isRegistered() && emailListConfig.studentStatus == StudentStatus.REG) ||
                       (!student.isRegistered() && emailListConfig.studentStatus == StudentStatus.UNREG) ||
                       (emailListConfig.studentStatus == StudentStatus.ALL)){
                        
                        if(isStudentCreatedInRange(student)){
                            studentEmailSet.add(student.getEmail());
                        }
                    }
                  
                }
            }
            
            
            if(emailListConfig.instructor){
                String q = "SELECT FROM " + Instructor.class.getName();
                List<Instructor> allInstructors = (List<Instructor>) pm.newQuery(q).execute();
                
                int i = 0;
                
                for(Instructor instructor : allInstructors){
                    if((instructor.getGoogleId() != null  && emailListConfig.instructorStatus == InstructorStatus.REG) ||
                       (instructor.getGoogleId() == null && emailListConfig.instructorStatus == InstructorStatus.UNREG) ||
                       (emailListConfig.instructorStatus == InstructorStatus.ALL)){
                        
                        if(isInstructorCreatedInRange(instructor)){
                            instructorEmailSet.add(instructor.getEmail());
                        }
                    }
                  
                }
            }
            
           
            
            if(studentEmailSet.size() > 0){
                for(String email : studentEmailSet){
                    w.write(email + ",");
                }
            } 
            
            if(instructorEmailSet.size() > 0){
                for(String email : instructorEmailSet){
                    w.write(email + ",");
                }
            }
            
            System.out.print("Student email num: " + studentEmailSet.size() + "\n");
            System.out.print("Instructor email num: " + instructorEmailSet.size() + "\n");
            
            w.close();
        } catch (IOException e) {
            System.err.println("Problem writing to the file statsTest.txt");
        }
    }
    
    @SuppressWarnings("deprecation")
    private boolean isInstructorCreatedInRange(Instructor instructor){
        
        Date instructorCreatedAtOriginal = getInstructorCreatedDate(instructor);
        
       
        if(instructorCreatedAtOriginal == null){
            return false;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(instructorCreatedAtOriginal.getYear() + 1900,
                instructorCreatedAtOriginal.getMonth(),
                instructorCreatedAtOriginal.getDate(), 
                0, 0, 0);
        
        Date instructorCreatedAt = cal.getTime();
        
        if (emailListConfig.instructorCreatedDateRangeEnd == null &&
            emailListConfig.instructorCreatedDateRangeStart == null ){
            //no range set
            return true;
        } else if(emailListConfig.instructorCreatedDateRangeStart != null &&
                  emailListConfig.instructorCreatedDateRangeEnd == null){
            //after a specific date
            if(instructorCreatedAt.after(emailListConfig.instructorCreatedDateRangeStart)){
                return true;
            } else {
                return false;
            }
            
        } else if(emailListConfig.instructorCreatedDateRangeStart == null &&
                emailListConfig.instructorCreatedDateRangeEnd != null){
            //before a specific date
            if(instructorCreatedAt.before(emailListConfig.instructorCreatedDateRangeEnd)){
                return true;
            } else {
                return false;
            }
            
        } else if(emailListConfig.instructorCreatedDateRangeStart != null &&
                emailListConfig.instructorCreatedDateRangeEnd != null){
            //within a date interval   
            if(instructorCreatedAt.after(emailListConfig.instructorCreatedDateRangeStart) &&
               instructorCreatedAt.before(emailListConfig.instructorCreatedDateRangeEnd)){
                return true;
            } else {
                return false;
            }
        }
        
        return false;
        
    }

    private Date getInstructorCreatedDate(Instructor instructor){
    
        if(instructor.getGoogleId() != null && !instructor.getGoogleId().isEmpty()){
            Account account = getAccountEntity(instructor.getGoogleId());
            if (account != null){
                return account.getCreatedAt();
            }
        }
        
        if(CourseIdToCreatedDateMap.get(instructor.getCourseId()) != null){
            return CourseIdToCreatedDateMap.get(instructor.getCourseId());
        }
        
        Course course = getCourseEntity(instructor.getCourseId());
        
        if(course != null){
            CourseIdToCreatedDateMap.put(instructor.getCourseId(), course.getCreatedAt());
            return course.getCreatedAt();
        }
        
        return null;
        
}
    
    @SuppressWarnings("deprecation")
    private boolean isStudentCreatedInRange(Student student){
        
        Date studentCreatedAtOriginal = getStudentCreatedDate(student);

        
        if(studentCreatedAtOriginal == null){
            return false;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(studentCreatedAtOriginal.getYear() + 1900,
                studentCreatedAtOriginal.getMonth(),
                studentCreatedAtOriginal.getDate(), 
                0, 0, 0);
        
        Date studentCreatedAt = cal.getTime();
        
        if (emailListConfig.studentCreatedDateRangeEnd == null &&
            emailListConfig.studentCreatedDateRangeStart == null ){
            //no range set
            return true;
        } else if(emailListConfig.studentCreatedDateRangeStart != null &&
                  emailListConfig.studentCreatedDateRangeEnd == null){
            //after a specific date
            if(studentCreatedAt.after(emailListConfig.studentCreatedDateRangeStart)){
                return true;
            } else {
                return false;
            }
            
        } else if(emailListConfig.studentCreatedDateRangeStart == null &&
                emailListConfig.studentCreatedDateRangeEnd != null){
            //before a specific date
            if(studentCreatedAt.before(emailListConfig.studentCreatedDateRangeEnd)){
                return true;
            } else {
                return false;
            }
            
        } else if(emailListConfig.studentCreatedDateRangeStart != null &&
                emailListConfig.studentCreatedDateRangeEnd != null){
            //within a date interval   
            if(studentCreatedAt.after(emailListConfig.studentCreatedDateRangeStart) &&
               studentCreatedAt.before(emailListConfig.studentCreatedDateRangeEnd)){
                return true;
            } else {
                return false;
            }
        }
        
        return false;
        
    }
    
    private Date getStudentCreatedDate(Student student){
        if(student.getGoogleId() != null && !student.getGoogleId().isEmpty()){
            Account account = getAccountEntity(student.getGoogleId());
            if (account != null){
                return account.getCreatedAt();
            }
        }
        
        if(CourseIdToCreatedDateMap.get(student.getCourseId()) != null){
            return CourseIdToCreatedDateMap.get(student.getCourseId());
        }
        
        Course course = getCourseEntity(student.getCourseId());
        
        if(course != null){
            CourseIdToCreatedDateMap.put(student.getCourseId(), course.getCreatedAt());
            return course.getCreatedAt();
        }
        
        return null;
        
    }
    
    private Course getCourseEntity(String courseId){
        
        Query q = pm.newQuery(Course.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("ID == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<Course> courseList = (List<Course>) q.execute(courseId);
        
        if (courseList.isEmpty() || JDOHelper.isDeleted(courseList.get(0))) {
            return null;
        }
    
        return courseList.get(0);
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
    
    
    private String getCurrentDateForDisplay(){
        Date now = new Date();
        
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTime(now);
        cal = TimeHelper.convertToUserTimeZone(cal, Const.SystemParams.ADMIN_TIMZE_ZONE_DOUBLE);
        
        System.out.print(formatTime(cal.getTime()));
        return formatTime(cal.getTime());
        
    }
    
    private String formatTime(Date date) {
        if (date == null)
            return "";
        return new SimpleDateFormat("[HH-mm]dd-MMM-yyyy]").format(date);
        
    }
    
    private boolean isValidDate(int day, int month, int year){
        
       boolean isDateValid = false; 
        
            
        if(day <= 0 || month <= 0 || year <= 0){
            isDateValid = false;
        } else if(day > getMaxNumOfDayForMonth(month, year)){
            isDateValid = false;
        } else {
          isDateValid = true;
        }
   
       if(!isDateValid){
           System.out.print("Date is not valid. Please Re-enter date.\n\n");
       } else {
           System.out.print("Date Entered is valid.\n\n");
       }
       
       return isDateValid;
        
    }
    
    private int getMaxNumOfDayForMonth(int month, int year){
        
        int days = 0;
        
        switch (month){
            
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                days = 31;
                break;
            case 4:
            case 6:
            case 9: 
            case 11: 
                days = 30;
                break;
            case 2:
                if( ((year % 4) == 0 && (year % 100) != 0)
                    || (year % 400) == 0 ){
                    days = 29;
                } else {
                    days = 28;
                }
                break;
            default:
                days = 0;
        };
        
        return days;
    }
    
    private Date getDate(int day, int month, int year){
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month - 1, day, 0, 0, 0);
        
        return cal.getTime();
        
    }

    
    class EmailListConfig{
        public boolean student = false;
        public boolean instructor = false;
        public StudentStatus studentStatus = StudentStatus.ALL;
        public InstructorStatus instructorStatus = InstructorStatus.ALL;
        public Date studentCreatedDateRangeStart = null;
        public Date studentCreatedDateRangeEnd = null;
        public Date instructorCreatedDateRangeStart = null;
        public Date instructorCreatedDateRangeEnd = null;
    }
}
