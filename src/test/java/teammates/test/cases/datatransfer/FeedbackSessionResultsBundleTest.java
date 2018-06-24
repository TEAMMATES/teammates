package teammates.test.cases.datatransfer;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.cases.logic.BaseLogicTest;

/**
 * SUT: {@link teammates.common.datatransfer.FeedbackSessionResultsBundle}.
 */
public class FeedbackSessionResultsBundleTest extends BaseLogicTest {

    private static FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();

    @Test
    public void testGetAllResponses() throws EntityDoesNotExistException {
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        removeAndRestoreDataBundle(responseBundle);

        ______TS("test getAllResponsesForQuestion function");

        FeedbackSessionAttributes session = responseBundle.feedbackSessions.get("contribSession");
        StudentAttributes student = responseBundle.students.get("student1InCourse1");
        FeedbackSessionResultsBundle results =
                fsLogic.getFeedbackSessionResultsForStudent(session.getFeedbackSessionName(),
                        session.getCourseId(), student.email);
        FeedbackQuestionAttributes fqa = responseBundle.feedbackQuestions.get("qn1InSession5InCourse1");
        FeedbackQuestionAttributes questionToGet =
                fqLogic.getFeedbackQuestion(fqa.feedbackSessionName, fqa.courseId, fqa.questionNumber);
        List<FeedbackResponseAttributes> allResponses = results.getActualResponses(questionToGet);

        List<String> allResponsesString = new ArrayList<>();
        List<String> allExpectedResponses = new ArrayList<>();
        FeedbackResponseAttributes fra;

        for (int i = 1; i <= 4; i++) {
            fra = responseBundle.feedbackResponses.get("response" + i + "ForQ1S5C1");
            allExpectedResponses.add(frLogic.getFeedbackResponse(questionToGet.getId(),
                    fra.giver, fra.recipient).toString());
            allResponsesString.add(allResponses.get(i - 1).toString());
        }
        assertEquals(4, allResponses.size());
        assertTrue("Responses are missing", allResponsesString.containsAll(allExpectedResponses));
    }
}
