package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;

/**
 * Represents the instructor feedback edit page of the website.
 */
public class InstructorFeedbackEditPage extends AppPage {
    private static final String CUSTOM_FEEDBACK_PATH_OPTION = "Custom feedback path";
    private static final String FEEDBACK_PATH_SEPARATOR = " will give feedback on ";
    private static final String CUSTOM_VISIBILITY_OPTION = "Custom visibility options";

    @FindBy(id = "btn-fs-edit")
    private WebElement fsEditButton;

    @FindBy(id = "btn-fs-save")
    private WebElement fsSaveButton;

    @FindBy(id = "btn-fs-delete")
    private WebElement fsDeleteButton;

    @FindBy(id = "btn-fs-copy")
    private WebElement fsCopyButton;

    @FindBy(id = "edit-course-id")
    private WebElement courseIdTextBox;

    @FindBy(id = "time-zone")
    private WebElement timezoneDropDown;

    @FindBy(id = "course-name")
    private WebElement courseNameTextBox;

    @FindBy(id = "edit-session-name")
    private WebElement sessionNameTextBox;

    @FindBy(id = "instructions")
    private WebElement instructionsEditor;

    @FindBy(id = "submission-start-date")
    private WebElement startDateBox;

    @FindBy(id = "submission-start-time")
    private WebElement startTimeDropdown;

    @FindBy(id = "submission-end-date")
    private WebElement endDateBox;

    @FindBy(id = "submission-end-time")
    private WebElement endTimeDropdown;

    @FindBy(id = "grace-period")
    private WebElement gracePeriodDropdown;

    @FindBy(id = "submission-status")
    private WebElement submissionStatusTextBox;

    @FindBy(id = "published-status")
    private WebElement publishStatusTextBox;

    @FindBy(id = "btn-change-visibility")
    private WebElement changeVisibilityButton;

    @FindBy(id = "session-visibility-custom")
    private WebElement customSessionVisibleTimeButton;

    @FindBy(id = "session-visibility-date")
    private WebElement sessionVisibilityDateBox;

    @FindBy(id = "session-visibility-time")
    private WebElement sessionVisibilityTimeDropdown;

    @FindBy(id = "session-visibility-at-open")
    private WebElement openSessionVisibleTimeButton;

    @FindBy(id = "response-visibility-custom")
    private WebElement customResponseVisibleTimeButton;

    @FindBy(id = "response-visibility-date")
    private WebElement responseVisibilityDateBox;

    @FindBy(id = "response-visibility-time")
    private WebElement responseVisibilityTimeDropdown;

    @FindBy(id = "response-visibility-immediately")
    private WebElement immediateResponseVisibleTimeButton;

    @FindBy(id = "response-visibility-manually")
    private WebElement manualResponseVisibleTimeButton;

    @FindBy(id = "btn-change-email")
    private WebElement changeEmailButton;

    @FindBy(id = "email-opening")
    private WebElement openingSessionEmailCheckbox;

    @FindBy(id = "email-closing")
    private WebElement closingSessionEmailCheckbox;

    @FindBy(id = "email-published")
    private WebElement publishedSessionEmailCheckbox;

    @FindBy(id = "btn-new-question")
    private WebElement addNewQuestionButton;

    @FindBy(id = "btn-copy-question")
    private WebElement copyQuestionButton;

    @FindBy(id = "preview-student")
    private WebElement previewAsStudentDropdown;

    @FindBy(id = "btn-preview-student")
    private WebElement previewAsStudentButton;

    @FindBy(id = "preview-instructor")
    private WebElement previewAsInstructorDropdown;

    @FindBy(id = "btn-preview-instructor")
    private WebElement previewAsInstructorButton;

    public InstructorFeedbackEditPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Edit Feedback Session");
    }

    public void verifySessionDetails(CourseAttributes course, FeedbackSessionAttributes feedbackSession) {
        waitForElementPresence(By.id("instructions"));
        assertEquals(getCourseId(), course.getId());
        assertEquals(getCourseName(), course.getName());
        assertEquals(getTimeZone(), feedbackSession.getTimeZone().toString());
        assertEquals(getFeedbackSessionName(), feedbackSession.getFeedbackSessionName());
        assertEquals(getInstructions(), feedbackSession.getInstructions());
        assertEquals(getStartDate(), getDateString(feedbackSession.getStartTime(), feedbackSession.getTimeZone()));
        assertEquals(getStartTime(), getTimeString(feedbackSession.getStartTime(), feedbackSession.getTimeZone()));
        assertEquals(getEndDate(), getDateString(feedbackSession.getEndTime(), feedbackSession.getTimeZone()));
        assertEquals(getEndTime(), getTimeString(feedbackSession.getEndTime(), feedbackSession.getTimeZone()));
        assertEquals(getGracePeriod(), feedbackSession.getGracePeriodMinutes() + " min");
        verifySubmissionStatus(feedbackSession);
        verifyPublishedStatus(feedbackSession);
        verifyVisibilitySettings(feedbackSession);
        verifyEmailSettings(feedbackSession);
    }

    private void verifySubmissionStatus(FeedbackSessionAttributes feedbackSession) {
        String submissionStatus = getSubmissionStatus();
        if (feedbackSession.isClosed()) {
            assertEquals(submissionStatus, "Closed");
        } else if (feedbackSession.isVisible() && (feedbackSession.isOpened() || feedbackSession.isInGracePeriod())) {
            assertEquals(submissionStatus, "Open");
        } else {
            assertEquals(submissionStatus, "Awaiting");
        }
    }

    private void verifyPublishedStatus(FeedbackSessionAttributes feedbackSession) {
        String publishedStatus = getPublishedStatus();
        if (feedbackSession.isPublished()) {
            assertEquals(publishedStatus, "Published");
        } else {
            assertEquals(publishedStatus, "Not Published");
        }
    }

    private void verifyVisibilitySettings(FeedbackSessionAttributes feedbackSession) {
        Instant sessionVisibleTime = feedbackSession.getSessionVisibleFromTime();
        Instant responseVisibleTime = feedbackSession.getResultsVisibleFromTime();

        // Default settings, assert setting section not expanded
        if (sessionVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)
                && responseVisibleTime.equals(Const.TIME_REPRESENTS_LATER)) {
            assertTrue(isElementPresent("btn-change-visibility"));
            return;
        }
        verifySessionVisibilitySettings(sessionVisibleTime, feedbackSession);
        verifyResponseVisibilitySettings(responseVisibleTime, feedbackSession);
    }

    private void verifySessionVisibilitySettings(Instant sessionVisibleTime,
                                                 FeedbackSessionAttributes feedbackSession) {
        if (sessionVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            assertTrue(openSessionVisibleTimeButton.isSelected());
        } else {
            assertTrue(customSessionVisibleTimeButton.isSelected());
            assertEquals(getSessionVisibilityDate(), getDateString(feedbackSession.getSessionVisibleFromTime(),
                    feedbackSession.getTimeZone()));
            assertEquals(getSessionVisibilityTime(), getTimeString(feedbackSession.getSessionVisibleFromTime(),
                    feedbackSession.getTimeZone()));
        }
    }

    private void verifyResponseVisibilitySettings(Instant responseVisibleTime,
                                                  FeedbackSessionAttributes feedbackSession) {
        if (responseVisibleTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            assertTrue(immediateResponseVisibleTimeButton.isSelected());
        } else if (responseVisibleTime.equals(Const.TIME_REPRESENTS_LATER)) {
            assertTrue(manualResponseVisibleTimeButton.isSelected());
        } else {
            assertTrue(customSessionVisibleTimeButton.isSelected());
            assertEquals(getResponseVisibilityDate(), getDateString(feedbackSession.getResultsVisibleFromTime(),
                    feedbackSession.getTimeZone()));
            assertEquals(getResponseVisibilityTime(), getTimeString(feedbackSession.getResultsVisibleFromTime(),
                    feedbackSession.getTimeZone()));
        }
    }

    private void verifyEmailSettings(FeedbackSessionAttributes feedbackSession) {
        boolean isOpeningEmailEnabled = feedbackSession.isOpeningEmailEnabled();
        boolean isClosingEmailEnabled = feedbackSession.isClosingEmailEnabled();
        boolean isPublishedEmailEnabled = feedbackSession.isPublishedEmailEnabled();

        // Default settings, assert setting section not expanded
        if (isOpeningEmailEnabled && isClosingEmailEnabled && isPublishedEmailEnabled) {
            assertTrue(isElementPresent("btn-change-email"));
            return;
        }
        if (isOpeningEmailEnabled) {
            assertTrue(openingSessionEmailCheckbox.isSelected());
        }
        if (isClosingEmailEnabled) {
            assertTrue(closingSessionEmailCheckbox.isSelected());
        }
        if (isPublishedEmailEnabled) {
            assertTrue(publishedSessionEmailCheckbox.isSelected());
        }
    }

    public void editSessionDetails(FeedbackSessionAttributes newFeedbackSessionDetails) {
        click(fsEditButton);
        setInstructions(newFeedbackSessionDetails.getInstructions());
        setSessionStartDateTime(newFeedbackSessionDetails.getStartTime(), newFeedbackSessionDetails.getTimeZone());
        setSessionEndDateTime(newFeedbackSessionDetails.getEndTime(), newFeedbackSessionDetails.getTimeZone());
        selectGracePeriod(newFeedbackSessionDetails.getGracePeriodMinutes());
        setVisibilitySettings(newFeedbackSessionDetails);
        setEmailSettings(newFeedbackSessionDetails);
        click(fsSaveButton);
    }

    public void copySessionToOtherCourse(CourseAttributes otherCourse, String sessionName) {
        click(fsCopyButton);
        WebElement copyFsModal = waitForElementPresence(By.id("copy-course-modal"));

        fillTextBox(copyFsModal.findElement(By.id("copy-session-name")), sessionName);
        List<WebElement> options = copyFsModal.findElements(By.className("form-check"));
        for (WebElement option : options) {
            String courseId = option.findElement(By.cssSelector("label span")).getText();
            if (courseId.equals(otherCourse.getId())) {
                click(option.findElement(By.tagName("input")));
                break;
            }
        }
        click(browser.driver.findElement(By.id("btn-confirm-copy-course")));
    }

    public void deleteSession() {
        clickAndConfirm(fsDeleteButton);
    }

    public FeedbackSubmitPage previewAsStudent(StudentAttributes student) {
        selectDropdownOptionByText(previewAsStudentDropdown, String.format("[%s] %s", student.team, student.name));
        click(previewAsStudentButton);
        ThreadHelper.waitFor(2000);
        switchToNewWindow();
        return changePageType(FeedbackSubmitPage.class);
    }

    public FeedbackSubmitPage previewAsInstructor(InstructorAttributes instructor) {
        selectDropdownOptionByText(previewAsInstructorDropdown, instructor.name);
        click(previewAsInstructorButton);
        ThreadHelper.waitFor(2000);
        switchToNewWindow();
        return changePageType(FeedbackSubmitPage.class);
    }

    public void verifyNumQuestions(int expected) {
        assertEquals(getNumQuestions(), expected);
    }

    public void verifyQuestionDetails(int questionNum, FeedbackQuestionAttributes feedbackQuestion) {
        scrollElementToCenter(getQuestionForm(questionNum));
        assertEquals(feedbackQuestion.getQuestionType(), getQuestionType(questionNum));
        assertEquals(feedbackQuestion.getQuestionNumber(), getQuestionNumber(questionNum));
        assertEquals(feedbackQuestion.getQuestionDetails().getQuestionText(), getQuestionBrief(questionNum));
        assertEquals(getQuestionDescription(questionNum), feedbackQuestion.getQuestionDescription());
        verifyFeedbackPathSettings(questionNum, feedbackQuestion);
        verifyQuestionVisibilitySettings(questionNum, feedbackQuestion);
    }

    private void verifyFeedbackPathSettings(int questionNum, FeedbackQuestionAttributes feedbackQuestion) {
        assertEquals(feedbackQuestion.getGiverType().toDisplayGiverName(), getFeedbackGiver(questionNum));
        String feedbackReceiver = getFeedbackReceiver(questionNum);
        assertEquals(feedbackQuestion.getRecipientType().toDisplayRecipientName(), feedbackReceiver);

        if (feedbackReceiver.equals(FeedbackParticipantType.INSTRUCTORS.toDisplayRecipientName())
                || feedbackReceiver.equals(FeedbackParticipantType.STUDENTS_EXCLUDING_SELF.toDisplayRecipientName())
                || feedbackReceiver.equals(FeedbackParticipantType.TEAMS_EXCLUDING_SELF.toDisplayRecipientName())) {
            verifyNumberOfEntitiesToGiveFeedbackTo(questionNum, feedbackQuestion.getNumberOfEntitiesToGiveFeedbackTo());
        }
    }

    private void verifyNumberOfEntitiesToGiveFeedbackTo(int questionNum, int numberOfEntitiesToGiveFeedbackTo) {
        WebElement questionForm = getQuestionForm(questionNum);
        if (numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS) {
            assertTrue(questionForm.findElement(By.id("unlimited-recipients")).isSelected());
        } else {
            assertTrue(questionForm.findElement(By.id("custom-recipients")).isSelected());
            assertEquals(questionForm.findElement(By.id("custom-recipients-number")).getAttribute("value"),
                    Integer.toString(numberOfEntitiesToGiveFeedbackTo));
        }
    }

    private void verifyQuestionVisibilitySettings(int questionNum, FeedbackQuestionAttributes feedbackQuestion) {
        WebElement questionForm = getQuestionForm(questionNum);
        String visibility = questionForm.findElement(By.cssSelector("#btn-question-visibility span")).getText();
        List<FeedbackParticipantType> showResponsesTo = feedbackQuestion.getShowResponsesTo();
        List<FeedbackParticipantType> showGiverNameTo = feedbackQuestion.getShowGiverNameTo();
        List<FeedbackParticipantType> showRecipientNameTo = feedbackQuestion.getShowRecipientNameTo();

        switch (visibility) {
        case "Shown anonymously to recipient and giver's team members, visible to instructors":
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.RECEIVER));
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS));
            assertEquals(showResponsesTo.size(), 3);

            assertTrue(showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertEquals(showGiverNameTo.size(), 1);

            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER));
            assertEquals(showRecipientNameTo.size(), 2);
            break;

        case "Visible to instructors only":
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertEquals(showResponsesTo.size(), 1);

            assertTrue(showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertEquals(showGiverNameTo.size(), 1);

            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertEquals(showRecipientNameTo.size(), 1);
            break;

        case "Shown anonymously to recipient and instructors":
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.RECEIVER));
            assertEquals(showResponsesTo.size(), 2);

            assertEquals(showGiverNameTo.size(), 0);

            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER));
            assertEquals(showRecipientNameTo.size(), 2);
            break;

        case "Shown anonymously to recipient, visible to instructors":
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.RECEIVER));
            assertEquals(showResponsesTo.size(), 2);

            assertTrue(showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertEquals(showGiverNameTo.size(), 1);

            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER));
            assertEquals(showRecipientNameTo.size(), 2);
            break;

        case "Shown anonymously to recipient and giver/recipient's team members, visible to instructors":
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.RECEIVER));
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS));
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
            assertEquals(showResponsesTo.size(), 4);

            assertTrue(showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertEquals(showGiverNameTo.size(), 1);

            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER));
            assertEquals(showRecipientNameTo.size(), 2);
            break;

        case "Visible to recipient and instructors":
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showResponsesTo.contains(FeedbackParticipantType.RECEIVER));
            assertEquals(showResponsesTo.size(), 2);

            assertTrue(showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showGiverNameTo.contains(FeedbackParticipantType.RECEIVER));
            assertEquals(showGiverNameTo.size(), 2);

            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS));
            assertTrue(showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER));
            assertEquals(showRecipientNameTo.size(), 2);
            break;

        default:
            verifyCustomQuestionVisibility(questionNum, feedbackQuestion);
            break;
        }
    }

    private void verifyCustomQuestionVisibility(int questionNum, FeedbackQuestionAttributes feedbackQuestion) {
        WebElement questionForm = getQuestionForm(questionNum);
        String visibility = questionForm.findElement(By.cssSelector("#btn-question-visibility span")).getText();
        assertEquals(visibility, CUSTOM_VISIBILITY_OPTION);

        FeedbackParticipantType giver = feedbackQuestion.getGiverType();
        FeedbackParticipantType receiver = feedbackQuestion.getRecipientType();
        WebElement customVisibilityTable = questionForm.findElement(By.id("custom-visibility-table"));
        assertVisibilityBoxesSelected(customVisibilityTable, giver, receiver, feedbackQuestion.getShowResponsesTo(), 1);
        assertVisibilityBoxesSelected(customVisibilityTable, giver, receiver, feedbackQuestion.getShowGiverNameTo(), 2);
        assertVisibilityBoxesSelected(customVisibilityTable, giver, receiver, feedbackQuestion.getShowRecipientNameTo(), 3);
    }

    private void assertVisibilityBoxesSelected(WebElement table, FeedbackParticipantType giver,
                                               FeedbackParticipantType receiver, List<FeedbackParticipantType> participants,
                                               int colNum) {
        List<FeedbackParticipantType> possibleTypes = new ArrayList(Arrays.asList(FeedbackParticipantType.RECEIVER,
                FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                FeedbackParticipantType.STUDENTS, FeedbackParticipantType.INSTRUCTORS));
        if (!giver.equals(FeedbackParticipantType.STUDENTS)) {
            possibleTypes.remove(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        }
        if (!receiver.equals(FeedbackParticipantType.STUDENTS)) {
            possibleTypes.remove(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        }
        if (receiver.equals(FeedbackParticipantType.NONE)
                || receiver.equals(FeedbackParticipantType.SELF)
                || receiver.equals(FeedbackParticipantType.OWN_TEAM)) {
            possibleTypes.remove(FeedbackParticipantType.RECEIVER);
            possibleTypes.remove(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        }

        List<WebElement> rows = table.findElements(By.tagName("tr"));
        int index = colNum - 1;
        for (FeedbackParticipantType participant : participants) {
            assertTrue(rows.get(possibleTypes.indexOf(participant)).findElements(By.tagName("input")).get(index)
                    .isSelected());
        }
    }

    public void addTemplateQuestion(int optionNum) {
        addNewQuestion(1);
        WebElement templateQuestionModal = waitForElementPresence(By.id("template-question-modal"));

        click(templateQuestionModal.findElements(By.tagName("input")).get(optionNum - 1));
        clickAndWaitForNewQuestion(browser.driver.findElement(By.id("btn-confirm-template")));
    }

    public void copyQuestion(String courseId, String questionText) {
        click(copyQuestionButton);
        WebElement copyQuestionModal = waitForElementPresence(By.id("copy-question-modal"));

        List<WebElement> rows = copyQuestionModal.findElements(By.cssSelector("tbody tr"));
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.get(1).getText().equals(courseId) && cells.get(4).getText().equals(questionText)) {
                markOptionAsSelected(cells.get(0).findElement(By.tagName("input")));
            }
        }
        clickAndWaitForNewQuestion(browser.driver.findElement(By.id("btn-confirm-copy-question")));
    }

    public void editQuestionNumber(int questionNum, int newQuestionNumber) {
        clickEditQuestionButton(questionNum);
        selectDropdownOptionByText(getQuestionForm(questionNum).findElement(By.id("question-number-dropdown")),
                Integer.toString(newQuestionNumber));
        clickSaveQuestionButton(questionNum);
    }

    public void editQuestionDetails(int questionNum, FeedbackQuestionAttributes feedbackQuestion) {
        clickEditQuestionButton(questionNum);
        inputQuestionDetails(questionNum, feedbackQuestion);
        clickSaveQuestionButton(questionNum);
    }

    private void inputQuestionDetails(int questionNum, FeedbackQuestionAttributes feedbackQuestion) {
        setQuestionBrief(questionNum, feedbackQuestion.getQuestionDetails().getQuestionText());
        setQuestionDescription(questionNum, feedbackQuestion.getQuestionDescription());
        FeedbackQuestionType questionType = feedbackQuestion.getQuestionType();
        if (!questionType.equals(FeedbackQuestionType.CONTRIB)) {
            setFeedbackPath(questionNum, feedbackQuestion);
            setQuestionVisibility(questionNum, feedbackQuestion);
        }
    }

    public void duplicateQuestion(int questionNum) {
        clickAndWaitForNewQuestion(getQuestionForm(questionNum).findElement(By.id("btn-duplicate-question")));
    }

    public void deleteQuestion(int questionNum) {
        clickAndConfirm(getQuestionForm(questionNum).findElement(By.id("btn-delete-question")));
    }

    public void verifyTextQuestionDetails(int questionNum, FeedbackTextQuestionDetails questionDetails) {
        String recommendLength = getRecommendedTextLengthField(questionNum).getAttribute("value");
        assertEquals(recommendLength, questionDetails.getRecommendedLength().toString());
    }

    public void addTextQuestion(FeedbackQuestionAttributes feedbackQuestion) {
        addNewQuestion(2);
        int questionNum = getNumQuestions();
        inputQuestionDetails(questionNum, feedbackQuestion);
        FeedbackTextQuestionDetails questionDetails = (FeedbackTextQuestionDetails) feedbackQuestion.getQuestionDetails();
        fillTextBox(getRecommendedTextLengthField(questionNum), questionDetails.getRecommendedLength().toString());
        clickSaveNewQuestionButton();
    }

    public void editTextQuestion(int questionNum, FeedbackTextQuestionDetails textQuestionDetails) {
        clickEditQuestionButton(questionNum);
        fillTextBox(getRecommendedTextLengthField(questionNum), textQuestionDetails.getRecommendedLength().toString());
        clickSaveQuestionButton(questionNum);
    }

    private String getCourseId() {
        return courseIdTextBox.getText();
    }

    private String getCourseName() {
        return courseNameTextBox.getText();
    }

    private String getTimeZone() {
        return timezoneDropDown.getText();
    }

    private String getFeedbackSessionName() {
        return sessionNameTextBox.getText();
    }

    private String getInstructions() {
        return getEditorRichText(instructionsEditor.findElement(By.tagName("editor")));
    }

    private String getStartDate() {
        return startDateBox.getAttribute("value");
    }

    private String getStartTime() {
        return getSelectedDropdownOptionText(startTimeDropdown.findElement(By.tagName("select")));
    }

    private String getEndDate() {
        return endDateBox.getAttribute("value");
    }

    private String getEndTime() {
        return getSelectedDropdownOptionText(endTimeDropdown.findElement(By.tagName("select")));
    }

    private String getSessionVisibilityDate() {
        return sessionVisibilityDateBox.getAttribute("value");
    }

    private String getSessionVisibilityTime() {
        return getSelectedDropdownOptionText(sessionVisibilityTimeDropdown.findElement(By.tagName("select")));
    }

    private String getResponseVisibilityDate() {
        return responseVisibilityDateBox.getAttribute("value");
    }

    private String getResponseVisibilityTime() {
        return getSelectedDropdownOptionText(responseVisibilityTimeDropdown.findElement(By.tagName("select")));
    }

    private String getGracePeriod() {
        return getSelectedDropdownOptionText(gracePeriodDropdown);
    }

    private String getSubmissionStatus() {
        return submissionStatusTextBox.getText();
    }

    private String getPublishedStatus() {
        return publishStatusTextBox.getText();
    }

    private String getDateString(Instant instant, ZoneId timeZone) {
        return DateTimeFormatter
                .ofPattern("EE, dd MMM, yyyy")
                .format(instant.atZone(timeZone));
    }

    private String getTimeString(Instant instant, ZoneId timeZone) {
        ZonedDateTime dateTime = instant.atZone(timeZone);
        if (dateTime.getHour() == 23 && dateTime.getMinute() == 59) {
            return "23:59H";
        }
        return DateTimeFormatter
                .ofPattern("HH:00")
                .format(instant.atZone(timeZone)) + "H";
    }

    private void setInstructions(String newInstructions) {
        writeToRichTextEditor(instructionsEditor.findElement(By.tagName("editor")), newInstructions);
    }

    private void setSessionStartDateTime(Instant startInstant, ZoneId timeZone) {
        setDateTime(startDateBox, startTimeDropdown, startInstant, timeZone);
    }

    private void setSessionEndDateTime(Instant endInstant, ZoneId timeZone) {
        setDateTime(endDateBox, endTimeDropdown, endInstant, timeZone);
    }

    private void setVisibilityDateTime(Instant startInstant, ZoneId timeZone) {
        setDateTime(sessionVisibilityDateBox, sessionVisibilityTimeDropdown, startInstant, timeZone);
    }

    private void setResponseDateTime(Instant endInstant, ZoneId timeZone) {
        setDateTime(responseVisibilityDateBox, responseVisibilityTimeDropdown, endInstant, timeZone);
    }

    private void setDateTime(WebElement dateBox, WebElement timeBox, Instant startInstant, ZoneId timeZone) {
        fillTextBox(dateBox, getDateString(startInstant, timeZone));

        selectDropdownOptionByText(timeBox.findElement(By.tagName("select")), getTimeString(startInstant, timeZone));
    }

    private void selectGracePeriod(long gracePeriodMinutes) {
        selectDropdownOptionByText(gracePeriodDropdown, gracePeriodMinutes + " min");
    }

    private void setVisibilitySettings(FeedbackSessionAttributes newFeedbackSession) {
        showVisibilitySettings();

        setSessionVisibilitySettings(newFeedbackSession);
        setResponseVisibilitySettings(newFeedbackSession);
    }

    private void setSessionVisibilitySettings(FeedbackSessionAttributes newFeedbackSession) {
        Instant sessionDateTime = newFeedbackSession.getSessionVisibleFromTime();
        if (sessionDateTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            click(openSessionVisibleTimeButton);
        } else {
            click(customSessionVisibleTimeButton);
            setVisibilityDateTime(sessionDateTime, newFeedbackSession.getTimeZone());
        }
    }

    private void setResponseVisibilitySettings(FeedbackSessionAttributes newFeedbackSession) {
        Instant responseDateTime = newFeedbackSession.getResultsVisibleFromTime();
        if (responseDateTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
            click(immediateResponseVisibleTimeButton);
        } else if (responseDateTime.equals(Const.TIME_REPRESENTS_LATER)) {
            click(manualResponseVisibleTimeButton);
        } else {
            click(customResponseVisibleTimeButton);
            setResponseDateTime(responseDateTime, newFeedbackSession.getTimeZone());
        }
    }

    private void setEmailSettings(FeedbackSessionAttributes newFeedbackSessionDetails) {
        showEmailSettings();
        if (newFeedbackSessionDetails.isOpeningEmailEnabled() != openingSessionEmailCheckbox.isSelected()) {
            click(openingSessionEmailCheckbox);
        }
        if (newFeedbackSessionDetails.isClosingEmailEnabled() != closingSessionEmailCheckbox.isSelected()) {
            click(closingSessionEmailCheckbox);
        }
        if (newFeedbackSessionDetails.isPublishedEmailEnabled() != publishedSessionEmailCheckbox.isSelected()) {
            click(publishedSessionEmailCheckbox);
        }
    }

    private void showVisibilitySettings() {
        if (isElementPresent(By.id("btn-change-visibility"))) {
            click(changeVisibilityButton);
        }
    }

    private void showEmailSettings() {
        if (isElementPresent(By.id("btn-change-email"))) {
            click(changeEmailButton);
        }
    }

    private int getNumQuestions() {
        return browser.driver.findElements(By.tagName("tm-question-edit-form")).size();
    }

    private WebElement getQuestionForm(int questionNum) {
        return browser.driver.findElements(By.tagName("tm-question-edit-form")).get(questionNum - 1);
    }

    private FeedbackQuestionType getQuestionType(int questionNum) {
        String questionDetails = getQuestionForm(questionNum).findElement(By.id("question-header")).getText();
        String questionType = questionDetails.split(" \\d+ ")[1].trim();

        switch (questionType) {
        case "Essay question":
            return FeedbackQuestionType.TEXT;
        case "Multiple-Choice (single answer) question":
            return FeedbackQuestionType.MCQ;
        case "Multiple-choice (multiple answers) question":
            return FeedbackQuestionType.MSQ;
        case "Numerical Scale Question":
            return FeedbackQuestionType.NUMSCALE;
        case "Distribute points (among options) question":
            return FeedbackQuestionType.CONSTSUM_OPTIONS;
        case "Distribute points (among recipients) question":
            return FeedbackQuestionType.CONSTSUM_RECIPIENTS;
        case "Team contribution question":
            return FeedbackQuestionType.CONTRIB;
        case "Rubric question":
            return FeedbackQuestionType.RUBRIC;
        case "Rank (options) question":
            return FeedbackQuestionType.RANK_OPTIONS;
        case "Rank (recipients) question":
            return FeedbackQuestionType.RANK_RECIPIENTS;
        default:
            throw new IllegalArgumentException("Unknown FeedbackQuestionType");
        }
    }

    private int getQuestionNumber(int questionNum) {
        return Integer.parseInt(getQuestionForm(questionNum).findElement(By.id("question-number")).getText());
    }

    private String getQuestionBrief(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("question-brief")).getAttribute("value");
    }

    private String getQuestionDescription(int questionNum) {
        WebElement editor = waitForElementPresence(By.cssSelector("#question-form-" + questionNum + " editor"));
        return getEditorRichText(editor);
    }

    private String getFeedbackGiver(int questionNum) {
        String feedbackPath = getFeedbackPath(questionNum);
        if (feedbackPath.equals(CUSTOM_FEEDBACK_PATH_OPTION)) {
            return getSelectedDropdownOptionText(getQuestionForm(questionNum).findElement(By.id("giver-type")));
        }
        return feedbackPath.split(FEEDBACK_PATH_SEPARATOR)[0];
    }

    private String getFeedbackReceiver(int questionNum) {
        String feedbackPath = getFeedbackPath(questionNum);
        if (feedbackPath.equals(CUSTOM_FEEDBACK_PATH_OPTION)) {
            return getSelectedDropdownOptionText(getQuestionForm(questionNum).findElement(By.id("receiver-type")));
        }
        return feedbackPath.split(FEEDBACK_PATH_SEPARATOR)[1];
    }

    private String getFeedbackPath(int questionNum) {
        WebElement questionForm = getQuestionForm(questionNum);
        return questionForm.findElement(By.cssSelector("#btn-feedback-path span")).getText();
    }

    private void setQuestionBrief(int questionNum, String newBrief) {
        fillTextBox(getQuestionForm(questionNum).findElement(By.id("question-brief")), newBrief);
    }

    private void setQuestionDescription(int questionNum, String newDescription) {
        WebElement editor = waitForElementPresence(By.cssSelector("#question-form-" + questionNum + " editor"));
        writeToRichTextEditor(editor, newDescription);
    }

    private void setFeedbackPath(int questionNum, FeedbackQuestionAttributes feedbackQuestion) {
        FeedbackParticipantType newGiver = feedbackQuestion.getGiverType();
        FeedbackParticipantType newRecipient = feedbackQuestion.getRecipientType();
        String feedbackPath = getFeedbackPath(questionNum);
        WebElement questionForm = getQuestionForm(questionNum);
        if (!feedbackPath.equals(CUSTOM_FEEDBACK_PATH_OPTION)) {
            selectFeedbackPathDropdownOption(questionNum, CUSTOM_FEEDBACK_PATH_OPTION + "...");
        }
        // Set to type STUDENT first to adjust NumberOfEntitiesToGiveFeedbackTo
        selectDropdownOptionByText(questionForm.findElement(By.id("giver-type")),
                FeedbackParticipantType.STUDENTS.toDisplayGiverName());
        selectDropdownOptionByText(questionForm.findElement(By.id("receiver-type")),
                FeedbackParticipantType.STUDENTS.toDisplayRecipientName());
        if (feedbackQuestion.getNumberOfEntitiesToGiveFeedbackTo() == Const.MAX_POSSIBLE_RECIPIENTS) {
            click(questionForm.findElement(By.id("unlimited-recipients")));
        } else {
            click(questionForm.findElement(By.id("custom-recipients")));
            fillTextBox(questionForm.findElement(By.id("custom-recipients-number")),
                    Integer.toString(feedbackQuestion.getNumberOfEntitiesToGiveFeedbackTo()));
        }

        selectDropdownOptionByText(questionForm.findElement(By.id("giver-type")), newGiver.toDisplayGiverName());
        selectDropdownOptionByText(questionForm.findElement(By.id("receiver-type")),
                newRecipient.toDisplayRecipientName());
    }

    private void selectFeedbackPathDropdownOption(int questionNum, String text) {
        WebElement questionForm = getQuestionForm(questionNum);
        click(questionForm.findElement(By.id("btn-feedback-path")));
        WebElement dropdown = questionForm.findElement(By.id("feedback-path-dropdown"));
        List<WebElement> options = dropdown.findElements(By.className("dropdown-item"));
        for (WebElement option : options) {
            if (option.getText().equals(text)) {
                click(option);
                return;
            }
        }
    }

    private void clickEditQuestionButton(int questionNum) {
        click(getQuestionForm(questionNum).findElement(By.id("btn-edit-question")));
    }

    private void clickSaveQuestionButton(int questionNum) {
        WebElement saveButton = getQuestionForm(questionNum).findElement(By.id("btn-save-question"));
        click(saveButton);
        waitForElementStaleness(saveButton);
    }

    private void setQuestionVisibility(int questionNum, FeedbackQuestionAttributes feedbackQuestion) {
        WebElement questionForm = getQuestionForm(questionNum);
        String visibility = questionForm.findElement(By.cssSelector("#btn-question-visibility span")).getText();
        if (!visibility.equals(CUSTOM_VISIBILITY_OPTION)) {
            selectVisibilityDropdownOption(questionNum, CUSTOM_VISIBILITY_OPTION + "...");
        }

        FeedbackParticipantType giver = feedbackQuestion.getGiverType();
        FeedbackParticipantType receiver = feedbackQuestion.getRecipientType();
        WebElement customVisibilityTable = questionForm.findElement(By.id("custom-visibility-table"));
        selectVisibilityBoxes(customVisibilityTable, giver, receiver, feedbackQuestion.getShowResponsesTo(), 1);
        selectVisibilityBoxes(customVisibilityTable, giver, receiver, feedbackQuestion.getShowGiverNameTo(), 2);
        selectVisibilityBoxes(customVisibilityTable, giver, receiver, feedbackQuestion.getShowRecipientNameTo(), 3);
    }

    private void selectVisibilityBoxes(WebElement table, FeedbackParticipantType giver,
                                       FeedbackParticipantType receiver, List<FeedbackParticipantType> participants,
                                       int colNum) {
        List<FeedbackParticipantType> possibleTypes = new ArrayList(Arrays.asList(FeedbackParticipantType.RECEIVER,
                FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                FeedbackParticipantType.STUDENTS, FeedbackParticipantType.INSTRUCTORS));
        if (!giver.equals(FeedbackParticipantType.STUDENTS)) {
            possibleTypes.remove(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        }
        if (!receiver.equals(FeedbackParticipantType.STUDENTS)) {
            possibleTypes.remove(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        }
        if (receiver.equals(FeedbackParticipantType.NONE)
                || receiver.equals(FeedbackParticipantType.SELF)
                || receiver.equals(FeedbackParticipantType.OWN_TEAM)) {
            possibleTypes.remove(FeedbackParticipantType.RECEIVER);
            possibleTypes.remove(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        }

        List<WebElement> rows = table.findElements(By.tagName("tr"));
        int index = colNum - 1;
        for (FeedbackParticipantType participant : participants) {
            markOptionAsSelected(rows.get(possibleTypes.indexOf(participant)).findElements(By.tagName("input")).get(index));
        }
    }

    private void selectVisibilityDropdownOption(int questionNum, String text) {
        WebElement questionForm = getQuestionForm(questionNum);
        click(questionForm.findElement(By.id("btn-question-visibility")));
        WebElement dropdown = questionForm.findElement(By.id("question-visibility-dropdown"));
        List<WebElement> options = dropdown.findElements(By.className("dropdown-item"));
        for (WebElement option : options) {
            if (option.getText().equals(text)) {
                click(option);
                return;
            }
        }
    }

    private void clickAndWaitForNewQuestion(WebElement button) {
        int newQuestionNum = getNumQuestions() + 1;
        click(button);
        waitForElementPresence(By.id("question-form-" + newQuestionNum));
    }

    private void addNewQuestion(int optionNumber) {
        click(addNewQuestionButton);
        WebElement newQuestionDropdown = waitForElementPresence(By.id("new-question-dropdown"));
        click(newQuestionDropdown.findElements(By.tagName("button")).get(optionNumber - 1));
    }

    private void clickSaveNewQuestionButton() {
        WebElement saveButton = browser.driver.findElement(By.id("btn-save-new"));
        click(saveButton);
        waitForElementStaleness(saveButton);
    }

    private WebElement getRecommendedTextLengthField(int questionNum) {
        return getQuestionForm(questionNum).findElement(By.id("recommended-length"));
    }
}
