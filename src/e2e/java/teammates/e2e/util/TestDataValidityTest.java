package teammates.e2e.util;

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
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.test.BaseTestCase;
import teammates.test.FileHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

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
    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    // SpotBugs false positive: https://github.com/spotbugs/spotbugs/issues/1694
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
                if (path.getFileName() == null) {
                    return;
                }

                String testPage = path.getFileName().toString().replace("E2ETest.json", "");
                DataBundle dataBundle = JsonUtils.fromJson(jsonString, DataBundle.class);

                dataBundle.accounts.forEach((id, account) -> {
                    if (!isValidTestGoogleId(account.getGoogleId(), testPage)) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid account google ID: " + account.getGoogleId());
                    }

                    if (!isValidTestEmail(account.getEmail())) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid account email: " + account.getEmail());
                    }
                });

                dataBundle.courses.forEach((id, course) -> {
                    if (!isValidTestCourseId(course.getId(), testPage)) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid course ID: " + course.getId());
                    }
                });

                dataBundle.students.forEach((id, student) -> {
                    if (!isValidTestGoogleId(student.getGoogleId(), testPage)) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid student google ID: " + student.getGoogleId());
                    }

                    if (!isValidTestEmail(student.getEmail())) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid student email: " + student.getEmail());
                    }
                });

                dataBundle.instructors.forEach((id, instructor) -> {
                    if (!isValidTestGoogleId(instructor.getGoogleId(), testPage)) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid instructor google ID: " + instructor.getGoogleId());
                    }

                    if (!isValidTestEmail(instructor.getEmail())) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid instructor email: " + instructor.getEmail());
                    }
                });

                dataBundle.feedbackSessions.forEach((id, session) -> {
                    if (!isValidTestCourseId(session.getCourseId(), testPage)) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid session course ID: " + session.getCourseId());
                    }

                    if (!isValidTestEmail(session.getCreatorEmail())) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid session creator email: " + session.getCreatorEmail());
                    }
                });

                dataBundle.feedbackResponses.forEach((id, response) -> {
                    if (!isValidTestCourseId(response.getCourseId(), testPage)) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid response course ID: " + response.getCourseId());
                    }

                    if (response.getGiver().contains("@") && !isValidTestEmail(response.getGiver())) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid response giver email: " + response.getGiver());
                    }

                    if (response.getRecipient().contains("@") && !isValidTestEmail(response.getRecipient())) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid response recipient email: " + response.getRecipient());
                    }
                });

                dataBundle.accountRequests.forEach((id, accountRequest) -> {
                    if (!isValidTestEmail(accountRequest.getEmail())) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid account request email: " + accountRequest.getEmail());
                    }
                });

                dataBundle.deadlineExtensions.forEach((id, deadlineExtension) -> {
                    if (!isValidTestCourseId(deadlineExtension.getCourseId(), testPage)) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid deadline extension course ID: " + deadlineExtension.getCourseId());
                    }

                    if (!isValidTestEmail(deadlineExtension.getUserEmail())) {
                        errors.computeIfAbsent(pathString, k -> new ArrayList<>())
                                .add("Invalid deadline extension user email: " + deadlineExtension.getUserEmail());
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

    private boolean isValidTestEmail(String email) {
        return email.endsWith(Const.TEST_EMAIL_DOMAIN);
    }

    private boolean isValidTestCourseId(String courseId, String testPage) {
        return courseId.matches(constructIdRegex(testPage)) && courseId.length() < 32;
    }

    private boolean isValidTestGoogleId(String googleId, String testPage) {
        if (googleId == null || googleId.equals("")) {
            // Empty google ID is always acceptable
            return true;
        }
        return googleId.matches(constructIdRegex(testPage)) && googleId.length() < 32;
    }

    private String constructIdRegex(String testPage) {
        // We set these rules for setting the prefix for IDs:
        // Rule 1: must start with predefined phrase
        // Rule 2: must be representative of the test but yet not too long

        String shortenedTestPage = testPage;

        // Trim the Page word at the end if any
        shortenedTestPage = shortenedTestPage
                .replaceFirst("Page$", "");

        // Shorten common words
        shortenedTestPage = shortenedTestPage
                .replaceFirst("^Admin", "A")
                .replaceFirst("^Instructor", "I")
                .replaceFirst("^Automated", "Aut")
                .replace("Feedback", "F")
                .replace("Student", "S")
                .replace("Course", "C")
                .replace("Question", "Qn")
                .replaceFirst("Session(s?)", "Ses$1")
                .replaceFirst("Reports?", "Rep")
                .replaceFirst("Results?", "Res")
                .replace("Details", "Det")
                .replace("Confirmation", "Conf")
                .replace("Reminders", "Rem")
                .replace("Notification", "Notif")
                .replace("Activity", "Act")
                .replace("IndividualExtension", "Ie");

        // Shorten question types
        shortenedTestPage = shortenedTestPage
                .replace("Recipient", "Rcpt")
                .replace("Option", "Opt")
                .replace("Contribution", "Contr")
                .replace("ConstSum", "CSum");

        // Prefix with tm.e2e.
        // Add validation at the end to ensure that the ID is not equal to the prefix only
        return "tm\\.e2e\\." + shortenedTestPage + "\\.(?:[A-Za-z0-9]+.)*[A-Za-z0-9]+";
    }

}
