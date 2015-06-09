package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.FeedbackSessionAttributes;

public class FeedbackSessionsNewForm {

    // default course id value
    public String courseIdForNewSession;
    // for highlighting the recently modified session
    public String feedbackSessionNameForSessionList;
    // default value of the type to display : evaluations or custom session
    public String feedbackSessionType;
    // If this is provided, then the attributes of this feedback session is the default values 
    // in the form
    public FeedbackSessionAttributes defaultFeedbackSession;
    // List of course ids to populate the dropdown with
    public List<String> courses;
    
    public String fsStartDate;
    public List<ElementTag> fsStartTimeOptions;
    public String fsEndDate;
    public List<ElementTag> fsEndTimeOptions;
    public List<ElementTag> gracePeriodOptions;
    
    public String sessionVisibleDateButtonCheckedAttribute;
    public String sessionVisibleDateValue;
    public String sessionVisibleDateDisabledAttribute;
    public List<ElementTag> sessionVisibleTimeOptions;
    
    
    // options for the select
    public List<ElementTag> coursesSelectField;
    public List<ElementTag> timezoneSelectField;
    
    
    public String formClasses = "form-group";
    public String courseFieldClasses = "form-control";
    public String sessionVisibleAtOpenCheckedAttribute;
    public String sessionVisiblePrivateCheckedAttribute;
    public String responseVisibleDateCheckedAttribute;
    public Object responseVisibleDateValue;
    public FeedbackSessionAttributes getDefaultFeedbackSession() {
        return defaultFeedbackSession;
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

    public Object getResponseVisibleDateValue() {
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
        this.defaultFeedbackSession = newFeedbackSession;
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
    
    public FeedbackSessionAttributes getNewFeedbackSession() {
        return defaultFeedbackSession;
    }
    
    public List<String> getCourses() {
        return courses;
    }
    
    
}
