package teammates.ui.template;

import java.util.Date;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.ui.controller.PageData;


/**
 * Data model for the form for creating/editing a feedback session.
 *
 */
public class FeedbackSessionsForm {
    private Url formSubmitAction;
    private String submitButtonText;
    
    // Default course id value
    private String courseIdForNewSession;
    
    private boolean isEditFsButtonsVisible;
    private boolean isFeedbackSessionTypeEditable;
    // List of options for feedback session type
    private List<ElementTag> feedbackSessionTypeOptions;
    
    private Url fsDeleteLink;
    private Url copyToLink;
    
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
    
    
    public static FeedbackSessionsForm getFsFormForExistingFs(PageData data, 
                                                              FeedbackSessionAttributes newFeedbackSession,
                                                              FeedbackSessionsAdditionalSettingsFormSegment additionalSettings) {
        FeedbackSessionsForm fsForm = new FeedbackSessionsForm();
        
        fsForm.setFsDeleteLink(
                   new Url(data.getInstructorFeedbackSessionDeleteLink(
                           newFeedbackSession.courseId,
                           newFeedbackSession.feedbackSessionName, 
                           "")));
        
        fsForm.setCopyToLink(new Url(data.getFeedbackSessionEditCopyLink()));
        
        fsForm.setCourseIdForNewSession(newFeedbackSession.courseId);
        
        fsForm.setFsNameEditable(false);
        fsForm.setFsName(newFeedbackSession.feedbackSessionName);
        
        fsForm.setCourseIdEditable(false);
        fsForm.setFeedbackSessionTypeEditable(false);
        
        fsForm.setEditFsButtonsVisible(true);
      
        fsForm.setTimezoneSelectField(data.getTimeZoneOptionsAsElementTags(newFeedbackSession.timeZone));

        fsForm.setInstructions(Sanitizer.sanitizeForHtml(newFeedbackSession.instructions.getValue()));
        
        fsForm.setFsStartDate(TimeHelper.formatDate(newFeedbackSession.startTime));
        fsForm.setFsStartTimeOptions(data.getTimeOptionsAsElementTags(newFeedbackSession.startTime));
        
        fsForm.setFsEndDate(TimeHelper.formatDate(newFeedbackSession.endTime));
        fsForm.setFsEndTimeOptions(data.getTimeOptionsAsElementTags(newFeedbackSession.endTime));
        
        fsForm.setGracePeriodOptions(data.getGracePeriodOptionsAsElementTags(newFeedbackSession.gracePeriod));
        
        fsForm.setSubmitButtonDisabled(false);
        fsForm.setFormSubmitAction(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_SAVE));
        fsForm.setSubmitButtonText("Save Changes");
        fsForm.setSubmitButtonVisible(false);
        
        fsForm.setEditButtonTags(new ElementTag("onclick", "enableEditFS()"));
        
        fsForm.setAdditionalSettings(additionalSettings);
        
        return fsForm;
    }
    
    public static FeedbackSessionsForm getFormForNewFs(PageData data, 
                                                       String defaultCourseId,
                                                       Map<String, InstructorAttributes> instructors,
                                                       FeedbackSessionAttributes feedbackSession, String feedbackSessionType,
                                                       String feedbackSessionNameForSessionList, 
                                                       List<String> courseIds, List<ElementTag> courseIdOptions,
                                                       List<ElementTag> fsTypeOptions,
                                                       FeedbackSessionsAdditionalSettingsFormSegment additionalSettings) {
        FeedbackSessionsForm newFsForm = new FeedbackSessionsForm();
        
        newFsForm.setIsShowNoCoursesMessage(courseIds.isEmpty());
        newFsForm.setCourseIdForNewSession(defaultCourseId);
        
        newFsForm.setFsNameEditable(true);
        newFsForm.setFsName(feedbackSession == null ? "" : feedbackSession.feedbackSessionName);
        
        newFsForm.setCourseIdEditable(true);
        newFsForm.setCourses(courseIds);
        
        newFsForm.setCoursesSelectField(courseIdOptions);
        
        newFsForm.setEditFsButtonsVisible(false);
        newFsForm.setFeedbackSessionTypeEditable(true);
        newFsForm.setFeedbackSessionTypeOptions(fsTypeOptions);

        newFsForm.setTimezoneSelectField(data.getTimeZoneOptionsAsElementTags(feedbackSession == null ? 
                                                                              Const.DOUBLE_UNINITIALIZED : 
                                                                              feedbackSession.timeZone));
        
        newFsForm.setInstructions(feedbackSession == null ?
                                  "Please answer all the given questions." :
                                  Sanitizer.sanitizeForHtml(feedbackSession.instructions.getValue()));
        
        newFsForm.setFsStartDate(feedbackSession == null ?
                                 TimeHelper.formatDate(TimeHelper.getNextHour()) :
                                 TimeHelper.formatDate(feedbackSession.startTime));
        
        
        Date startDate = feedbackSession == null ? null : feedbackSession.startTime;
        newFsForm.setFsStartTimeOptions(data.getTimeOptionsAsElementTags(startDate));
        
        newFsForm.setFsEndDate(feedbackSession == null ?
                               "" : 
                               TimeHelper.formatDate(feedbackSession.endTime));
        
        Date endDate = feedbackSession == null ? null : feedbackSession.endTime;
        newFsForm.setFsEndTimeOptions(data.getTimeOptionsAsElementTags(endDate));
        
        newFsForm.setGracePeriodOptions(data.getGracePeriodOptionsAsElementTags(feedbackSession == null ? 
                                                                                Const.INT_UNINITIALIZED : 
                                                                                feedbackSession.gracePeriod));
        
        newFsForm.setSubmitButtonDisabled(courseIds.isEmpty());
        newFsForm.setFormSubmitAction(new Url(Const.ActionURIs.INSTRUCTOR_FEEDBACK_ADD));
        newFsForm.setSubmitButtonText("Create Feedback Session");
        newFsForm.setSubmitButtonVisible(true);
        
        newFsForm.setAdditionalSettings(additionalSettings);
        
        return newFsForm;
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
    
    public void setAdditionalSettings(FeedbackSessionsAdditionalSettingsFormSegment additionalSettings) {
        this.additionalSettings = additionalSettings;
    }

    public boolean isSubmitButtonDisabled() {
        return isSubmitButtonDisabled;
    }

    public void setSubmitButtonDisabled(boolean isSubmitButtonDisabled) {
        this.isSubmitButtonDisabled = isSubmitButtonDisabled;
    }

    public void setIsShowNoCoursesMessage(boolean showNoCoursesMessage) {
        this.isShowNoCoursesMessage = showNoCoursesMessage;
    }

    public Url getFormSubmitAction() {
        return formSubmitAction;
    }

    public void setFormSubmitAction(Url formSubmitAction) {
        this.formSubmitAction = formSubmitAction;
    }

    public boolean isFeedbackSessionTypeEditable() {
        return isFeedbackSessionTypeEditable;
    }

    public void setFeedbackSessionTypeEditable(boolean isFeedbackSessionTypeEditable) {
        this.isFeedbackSessionTypeEditable = isFeedbackSessionTypeEditable;
    }

    public boolean isCourseIdEditable() {
        return isCourseIdEditable;
    }

    public void setCourseIdEditable(boolean isCourseIdEditable) {
        this.isCourseIdEditable = isCourseIdEditable;
    }

    public void setShowNoCoursesMessage(boolean isShowNoCoursesMessage) {
        this.isShowNoCoursesMessage = isShowNoCoursesMessage;
    }

    public boolean isFsNameEditable() {
        return isFsNameEditable;
    }

    public void setFsNameEditable(boolean isFsNameEditable) {
        this.isFsNameEditable = isFsNameEditable;
    }

    public String getSubmitButtonText() {
        return submitButtonText;
    }

    public void setSubmitButtonText(String submitButtonText) {
        this.submitButtonText = submitButtonText;
    }

    public boolean isSubmitButtonVisible() {
        return isSubmitButtonVisible;
    }

    public void setSubmitButtonVisible(boolean isSubmitButtonVisible) {
        this.isSubmitButtonVisible = isSubmitButtonVisible;
    }

    public Url getFsDeleteLink() {
        return fsDeleteLink;
    }

    public void setFsDeleteLink(Url fsDeleteLink) {
        this.fsDeleteLink = fsDeleteLink;
    }

    public Url getCopyToLink() {
        return copyToLink;
    }

    public void setCopyToLink(Url copyToLink) {
        this.copyToLink = copyToLink;
    }

    public boolean isEditFsButtonsVisible() {
        return isEditFsButtonsVisible;
    }

    public void setEditFsButtonsVisible(boolean isEditFsButtonsVisible) {
        this.isEditFsButtonsVisible = isEditFsButtonsVisible;
    }

    public ElementTag getEditButtonTags() {
        return editButtonTags;
    }

    public void setEditButtonTags(ElementTag editButtonTags) {
        this.editButtonTags = editButtonTags;
    }
    
}
