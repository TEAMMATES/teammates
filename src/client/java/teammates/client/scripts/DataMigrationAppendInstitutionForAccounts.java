package teammates.client.scripts;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;


public class DataMigrationAppendInstitutionForAccounts extends RemoteApiClient {
    
    private static final boolean isTrial = true;
    
    // TODO: remove pm and use Datastore.initialize(); as done in GenerateFeedbackReport
    protected static final PersistenceManager pm = JDOHelper
            .getPersistenceManagerFactory("transactions-optional")
            .getPersistenceManager();
    
    public static void main(String[] args) throws IOException {
        DataMigrationAppendInstitutionForAccounts migrator = new DataMigrationAppendInstitutionForAccounts();
        migrator.doOperationRemotely();
    }
    
    protected void doOperation() {
        appendInstitutionForAccounts();
    }
    
    private static void appendInstitutionForAccounts() {
        // Instructor Accounts get Institute for an Instructor
        String query = "select from " + Account.class.getName()
                + " where isInstructor == true";
        
        @SuppressWarnings("unchecked")
        List<Account> instructorAccounts = (List<Account>) pm.newQuery(query).execute();
        
        HashMap<String, String> instructorInstitutions = new HashMap<String, String>();
        
        for (Account a : instructorAccounts) {
            if (a.getInstitute() == null || a.getInstitute().isEmpty()) {
                a.setInstitute("National University of Singapore");
            }
            instructorInstitutions.put(a.getGoogleId(), a.getInstitute());
        }
        
        System.out.println("Finished mapping instructor-institutions: " + instructorInstitutions.size());
        
        //======================================================================
        // Given Institute for Instructor create Course-Institute pair
        query = "select from " + Instructor.class.getName();
        
        @SuppressWarnings("unchecked")
        List<Instructor> instructors = (List<Instructor>) pm.newQuery(query).execute();
        
        HashMap<String, String> courseInstitutions = new HashMap<String, String>();
        
        for (Instructor i : instructors) {
            courseInstitutions.put(i.getCourseId(), instructorInstitutions.get(i.getGoogleId()));
        }
        
        System.out.println("Finished mapping course-institutions: " + courseInstitutions.size());
        
        //======================================================================
        // Given Course-Institute Pair create Student-Institute Pair
        query = "select from " + Student.class.getName()
                + " where ID != null";
        
        @SuppressWarnings("unchecked")
        List<Student> students = (List<Student>) pm.newQuery(query).execute();
        
        HashMap<String, String> studentInstitutions = new HashMap<String, String>();
        
        for (Student s : students) {
            studentInstitutions.put(s.getGoogleId(), courseInstitutions.get(s.getCourseId()));
        }
        
        System.out.println("Finished mapping student-institutions: " + studentInstitutions.size());
        
        //======================================================================
        // *******************************************************************
        // Can test the functionality up to this point to check correctness of mapping
        // Only the following loop does the appending
        // *******************************************************************
        //======================================================================
        // Student Accounts append Institute from Student-Institute pair        
        int count = 0;
        for (String id : studentInstitutions.keySet()) {
            query = "select from " + Account.class.getName()
                    + " where googleId == \"" + id + "\"";
            
            @SuppressWarnings("unchecked")
            List<Account> studentAccounts = (List<Account>) pm.newQuery(query).execute();
            if (studentAccounts.size() > 0) {
                Account a = studentAccounts.get(0);
                if (a.getInstitute() == null || a.getInstitute().equals("")) {
                    System.out.println("Assigning '" + studentInstitutions.get(a.getGoogleId()) + "' to '" + a.getGoogleId());
                    if (!isTrial) {
                        Account newA = new Account(a.getGoogleId(), a.getName(), false, a.getEmail(), studentInstitutions.get(a.getGoogleId()));
                        pm.deletePersistent(a);
                        pm.makePersistent(newA);
                    }
                    count++;
                }
            }
        }
        
        System.out.println("Appended for " + count + " entities");
    }
    
}
