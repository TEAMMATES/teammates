package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;

/**
 * Represents the instructor search page.
 */
public class InstructorSearchPage extends AppPage {

    @FindBy(id = "search-keyword")
    private WebElement searchKeyword;

    @FindBy(id = "btn-search")
    private WebElement searchButton;

    @FindBy(id = "students-checkbox")
    private WebElement studentsCheckbox;

    @FindBy(id = "comment-checkbox")
    private WebElement commentCheckbox;

    public InstructorSearchPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Search");
    }

    public void verifyNumCoursesInStudentResults(int expectedNum) {
        List<WebElement> studentCoursesResult = getStudentCoursesResult();
        assertEquals(expectedNum, studentCoursesResult.size());
    }

    public void search(boolean searchForStudents, boolean searchForComments, String searchTerm) {
        if (searchForStudents && !studentsCheckbox.isSelected()
                || !searchForStudents && studentsCheckbox.isSelected()) {
            click(studentsCheckbox);
        }

        if (searchForComments && !commentCheckbox.isSelected()
                || !searchForComments && commentCheckbox.isSelected()) {
            click(commentCheckbox);
        }

        if (searchForStudents || searchForComments) {
            searchKeyword.clear();
            searchKeyword.sendKeys(searchTerm);
            click(searchButton);
            WebElement loadingContainer = null;
            try {
                loadingContainer = waitForElementPresence(By.className("loading-container"));
            } catch (TimeoutException e) {
                // loading has finished before this block is reached
            }
            if (loadingContainer != null) {
                waitForElementStaleness(loadingContainer);
            }
        } else {
            verifyUnclickable(searchButton);
        }
    }

    private List<WebElement> getStudentCoursesResult() {
        return browser.driver.findElements(By.className("student-course-table"));
    }

    private String createHeaderText(CourseAttributes course) {
        return "[" + course.getId() + "]";
    }

    public void verifyStudentDetails(Map<String, CourseAttributes> courses, Map<String, StudentAttributes[]> students) {
        List<WebElement> studentCoursesResult = getStudentCoursesResult();
        assertEquals(students.size(), courses.size());
        assertEquals(students.size(), studentCoursesResult.size());

        students.forEach((courseId, studentsForCourse) -> verifyStudentDetails(courses.get(courseId), studentsForCourse));
    }

    public void verifyStudentDetails(CourseAttributes course, StudentAttributes[] students) {
        WebElement targetCourse = getStudentTableForHeader(course);
        if (targetCourse == null) {
            fail("Course with ID " + course.getId() + " is not found");
        }

        WebElement studentList = targetCourse.findElement(By.tagName("table"));
        verifyTableBodyValues(studentList, getExpectedStudentValues(students));
    }

    private WebElement getStudentTableForHeader(CourseAttributes course) {
        String targetHeader = createHeaderText(course);
        List<WebElement> studentCoursesResult = getStudentCoursesResult();

        return studentCoursesResult.stream().filter(studentCourse -> {
            String courseHeader = studentCourse.findElement(By.className("card-header")).getText();
            return targetHeader.equals(courseHeader);
        }).findFirst().orElse(null);
    }

    private String[][] getExpectedStudentValues(StudentAttributes[] students) {
        String[][] expected = new String[students.length][6];
        for (int i = 0; i < students.length; i++) {
            StudentAttributes student = students[i];
            expected[i][0] = "View Photo";
            expected[i][1] = student.getSection();
            expected[i][2] = student.getTeam();
            expected[i][3] = student.getName();
            expected[i][4] = student.getGoogleId().isEmpty() ? "Yet to Join" : "Joined";
            expected[i][5] = student.getEmail();
        }
        return expected;
    }

    public void deleteStudent(CourseAttributes course, String studentEmail) {
        clickAndConfirm(getDeleteButton(course, studentEmail));
        waitUntilAnimationFinish();
    }

    private WebElement getDeleteButton(CourseAttributes course, String studentEmail) {
        WebElement studentRow = getStudentRow(course, studentEmail);
        return studentRow.findElement(By.id("btn-delete"));
    }

    private WebElement getStudentRow(CourseAttributes course, String studentEmail) {
        WebElement targetCourse = getStudentTableForHeader(course);
        if (targetCourse == null) {
            fail("Course with ID " + course.getId() + " is not found");
        }

        List<WebElement> studentRows = targetCourse.findElements(By.cssSelector("tbody tr"));
        for (WebElement studentRow : studentRows) {
            List<WebElement> studentCells = studentRow.findElements(By.tagName("td"));
            if (studentCells.get(5).getText().equals(studentEmail)) {
                return studentRow;
            }
        }
        return null;
    }

    public InstructorCourseStudentDetailsViewPage clickViewStudent(CourseAttributes course, String studentEmail) {
        WebElement studentRow = getStudentRow(course, studentEmail);
        WebElement viewButton = studentRow.findElement(By.id("btn-view-details"));
        click(viewButton);
        ThreadHelper.waitFor(2000);
        switchToNewWindow();
        return changePageType(InstructorCourseStudentDetailsViewPage.class);
    }

    public InstructorCourseStudentDetailsEditPage clickEditStudent(CourseAttributes course, String studentEmail) {
        WebElement studentRow = getStudentRow(course, studentEmail);
        WebElement viewButton = studentRow.findElement(By.id("btn-edit-details"));
        click(viewButton);
        ThreadHelper.waitFor(2000);
        switchToNewWindow();
        return changePageType(InstructorCourseStudentDetailsEditPage.class);
    }

    public InstructorStudentRecordsPage clickViewAllRecords(CourseAttributes course, String studentEmail) {
        WebElement studentRow = getStudentRow(course, studentEmail);
        WebElement viewButton = studentRow.findElement(By.id("btn-view-records"));
        click(viewButton);
        ThreadHelper.waitFor(2000);
        switchToNewWindow();
        return changePageType(InstructorStudentRecordsPage.class);
    }

    public void verifyCommentSearchResults(CommentSearchSessionResult[] commentSearchSessionResults,
            Collection<StudentAttributes> students, Collection<InstructorAttributes> instructors) {
        List<WebElement> sessionRows = browser.driver.findElements(By.className("comment-search-session"));
        assertEquals(commentSearchSessionResults.length, sessionRows.size());

        for (WebElement sessionRow : sessionRows) {
            String sessionAndCourse = sessionRow.findElement(By.id("comment-search-session-name")).getText();

            // Iterate as the result order may not be guaranteed
            CommentSearchSessionResult matchedSession = null;
            for (CommentSearchSessionResult sessionResult : commentSearchSessionResults) {
                String expectedSessionAndCourse = String.format("Session: %s (%s)",
                        sessionResult.session.getFeedbackSessionName(), sessionResult.session.getCourseId());
                if (expectedSessionAndCourse.equals(sessionAndCourse)) {
                    matchedSession = sessionResult;
                    break;
                }
            }
            if (matchedSession == null) {
                fail("Session of comment is not found");
            }

            verifyResponsesAndComments(sessionRow, matchedSession, students, instructors);
        }
    }

    private void verifyResponsesAndComments(WebElement sessionRow, CommentSearchSessionResult expectedSession,
            Collection<StudentAttributes> students, Collection<InstructorAttributes> instructors) {
        List<WebElement> responses = sessionRow.findElements(By.className("comment-search-response"));
        assertEquals(expectedSession.responses.length, responses.size());

        for (WebElement response : responses) {
            String questionText = response.findElement(By.id("comment-search-question-text")).getText();
            String responseGiverRecipient =
                    response.findElement(By.id("comment-search-response-giver-recipient")).getText();

            // Iterate as the result order may not be guaranteed
            CommentSearchResponseResult matchedResponse = null;
            for (CommentSearchResponseResult responseResult : expectedSession.responses) {
                String expectedQuestionText = String.format("Question %d:%s%s",
                        responseResult.question.questionNumber, System.lineSeparator(),
                        responseResult.question.questionDetails.getQuestionText());

                String giverName = getGiverName(responseResult.question.giverType,
                        responseResult.response.giver, students, instructors);
                String giverTeam = getGiverTeam(responseResult.question.giverType,
                        responseResult.response.giver, students);

                String expectedResponseGiverRecipient = String.format("From: %s (%s) To: %s",
                        giverName, giverTeam, responseResult.response.recipient);

                if (expectedQuestionText.equals(questionText)
                        && expectedResponseGiverRecipient.equals(responseGiverRecipient)) {
                    matchedResponse = responseResult;
                    break;
                }
            }
            if (matchedResponse == null) {
                fail("Response of comment is not found");
            }

            verifyComments(response, matchedResponse, students, instructors);
        }
    }

    private void verifyComments(WebElement response, CommentSearchResponseResult matchedResponse,
            Collection<StudentAttributes> students, Collection<InstructorAttributes> instructors) {
        List<WebElement> comments = response.findElements(By.className("comment-row"));
        assertEquals(matchedResponse.comments.length, comments.size());

        for (WebElement comment : comments) {
            String commentGiverName = comment.findElement(By.id("comment-giver-name")).getText();
            String commentText = comment.findElement(By.id("comment-text")).getText();

            // Iterate as the result order may not be guaranteed
            boolean hasMatch = false;
            for (FeedbackResponseCommentAttributes frc : matchedResponse.comments) {
                String expectedCommentGiverName = String.format("%s %s commented at",
                        frc.isCommentFromFeedbackParticipant ? "Student" : "Instructor",
                        getGiverName(frc.commentGiverType, frc.commentGiver, students, instructors));

                // Normally, the comment text needs to be parsed for HTML and sanitized first.
                // For E2E testing purpose, we will keep the comment simple and skip that step unless absolutely needed.
                String expectedCommentText = frc.commentText;

                if (expectedCommentGiverName.equals(commentGiverName)
                        && expectedCommentText.equals(commentText)) {
                    hasMatch = true;
                    break;
                }
            }
            if (!hasMatch) {
                fail("Comment is not found");
            }
        }
    }

    private String getGiverName(FeedbackParticipantType giverType, String giverEmail,
             Collection<StudentAttributes> students, Collection<InstructorAttributes> instructors) {
        switch (giverType) {
        case SELF:
        case INSTRUCTORS:
            InstructorAttributes matchedInstructor = instructors.stream()
                    .filter(instructor -> instructor.email.equals(giverEmail))
                    .findFirst()
                    .orElse(null);
            return matchedInstructor == null ? null : matchedInstructor.name;
        case STUDENTS:
            StudentAttributes matchedStudent = students.stream()
                    .filter(student -> student.email.equals(giverEmail))
                    .findFirst()
                    .orElse(null);
            return matchedStudent == null ? null : matchedStudent.name;
        case TEAMS:
            return giverEmail;
        default:
            throw new RuntimeException("Invalid giver type");
        }
    }

    private String getGiverTeam(FeedbackParticipantType giverType, String giverEmail,
            Collection<StudentAttributes> students) {
        switch (giverType) {
        case SELF:
        case INSTRUCTORS:
            return Const.USER_TEAM_FOR_INSTRUCTOR;
        case STUDENTS:
            StudentAttributes matchedStudent = students.stream()
                    .filter(student -> student.email.equals(giverEmail))
                    .findFirst()
                    .orElse(null);
            return matchedStudent == null ? null : matchedStudent.team;
        case TEAMS:
            return giverEmail;
        default:
            throw new RuntimeException("Invalid giver type");
        }
    }

    /**
     * Represents a session row (session + responses + comments) in the comment search result table.
     */
    public static class CommentSearchSessionResult {

        public FeedbackSessionAttributes session;
        public CommentSearchResponseResult[] responses;

    }

    /**
     * Represents a response row (response + comments) in the comment search result table.
     */
    public static class CommentSearchResponseResult {

        public FeedbackQuestionAttributes question;
        public FeedbackResponseAttributes response;
        public FeedbackResponseCommentAttributes[] comments;

    }

}
