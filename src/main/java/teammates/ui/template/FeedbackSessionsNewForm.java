package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.FeedbackSessionAttributes;

public class FeedbackSessionsNewForm {

    // default course id value
    private String courseIdForNewSession;
    // for highlighting the recently modified session
    private String feedbackSessionNameForSessionList;
    // default value of the type to display : evaluations or custom session
    private String feedbackSessionType;
    
    // List of course ids to populate the dropdown with
    private List<String> courses;
    
    private String fsName;
    private String instructions;
    
    private String fsStartDate;
    private List<ElementTag> fsStartTimeOptions;
    private String fsEndDate;
    private List<ElementTag> fsEndTimeOptions;
    private List<ElementTag> gracePeriodOptions;
    
    private String sessionVisibleDateButtonCheckedAttribute;
    private String sessionVisibleDateValue;
    private String sessionVisibleDateDisabledAttribute;
    private List<ElementTag> sessionVisibleTimeOptions;
    
    
    // options for the select
    private List<ElementTag> coursesSelectField;
    private List<ElementTag> timezoneSelectField;
    
    
    private String formClasses = "form-group";
    private String courseFieldClasses = "form-control";
    private String sessionVisibleAtOpenCheckedAttribute;
    private String sessionVisiblePrivateCheckedAttribute;
    private String responseVisibleDateCheckedAttribute;
    private String responseVisibleDateValue;
    


    public String responseVisibleDisabledAttribute;
    public List<ElementTag> responseVisibleTimeOptions;
    public String responseVisibleImmediatelyCheckedAttribute;
    public String responseVisiblePublishManuallyCheckedAttribute;
    public String responseVisibleNeverCheckedAttribute;
    public String submitButtonDisabledAttribute;
    
    
    
    public FeedbackSessionsNewForm(String courseIdForNewSession,
                                   String feedbackSessionType, FeedbackSessionAttributes newFeedbackSession,
                                   List<String> courses) {
        this.courseIdForNewSession = courseIdForNewSession;
        this.feedbackSessionType = feedbackSessionType;
        this.fsName = newFeedbackSession == null ? "" : newFeedbackSession.feedbackSessionName;
        this.courses = courses;
    }
    
    public String getCourseIdForNewSession() {
        return courseIdForNewSession;
    }
    
    public String getFeedbackSessionNameForSessionList() {
        return feedbackSessionNameForSessionList;
    }
    
    public String getFeedbackSessionType() {
        return feedbackSessionType;
    }
    
    public List<String> getCourses() {
        return courses;
    }

    public void setFormClasses(String formClasses) {
        this.formClasses = formClasses;
    }

    public void setCourseFieldClasses(String courseFieldClasses) {
        this.courseFieldClasses = courseFieldClasses;
    }

    public void setFeedbackSessionNameForSessionList(String feedbackSessionNameForSessionList) {
        this.feedbackSessionNameForSessionList = feedbackSessionNameForSessionList;
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

    public void setSessionVisibleDateButtonCheckedAttribute(String sessionVisibleDateButtonCheckedAttribute) {
        this.sessionVisibleDateButtonCheckedAttribute = sessionVisibleDateButtonCheckedAttribute;
    }

    public void setGracePeriodOptions(List<ElementTag> gracePeriodOptions) {
        this.gracePeriodOptions = gracePeriodOptions;
    }

    public void setSessionVisibleDateValue(String sessionVisibleDateValue) {
        this.sessionVisibleDateValue = sessionVisibleDateValue;
    }

    public void setSessionVisibleDateDisabledAttribute(String sessionVisibleDateDisabledAttribute) {
        this.sessionVisibleDateDisabledAttribute = sessionVisibleDateDisabledAttribute;
    }

    public void setSessionVisibleTimeOptions(List<ElementTag> sessionVisibleTimeOptions) {
        this.sessionVisibleTimeOptions = sessionVisibleTimeOptions;
    }

    public void setSessionVisibleAtOpenCheckedAttribute(String sessionVisibleAtOpenCheckedAttribute) {
        this.sessionVisibleAtOpenCheckedAttribute = sessionVisibleAtOpenCheckedAttribute;
    }

    public void setSessionVisiblePrivateCheckedAttribute(String sessionVisiblePrivateCheckedAttribute) {
        this.sessionVisiblePrivateCheckedAttribute = sessionVisiblePrivateCheckedAttribute;
    }

    public void setResponseVisibleDateCheckedAttribute(String responseVisibleDateCheckedAttribute) {
        this.responseVisibleDateCheckedAttribute = responseVisibleDateCheckedAttribute;
    }

    public void setResponseVisibleDateValue(String responseVisibleDateValue) {
        this.responseVisibleDateValue = responseVisibleDateValue;
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

    public String getSessionVisibleDateButtonCheckedAttribute() {
        return sessionVisibleDateButtonCheckedAttribute;
    }

    public String getSessionVisibleDateValue() {
        return sessionVisibleDateValue;
    }

    public String getSessionVisibleDateDisabledAttribute() {
        return sessionVisibleDateDisabledAttribute;
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

    public String getFormClasses() {
        return formClasses;
    }

    public String getCourseFieldClasses() {
        return courseFieldClasses;
    }

    public String getSessionVisibleAtOpenCheckedAttribute() {
        return sessionVisibleAtOpenCheckedAttribute;
    }

    public String getSessionVisiblePrivateCheckedAttribute() {
        return sessionVisiblePrivateCheckedAttribute;
    }

    public String getResponseVisibleDateCheckedAttribute() {
        return responseVisibleDateCheckedAttribute;
    }

    public String getResponseVisibleDateValue() {
        return responseVisibleDateValue;
    }

    public String getResponseVisibleDisabledAttribute() {
        return responseVisibleDisabledAttribute;
    }

    public List<ElementTag> getResponseVisibleTimeOptions() {
        return responseVisibleTimeOptions;
    }

    public String getResponseVisibleImmediatelyCheckedAttribute() {
        return responseVisibleImmediatelyCheckedAttribute;
    }

    public String getResponseVisiblePublishManuallyCheckedAttribute() {
        return responseVisiblePublishManuallyCheckedAttribute;
    }

    public String getResponseVisibleNeverCheckedAttribute() {
        return responseVisibleNeverCheckedAttribute;
    }

    public String getSubmitButtonDisabledAttribute() {
        return submitButtonDisabledAttribute;
    }
    
    
}
