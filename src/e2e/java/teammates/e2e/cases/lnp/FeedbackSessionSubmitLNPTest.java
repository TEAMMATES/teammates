package teammates.e2e.cases.lnp;

import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.e2e.util.JMeterElements;
import teammates.e2e.util.LNPTestData;
import teammates.ui.webapi.request.StudentsEnrollRequest;

/**
 * L&P Test Case for instructor's student enrollment API endpoint.
 */
public class FeedbackSessionSubmitLNPTest extends BaseLNPTestCase {

    private static final int NUMBER_OF_USER_ACCOUNTS = 100;
    private static final int RAMP_UP_PERIOD = 2;
    private static final String STUDENT_NAME = "LnPStudent";
    private static final String STUDENT_EMAIL = "personalEmail";

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

                courses.put("course", CourseAttributes.builder("TestData.CS101")
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
                        InstructorAttributes.builder("TestData.CS101", "tmms.test@gmail.tmt")
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

                for (int i = 0; i < NUMBER_OF_USER_ACCOUNTS; i++) {
                    students.put(STUDENT_NAME + i,
                            StudentAttributes.builder("TestData.CS101", STUDENT_EMAIL + i + "@gmail.tmt")
                                .withGoogleId(STUDENT_NAME + i + ".tmms")
                                .withName(STUDENT_NAME + i)
                                .withComment("This student's name is " + STUDENT_NAME + i)
                                .withTeamName("Team 1")
                                .withSectionName("None")
                                .build()
                    );
                }

                return students;
            }

            @Override
            public List<String> generateCsvHeaders() {
                List<String> headers = new ArrayList<>();

                headers.add("loginId");
                headers.add("isAdmin");
                headers.add("courseId");
                headers.add("enrollData");

                return headers;
            }

            @Override
            public List<List<String>> generateCsvData() {
                DataBundle dataBundle = loadDataBundle(getJsonDataPath());
                List<List<String>> csvData = new ArrayList<>();

                /*
                dataBundle.instructors.forEach((key, instructor) -> {
                    List<String> csvRow = new ArrayList<>();

                    csvRow.add(instructor.googleId);
                    csvRow.add("no");
                    csvRow.add(instructor.courseId);

                    // Create and add student enrollment data with a team number corresponding to each section number
                    List<StudentsEnrollRequest.StudentEnrollRequest> enrollRequests = new ArrayList<>();

                    for (int i = 0; i < NUM_STUDENTS_PER_INSTRUCTOR; i++) {

                        String name = instructor.name + ".Student" + i;
                        String email = instructor.name + ".Student" + i + "@gmail.tmt";
                        String team = String.valueOf(i / NUM_STUDENTS_PER_SECTION);
                        String section = String.valueOf(i / NUM_STUDENTS_PER_SECTION);
                        String comment = "no comment";

                        enrollRequests.add(
                                new StudentsEnrollRequest.StudentEnrollRequest(name, email, team, section, comment));
                    }
                    String enrollData = sanitizeForCsv(JsonUtils.toJson(new StudentsEnrollRequest(enrollRequests)));
                    csvRow.add(enrollData);

                    csvData.add(csvRow);
                });
                */

                return csvData;
            }
        };
    }

    private Map<String, String> getRequestHeaders() {
        Map<String, String> headers = new HashMap<>();

        headers.put("X-CSRF-TOKEN", "${csrfToken}");
        headers.put("Content-Type", "text/csv");

        return headers;
    }

    private String getTestEndpoint() {
        return Const.ResourceURIs.URI_PREFIX + Const.ResourceURIs.STUDENTS + "?courseid=${courseId}";
    }

    @Override
    protected ListedHashTree getLnpTestPlan() {
        ListedHashTree testPlan = new ListedHashTree(JMeterElements.testPlan());
        HashTree threadGroup = testPlan.add(
                JMeterElements.threadGroup(1, RAMP_UP_PERIOD, 1));

        threadGroup.add(JMeterElements.csvDataSet(getPathToTestDataFile(getCsvConfigPath())));
        threadGroup.add(JMeterElements.cookieManager());
        threadGroup.add(JMeterElements.defaultSampler());

        threadGroup.add(JMeterElements.onceOnlyController())
                .add(JMeterElements.loginSampler())
                .add(JMeterElements.csrfExtractor("csrfToken"));

        // Add HTTP sampler for test endpoint
        HeaderManager headerManager = JMeterElements.headerManager(getRequestHeaders());
        threadGroup.add(JMeterElements.httpSampler(getTestEndpoint(), PUT, "${enrollData}"))
                .add(headerManager);

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
