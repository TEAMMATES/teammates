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

    // List of course ids to populate the dropdown with
    private List<String> courses;
    
    private boolean isCourseOptionsEmpty;
    
    // Default feedback session name in the new session form
    private String fsName;

    // Default instructions for the form
    private String instructions;
    
    private String fsStartDate;
    private List<ElementTag> fsStartTimeOptions;
    private String fsEndDate;
    private List<ElementTag> fsEndTimeOptions;
    private List<ElementTag> gracePeriodOptions;
    
    private boolean isSessionVisibleDateButtonChecked;
    private String sessionVisibleDateValue;
    private boolean isSessionVisibleDateDisabled;
    private List<ElementTag> sessionVisibleTimeOptions;
    
    
    // options for the select
    private List<ElementTag> coursesSelectField;
    private List<ElementTag> timezoneSelectField;
    
    
    private boolean sessionVisibleAtOpenChecked;
    private boolean sessionVisiblePrivateChecked;
    private boolean isResponseVisibleDateChecked;
    private String responseVisibleDateValue;
    


    private boolean isResponseVisibleDisabled;
    private List<ElementTag> responseVisibleTimeOptions;
    private boolean isResponseVisibleImmediatelyChecked;
    private boolean isResponseVisiblePublishManuallyChecked;
    

    private boolean isResponseVisibleNeverChecked;
    private boolean isSubmitButtonDisabled;
    

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

    public void setSessionVisibleDateButtonChecked(boolean isSessionVisibleDateButtonChecked) {
        this.isSessionVisibleDateButtonChecked = isSessionVisibleDateButtonChecked;
    }

    public void setGracePeriodOptions(List<ElementTag> gracePeriodOptions) {
        this.gracePeriodOptions = gracePeriodOptions;
    }

    public void setSessionVisibleDateValue(String sessionVisibleDateValue) {
        this.sessionVisibleDateValue = sessionVisibleDateValue;
    }

    public void setSessionVisibleDateDisabled(boolean isSessionVisibleDateDisabled) {
        this.isSessionVisibleDateDisabled = isSessionVisibleDateDisabled;
    }

    public void setSessionVisibleTimeOptions(List<ElementTag> sessionVisibleTimeOptions) {
        this.sessionVisibleTimeOptions = sessionVisibleTimeOptions;
    }

    public void setSessionVisibleAtOpenChecked(boolean sessionVisibleAtOpenChecked) {
        this.sessionVisibleAtOpenChecked = sessionVisibleAtOpenChecked;
    }

    public void setSessionVisiblePrivateChecked(boolean isSessionVisiblePrivateChecked) {
        this.sessionVisiblePrivateChecked = isSessionVisiblePrivateChecked;
    }

    public void setResponseVisibleDateChecked(boolean isResponseVisibleDateChecked) {
        this.isResponseVisibleDateChecked = isResponseVisibleDateChecked;
    }

    public void setResponseVisibleDateValue(String responseVisibleDateValue) {
        this.responseVisibleDateValue = responseVisibleDateValue;
    }
    
    public boolean isCourseOptionsEmpty() {
        return isCourseOptionsEmpty;
    }

    public void setCourseOptionsEmpty(boolean isCourseOptionsEmpty) {
        this.isCourseOptionsEmpty = isCourseOptionsEmpty;
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

    public boolean isSessionVisibleDateButtonChecked() {
        return isSessionVisibleDateButtonChecked;
    }

    public String getSessionVisibleDateValue() {
        return sessionVisibleDateValue;
    }

    public boolean isSessionVisibleDateDisabled() {
        return isSessionVisibleDateDisabled;
    }

    public List<ElementTag> getSessionVisibleTimeOptions() {
        return sessionVisibleTimeOptions;
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


    public boolean isSessionVisibleAtOpenChecked() {
        return sessionVisibleAtOpenChecked;
    }

    public boolean isSessionVisiblePrivateChecked() {
        return sessionVisiblePrivateChecked;
    }

    public boolean isResponseVisibleDateChecked() {
        return isResponseVisibleDateChecked;
    }

    public String getResponseVisibleDateValue() {
        return responseVisibleDateValue;
    }

    public boolean isResponseVisibleDisabled() {
        return isResponseVisibleDisabled;
    }

    public List<ElementTag> getResponseVisibleTimeOptions() {
        return responseVisibleTimeOptions;
    }

    public boolean isResponseVisibleImmediatelyChecked() {
        return isResponseVisibleImmediatelyChecked;
    }

    public boolean isResponseVisiblePublishManuallyChecked() {
        return isResponseVisiblePublishManuallyChecked;
    }

    public boolean isResponseVisibleNeverChecked() {
        return isResponseVisibleNeverChecked;
    }

    public boolean isSubmitButtonDisabled() {
        return isSubmitButtonDisabled;
    }
    
    public void setResponseVisibleDisabled(boolean isResponseVisibleDisabled) {
        this.isResponseVisibleDisabled = isResponseVisibleDisabled;
    }

    public void setResponseVisibleTimeOptions(List<ElementTag> responseVisibleTimeOptions) {
        this.responseVisibleTimeOptions = responseVisibleTimeOptions;
    }

    public void setResponseVisibleImmediatelyChecked(boolean isResponseVisibleImmediatelyChecked) {
        this.isResponseVisibleImmediatelyChecked = isResponseVisibleImmediatelyChecked;
    }

    public void setResponseVisiblePublishManuallyChecked(boolean isResponseVisiblePublishManuallyChecked) {
        this.isResponseVisiblePublishManuallyChecked = isResponseVisiblePublishManuallyChecked;
    }

    public void setResponseVisibleNeverChecked(boolean isResponseVisibleNeverChecked) {
        this.isResponseVisibleNeverChecked = isResponseVisibleNeverChecked;
    }

    public void setSubmitButtonDisabled(boolean isSubmitButtonDisabled) {
        this.isSubmitButtonDisabled = isSubmitButtonDisabled;
    }
}
