package teammates.client.scripts.sql;

import java.util.Scanner;

import teammates.client.connector.DatastoreClient;
import teammates.client.util.ClientProperties;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Notification;
import teammates.storage.entity.UsageStatistics;

/*
 * This class is used to remove non-course data from Datastore.
 * FOR TESTING Purpose only. DO NOT USE IN PRODUCTION.
 */
public class RemoveNonCourseDataFromDataStore extends DatastoreClient {
    protected void removeNonCourseDataFromDataStore() {
        for (Account account : ofy().load().type(Account.class).list()) {
            ofy().delete().entity(account).now();
        }
        for (AccountRequest accountRequest : ofy().load().type(AccountRequest.class).list()) {
            ofy().delete().entity(accountRequest).now();
        }
        for (Notification notification : ofy().load().type(Notification.class).list()) {
            ofy().delete().entity(notification).now();
        }
        for (UsageStatistics usageStatistics : ofy().load().type(UsageStatistics.class).list()) {
            ofy().delete().entity(usageStatistics).now();
        }

    }

    protected void verifyCounts() {
        System.out.println(String.format("Num of accounts in Datastore: %d", ofy().load().type(Account.class).count()));
        System.out.println(String.format("Num of account requests in Datastore: %d",
                ofy().load().type(AccountRequest.class).count()));
        System.out.println(
                String.format("Num of notifications in Datastore: %d", ofy().load().type(Notification.class).count()));
        System.out.println(String.format("Num of usage statistics in Datastore: %d", ofy().load()
                .type(UsageStatistics.class).count()));
    }

    public static void main(String[] args) throws Exception {
        new RemoveNonCourseDataFromDataStore().doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        System.out.println("WARNING: This operation will delete all non course data from datastore.");
        String appUrl = ClientProperties.TARGET_URL.replaceAll("^https?://", "");
        String appDomain = appUrl.split(":")[0];
        int appPort = appUrl.contains(":") ? Integer.parseInt(appUrl.split(":")[1]) : 443;
        System.out.println("Target Datastore for this operation: " + appDomain + ":" + appPort);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to delete all data in datastore (Y/N): ");
        String input = scanner.nextLine();

        if ("Y".equalsIgnoreCase(input)) {
            System.out.println("Deleting all non course data...");
            scanner.close();
        } else {
            System.out.println("Operation cancelled.");
            scanner.close();
            return;
        }
        try {
            // ask if user is sure to proceed

            System.out.println("--- Before remove operation ---");
            this.verifyCounts();
            System.out.println("--- Starting remove operation ---");
            this.removeNonCourseDataFromDataStore();
            System.out.println("--- After remove operation ---");
            this.verifyCounts();
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
