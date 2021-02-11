package teammates.client.scripts;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.storage.entity.FeedbackSession;

/**
 * Script to generate emails of active instructors within a period.
 */
public class ListActiveInstructors extends RemoteApiClient {
    @Override
    protected void doOperation() {
        //2010/01/01
        int startDate = 1;
        int startMonth = 1;
        int startYear = 2020;
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
        List<FeedbackSession> feedbackSessions = ofy().load().type(FeedbackSession.class).list();
        int startIndex = getTimeIndex(feedbackSessions, startTime);
        int endIndex = getTimeIndex(feedbackSessions, endTime);

        List<FeedbackSession> desiredFeedbackSessions = feedbackSessions.subList(startIndex, endIndex + 1);
        desiredFeedbackSessions.forEach(feedbackSession -> {
            activeInstructorEmails.add(feedbackSession.getCreatorEmail());
        });

        StringBuilder results = new StringBuilder();
        for (String email : activeInstructorEmails) {
            results.append(email).append(", \n");
        }
        System.out.println(results.toString());
    }

    /**
     * Get the index of desired time with binary search.
     * @param feedbackSessions All feedback sessions to search
     * @param desiredTime The timestamp to search for
     * @return timeIndex
     */
    public int getTimeIndex(List<FeedbackSession> feedbackSessions, Instant desiredTime) {
        int mid = 0;
        int start = 0;
        int end = feedbackSessions.size() - 1;

        while (start <= end) {
            mid = start + (end - start) / 2;
            Instant currentTime = feedbackSessions.get(mid).getCreatedTime();

            if (currentTime.equals(desiredTime)) {
                start = mid;
            } else if (currentTime.isAfter(desiredTime)) {
                end = mid - 1;
            } else {
                start = mid + 1;
            }

        }

        return mid;
    }

    public static void main(String[] args) throws Exception {
        new ListActiveInstructors().doOperationRemotely();
    }
}
