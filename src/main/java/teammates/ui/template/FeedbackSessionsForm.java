package teammates.ui.template;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;
import teammates.ui.pagedata.PageData;

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

    private String fsTimeZone;

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

    private String submissionStatus;
    private String publishedStatus;

    private FeedbackSessionsAdditionalSettingsFormSegment additionalSettings;

    public static FeedbackSessionsForm getFsFormForExistingFs(FeedbackSessionAttributes existingFs,
                                                  FeedbackSessionsAdditionalSettingsFormSegment additionalSettings,
                                                  String fsDeleteLink, String fsEditCopyLink) {
        FeedbackSessionsForm fsForm = new FeedbackSessionsForm();

        fsForm.fsDeleteLink = fsDeleteLink;

        fsForm.copyToLink = fsEditCopyLink;

        fsForm.courseId = existingFs.getCourseId();

        fsForm.isFsNameEditable = false;
        fsForm.fsName = existingFs.getFeedbackSessionName();

        fsForm.isCourseIdEditable = false;
        fsForm.isFeedbackSessionTypeEditable = false;

        fsForm.isEditFsButtonsVisible = true;

        fsForm.fsTimeZone = existingFs.getTimeZone().getId();

        fsForm.instructions = SanitizationHelper.sanitizeForRichText(existingFs.getInstructions().getValue());

        fsForm.fsStartDate = TimeHelper.formatDateForSessionsForm(existingFs.getStartTimeLocal());
        fsForm.fsStartTimeOptions = PageData.getTimeOptionsAsElementTags(existingFs.getStartTimeLocal());

        fsForm.fsEndDate = TimeHelper.formatDateForSessionsForm(existingFs.getEndTimeLocal());
        fsForm.fsEndTimeOptions = PageData.getTimeOptionsAsElementTags(existingFs.getEndTimeLocal());

        fsForm.gracePeriodOptions = PageData.getGracePeriodOptionsAsElementTags(existingFs.getGracePeriodMinutes());

        fsForm.isSubmitButtonDisabled = false;
        fsForm.isSubmitButtonVisible = false;
        fsForm.formSubmitActionLink = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_SAVE;
        fsForm.submitButtonText = "Save Changes";

        fsForm.additionalSettings = additionalSettings;

        fsForm.submissionStatus = PageData.getInstructorSubmissionStatusForFeedbackSession(existingFs);
        fsForm.publishedStatus = PageData.getInstructorPublishedStatusForFeedbackSession(existingFs);

        return fsForm;
    }

    public static FeedbackSessionsForm getFormForNewFs(FeedbackSessionAttributes feedbackSession,
                                                       List<ElementTag> fsTypeOptions,
                                                       String defaultCourseId,
                                                       List<String> courseIds, List<ElementTag> courseIdOptions,
                                                       Map<String, InstructorAttributes> instructors,
                                                       FeedbackSessionsAdditionalSettingsFormSegment additionalSettings,
                                                       boolean isSubmitButtonDisabled) {
        FeedbackSessionsForm newFsForm = new FeedbackSessionsForm();

        newFsForm.isShowNoCoursesMessage = courseIds.isEmpty();
        newFsForm.courseId = defaultCourseId;

        newFsForm.isFsNameEditable = true;
        newFsForm.fsName = feedbackSession == null ? "" : feedbackSession.getFeedbackSessionName();

        newFsForm.isCourseIdEditable = true;
        newFsForm.courses = courseIds;

        newFsForm.coursesSelectField = courseIdOptions;

        newFsForm.isEditFsButtonsVisible = false;
        newFsForm.isFeedbackSessionTypeEditable = true;
        newFsForm.feedbackSessionTypeOptions = fsTypeOptions;

        newFsForm.fsTimeZone = feedbackSession == null ? null : feedbackSession.getTimeZone().getId();

        newFsForm.instructions = feedbackSession == null
                               ? "Please answer all the given questions."
                               : SanitizationHelper.sanitizeForRichText(feedbackSession.getInstructions().getValue());

        newFsForm.fsStartDate = feedbackSession == null
                              ? ""
                              : TimeHelper.formatDateForSessionsForm(feedbackSession.getStartTimeLocal());

        LocalDateTime startDate = feedbackSession == null ? null : feedbackSession.getStartTimeLocal();
        newFsForm.fsStartTimeOptions = PageData.getTimeOptionsAsElementTags(startDate);

        newFsForm.fsEndDate = feedbackSession == null
                            ? ""
                            : TimeHelper.formatDateForSessionsForm(feedbackSession.getEndTimeLocal());

        LocalDateTime endDate = feedbackSession == null ? null : feedbackSession.getEndTimeLocal();
        newFsForm.fsEndTimeOptions = PageData.getTimeOptionsAsElementTags(endDate);

        newFsForm.gracePeriodOptions = PageData.getGracePeriodOptionsAsElementTags(feedbackSession == null
                                                                              ? Const.INT_UNINITIALIZED
                                                                              : feedbackSession.getGracePeriodMinutes());

        newFsForm.isSubmitButtonDisabled = isSubmitButtonDisabled;
        newFsForm.formSubmitActionLink = Const.ActionURIs.INSTRUCTOR_FEEDBACK_ADD;
        newFsForm.submitButtonText = "Create Feedback Session";
        newFsForm.isSubmitButtonVisible = true;

        newFsForm.additionalSettings = additionalSettings;

        newFsForm.submissionStatus = feedbackSession == null
                                     ? ""
                                     : PageData.getInstructorSubmissionStatusForFeedbackSession(feedbackSession);
        newFsForm.publishedStatus = feedbackSession == null
                                    ? ""
                                    : PageData.getInstructorPublishedStatusForFeedbackSession(feedbackSession);
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

    public String getFsTimeZone() {
        return fsTimeZone == null ? "" : fsTimeZone;
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

    public String getSubmissionStatus() {
        return submissionStatus;
    }

    public String getPublishedStatus() {
        return publishedStatus;
    }
}
