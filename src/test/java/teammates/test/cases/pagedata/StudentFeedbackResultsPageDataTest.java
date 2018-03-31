package teammates.test.cases.pagedata;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.StringHelper;
import teammates.logic.api.Logic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.ui.pagedata.StudentFeedbackResultsPageData;
import teammates.ui.template.StudentFeedbackResultsQuestionWithResponses;

/**
 * SUT: {@link StudentFeedbackResultsPageData}.
 */
public class StudentFeedbackResultsPageDataTest extends BaseComponentTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public void classSetup() {
        removeAndRestoreTypicalDataBundle();
    }

    @Test
    public void testAll() throws EntityDoesNotExistException {
        ______TS("typical success case");

        AccountAttributes account = dataBundle.accounts.get("student1InCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        assertNotNull(student);
        String dummyKey = "key123";
        student.key = dummyKey;
        Logic logic = new Logic();

        StudentFeedbackResultsPageData pageData = new StudentFeedbackResultsPageData(account, student, dummySessionToken);

        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionsWithResponses = new LinkedHashMap<>();

        FeedbackQuestionAttributes question1 = dataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        assertNotNull(question1);
        FeedbackQuestionAttributes question2 = dataBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        assertNotNull(question2);

        List<FeedbackResponseAttributes> responsesForQ1 = new ArrayList<>();
        List<FeedbackResponseAttributes> responsesForQ2 = new ArrayList<>();

        /* Question 1 with responses */
        responsesForQ1.add(dataBundle.feedbackResponses.get("response1ForQ1S1C1"));
        questionsWithResponses.put(question1, responsesForQ1);

        /* Question 2 with responses */
        responsesForQ2.add(dataBundle.feedbackResponses.get("response1ForQ2S1C1"));
        responsesForQ2.add(dataBundle.feedbackResponses.get("response2ForQ2S1C1"));
        questionsWithResponses.put(question2, responsesForQ2);

        // need to obtain questionId and responseId as methods in FeedbackSessionResultsBundle require them
        questionsWithResponses = getActualQuestionsAndResponsesWithId(
                                        logic, questionsWithResponses);

        pageData.setBundle(logic.getFeedbackSessionResultsForStudent(
                question1.feedbackSessionName, question1.courseId, student.email));
        pageData.init(questionsWithResponses);

        StudentFeedbackResultsQuestionWithResponses questionBundle1 =
                pageData.getFeedbackResultsQuestionsWithResponses().get(0);
        StudentFeedbackResultsQuestionWithResponses questionBundle2 =
                pageData.getFeedbackResultsQuestionsWithResponses().get(1);

        assertNotNull(pageData.getFeedbackResultsQuestionsWithResponses());
        assertEquals(2, pageData.getFeedbackResultsQuestionsWithResponses().size());
        assertEquals("You are viewing feedback results as <span class='text-danger text-bold text-large'>"
                      + "student1 In Course1</td></div>'\"</span>. You may submit feedback for sessions that are "
                      + "currently open and view results without logging in. "
                      + "To access other features you need <a href='/page/studentCourseJoinAuthentication?"
                      + "key=" + StringHelper.encrypt(dummyKey)
                      + "&studentemail=student1InCourse1%40gmail.tmt&courseid=idOfTypicalCourse1' class='link'>"
                      + "to login using a Google account</a> (recommended).",
                      pageData.getRegisterMessage());

        assertNotNull(questionBundle1.getQuestionDetails());
        assertNotNull(questionBundle2.getQuestionDetails());

        assertEquals("1", questionBundle1.getQuestionDetails().getQuestionIndex());
        assertEquals("2", questionBundle2.getQuestionDetails().getQuestionIndex());

        assertEquals("", questionBundle1.getQuestionDetails().getAdditionalInfo());
        assertEquals("", questionBundle2.getQuestionDetails().getAdditionalInfo());

        assertNotNull(questionBundle1.getResponseTables());
        assertNotNull(questionBundle2.getResponseTables());

        assertEquals("You", questionBundle1.getResponseTables().get(0).getRecipientName());

        assertNotNull(questionBundle1.getResponseTables().get(0).getResponses());

        assertEquals("You", questionBundle1.getResponseTables().get(0).getResponses()
                                        .get(0).getGiverName());

        assertEquals("Student 1 self feedback.", questionBundle1.getResponseTables().get(0).getResponses()
                                        .get(0).getAnswer());

        ______TS("student in unregistered course");

        student = dataBundle.students.get("student1InUnregisteredCourse");

        pageData = new StudentFeedbackResultsPageData(account, student, dummySessionToken);
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionsWithResponsesUnregistered =
                new LinkedHashMap<>();

        pageData.init(questionsWithResponsesUnregistered);

        assertTrue(pageData.getFeedbackResultsQuestionsWithResponses().isEmpty());

        assertEquals("regKeyForStuNotYetJoinCourse", student.key);
        assertEquals("idOfUnregisteredCourse", student.course);
        assertEquals("student1InUnregisteredCourse@gmail.tmt", student.email);

        assertEquals("You are viewing feedback results as "
                      + "<span class='text-danger text-bold text-large'>student1 In "
                      + "unregisteredCourse</span>. You may submit feedback for sessions that are currently open "
                      + "and view results without logging in. To access other features you need "
                      + "<a href='/page/studentCourseJoinAuthentication?key="
                      + StringHelper.encrypt("regKeyForStuNotYetJoinCourse")
                      + "&studentemail=student1InUnregisteredCourse%40gmail.tmt&courseid=idOfUnregisteredCourse' "
                      + "class='link'>to login using a Google account</a> (recommended).",
                      pageData.getRegisterMessage());
    }

    private Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> getActualQuestionsAndResponsesWithId(
            Logic logic, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionsWithResponses) {
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> actualQuestionsWithResponses =
                new LinkedHashMap<>();
        questionsWithResponses.forEach((dataBundleQuestion, dataBundleResponses) -> {
            FeedbackQuestionAttributes actualQuestion = logic.getFeedbackQuestion(
                                                                    dataBundleQuestion.feedbackSessionName,
                                                                    dataBundleQuestion.courseId,
                                                                    dataBundleQuestion.questionNumber);

            List<FeedbackResponseAttributes> actualResponses = new ArrayList<>();
            for (FeedbackResponseAttributes dataBundleResponse : dataBundleResponses) {
                FeedbackResponseAttributes actualResponse = logic.getFeedbackResponse(
                                                                    actualQuestion.getId(),
                                                                    dataBundleResponse.giver,
                                                                    dataBundleResponse.recipient);
                actualResponses.add(actualResponse);
            }
            actualQuestionsWithResponses.put(actualQuestion, actualResponses);
        });
        return actualQuestionsWithResponses;
    }
}
