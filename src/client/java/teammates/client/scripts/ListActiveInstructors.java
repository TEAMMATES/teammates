package teammates.client.scripts;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.storage.entity.FeedbackSession;

/**
 * Script to generate emails of active instructors within a period.
 */
public class ListActiveInstructors extends RemoteApiClient {
    @Override
    protected void doOperation() {
        //2020/01/01
        int startDate = 1;
        int startMonth = 1;
        int startYear = 2020;
        //2021/02/01
        int endDate = 1;
        int endMonth = 2;
        int endYear = 2021;

        LocalDate startPoint = LocalDate.of(startYear, startMonth, startDate);
        LocalDate endPoint = LocalDate.of(endYear, endMonth, endDate);
        long startTime = startPoint.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        long endTime = endPoint.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();

        Set<String> activeInstructorEmails = new HashSet<>();
        ofy().load().type(FeedbackSession.class).forEach(feedbackSession -> {
            Instant createdTime = feedbackSession.getCreatedTime();
            //validate active period of instructors
            if (createdTime.isAfter(Instant.ofEpochMilli(startTime))
                    && createdTime.isBefore(Instant.ofEpochMilli(endTime))) {
                activeInstructorEmails.add(feedbackSession.getCreatorEmail());
            }
        });

        StringBuilder results = new StringBuilder();
        for (String email : activeInstructorEmails) {
            results.append(email).append(", \n");
        }
        System.out.println(results.toString());
    }

    public static void main(String[] args) throws Exception {
        new ListActiveInstructors().doOperationRemotely();
    }
}
