package teammates.e2e.cases.lnp;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.Const;
import teammates.e2e.util.JMeterElements;
import teammates.e2e.util.LNPTestData;
import teammates.storage.entity.CourseStudent;

/**
 * L&P Test Case for instructor's student enrollment API endpoint.
 */
public class FeedbackSessionViewLNPTest extends BaseLNPTestCase {

    private static final int NUMBER_OF_USER_ACCOUNTS = 80;
    private static final int RAMP_UP_PERIOD = 3;
    private static final String STUDENT_NAME = "LnPStudent";
    private static final String STUDENT_EMAIL = "personalEmail";

    private static final String INSTRUCTOR_EMAIL = "tmms.test@gmail.tmt";

    private static final String COURSE_ID = "TestData.CS101";

    @Override
    protected LNPTestData getTestData() {
        return new LNPTestData() {
            @Override
            protected Map<String, AccountAttributes> generateAccounts() {
                Map<String, AccountAttributes> accounts = new LinkedHashMap<>();

                for (int i = 0; i < NUMBER_OF_USER_ACCOUNTS; i++) {
                    accounts.put(STUDENT_NAME + i, AccountAttributes.builder(STUDENT_NAME + i + ".tmms")
                            .withEmail(STUDENT_EMAIL + i + "@gmail.tmt")
                            .withName(STUDENT_NAME + i)
                            .withIsInstructor(false)
                            .withInstitute("TEAMMATES Test Institute 2")
                            .build()
                    );
                }

                return accounts;
            }

            @Override
            protected Map<String, CourseAttributes> generateCourses() {
                Map<String, CourseAttributes> courses = new LinkedHashMap<>();

                courses.put("course", CourseAttributes.builder(COURSE_ID)
                        .withName("Feedback Load Testing")
                        .withTimezone(ZoneId.of("UTC"))
                        .build()
                );

                return courses;
            }

            @Override
            protected Map<String, InstructorAttributes> generateInstructors() {
                Map<String, InstructorAttributes> instructors = new LinkedHashMap<>();

                instructors.put("teammates.test.instructor",
                        InstructorAttributes.builder(COURSE_ID, INSTRUCTOR_EMAIL)
                                .withGoogleId("TestData.instructor")
                                .withName("Teammates Test")
                                .withRole("Co-owner")
                                .withIsDisplayedToStudents(true)
                                .withDisplayedName("Co-owner")
                                .withPrivileges(new InstructorPrivileges(
                                        Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER))
                                .build()
                );

                return instructors;
            }

            @Override
            protected Map<String, StudentAttributes> generateStudents() {
                Map<String, StudentAttributes> students = new LinkedHashMap<>();
                StudentAttributes studentAttribute;
                CourseStudent courseStudent;

                for (int i = 0; i < NUMBER_OF_USER_ACCOUNTS; i++) {
                    courseStudent = new CourseStudent(STUDENT_EMAIL + i + "@gmail.tmt",
                                            STUDENT_NAME + i,
                                            STUDENT_NAME + i + ".tmms",
                                            "This student's name is " + STUDENT_NAME + i,
                                            COURSE_ID,
                                            "Team 1",
                                            "None");
                    studentAttribute = StudentAttributes.valueOf(courseStudent);
                    students.put(STUDENT_NAME + i, studentAttribute);
                }

                return students;
            }

            @Override
            protected Map<String, FeedbackSessionAttributes> generateFeedbackSessions() {
                Map<String, FeedbackSessionAttributes> feedbackSessions = new LinkedHashMap<>();

                FeedbackSessionAttributes session = FeedbackSessionAttributes
                                                            .builder("Test Feedback Session", COURSE_ID)
                                                            .withCreatorEmail(INSTRUCTOR_EMAIL)
                                                            .withStartTime(Instant.now())
                                                            .withEndTime(Instant.now().plusSeconds(500))
                                                            .withSessionVisibleFromTime(Instant.now())
                                                            .withResultsVisibleFromTime(Instant.now())
                                                            .build();

                feedbackSessions.put("Test Feedback Session", session);

                return feedbackSessions;
            }

            @Override
            protected Map<String, FeedbackQuestionAttributes> generateFeedbackQuestions() {
                Map<String, FeedbackQuestionAttributes> feedbackQuestions = new LinkedHashMap<>();
                FeedbackQuestionDetails details = new FeedbackTextQuestionDetails("Test Question");
                feedbackQuestions.put("QuestionTest",
                        FeedbackQuestionAttributes.builder()
                            .withFeedbackSessionName("Test Feedback Session")
                            .withCourseId(COURSE_ID)
                            .withQuestionDetails(details)
                            .build()
                );

                return feedbackQuestions;
            }

            @Override
            public List<String> generateCsvHeaders() {
                List<String> headers = new ArrayList<>();

                headers.add("loginId");
                headers.add("isAdmin");
                headers.add("googleId");
                headers.add("courseId");
                headers.add("fsname");

                return headers;
            }

            @Override
            public List<List<String>> generateCsvData() {
                DataBundle dataBundle = loadDataBundle(getJsonDataPath());
                List<List<String>> csvData = new ArrayList<>();

                dataBundle.students.forEach((key, student) -> {
                    List<String> csvRow = new ArrayList<>();

                    csvRow.add(student.googleId); // "googleId" is used for logging in, not "email"
                    csvRow.add("no");
                    csvRow.add(student.googleId);
                    csvRow.add(COURSE_ID);
                    csvRow.add("Test Feedback Session");

                    csvData.add(csvRow);
                });

                return csvData;
            }
        };
    }

    @Override
    protected ListedHashTree getLnpTestPlan() {
        ListedHashTree testPlan = new ListedHashTree(JMeterElements.testPlan());
        HashTree threadGroup = testPlan.add(
                JMeterElements.threadGroup(NUMBER_OF_USER_ACCOUNTS, RAMP_UP_PERIOD, 1));
        threadGroup.add(JMeterElements.csvDataSet(getPathToTestDataFile(getCsvConfigPath())));
        threadGroup.add(JMeterElements.cookieManager());
        threadGroup.add(JMeterElements.defaultSampler());
        threadGroup.add(JMeterElements.onceOnlyController())
                .add(JMeterElements.loginSampler());

        // Add HTTP samplers for test endpoint
        String firstPath = "webapi/student?courseid=${courseId}";
        threadGroup.add(JMeterElements.httpSampler(firstPath, GET, null));

        String secondPath = "webapi/session?courseid=${courseId}&fsname=${fsname}&intent=STUDENT_SUBMISSION";
        threadGroup.add(JMeterElements.httpSampler(secondPath, GET, null));

        String thirdPath = "webapi/questions?courseid=${courseId}&fsname=${fsname}&intent=STUDENT_SUBMISSION";
        threadGroup.add(JMeterElements.httpSampler(thirdPath, GET, null));

        return testPlan;
    }

    @BeforeClass
    public void classSetup() {
        createTestData();
        persistTestData();
    }

    @Test
    public void runLnpTest() throws IOException {
        runJmeter(false);
    }

    /**
     * Removes the entities added for the instructors' student enrollment L&P test.
     */
    @AfterClass
    public void classTearDown() throws IOException {
        // There is no need to add the newly enrolled students to the JSON DataBundle#students. This is because the new
        // CourseStudent entities that were created are automatically deleted when the corresponding course is deleted.
        deleteTestData();
        deleteDataFiles();
    }

}
