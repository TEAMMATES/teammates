package teammates.client.scripts;

import java.time.Instant;
import java.util.ArrayList;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.storage.entity.FeedbackSession;

/**
 * Script to generate emails of active instructors within a period.
 */
public class ListActiveInstructors extends RemoteApiClient {
    @Override
    protected void doOperation() {
        //2020/01/01
        long startTime = 1577808000000L;
        //2021/02/01
        long endTime = 1612108800000L;

        ArrayList<String> activeInstructorEmails = new ArrayList<>();
        ofy().load().type(FeedbackSession.class).forEach(feedbackSession -> {
            Instant createdTime = feedbackSession.getCreatedTime();
            //validate active period of instructors
            if (createdTime.isAfter(Instant.ofEpochMilli(startTime))
                    && createdTime.isBefore(Instant.ofEpochMilli(endTime))) {
                String creatorEmail = feedbackSession.getCreatorEmail();
                if (!activeInstructorEmails.contains(creatorEmail)) {
                    activeInstructorEmails.add(creatorEmail);
                }
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
