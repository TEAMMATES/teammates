package teammates.client.scripts;

import java.util.Map;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.JsonUtils;
import teammates.test.driver.BackDoor;
import teammates.test.driver.FileHelper;
import teammates.test.driver.TestProperties;

/**
 * Usage: This script imports a large data bundle to the appengine. The target of the script is the app with
 * appID in the test.properties file.Can use DataGenerator.java to generate random data.
 *
 * <p>Notes:
 * <ul>
 * <li>Edit SOURCE_FILE_NAME before use</li>
 * <li>Should not have any limit on the size of the databundle. However, the number of entities per request
 * should not be set to too large as it may cause DeadlineExceededException (especially for evaluations)</li>
 * </ul>
 */
public final class ImportData {
    //
    // Data source file name (under src/test/resources/data folder) to import
    private static final String SOURCE_FILE_NAME = "ResultFileName.json";

    private static final int MAX_NUMBER_OF_ENTITY_PER_REQUEST = 100;
    private static final int MAX_NUMBER_OF_EVALUATION_PER_REQUEST = 1;

    private static DataBundle data;
    private static String jsonString;

    private ImportData() {
        // script, not meant to be instantiated
    }

    public static void main(String[] args) throws Exception {
        jsonString = FileHelper.readFile(TestProperties.TEST_DATA_FOLDER + "/" + SOURCE_FILE_NAME);
        data = JsonUtils.fromJson(jsonString, DataBundle.class);

        String status = "";
        do {
            long start = System.currentTimeMillis();
            boolean hasAccounts = !data.accounts.isEmpty();
            boolean hasInstructors = !data.instructors.isEmpty();
            boolean hasCourses = !data.courses.isEmpty();
            boolean hasStudents = !data.students.isEmpty();

            if (hasAccounts) {
                // Accounts
                status = persist(data.accounts);
            } else if (hasInstructors) {
                // Instructors
                status = persist(data.instructors);
            } else if (hasCourses) {
                // Courses
                status = persist(data.courses);
            } else if (hasStudents) {
                // Students
                status = persist(data.students);
            } else {
                // No more data, break the loop
                System.out.print("\n Finish!");
                break;
            }
            long elapsedTimeMillis = System.currentTimeMillis() - start;

            // Get elapsed time in seconds of the current request
            float elapsedTimeSec = elapsedTimeMillis / 1000F;
            System.out.print(status + " in " + elapsedTimeSec + " s\n");

        } while (true);

    }

    /**
     * This method will persist a number of entity and remove them from the source, return the
     * status of the operation.
     *
     * @param map - HashMap which has data to persist
     * @return status of the Backdoor operation
     */
    private static String persist(Map<String, ?> map) {
        DataBundle bundle = new DataBundle();
        int count = 0;
        String type = "";
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            String key = entry.getKey();
            Object obj = entry.getValue();

            if (obj instanceof AccountAttributes) {
                type = "AccountData";
                AccountAttributes accountData = (AccountAttributes) obj;
                bundle.accounts.put(key, accountData);
            } else if (obj instanceof InstructorAttributes) {
                type = "InstructorData";
                InstructorAttributes instructorData = (InstructorAttributes) obj;
                bundle.instructors.put(key, instructorData);
            } else if (obj instanceof CourseAttributes) {
                type = "CourseData";
                CourseAttributes courseData = (CourseAttributes) obj;
                bundle.courses.put(key, courseData);
            } else if (obj instanceof StudentAttributes) {
                type = "StudentData";
                StudentAttributes studentData = (StudentAttributes) obj;
                bundle.students.put(key, studentData);
            }
            count++;
            System.out.print(key + "\n");
            if ("EvaluationData".equals(type) && count >= MAX_NUMBER_OF_EVALUATION_PER_REQUEST
                    || count >= MAX_NUMBER_OF_ENTITY_PER_REQUEST) {
                break;
            }
        }
        System.out.print(count + " entities of type " + type + " left " + map.size() + " \n");

        return BackDoor.restoreDataBundle(bundle);
    }
}
