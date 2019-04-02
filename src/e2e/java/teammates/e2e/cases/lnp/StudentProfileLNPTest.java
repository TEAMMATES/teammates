package teammates.e2e.cases.lnp;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.CSVDataSet;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.OnceOnlyController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.OnceOnlyControllerGui;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.protocol.http.config.gui.HttpDefaultsGui;
import org.apache.jmeter.protocol.http.control.CookieManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.CookiePanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.testbeans.gui.TestBeanGUI;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.apache.jmeter.threads.SetupThreadGroup;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
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
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.e2e.util.LNPTestData;

/**
 * L&P Test Case for student profile API endpoint.
 */
public final class StudentProfileLNPTest extends BaseLNPTestCase {

    private static final String JSON_DATA_PATH = "/studentProfileData.json";
    private static final String CSV_CONFIG_PATH = "/studentProfileConfig.csv";

    private static final int NUMBER_OF_USER_ACCOUNTS = 500;
    private static final String USER_NAME = "DummyUser";
    private static final String USER_EMAIL = "personalEmail";

    @Override
    protected LNPTestData getTestData() {
        return new LNPTestData() {
            @Override
            protected Map<String, AccountAttributes> generateAccounts() {
                Map<String, AccountAttributes> accounts = new HashMap<>();

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
                Map<String, CourseAttributes> courses = new HashMap<>();

                courses.put("course", CourseAttributes.builder("TestData.CS101")
                        .withName("Intro To Programming")
                        .withTimezone(ZoneId.of("UTC"))
                        .build()
                );

                return courses;
            }

            @Override
            protected Map<String, InstructorAttributes> generateInstructors() {
                Map<String, InstructorAttributes> instructors = new HashMap<>();

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
                Map<String, StudentAttributes> students = new HashMap<>();

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
            protected Map<String, FeedbackSessionAttributes> generateFeedbackSessions() {
                return new HashMap<>();
            }

            @Override
            protected Map<String, FeedbackQuestionAttributes> generateFeedbackQuestions() {
                return new HashMap<>();
            }

            @Override
            protected Map<String, FeedbackResponseAttributes> generateFeedbackResponses() {
                return new HashMap<>();
            }

            @Override
            protected Map<String, FeedbackResponseCommentAttributes> generateFeedbackResponseComments() {
                return new HashMap<>();
            }

            @Override
            protected Map<String, StudentProfileAttributes> generateProfiles() {
                Map<String, StudentProfileAttributes> profiles = new HashMap<>();

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
                DataBundle dataBundle = loadDataBundle(JSON_DATA_PATH);
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
    protected String getCsvConfigPath() {
        return CSV_CONFIG_PATH;
    }

    @Override
    protected String getJsonDataPath() {
        return JSON_DATA_PATH;
    }

    /**
     * Returns the JMeter test plan for the Student Profile endpoint.
     */
    @Override
    protected HashTree generateTestPlan() {
        // TestPlan
        TestPlan testPlan = new TestPlan();
        testPlan.setName("Student Profile Test Plan");
        testPlan.setEnabled(true);
        testPlan.setUserDefinedVariables(new Arguments());
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());

        // Thread Group controller
        LoopController loopController = new LoopController();
        loopController.setEnabled(true);
        loopController.setLoops(1);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());

        // Thread Group
        ThreadGroup threadGroup = new SetupThreadGroup();
        threadGroup.setName("Thread Group");
        threadGroup.setNumThreads(NUMBER_OF_USER_ACCOUNTS);
        threadGroup.setRampUp(2);
        threadGroup.setProperty(new StringProperty(ThreadGroup.ON_SAMPLE_ERROR, ThreadGroup.ON_SAMPLE_ERROR_CONTINUE));
        threadGroup.setSamplerController(loopController);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());

        // CSVConfig
        CSVDataSet csvDataSet = new CSVDataSet();
        csvDataSet.setName("CSV Data Config: User Details");
        csvDataSet.setProperty(new StringProperty("filename", "src/e2e/resources/data/studentProfileConfig.csv"));
        csvDataSet.setProperty(new StringProperty("delimiter", "|"));
        csvDataSet.setProperty(new StringProperty("shareMode", "shareMode.all"));
        csvDataSet.setProperty("ignoreFirstLine", true);
        csvDataSet.setProperty("quoted", true);
        csvDataSet.setProperty("recycle", true);
        csvDataSet.setProperty("stopThread", false);
        csvDataSet.setProperty(TestElement.TEST_CLASS, CSVDataSet.class.getName());
        csvDataSet.setProperty(TestElement.GUI_CLASS, TestBeanGUI.class.getName());

        // CookieManager
        CookieManager cookieManager = new CookieManager();
        cookieManager.setName("HTTP Cookie Manager");
        cookieManager.setClearEachIteration(false);
        cookieManager.setCookiePolicy("standard");
        cookieManager.setProperty(TestElement.TEST_CLASS, CookieManager.class.getName());
        cookieManager.setProperty(TestElement.GUI_CLASS, CookiePanel.class.getName());

        // HTTP Default Sampler
        ConfigTestElement defaultSampler = new ConfigTestElement();
        defaultSampler.setEnabled(true);
        defaultSampler.setProperty(new TestElementProperty(HTTPSampler.ARGUMENTS, new Arguments()));
        defaultSampler.setProperty(HTTPSampler.DOMAIN, "localhost");
        defaultSampler.setProperty(HTTPSampler.PORT, "8080");
        defaultSampler.setName("HTTP Request Defaults");
        defaultSampler.setProperty(TestElement.TEST_CLASS, ConfigTestElement.class.getName());
        defaultSampler.setProperty(TestElement.GUI_CLASS, HttpDefaultsGui.class.getName());

        // Login HTTP Request
        HTTPSamplerProxy loginSampler = new HTTPSamplerProxy();
        loginSampler.setName("Login");
        loginSampler.setPath("_ah/login?action=Log+In&email=${email}&isAdmin=${isAdmin}&continue=http://localhost:8080/webapi/auth?frontendUrl=http://localhost:4200");
        loginSampler.setMethod("POST");
        loginSampler.setFollowRedirects(true);
        loginSampler.setUseKeepAlive(true);
        loginSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        loginSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        // Controller for Login request
        OnceOnlyController onceOnlyController = new OnceOnlyController();
        onceOnlyController.setName("Only once controller");
        onceOnlyController.setProperty(TestElement.TEST_CLASS, OnceOnlyController.class.getName());
        onceOnlyController.setProperty(TestElement.GUI_CLASS, OnceOnlyControllerGui.class.getName());

        // Student Profile HTTP Request
        HTTPSamplerProxy studentProfileSampler = new HTTPSamplerProxy();
        studentProfileSampler.setName("Student Profile");
        studentProfileSampler.setPath("webapi/student/profile?googleid=${googleid}");
        studentProfileSampler.setMethod("GET");
        studentProfileSampler.addArgument("googleid", "${googleid}");
        studentProfileSampler.setEnabled(true);
        studentProfileSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        studentProfileSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        // Create Test plan
        HashTree testPlanHashTree = new ListedHashTree();
        HashTree threadGroupHashTree = testPlanHashTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(csvDataSet);
        threadGroupHashTree.add(cookieManager);
        threadGroupHashTree.add(defaultSampler);
        threadGroupHashTree.add(onceOnlyController, loginSampler);
        threadGroupHashTree.add(studentProfileSampler);

        return testPlanHashTree;
    }

    @BeforeClass
    public void classSetup() {
        createTestData();
        persistTestData(JSON_DATA_PATH);
    }

    @Test
    public void runLnpTest() throws Exception {
        runJmeter(false);
    }

    @AfterClass
    public void classTearDown() throws Exception {
        deleteTestData(JSON_DATA_PATH);
        deleteDataFiles();
    }

}
