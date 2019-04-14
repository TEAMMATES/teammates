package teammates.e2e.cases.lnp;

import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.e2e.util.LNPTestData;

/**
 * L&P Test Case for student profile API endpoint.
 */
public final class StudentProfileLNPTest extends BaseLNPTestCase {

    private static final int NUMBER_OF_USER_ACCOUNTS = 500;
    private static final String USER_NAME = "DummyUser";
    private static final String USER_EMAIL = "personalEmail";

    @Override
    protected LNPTestData getTestData() {
        return new LNPTestData() {
            @Override
            protected Map<String, AccountAttributes> generateAccounts() {
                Map<String, AccountAttributes> accounts = new LinkedHashMap<>();

                for (int i = 0; i < NUMBER_OF_USER_ACCOUNTS; i++) {
                    accounts.put(USER_NAME + i, AccountAttributes.builder(USER_NAME + i + ".tmms")
                            .withEmail(USER_EMAIL + i + "@gmail.tmt")
                            .withName(USER_NAME + i)
                            .withIsInstructor(false)
                            .withInstitute("TEAMMATES Test Institute 1")
                            .build()
                    );
                }

                return accounts;
            }

            @Override
            protected Map<String, CourseAttributes> generateCourses() {
                Map<String, CourseAttributes> courses = new LinkedHashMap<>();

                courses.put("course", CourseAttributes.builder("TestData.CS101")
                        .withName("Intro To Programming")
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
                    students.put(USER_NAME + i, StudentAttributes.builder("TestData.CS101", USER_EMAIL + i + "@gmail.tmt")
                            .withGoogleId(USER_NAME + i + ".tmms")
                            .withName(USER_NAME + i)
                            .withComment("This student's name is " + USER_NAME + i)
                            .withTeamName("Team 1")
                            .withSectionName("None")
                            .build()
                    );
                }

                return students;
            }

            @Override
            protected Map<String, StudentProfileAttributes> generateProfiles() {
                Map<String, StudentProfileAttributes> profiles = new LinkedHashMap<>();

                for (int i = 0; i < NUMBER_OF_USER_ACCOUNTS; i++) {
                    profiles.put(USER_NAME + i, StudentProfileAttributes.builder(USER_NAME + i + ".tmms")
                            .withEmail(USER_EMAIL + i + "@gmail.tmt")
                            .withShortName(String.valueOf(i))
                            .withInstitute("TEAMMATES Test Institute 222")
                            .withMoreInfo("I am " + i)
                            .withPictureKey("")
                            .withGender(StudentProfileAttributes.Gender.MALE)
                            .withNationality("American")
                            .build()
                    );
                }

                return profiles;
            }

            @Override
            public List<String> generateCsvHeaders() {
                List<String> headers = new ArrayList<>();

                headers.add("email");
                headers.add("isAdmin");
                headers.add("googleid");

                return headers;
            }

            @Override
            public List<List<String>> generateCsvData() {
                DataBundle dataBundle = loadDataBundle(getJsonDataPath());
                List<List<String>> csvData = new ArrayList<>();

                dataBundle.students.forEach((key, student) -> {
                    List<String> csvRow = new ArrayList<>();

                    csvRow.add(student.googleId); // "googleid" is used for logging in, not "email"
                    csvRow.add("no");
                    csvRow.add(student.googleId);

                    csvData.add(csvRow);
                });

                return csvData;
            }
        };
    }

    @Override
    protected int getNumberOfThreads() {
        return NUMBER_OF_USER_ACCOUNTS;
    }

    @Override
    protected int getRampUpPeriod() {
        return 2;
    }

    @Override
    protected String getTestEndpoint() {
        return Const.ResourceURIs.URI_PREFIX + Const.ResourceURIs.STUDENT_PROFILE + "?googleid=${googleid}";
    }

    @Override
    protected String getTestMethod() {
        return "GET";
    }

    @Override
    protected Map<String, String> getTestParameters() {
        Map<String, String> args = new HashMap<>();
        args.put("googleid", "${googleid}");
        return args;
    }

    @BeforeClass
    public void classSetup() {
        createTestData();
        persistTestData();
    }

    @Test
    public void runLnpTest() throws IOException {
        runJmeter(true);
        // TODO: Generate summary report from .jtl results file / ResultCollector.
    }

    @AfterClass
    public void classTearDown() throws IOException {
        deleteTestData();
        deleteDataFiles();
    }

}
