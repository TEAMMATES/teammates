package teammates.test.cases.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link teammates.common.datatransfer.FeedbackSessionResultsBundle}.
 */
public class FeedbackSessionResultsBundleTest extends BaseTestCase {

    @Test
    public void testGetActualResponses() {
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsBundleTest.json");

        FeedbackSessionAttributes session = responseBundle.feedbackSessions.get("session1InCourse1");

        Map<String, String> emailNameTable = new HashMap<>();
        emailNameTable.put("student1InCourse1@gmail.tmt", "Student 1 in course 1");
        emailNameTable.put("student2InCourse1@gmail.tmt", "Student in two courses");
        emailNameTable.put("student3InCourse1@gmail.tmt", "student3 In Course1");

        Map<String, boolean[]> visibilityTable = new HashMap<>();
        boolean[] visibility = new boolean[2];
        visibilityTable.put("response1ForQ1S1C1", visibility);
        visibilityTable.put("response2ForQ1S1C1", visibility);
        visibilityTable.put("response1ForQ2S1C1", visibility);
        visibilityTable.put("response2ForQ2S1C1", visibility);
        visibilityTable.put("response3ForQ2S1C1", visibility);

        List<String> allExpectedResponses = new ArrayList<>();
        allExpectedResponses.add(responseBundle.feedbackResponses.get("response1ForQ1S1C1").toString());
        allExpectedResponses.add(responseBundle.feedbackResponses.get("response2ForQ1S1C1").toString());

        FeedbackSessionResultsBundle bundle =
                new FeedbackSessionResultsBundle(session, new ArrayList<>(responseBundle.feedbackResponses.values()),
                        responseBundle.feedbackQuestions, emailNameTable, new HashMap<>(),
                        new HashMap<>(), null, visibilityTable, null,
                        new CourseRoster(new ArrayList<>(responseBundle.students.values()),
                        new ArrayList<>(responseBundle.instructors.values())), null);

        ______TS("Test question having responses");
        FeedbackQuestionAttributes fqa = responseBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        List<FeedbackResponseAttributes> allResponses = bundle.getActualUnsortedResponses(fqa);
        assertEquals(2, allResponses.size());
        List<String> allResponsesString = new ArrayList<>();
        allResponsesString.add(allResponses.get(0).toString());
        allResponsesString.add(allResponses.get(1).toString());
        assertEquals(allExpectedResponses, allResponsesString);

        ______TS("Test question having no responses");
        fqa = responseBundle.feedbackQuestions.get("qn3InSession1InCourse1");
        allResponses = bundle.getActualUnsortedResponses(fqa);
        assertEquals(0, allResponses.size());
    }
}
