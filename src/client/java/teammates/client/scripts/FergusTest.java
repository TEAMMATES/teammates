package teammates.client.scripts;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import teammates.client.connector.DatastoreClient;
import teammates.storage.entity.FeedbackResponse;

/*
*   Handles getting of the stats
*/
public class FergusTest extends DatastoreClient {

    // Runs the test
    private static final int hour = 60 * 60;
    private static final int week = hour * 24 * 7;
    private static final int month = week * 4;
    private static final int year = month * 12;
    private static final int million = 1000000;
    private static final int tenmillion = 10000000;

    private static final String COURSE_ID = "TestData.500S30Q100T";

    private static final String STUDENT_EMAIL = "studentEmail@gmail.tmt";

    private static final String GIVER_SECTION_NAME = "Section 1";

    private static final String FEEDBACK_SESSION_NAME = "Test Feedback Session";

    private static final String FEEDBACK_QUESTION_ID = "QuestionTest";

    private FergusTest() {
    }

    public static void getCount() {
        long DEFAULT_INTERVAL = 50;
        Instant startTime = Instant.now().minusSeconds(year);
        Instant endTime = Instant.now();
        long timeDifference = endTime.getEpochSecond() - startTime.getEpochSecond();
        long defaultIntervalSize = Math.floorDiv(timeDifference, DEFAULT_INTERVAL);
        long buffer = timeDifference - (defaultIntervalSize * DEFAULT_INTERVAL);

        // Two choices. Async, or batch. Count is synchronous, and not good 
        Map<Instant, Integer> hashCount = new HashMap<>();
        Instant currentTime = startTime;
        Query<FeedbackResponse> intialQuery = ObjectifyService.ofy().load().type(FeedbackResponse.class)
                .project("createdAt");
        for (long i = 0; i < DEFAULT_INTERVAL; i++) {
            long secondsToNextInterval = buffer <= 0 ? defaultIntervalSize : defaultIntervalSize + 1;
            buffer -= 1;
            endTime = currentTime.plusSeconds(secondsToNextInterval);
            Integer count = intialQuery.filter("createdAt >=", currentTime).filter("createdAt <=", endTime).list()
                    .size();
            System.out.println(
                    "Doing " + i + "th with count " + count + " with interval " + currentTime + " to " + endTime);
            hashCount.put(currentTime, count);
            currentTime = endTime;
        }
        System.out.println(hashCount);
    }
    
    public static void getTotalCount() {
        Query<FeedbackResponse> intialQuery = ObjectifyService.ofy().load().type(FeedbackResponse.class).project("createdAt");
        System.out.println("Total responses: " + intialQuery.list().size());
    }

    public static void deleteAllResponses() {
        Iterable<Key<FeedbackResponse>> allKeys = ObjectifyService.ofy().load().type(FeedbackResponse.class).keys();
        ObjectifyService.ofy().delete().keys(allKeys).now();
    }

    public static void generateResponses() {
        int STARTING_ID = 1;
        int NUMBER_OF_FEEDBACK_QUESTIONS = tenmillion;
        int CHUNKER = 10;
        for (int i = 0; i < CHUNKER; i++) {
            FeedbackResponse[] arr = new FeedbackResponse[NUMBER_OF_FEEDBACK_QUESTIONS / 10];
            for (int j = 0; j < NUMBER_OF_FEEDBACK_QUESTIONS / 10; j++) {
                int secondsOffset = (int) (Math.random() * (year));
                STARTING_ID++;
                FeedbackResponse feedback = new FeedbackResponse(FEEDBACK_SESSION_NAME, COURSE_ID,
                        Integer.toString(STARTING_ID),
                        null, STUDENT_EMAIL, "Section" + i, "Bob", STUDENT_EMAIL, "Nothing");
                feedback.setCreatedAt(Instant.now().minusSeconds(secondsOffset));
                arr[j] = feedback;
            }
            System.out.println("Finished creating, now saving chunk " + i);
            ObjectifyService.ofy().save().entities(arr).now();    
        }
        // Array creation of 1 million takes only 2 second, saving is a different story
        System.out.println("Finished generating!");
    }


    @Override
    protected void doOperation() {
        //getCount(); // 
        //getTotalCount();
        generateResponses();    
        //deleteAllResponses();     // Takes about 900 seconds for >10m responses
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        System.out.println("Start timer at " + (startTime));
        new FergusTest().doOperationRemotely();
        long endTime = System.currentTimeMillis();
        System.out.println("That took " + ((endTime - startTime)) + " milliseconds");
    }

}
