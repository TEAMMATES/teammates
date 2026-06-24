package teammates.logic.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeMethod;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.test.BaseTestCase;
import teammates.test.scenariobuilder.GivenData;

/**
 * Base class for in-memory statistics calculator tests.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class BaseStatisticsTestCase extends BaseTestCase {

    /**
     * Scenario builder for the current test case.
     */
    protected GivenData given;

    @BeforeMethod(alwaysRun = true)
    @Override
    public void beforeMethod(java.lang.reflect.Method method) {
        super.beforeMethod(method);
        given = new GivenData(currentTestName);
    }

    /**
     * Returns the in-memory data bundle for the current scenario.
     */
    protected DataBundle dataBundle() {
        return given.getDataBundle();
    }

    /**
     * Returns the feedback question with the given alias.
     */
    protected FeedbackQuestion question(String alias) {
        return dataBundle().feedbackQuestions.get(alias);
    }

    /**
     * Returns the feedback response with the given alias.
     */
    protected FeedbackResponse response(String alias) {
        return dataBundle().feedbackResponses.get(alias);
    }

    /**
     * Returns the student with the given alias.
     */
    protected Student student(String alias) {
        return dataBundle().students.get(alias);
    }

    /**
     * Returns the responses matching the given aliases in order.
     */
    protected List<FeedbackResponse> responses(String... aliases) {
        List<FeedbackResponse> responses = new ArrayList<>();
        for (String alias : aliases) {
            responses.add(response(alias));
        }
        return responses;
    }

    /**
     * Builds a session results bundle for a single question and its responses.
     */
    protected SessionResultsBundle bundleForQuestion(String questionAlias, String... responseAliases) {
        FeedbackQuestion question = question(questionAlias);
        List<FeedbackResponse> responses = responses(responseAliases);
        Map<UUID, Boolean> responseVisibility = responses.stream()
                .filter(response -> response.getId() != null)
                .collect(Collectors.toMap(FeedbackResponse::getId, response -> true));
        List<Student> students = new ArrayList<>(dataBundle().students.values());
        List<Instructor> instructors = new ArrayList<>(dataBundle().instructors.values());

        return new SessionResultsBundle(
                List.of(question),
                Set.of(),
                responses,
                List.of(),
                responseVisibility,
                responseVisibility,
                Map.of(),
                new CourseRoster(students, instructors));
    }
}
