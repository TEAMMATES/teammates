package teammates.e2e.cases.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.JsonUtils;
import teammates.e2e.util.TestProperties;
import teammates.test.BaseTestCase;
import teammates.test.FileHelper;

/**
 * Checks for test data validity.
 *
 * <p>As E2E tests can be run against actual production server, the test data needs to be carefully designed
 * such that they do not consist any form of data that may be resembling what users can be using.
 *
 * <p>For the above reason, the following fields are checked:
 * <ul>
 * <li>Account Google ID</li>
 * <li>Course ID</li>
 * <li>Student email</li>
 * <li>Instructor email</li>
 * </ul>
 *
 * <p>In order to guarantee data safety as much as possible, we set some form of validation rule for the above fields
 * such that the likelihood of any user using the same identifier is very low or zero.
 */
public class TestDataValidityTest extends BaseTestCase {

    @Test
    public void checkTestDataValidity() throws IOException {
        Map<String, List<String>> errors = new HashMap<>();
        try (Stream<Path> paths = Files.walk(Paths.get(TestProperties.TEST_DATA_FOLDER))) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                String pathString = path.toString();
                String jsonString;
                try {
                    jsonString = FileHelper.readFile(pathString);
                } catch (IOException e) {
                    errors.put(pathString, Collections.singletonList("Error reading file: " + e.getMessage()));
                    return;
                }

                DataBundle dataBundle = JsonUtils.fromJson(jsonString, DataBundle.class);

                dataBundle.accounts.forEach((id, account) -> {
                    if (!isValidTestGoogleId(account.googleId)) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid account google ID: " + account.googleId);
                    }

                    if (!isValidTestEmail(account.email)) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid account email: " + account.email);
                    }
                });

                dataBundle.courses.forEach((id, course) -> {
                    if (!isValidTestCourseId(course.getId())) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid course ID: " + course.getId());
                    }
                });

                dataBundle.students.forEach((id, student) -> {
                    if (!isValidTestGoogleId(student.googleId)) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid student google ID: " + student.googleId);
                    }

                    if (!isValidTestEmail(student.email)) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid student email: " + student.email);
                    }
                });

                dataBundle.instructors.forEach((id, instructor) -> {
                    if (!isValidTestGoogleId(instructor.googleId)) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid instructor google ID: " + instructor.googleId);
                    }

                    if (!isValidTestEmail(instructor.email)) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid instructor email: " + instructor.email);
                    }
                });
            });
        }

        if (!errors.isEmpty()) {
            String errorItems = errors.entrySet().stream()
                    .map(entry -> {
                        return entry.getKey() + System.lineSeparator()
                                + entry.getValue().stream().collect(Collectors.joining(System.lineSeparator()));
                    })
                    .collect(Collectors.joining(System.lineSeparator()));
            fail("Invalid test data exists." + System.lineSeparator() + errorItems);
        }
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private boolean isValidTestEmail(String email) {
        // TODO
        return true;
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private boolean isValidTestCourseId(String courseId) {
        // TODO
        return true;
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private boolean isValidTestGoogleId(String googleId) {
        // TODO
        return true;
    }

}
