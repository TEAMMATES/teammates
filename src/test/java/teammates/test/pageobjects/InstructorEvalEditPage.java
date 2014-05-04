package teammates.test.pageobjects;

import java.util.Date;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.Const;
import teammates.common.util.TimeHelper;

public class InstructorEvalEditPage extends AppPage {
    
    @FindBy(id = "starttime")
    private WebElement startTimeDropdown;
    
    @FindBy(id = "deadlinetime")
    private WebElement endTimeDropdown;
    
    @FindBy(id = "commentsstatus_enabled")
    private WebElement p2pEnabledOption;
    
    @FindBy(id = "commentsstatus_disabled")
    private WebElement p2pDisabledOption;
    
    @FindBy(id = "graceperiod")
    private WebElement gracePeriodDropdown;
    
    @FindBy(id = "instr")
    private WebElement instructionsTextBox;
    
    @FindBy(id = "button_submit")
    private WebElement submitButton;

    public InstructorEvalEditPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Edit Evaluation</h1>");
    }
    
    public InstructorEvalEditPage submitUpdate(
            Date startTime,
            Date endTime, 
            boolean p2pEnabled, 
            String instructions,
            int gracePeriod) {
        
        JavascriptExecutor js = (JavascriptExecutor) browser.driver;
        
        // Select grace period
        selectDropdownByVisibleValue(gracePeriodDropdown, Integer.toString(gracePeriod)+ " mins");
        
        // Select deadline date
         js.executeScript("$('#" + Const.ParamsNames.EVALUATION_DEADLINE
                + "')[0].value='" + TimeHelper.formatDate(endTime) + "';");
        selectDropdownByVisibleValue(endTimeDropdown,
                TimeHelper.convertToDisplayValueInTimeDropDown(endTime));
        
        // Select start date
        js.executeScript("$('#" + Const.ParamsNames.EVALUATION_START
                + "')[0].value='" + TimeHelper.formatDate(startTime) + "';");
        selectDropdownByVisibleValue(startTimeDropdown,
                TimeHelper.convertToDisplayValueInTimeDropDown(startTime));
    
        // Allow P2P comment
        if (p2pEnabled) {
            p2pEnabledOption.click();
        } else {
            p2pDisabledOption.click();
        }
    
        // Fill in instructions
        fillTextBox(instructionsTextBox, instructions);
    
        clickSubmitButton();
        
        return this;
    }
    
    public void clickSubmitButton(){
        submitButton.click();
        waitForPageToLoad();
    }

}
