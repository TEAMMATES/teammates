package teammates.client.scripts;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

import teammates.client.connector.DatastoreClient;
import teammates.storage.entity.FeedbackSession;

/**
 * Script to generate emails of active instructors within a period.
 */
public class ListActiveInstructors extends DatastoreClient {
    @Override
    protected void doOperation() {
        //2010/01/01
        int startDate = 1;
        int startMonth = 1;
        int startYear = 2010;
        //2021/02/01
        int endDate = 1;
        int endMonth = 3;
        int endYear = 2021;

        LocalDate startPoint = LocalDate.of(startYear, startMonth, startDate);
        LocalDate endPoint = LocalDate.of(endYear, endMonth, endDate);
        long startTimeInMilli = startPoint.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        long endTimeInMilli = endPoint.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        Instant startTime = Instant.ofEpochMilli(startTimeInMilli);
        Instant endTime = Instant.ofEpochMilli(endTimeInMilli);

        Set<String> activeInstructorEmails = new HashSet<>();

        ofy().load().type(FeedbackSession.class)
                .filter("startTime >=", startTime)
                .filter("startTime <=", endTime).project().forEach(feedbackSession -> {
                    activeInstructorEmails.add(feedbackSession.getCreatorEmail());
                });

        StringBuilder results = new StringBuilder();
        for (String email : activeInstructorEmails) {
            results.append(email).append(", \n");
        }
        System.out.println(results.toString());
    }

    public static void main(String[] args) {
        new ListActiveInstructors().doOperationRemotely();
    }
}
