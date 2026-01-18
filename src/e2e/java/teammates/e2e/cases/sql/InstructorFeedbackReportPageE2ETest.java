package teammates.e2e.cases.sql;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.common.util.AppUrl;
import teammates.e2e.pageobjects.InstructorFeedbackResultsPage;
import teammates.e2e.util.TestProperties;

public class InstructorFeedbackReportPageE2ETest extends BaseE2ETestCase {

    private Instructor instructor;
    private String fileName;
    private Student studentToEmail;

    private Collection<Instructor> instructors;
    private Collection<Student> students;

    private AppUrl resultsUrl;
    private InstructorFeedbackResultsPage resultsPage;

    // Maps to organise responses
    private Map<FeedbackQuestion, List<FeedbackResponse>> questionToResponses;
    private Map<FeedbackQuestion, Map<String, List<FeedbackResponse>>> questionToGiverToResponses;
    private Map<FeedbackQuestion, Map<String, List<FeedbackResponse>>> questionToRecipientToResponses;

    // We either test all questions or just use qn2
    private FeedbackQuestion qn2;
    private List<FeedbackResponse> qn2Responses;
    private Map<String, List<FeedbackResponse>> qn2GiverResponses;
    private Map<String, List<FeedbackResponse>> qn2RecipientResponses;

    // For testing section filtering
    private String section;
    private List<FeedbackResponse> filteredQn2Responses;
    private Map<String, List<FeedbackResponse>> filteredQn2GiverResponses;
    private Map<String, List<FeedbackResponse>> filteredQn2RecipientResponses;

    // For testing missing responses
    private FeedbackResponse missingResponse;
    private Map<String, List<FeedbackResponse>> qn2GiverResponsesWithMissing;
    private Map<String, List<FeedbackResponse>> qn2RecipientResponsesWithMissing;

    // For testing comment
    private FeedbackResponse responseWithComment;
    private FeedbackResponseCommentAttributes comment;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadSqlDataBundle("/InstructorFeedbackReportPageE2ETestSql.json"));
        studentToEmail = testData.students.get("Emily");
        studentToEmail.setEmail(TestProperties.TEST_EMAIL);

        instructor = testData.instructors.get("tm.e2e.IFRep.instr");
        FeedbackSession fileSession = testData.feedbackSessions.get("Open Session 2");
        fileName = "/" + fileSession.getCourseId() + "_" + fileSession.getName() + "_result.csv";

        instructors = testData.instructors.values();
        students = testData.students.values();
    }

    @Test
    @Override
    protected void testAll() {
        
    }
  
}
