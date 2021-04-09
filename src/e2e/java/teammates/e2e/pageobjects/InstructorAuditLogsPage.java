package teammates.e2e.pageobjects;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Represents the instructor audit logs page of the website.
 */
public class InstructorAuditLogsPage extends AppPage {
    private List<WebElement> sessionCards;

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
        sessionCards = logsOutput.findElements(By.className("card"));
    }

    public List<String> getLogsData() {
        return sessionCards.stream().map(card -> card.findElement(By.tagName("i"))
                .getText()).collect(Collectors.toList());
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
