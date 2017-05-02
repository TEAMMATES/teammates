package teammates.client.scripts;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.storage.entity.Account;
import teammates.storage.entity.CourseStudent;
import teammates.storage.entity.Instructor;

public class DataMigrationAppendInstitutionForAccounts extends RemoteApiClient {

    private static final boolean isTrial = true;

    public static void main(String[] args) throws IOException {
        DataMigrationAppendInstitutionForAccounts migrator = new DataMigrationAppendInstitutionForAccounts();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        appendInstitutionForAccounts();
    }

    private static void appendInstitutionForAccounts() {
        // Instructor Accounts get Institute for an Instructor
        String query = "select from " + Account.class.getName()
                + " where isInstructor == true";

        @SuppressWarnings("unchecked")
        List<Account> instructorAccounts = (List<Account>) PM.newQuery(query).execute();

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
        List<Instructor> instructors = (List<Instructor>) PM.newQuery(query).execute();

        HashMap<String, String> courseInstitutions = new HashMap<String, String>();

        for (Instructor i : instructors) {
            courseInstitutions.put(i.getCourseId(), instructorInstitutions.get(i.getGoogleId()));
        }

        System.out.println("Finished mapping course-institutions: " + courseInstitutions.size());

        //======================================================================
        // Given Course-Institute Pair create Student-Institute Pair
        query = "select from " + CourseStudent.class.getName()
                + " where ID != null";

        @SuppressWarnings("unchecked")
        List<CourseStudent> students = (List<CourseStudent>) PM.newQuery(query).execute();

        HashMap<String, String> studentInstitutions = new HashMap<String, String>();

        for (CourseStudent s : students) {
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
            List<Account> studentAccounts = (List<Account>) PM.newQuery(query).execute();

            if (studentAccounts.isEmpty()) {
                continue;
            }
            Account a = studentAccounts.get(0);
            if (a.getInstitute() == null || a.getInstitute().isEmpty()) {
                System.out.println("Assigning '" + studentInstitutions.get(a.getGoogleId()) + "' to '" + a.getGoogleId());
                if (!isTrial) {
                    Account newA = new Account(a.getGoogleId(), a.getName(), false, a.getEmail(),
                                               studentInstitutions.get(a.getGoogleId()));
                    PM.deletePersistent(a);
                    PM.makePersistent(newA);
                }
                count++;
            }
        }

        System.out.println("Appended for " + count + " entities");
    }

}
