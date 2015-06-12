package teammates.ui.template;

import java.util.List;

/**
 * Data model for the form for creating a new feedback session.
 *
 */
public class FeedbackSessionsForm {

    // Default course id value
    private String courseIdForNewSession;
    
    private String feedbackSessionNameForSessionList;
    
    // List of options for feedback session type
    private List<ElementTag> feedbackSessionTypeOptions;
    // options for selecting which course to make a fs in
    private List<ElementTag> coursesSelectField;
    
    private List<ElementTag> timezoneSelectField;

    // List of course ids to populate the dropdown with
    private List<String> courses;

    
    // Default feedback session name in the new session form
    private String fsName;

    // Default instructions for the form
    private String instructions;
    
    private String fsStartDate;
    private List<ElementTag> fsStartTimeOptions;
    private String fsEndDate;
    private List<ElementTag> fsEndTimeOptions;
    private List<ElementTag> gracePeriodOptions;
    
    private boolean isSubmitButtonDisabled;
    
    private AdditionalSettingsFormSegment additionalSettings;
    
    public FeedbackSessionsForm() {
    }
    
    public void setCourseIdForNewSession(String courseIdForNewSession) {
        this.courseIdForNewSession = courseIdForNewSession;
    }
    
    public void setFsName(String fsName) {
        this.fsName = fsName;
    }
    
    public void setCourses(List<String> courses) {
        this.courses = courses;
    }
    
    public String getCourseIdForNewSession() {
        return courseIdForNewSession;
    }
    
    public List<String> getCourses() {
        return courses;
    }
    
    public List<ElementTag> getFeedbackSessionTypeOptions() {
        return feedbackSessionTypeOptions;
    }

    public void setFeedbackSessionTypeOptions(List<ElementTag> feedbackSessionTypeOptions) {
        this.feedbackSessionTypeOptions = feedbackSessionTypeOptions;
    }

    public void setCoursesSelectField(List<ElementTag> coursesSelectField) {
        this.coursesSelectField = coursesSelectField;
    }

    public void setTimezoneSelectField(List<ElementTag> timezoneSelectField) {
        this.timezoneSelectField = timezoneSelectField;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setFsStartDate(String fsStartDate) {
        this.fsStartDate = fsStartDate;
    }

    public void setFsStartTimeOptions(List<ElementTag> fsStartTimeOptions) {
        this.fsStartTimeOptions = fsStartTimeOptions;
    }

    public void setFsEndDate(String fsEndDate) {
        this.fsEndDate = fsEndDate;
    }

    public void setFsEndTimeOptions(List<ElementTag> fsEndTimeOptions) {
        this.fsEndTimeOptions = fsEndTimeOptions;
    }

    

    public void setGracePeriodOptions(List<ElementTag> gracePeriodOptions) {
        this.gracePeriodOptions = gracePeriodOptions;
    }
    
    
    
    public String getFsName() {
        return fsName;
    }
    
    public String getInstructions() {
        return instructions;
    }

    public String getFsStartDate() {
        return fsStartDate;
    }

    public List<ElementTag> getFsStartTimeOptions() {
        return fsStartTimeOptions;
    }

    public String getFsEndDate() {
        return fsEndDate;
    }

    public List<ElementTag> getFsEndTimeOptions() {
        return fsEndTimeOptions;
    }

    public List<ElementTag> getGracePeriodOptions() {
        return gracePeriodOptions;
    }

   

    public List<ElementTag> getCoursesSelectField() {
        return coursesSelectField;
    }

    public List<ElementTag> getTimezoneSelectField() {
        return timezoneSelectField;
    }
    
    public void setFeedbackSessionNameForSessionList(String feedbackSessionNameForSessionList) {
        this.feedbackSessionNameForSessionList = feedbackSessionNameForSessionList;
    }
    
    public String getfeedbackSessionNameForSessionList() {
        return this.feedbackSessionNameForSessionList;
    }

    public AdditionalSettingsFormSegment getAdditionalSettings() {
        return this.additionalSettings;
    }
    
    public void setAdditionalSettings(AdditionalSettingsFormSegment additionalSettings) {
        this.additionalSettings = additionalSettings;
    }
    

    public boolean isSubmitButtonDisabled() {
        return isSubmitButtonDisabled;
    }
    
    

    public void setSubmitButtonDisabled(boolean isSubmitButtonDisabled) {
        this.isSubmitButtonDisabled = isSubmitButtonDisabled;
    }
}
