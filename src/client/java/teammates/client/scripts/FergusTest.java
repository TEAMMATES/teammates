package teammates.client.scripts;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import teammates.client.connector.DatastoreClient;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseStatisticsMinute;

/*
*   Handles getting of the stats
*/
public class FergusTest extends DatastoreClient {

    // Runs the test
    private static final int HOUR = 60 * 60;
    private static final int WEEK = HOUR * 24 * 7;
    private static final int MONTH = WEEK * 4;
    private static final int YEAR = MONTH * 12;
    private static final int million = 1000000;
    private static final int tenmillion = 10000000;

    private static final String COURSE_ID = "TestData.500S30Q100T";

    private static final String STUDENT_EMAIL = "studentEmail@gmail.tmt";

    private static final String GIVER_SECTION_NAME = "Section 1";

    private static final String FEEDBACK_SESSION_NAME = "Test Feedback Session";

    private static final String FEEDBACK_QUESTION_ID = "QuestionTest";

    private FergusTest() {
    }

    public static void updateForPastMinute() {  // Function will be called 5 seconds after the minute passes.
        Query<FeedbackResponse> intialQuery = ObjectifyService.ofy().load().type(FeedbackResponse.class)
                .project("createdAt");
        Instant.now().get

    }

    // Start running updateForPastMinute for about 5 minutes first, then run this function
    // This function will update for all time, and then write over some of the current data.
/*     public static void updateForAlltime() {
    Instant fnStartTime = Instant.now(); // Function should not overwrite this.
    int CHUNK_BY = MONTH;
    // StartTime put as 2010 first

    Date date = new Date();

    Query<FeedbackResponse> intialQuery = ObjectifyService.ofy().load().type(FeedbackResponse.class)
            .project("createdAt");

    Integer count = intialQuery.filter("createdAt >=", startTime).filter("createdAt <=", endTime).list()
            .size();


    // Chunk this, add to task queue to process it!
    // I'm thinking chunk by MONTH


}
 */
public static void generateStatisticsMinute() {
        ZoneOffset currentOffset = OffsetDateTime.now().getOffset();
        Instant intervalEndTime = LocalDateTime.now()
                .truncatedTo(ChronoUnit.SECONDS)
                .withMinute(0)
                .withSecond(0)
                .toInstant(currentOffset);

        Instant intervalRepresentativeTime = intervalEndTime.minusSeconds(30 * 60);
        Instant intervalStartTime = intervalEndTime.minusSeconds(30 * 60).minusMillis(1);
        try {
            int count = ObjectifyService.ofy().load()
            .type(FeedbackResponse.class)
            .project("createdAt")
            .filter("createdAt >", intervalStartTime)
            .filter("createdAt <", intervalEndTime)
            .list()
            .size();

            FeedbackResponseStatisticsMinute newEntry = new FeedbackResponseStatisticsMinute(
            intervalRepresentativeTime.toString(), count);
            ObjectifyService.ofy().save().entities(newEntry).now();
        } catch (Error e) {
            System.out.println(e);
        }

    }

    public static void getCount() {
        long DEFAULT_INTERVAL = 50;
        Instant startTime = Instant.now().minusSeconds(YEAR);
        Instant endTime = Instant.now();
        long timeDifference = endTime.getEpochSecond() - startTime.getEpochSecond();
        long defaultIntervalSize = Math.floorDiv(timeDifference, DEFAULT_INTERVAL);
        long buffer = timeDifference - (defaultIntervalSize * DEFAULT_INTERVAL);

        Map<Instant, Integer> hashCount = new HashMap<>();
        Instant currentTime = startTime;
        Query<FeedbackResponse> intialQuery = ObjectifyService.ofy().load().type(FeedbackResponse.class)
                .project("createdAt");
        int totalCount = 0;
        for (long i = 0; i < DEFAULT_INTERVAL; i++) {
            long secondsToNextInterval = buffer <= 0 ? defaultIntervalSize : defaultIntervalSize + 1;
            buffer -= 1;
            endTime = currentTime.plusSeconds(secondsToNextInterval);
            Integer count = intialQuery.filter("createdAt >", currentTime).filter("createdAt <", endTime).list().size();
            System.out.println(
                    "Doing " + i + "th with count " + count + " with interval " + currentTime + " to " + endTime);
            hashCount.put(currentTime, count);
            totalCount += count;
            currentTime = endTime;
        }
        System.out.println(hashCount);
        System.out.println(totalCount);
    }

    public static void getTotalCount() {
        Query<FeedbackResponse> intialQuery = ObjectifyService.ofy().load().type(FeedbackResponse.class);
        System.out.println("Total responses: " + intialQuery.count());
    }

    public static void deleteAllResponses() {
        Iterable<Key<FeedbackResponse>> allKeys = ObjectifyService.ofy().load().type(FeedbackResponse.class).keys();
        ObjectifyService.ofy().delete().keys(allKeys).now();
    }

    public static void generateResponses() {
        int STARTING_ID = 1;
        int NUMBER_OF_FEEDBACK_QUESTIONS = 100000;
        int CHUNKER = 10; // Prevent Java heap overflow
        for (int i = 0; i < CHUNKER; i++) {
            FeedbackResponse[] arr = new FeedbackResponse[NUMBER_OF_FEEDBACK_QUESTIONS / CHUNKER];
            for (int j = 0; j < NUMBER_OF_FEEDBACK_QUESTIONS / CHUNKER; j++) {
                int secondsOffset = (int) (Math.random() * YEAR);
                STARTING_ID++;
                FeedbackResponse feedback = new FeedbackResponse(FEEDBACK_SESSION_NAME, COURSE_ID,
                        generateId(Integer.toString(secondsOffset), Integer.toString(STARTING_ID)),
                        null, STUDENT_EMAIL, "Section" + i, "Bob", STUDENT_EMAIL, "Nothing");
                feedback.setCreatedAt(Instant.now().minusSeconds(secondsOffset));
                arr[j] = feedback;
            }
            System.out.println("Finished creating, now saving chunk " + i);
            ObjectifyService.ofy().save().entities(arr).now();
        }
        System.out.println("Finished generating!");
    }

    public static void generateResponsesNow() {
        int STARTING_ID = tenmillion;
        int NUMBER_OF_FEEDBACK_QUESTIONS = 10;
        FeedbackResponse[] arr = new FeedbackResponse[NUMBER_OF_FEEDBACK_QUESTIONS];
        for (int i = 0; i < NUMBER_OF_FEEDBACK_QUESTIONS; i++) {
            int randomNumber = (int) (Math.random() * YEAR);
            STARTING_ID++;
            FeedbackResponse feedback = new FeedbackResponse(FEEDBACK_SESSION_NAME, COURSE_ID,
                    generateId(Integer.toString(randomNumber), Integer.toString(STARTING_ID)),
                    null, STUDENT_EMAIL, "Section" + i, "Bob", STUDENT_EMAIL, "Nothing");
            feedback.setCreatedAt(Instant.now());
            arr[i] = feedback;
        }
        ObjectifyService.ofy().save().entities(arr).now();
    }

    public static String generateId(String feedbackQuestionId, String giver) {
        return feedbackQuestionId + '%' + giver;
    }

    @Override
    protected void doOperation() {
        // getCount(); //
        // getTotalCount();
        // generateResponses();
        generateStatisticsMinute();
        //generateResponsesNow();
        // deleteAllResponses();
        // System.out.println(ZonedDateTime().now());
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        System.out.println("Start timer at " + (startTime));
        new FergusTest().doOperationRemotely();
        long endTime = System.currentTimeMillis();
        System.out.println("That took " + (endTime - startTime) + " milliseconds or " +  ((endTime - startTime)/1000) + " seconds or " + (((endTime - startTime)/1000)/60 + " minutes.") );
    }

}
