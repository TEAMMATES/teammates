package teammates.client.scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.JsonUtils;
import teammates.test.driver.BackDoor;
import teammates.test.driver.FileHelper;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

/**
 * Annotations for Performance tests with
 * <ul>
 * <li>Name: name of the test</li>
 * <li>CustomTimer: (default is false) if true, the function will return the duration need to recorded itself.
 *                  If false, the function return the status of the test and expected the function
 *                  which called it to record the duration.</li>
 * </ul>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface PerformanceTest {
    String name() default "";
    boolean customTimer() default false;
}

/**
 * Usage: This script is to profile performance of the app with id in test.properties. To run multiple instance
 * of this script in parallel, use ParallelProfiler.Java.
 *
 * <p>Notes:
 * <ul>
 * <li>Edit name of the report file, the result will be written to a file in src/test/resources/data folder</li>
 * <li>Make sure that the data in PerformanceProfilerImportData.json is imported (by using ImportData.java)</li>
 * </ul>
 */
public class PerformanceProfiler extends Thread {

    private static final String defaultReportPath = TestProperties.TEST_DATA_FOLDER + "/" + "nameOfTheReportFile.txt";
    private static final int NUM_OF_RUNS = 2;
    private static final int WAIT_TIME_TEST = 1000; //waiting time between tests, in ms
    private static final int WAIT_TIME_RUN = 5000; //waiting time between runs, in ms
    private static final String runningDataSourceFile = "PerformanceProfilerRunningData.json";

    private String reportFilePath;
    private DataBundle data;
    private Map<String, ArrayList<Float>> results = new HashMap<>();

    protected PerformanceProfiler(String path) {
        reportFilePath = path;
    }

    @Override
    public void run() {
        //Data used for profiling
        String jsonString = "";
        try {
            jsonString = FileHelper.readFile(TestProperties.TEST_DATA_FOLDER + "/" + runningDataSourceFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = JsonUtils.fromJson(jsonString, DataBundle.class);

        //Import previous results
        try {
            results = importReportFile(reportFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Browser browser;
        for (int i = 0; i < NUM_OF_RUNS; i++) {
            browser = BrowserPool.getBrowser();
            //overcome initial loading time with the below line
            //getInstructorAsJson();

            //get all methods with annotation and record time
            Method[] methods = PerformanceProfiler.class.getMethods();
            for (Method method : methods) {
                performMethod(method);
            }

            // Wait between runs
            BrowserPool.release(browser);
            try {
                Thread.sleep(WAIT_TIME_RUN);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Write the results back to file
        try {
            printResult(reportFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print("\n Finished!");
    }

    /**
     * This function perform the method and print the return value for debugging.
     */
    private void performMethod(Method method) {
        if (method.isAnnotationPresent(PerformanceTest.class)) {
            PerformanceTest test = method.getAnnotation(PerformanceTest.class);
            String name = test.name();
            boolean customTimer = test.customTimer();
            Type type = method.getReturnType();
            if (!results.containsKey(name)) {
                results.put(name, new ArrayList<Float>());
            }
            try {
                float duration = 0;
                if (type.equals(String.class) && !customTimer) {
                    long startTime = System.nanoTime();
                    Object retVal = method.invoke(this);
                    long endTime = System.nanoTime();
                    duration = (float) ((endTime - startTime) / 1000000.0); //in miliSecond
                    System.out.print("Name: " + name + "\tTime: " + duration + "\tVal: " + retVal.toString() + "\n");
                } else if (type.equals(Long.class) && customTimer) {
                    duration = (float) ((Long) method.invoke(this) / 1000000.0);
                    System.out.print("Name: " + name + "\tTime: " + duration + "\n");
                }
                // Add new duration to the arrayList of the test.
                ArrayList<Float> countList = results.get(name);
                countList.add(duration);
            } catch (Exception e) {
                System.out.print(e.toString());
            }

            // reduce chance of data not being persisted
            try {
                Thread.sleep(WAIT_TIME_TEST);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Run this script as an single-thread Java application (for simple, non-parallel profiling).
     * For parallel profiling, please use ParallelProfiler.java.
     */
    public static void main(String[] args) {
        // Run this script as an single-thread Java application (for simple, non-parallel profiling)
        // For parallel profiling, please use ParallelProfiler.java
        new PerformanceProfiler(defaultReportPath).start();
    }

    /**
     * The results from file stored in filePath.
     * @return {@code HashMap<nameOfTest, durations>} of the report stored in filePath
     */
    private static HashMap<String, ArrayList<Float>> importReportFile(String filePath) throws IOException {
        HashMap<String, ArrayList<Float>> results = new HashMap<>();
        File reportFile = new File(filePath);

        // Create the report file if not existed
        if (!reportFile.exists()) {
            try {
                reportFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }

        //Import old data to the HashMap
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            System.out.println(strLine);
            String[] strs = strLine.split("\\|");

            String testName = strs[0];
            String[] durations = strs[2].split("\\,");

            ArrayList<Float> arr = new ArrayList<>();
            for (String str : durations) {
                Float f = Float.parseFloat(str);
                arr.add(f);
            }
            results.put(testName, arr);
        }
        br.close();
        return results;
    }

    /**
     * Writes the results to the file with path filePath.
     */
    private void printResult(String filePath) throws IOException {
        List<String> list = new ArrayList<>();
        for (String str : results.keySet()) {
            list.add(str);
        }
        list.sort(null);
        FileWriter fstream = new FileWriter(filePath);
        BufferedWriter out = new BufferedWriter(fstream);

        for (String str : list) {
            StringBuilder lineStrBuilder = new StringBuilder();
            ArrayList<Float> arr = results.get(str);
            Float total = 0.0f;
            for (Float f : arr) {
                total += f;
                lineStrBuilder.append(f).append(" , ");
            }
            String lineStr = lineStrBuilder.substring(0, lineStrBuilder.length() - 3); //remove last comma
            Float average = total / arr.size();
            out.write(str + "| " + average + " | " + lineStr + "\n");
        }
        out.close();
    }

    // TODO: this class needs to be tweaked to work with the new Browser class

    // Performance Tests , the order of these tests is also the order they will run

    /*

    @PerformanceTest(name = "Instructor login",customTimer = true)
    public Long instructorLogin() {
        browser.goToUrl(TestProperties.TEAMMATES_URL);
        browser.click(browser.instructorLoginButton);
        long startTime = System.nanoTime();
        browser.login("testingforteammates@gmail.com", "testingforteammates", false);
        return System.nanoTime()-startTime;
    }

    @PerformanceTest(name = "Instructor home page")
    String instructorHomePage() {
        browser.goToUrl(TestProperties.TEAMMATES_URL + "/page/instructorHome");
        return "";
    }
    @PerformanceTest(name = "Instructor eval page")
    public String instructorEval() {
        browser.goToUrl(TestProperties.TEAMMATES_URL + "/page/instructorEval");
        return "";
    }

    @PerformanceTest(name = "Instructor add eval",customTimer = true)
    public Long instructorAddEval() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.add(Calendar.DATE, +1);
        Date date1 = cal.getTime();
        cal.add(Calendar.DATE, +2);
        Date date2 = cal.getTime();
        long startTime = System.nanoTime();
        browser.addEvaluation("idOf_Z2_Cou0_of_Coo0", "test", date1, date2, true,
                              "This is the instructions, please follow", 5);
        browser.waitForStatusMessage(Common.STATUS_EVALUATION_ADDED);
        return System.nanoTime() - startTime;
    }

    @PerformanceTest(name = "Instructor eval page")
    public String instructorEval2() {
        browser.goToUrl(TestProperties.TEAMMATES_URL + "/page/instructorEval");
        return "";
    }

    @PerformanceTest(name = "Instructor delete eval*",customTimer = true)
    public Long instructorDeleteEval() {
        int evalRowID = browser.getEvaluationRowID("idOf_Z2_Cou0_of_Coo0", "test");
        By deleteLinkLocator = browser.getInstructorEvaluationDeleteLinkLocator(evalRowID);
        long startTime =System.nanoTime();
        browser.clickAndConfirm(deleteLinkLocator);
        return System.nanoTime() - startTime;
    }

    @PerformanceTest(name = "Instructor course page")
    public String instructorCourse() {
        browser.goToUrl(TestProperties.TEAMMATES_URL + "/page/instructorCourse");
        return "";
    }

    @PerformanceTest(name = "Instructor add course",customTimer = true)
    public Long instructorAddCourse() {
        long startTime = System.nanoTime();
        browser.addCourse("testcourse", "testcourse");
        browser.waitForStatusMessage(Common.STATUS_COURSE_ADDED);
        return System.nanoTime() - startTime;
    }

    @PerformanceTest(name = "Instructor course page")
    public String instructorCourse2() {
        browser.goToUrl(TestProperties.TEAMMATES_URL + "/page/instructorCourse");
        return "";
    }

    @PerformanceTest(name = "Instructor delete course*",customTimer = true)
    public Long instructorDeleteCourse() throws Exception {
        String courseId = "testcourse";
        int courseRowId = browser.getCourseRowID(courseId);
        By deleteLinkLocator = browser.getInstructorCourseDeleteLinkLocator(courseRowId);
        long startTime = System.nanoTime();
        browser.clickAndConfirm(deleteLinkLocator);
        return System.nanoTime() - startTime;
    }

    @PerformanceTest(name = "Instructor course student detail page")
    public String instructorCourseStudentDetails() {
        browser.goToUrl(TestProperties.TEAMMATES_URL
                        + "/page/instructorCourseStudentDetails?courseid=idOf_Z2_Cou0_of_Coo0"
                        + "&studentemail=testingforteammates%40gmail.com");
        return "";
    }

    @PerformanceTest(name = "Instructor course enroll page")
    public String instructorCourseEnroll() {
        browser.goToUrl(TestProperties.TEAMMATES_URL + "/page/instructorCourseEnroll?courseid=idOf_Z2_Cou0_of_Coo0");
        return "";
    }

    @PerformanceTest(name = "Instructor course enroll student*",customTimer = true)
    public Long instructorCourseEnrollStudent() {
        String enrollString = "Team 1 | teststudent | alice.b.tmms@gmail.com | This comment has been changed\n";
        browser.fillString(By.id("enrollstudents"), enrollString);
        long startTime = System.nanoTime();
        browser.click(By.id("button_enroll"));
        return System.nanoTime() - startTime;
    }

    @PerformanceTest(name = "Instructor course enroll page")
    public String instructorCourseDetails() {
        browser.goToUrl(TestProperties.TEAMMATES_URL + "/page/instructorCourseDetails?courseid=idOf_Z2_Cou0_of_Coo0");
        return "";
    }

    @PerformanceTest(name = "Instructor course delete student *",customTimer = true)
    public Long instructorCourseDeleteStudent() {
        int studentRowId = browser.getStudentRowId("teststudent");
        long startTime = System.nanoTime();
        browser.clickInstructorCourseDetailStudentDeleteAndConfirm(studentRowId);
        return System.nanoTime() - startTime;
    }

    @PerformanceTest(name = "Instructor eval results")
    public String instructorEvalResults() {
        browser.goToUrl(TestProperties.TEAMMATES_URL
                        + "/page/instructorEvalResults?courseid=idOf_Z2_Cou0_of_Coo0"
                        + "&evaluationname=Z2_Eval0_in_Cou0_of_Coo0");
        return "";
    }

    @PerformanceTest(name = "Instructor view student eval ")
    public String instructorViewStuEval() {
        browser.goToUrl(TestProperties.TEAMMATES_URL
                        + "/page/instructorEvalSubmissionView?courseid=idOf_Z2_Cou0_of_Coo0"
                        + "&evaluationname=Z2_Eval0_in_Cou0_of_Coo0&studentemail=Z2_Stu59Email%40gmail.com");
        return "";
    }

    @PerformanceTest(name = "Instructor help page ")
    public String instructorHelp() {
        browser.goToUrl(TestProperties.TEAMMATES_URL + "/instructorHelp.jsp");
        return "";
    }

    @PerformanceTest(name = "Instructor log out")
    public String instructorLogout() {

        browser.logout();
        return "";
    }

    @PerformanceTest(name = "Student login")
    public String stuLogin() {
        browser.loginStudent("testingforteammates@gmail.com","testingforteammates");
        return "";
    }
    @PerformanceTest(name = "Student homepage")
    public String stuHomepage() {
        browser.goToUrl(TestProperties.TEAMMATES_URL + "/page/studentHome");
        return "";
    }

    @PerformanceTest(name = "Student course detail page")
    public String stuCoursepage() {
        browser.goToUrl(TestProperties.TEAMMATES_URL + "/page/studentCourseDetails?courseid=idOf_Z2_Cou0_of_Coo0");
        return "";
    }

    @PerformanceTest(name = "Student edit submission page")
    public String stuEditSubmissionPage() {
        browser.goToUrl(TestProperties.TEAMMATES_URL
                        + "/page/studentEvalEdit?courseid=idOf_Z2_Cou0_of_Coo0"
                        + "&evaluationname=Z2_Eval0_in_Cou0_of_Coo0");
        return "";
    }
    @PerformanceTest(name = "Student edit submission ")
    public String stuEditSubmission() {
        browser.goToUrl(TestProperties.TEAMMATES_URL + "/page/studentCourseDetails?courseid=idOf_Z2_Cou0_of_Coo0");
        return "";
    }
    @PerformanceTest(name = "Student eval result ")
    public String stuEvalResultPage() {
        browser.goToUrl(TestProperties.TEAMMATES_URL
                        + "/page/studentEvalResults?courseid=idOf_Z2_Cou0_of_Coo0"
                        + "&evaluationname=Z2_Eval0_in_Cou0_of_Coo0");
        return "";
    }

    @PerformanceTest(name = "Student log out")
    public String stuLogout() {

        browser.logout();
        return "";
    }

    @PerformanceTest(name = "BD create instructor")
    public String createInstructor() {
        String status = "";
        Set<String> set = data.instructors.keySet();
        for (String instructorKey : set) {
            InstructorAttributes instructor = data.instructors.get(instructorKey);
            status += BackDoor.createInstructor(instructor);
        }
        return status;
    }
    @PerformanceTest(name = "BD get instructor")
    public String getInstructorAsJson() {
        String status = "";
        Set<String> set = data.instructors.keySet();
        for (String instructorKey : set) {
            InstructorAttributes instructor = data.instructors.get(instructorKey);
            status += BackDoor.getInstructorAsJson(instructor.googleId, instructor.courseId);
        }
        return status;
    }

    @PerformanceTest(name = "BD get courses by instructor")
    public String getCoursesByInstructor() {
        String status = "";
        Set<String> set = data.instructors.keySet();
        for (String instructorKey : set) {
            InstructorAttributes instructor = data.instructors.get(instructorKey);
            String[] courses = BackDoor.getCoursesByInstructorId(instructor.googleId);
            for (String courseName : courses) {
                status += " " + courseName;
            }
        }
        return status;
    }
    @PerformanceTest(name = "BD create course")
    public String createCourse() {
        String status = "";
        Set<String> set = data.courses.keySet();
        for (String courseKey : set) {
            CourseAttributes course = data.courses.get(courseKey);
            status += " " + BackDoor.createCourse(course);
        }
        return status;
    }

    @PerformanceTest(name = "BD get course")
    public String getCourseAsJson() {
        String status = "";
        Set<String> set = data.courses.keySet();
        for (String courseKey : set) {
            CourseAttributes course = data.courses.get(courseKey);
            status += " " + BackDoor.getCourseAsJson(course.id);
        }
        return status;
    }

    @PerformanceTest(name = "BD create student")
    public String createStudent() {
        String status = "";
        Set<String> set = data.students.keySet();
        for (String studentKey : set) {
            StudentAttributes student = data.students.get(studentKey);
            status += " " + BackDoor.createStudent(student);
        }
        return status;
    }

    // The method createSubmission is not implemented in BackDoor yet.
    @PerformanceTest(name = "BD create submission")
    static public String createSubmissions()
    {
        String status = "";
        Set<String> set = data.submissions.keySet();
        for (String submissionKey : set)
        {
            SubmissionData submission = data.submissions.get(submissionKey);
            status += " " + BackDoor.createSubmission(submission);
        }
        return status;
    }

    */

    @PerformanceTest(name = "BD get student")
    public String getStudent() {
        StringBuilder status = new StringBuilder();
        Set<String> set = data.students.keySet();
        for (String studentKey : set) {
            StudentAttributes student = data.students.get(studentKey);
            status.append(' ').append(JsonUtils.toJson(BackDoor.getStudent(student.course, student.email)));
        }
        return status.toString();
    }

    @PerformanceTest(name = "BD get key for student")
    public String getKeyForStudent() {
        StringBuilder status = new StringBuilder();
        Set<String> set = data.students.keySet();
        for (String studentKey : set) {
            StudentAttributes student = data.students.get(studentKey);
            status.append(' ').append(BackDoor.getEncryptedKeyForStudent(student.course, student.email));
        }
        return status.toString();
    }

    @PerformanceTest(name = "BD edit student")
    public String editStudent() {
        StringBuilder status = new StringBuilder();
        Set<String> set = data.students.keySet();
        for (String studentKey : set) {
            StudentAttributes student = data.students.get(studentKey);
            status.append(' ').append(BackDoor.editStudent(student.email, student));
        }
        return status.toString();
    }

    @PerformanceTest(name = "BD delete student")
    public String deleteStudent() {
        StringBuilder status = new StringBuilder();
        Set<String> set = data.students.keySet();
        for (String studentKey : set) {
            StudentAttributes student = data.students.get(studentKey);
            status.append(' ').append(BackDoor.deleteStudent(student.course, student.email));
        }
        return status.toString();
    }

    @PerformanceTest(name = "BD Delete Course")
    public String deleteCourse() {
        StringBuilder status = new StringBuilder();
        Set<String> set = data.courses.keySet();
        for (String courseKey : set) {
            CourseAttributes course = data.courses.get(courseKey);
            status.append(' ').append(BackDoor.deleteCourse(course.getId()));
        }
        return status.toString();
    }

    @PerformanceTest(name = "BD Delete Instructor")
    public String deleteInstructor() {
        StringBuilder status = new StringBuilder();
        Set<String> set = data.instructors.keySet();
        for (String instructorKey : set) {
            InstructorAttributes instructor = data.instructors.get(instructorKey);
            status.append(BackDoor.deleteInstructor(instructor.email, instructor.courseId));
        }
        return status.toString();
    }
}
