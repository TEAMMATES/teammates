package teammates.ui.template;

import java.util.Date;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.TimeHelper;
import teammates.ui.controller.PageData;


/**
 * Data model for the form for creating/editing a feedback session.
 *
 */
public class FeedbackSessionsForm {
    private String formSubmitActionLink;
    private String submitButtonText;
    
    // Default course id value
    private String courseId;
    
    private boolean isEditFsButtonsVisible;
    private boolean isFeedbackSessionTypeEditable;
    // List of options for feedback session type
    private List<ElementTag> feedbackSessionTypeOptions;
    
    private String fsDeleteLink;
    private String copyToLink;
    
    private boolean isCourseIdEditable;
    // options for selecting which course to make a fs in
    private List<ElementTag> coursesSelectField;
    
    private List<ElementTag> timezoneSelectField;

    // List of course ids to populate the dropdown with
    private List<String> courses;

    private boolean isFsNameEditable;

    private String fsName;

    private String instructions;
    
    private String fsStartDate;
    private List<ElementTag> fsStartTimeOptions;
    private String fsEndDate;
    private List<ElementTag> fsEndTimeOptions;
    private List<ElementTag> gracePeriodOptions;
    
    private boolean isShowNoCoursesMessage;
    private boolean isSubmitButtonDisabled;
    private boolean isSubmitButtonVisible;
    
    private ElementTag editButtonTags;
    private FeedbackSessionsAdditionalSettingsFormSegment additionalSettings;
    
    public FeedbackSessionsForm() {
    }
    
    
    public static FeedbackSessionsForm getFsFormForExistingFs(FeedbackSessionAttributes existingFs,
                                                  FeedbackSessionsAdditionalSettingsFormSegment additionalSettings,
                                                  String fsDeleteLink, String fsEditCopyLink) {
        FeedbackSessionsForm fsForm = new FeedbackSessionsForm();
        
        fsForm.fsDeleteLink = fsDeleteLink;
        
        fsForm.copyToLink = fsEditCopyLink;
        
        fsForm.courseId = existingFs.courseId;
        
        fsForm.isFsNameEditable = false;
        fsForm.fsName = existingFs.feedbackSessionName;
        
        fsForm.isCourseIdEditable = false;
        fsForm.isFeedbackSessionTypeEditable = false;
        
        fsForm.isEditFsButtonsVisible = true;
      
        fsForm.timezoneSelectField = PageData.getTimeZoneOptionsAsElementTags(existingFs.timeZone);

        fsForm.instructions = Sanitizer.sanitizeForHtml(existingFs.instructions.getValue());
        
        fsForm.fsStartDate = TimeHelper.formatDate(existingFs.startTime);
        fsForm.fsStartTimeOptions = PageData.getTimeOptionsAsElementTags(existingFs.startTime);
        
        fsForm.fsEndDate = TimeHelper.formatDate(existingFs.endTime);
        fsForm.fsEndTimeOptions = PageData.getTimeOptionsAsElementTags(existingFs.endTime);
        
        fsForm.gracePeriodOptions = PageData.getGracePeriodOptionsAsElementTags(existingFs.gracePeriod);
        
        fsForm.isSubmitButtonDisabled = false;
        fsForm.isSubmitButtonVisible = false;
        fsForm.formSubmitActionLink = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_SAVE;
        fsForm.submitButtonText = "Save Changes";
        
        fsForm.editButtonTags = new ElementTag("onclick", "enableEditFS()");
        
        fsForm.additionalSettings = additionalSettings;
        
        return fsForm;
    }
    
    public static FeedbackSessionsForm getFormForNewFs(FeedbackSessionAttributes feedbackSession,
                                                       List<ElementTag> fsTypeOptions,
                                                       String defaultCourseId,
                                                       List<String> courseIds, List<ElementTag> courseIdOptions,
                                                       Map<String, InstructorAttributes> instructors,
                                                       FeedbackSessionsAdditionalSettingsFormSegment additionalSettings) {
        FeedbackSessionsForm newFsForm = new FeedbackSessionsForm();
        
        newFsForm.isShowNoCoursesMessage = courseIds.isEmpty();
        newFsForm.courseId = defaultCourseId;
        
        newFsForm.isFsNameEditable = true;
        newFsForm.fsName = feedbackSession == null ? "" : feedbackSession.feedbackSessionName;
        
        newFsForm.isCourseIdEditable = true;
        newFsForm.courses = courseIds;
        
        newFsForm.coursesSelectField = courseIdOptions;
        
        newFsForm.isEditFsButtonsVisible = false;
        newFsForm.isFeedbackSessionTypeEditable = true;
        newFsForm.feedbackSessionTypeOptions = fsTypeOptions;

        newFsForm.timezoneSelectField = PageData.getTimeZoneOptionsAsElementTags(feedbackSession == null 
                                                                            ? Const.DOUBLE_UNINITIALIZED 
                                                                            : feedbackSession.timeZone);
        
        newFsForm.instructions = feedbackSession == null 
                               ? "Please answer all the given questions." 
                               : Sanitizer.sanitizeForHtml(feedbackSession.instructions.getValue());
        
        newFsForm.fsStartDate = feedbackSession == null 
                              ? TimeHelper.formatDate(TimeHelper.getNextHour()) 
                              : TimeHelper.formatDate(feedbackSession.startTime);
        
        
        Date startDate = feedbackSession == null ? null : feedbackSession.startTime;
        newFsForm.fsStartTimeOptions = PageData.getTimeOptionsAsElementTags(startDate);
        
        newFsForm.fsEndDate = feedbackSession == null 
                            ? ""  
                            : TimeHelper.formatDate(feedbackSession.endTime);
        
        Date endDate = feedbackSession == null ? null : feedbackSession.endTime;
        newFsForm.fsEndTimeOptions = PageData.getTimeOptionsAsElementTags(endDate);
        
        newFsForm.gracePeriodOptions = PageData.getGracePeriodOptionsAsElementTags(feedbackSession == null  
                                                                              ? Const.INT_UNINITIALIZED  
                                                                              : feedbackSession.gracePeriod);
        
        newFsForm.isSubmitButtonDisabled= courseIds.isEmpty();
        newFsForm.formSubmitActionLink = Const.ActionURIs.INSTRUCTOR_FEEDBACK_ADD;
        newFsForm.submitButtonText = "Create Feedback Session";
        newFsForm.isSubmitButtonVisible = true;
        
        newFsForm.additionalSettings = additionalSettings;
        
        return newFsForm;
    }
    
    public void setFsName(String fsName) {
        this.fsName = fsName;
    }
    
    public String getCourseId() {
        return courseId;
    }
    
    public List<String> getCourses() {
        return courses;
    }
    
    public List<ElementTag> getFeedbackSessionTypeOptions() {
        return feedbackSessionTypeOptions;
    }
    
    public boolean isShowNoCoursesMessage() {
        return isShowNoCoursesMessage;
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

    public FeedbackSessionsAdditionalSettingsFormSegment getAdditionalSettings() {
        return this.additionalSettings;
    }

    public boolean isSubmitButtonDisabled() {
        return isSubmitButtonDisabled;
    }

    public String getFormSubmitAction() {
        return formSubmitActionLink;
    }

    public boolean isFeedbackSessionTypeEditable() {
        return isFeedbackSessionTypeEditable;
    }

    public boolean isCourseIdEditable() {
        return isCourseIdEditable;
    }

    public boolean isFsNameEditable() {
        return isFsNameEditable;
    }

    public String getSubmitButtonText() {
        return submitButtonText;
    }

    public boolean isSubmitButtonVisible() {
        return isSubmitButtonVisible;
    }

    public void setSubmitButtonVisible(boolean isSubmitButtonVisible) {
        this.isSubmitButtonVisible = isSubmitButtonVisible;
    }

    public String getFsDeleteLink() {
        return fsDeleteLink;
    }


    public String getCopyToLink() {
        return copyToLink;
    }


    public boolean isEditFsButtonsVisible() {
        return isEditFsButtonsVisible;
    }


    public ElementTag getEditButtonTags() {
        return editButtonTags;
    }
    
}
