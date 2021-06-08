package teammates.e2e.pageobjects;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.ThreadHelper;

/**
 * Represents the instructor audit logs page of the website.
 */
public class InstructorAuditLogsPage extends AppPage {
    private final Map<String, Boolean> isLogPresentForSession = new HashMap<>();

    @FindBy(id = "course-id-dropdown")
    private WebElement courseIdDropDown;

    @FindBy(id = "student-name-dropdown")
    private WebElement studentNameDropDown;

    @FindBy(id = "logs-from-datepicker")
    private WebElement logsFromDatepicker;

    @FindBy(id = "logs-to-datepicker")
    private WebElement logsToDatepicker;

    @FindBy(id = "logs-from-timepicker")
    private WebElement logsFromTimepicker;

    @FindBy(id = "logs-to-timepicker")
    private WebElement logsToTimepicker;

    @FindBy(id = "search-button")
    private WebElement searchButton;

    @FindBy(id = "logs-output")
    private WebElement logsOutput;

    public InstructorAuditLogsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Student Activity Logs");
    }

    public void startSearching() {
        click(searchButton);
        ThreadHelper.waitFor(3000);
        logsOutput
                .findElements(By.className("card"))
                .forEach(card -> {
                    if (!card.findElements(By.className("fa-chevron-down")).isEmpty()) {
                        click(card.findElement(By.className("fa-chevron-down")));
                    }
                    String sessionName = card.findElement(By.className("card-header")).getText().trim();
                    isLogPresentForSession.put(sessionName, !card.findElements(By.className("card-body")).isEmpty());
                });
    }

    public Boolean isLogPresentForSession(String sessionName) {
        return isLogPresentForSession.get(sessionName);
    }

    public String getCourseId() {
        return getSelectedDropdownOptionText(courseIdDropDown);
    }

    public String getStudentName() {
        return getSelectedDropdownOptionText(studentNameDropDown);
    }

    public String getLogsFromDate() {
        return logsFromDatepicker.getAttribute("value");
    }

    public String getLogsToDate() {
        return logsToDatepicker.getAttribute("value");
    }

    public String getLogsFromTime() {
        return getSelectedDropdownOptionText(logsFromTimepicker.findElement(By.className("form-control")));
    }

    public String getLogsToTime() {
        return getSelectedDropdownOptionText(logsToTimepicker.findElement(By.className("form-control")));
    }

    public void setCourseId(String courseId) {
        selectDropdownOptionByText(courseIdDropDown, courseId);
    }

    public void setStudentName(String studentName) {
        selectDropdownOptionByText(studentNameDropDown, studentName);
    }

    public void setLogsFromDateTime(Instant instant, ZoneId timeZone) {
        setDateTime(logsFromDatepicker, logsFromTimepicker.findElement(By.className("form-control")),
                instant, timeZone);
    }

    public void setLogsToDateTime(Instant instant, ZoneId timeZone) {
        setDateTime(logsToDatepicker, logsToTimepicker.findElement(By.className("form-control")),
                instant, timeZone);
    }

    private String getDateString(Instant instant, ZoneId timeZone) {
        return DateTimeFormatter
                .ofPattern("EE, dd MMM, yyyy")
                .format(instant.atZone(timeZone));
    }

    private String getTimeString(Instant instant, ZoneId timeZone) {
        ZonedDateTime dateTime = instant.atZone(timeZone);
        if (dateTime.getHour() == 0) {
            return "23:59H";
        }
        return DateTimeFormatter
                .ofPattern("HH:00")
                .format(instant.atZone(timeZone)) + "H";
    }

    private void setDateTime(WebElement dateBox, WebElement timeBox, Instant startInstant, ZoneId timeZone) {
        fillTextBox(dateBox, getDateString(startInstant, timeZone));

        selectDropdownOptionByText(timeBox, getTimeString(startInstant, timeZone));
    }
}
