package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import javax.jdo.Query;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.storage.entity.Account;

public class InstructorStudentInstitueMigrator extends RemoteApiClient {

    private static String fromInstitute = "Original Institute Name";
    private static String toInstitute = "New Institute Name";

    private static final String NO_MATCHING_INSTITUTE = "No Matching Accounts Found for Institue: %s";
    private static final int PROGRESS_STEP = 100;
    private static int counter;

    public static void main(String[] args) throws IOException {

        InstructorStudentInstitueMigrator migrator = new InstructorStudentInstitueMigrator();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        Query q = PM.newQuery(Account.class);
        q.declareParameters("String instituteName");
        q.setFilter("institute == instituteName");

        @SuppressWarnings("unchecked")
        List<Account> accountsList = (List<Account>) q.execute(fromInstitute);

        for (Account a : accountsList) {
            a.setInstitute(toInstitute);
            updateProgressIndicator(accountsList.size());
        }

        if (accountsList.isEmpty()) {
            System.out.printf(NO_MATCHING_INSTITUTE, fromInstitute);
        }

        PM.close();

    }

    private void updateProgressIndicator(int total) {
        counter++;
        if (counter % PROGRESS_STEP == 0 || counter == total) {
            System.out.printf("total accounts modified %d/%d %n", counter, total);
        }
    }

}
