package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.ui.template.ElementTag;
import teammates.ui.template.FeedbackSessionsAdditionalSettingsFormSegment;
import teammates.ui.template.FeedbackSessionsCopyFromModal;
import teammates.ui.template.FeedbackSessionsForm;
import teammates.ui.template.FeedbackSessionsTable;
import teammates.ui.template.FeedbackSessionsTableRow;
import teammates.ui.template.InstructorFeedbackSessionActions;
import teammates.ui.template.SoftDeletedFeedbackSessionsTable;
import teammates.ui.template.SoftDeletedFeedbackSessionsTableRow;

public class InstructorFeedbackSessionsPageData extends PageData {

    // Flag for deciding if loading the sessions table, or the new sessions form.
    // if true -> loads the sessions table, else load the form
    private boolean isUsingAjax;

    private FeedbackSessionsTable fsList;
    private SoftDeletedFeedbackSessionsTable softDeletedFsList;
    private FeedbackSessionsForm newFsForm;
    private FeedbackSessionsCopyFromModal copyFromModal;
    private List<CourseAttributes> courseAttributes;
    private Map<String, InstructorAttributes> instructorsForCourses;

    public InstructorFeedbackSessionsPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }

    public boolean isUsingAjax() {
        return isUsingAjax;
    }

    /**
     * Initializes the PageData.
     * @param courses                    courses that the user is an instructor of
     * @param courseIdForNewSession      the course id to automatically select in the dropdown
     * @param existingFeedbackSessions   list of existing feedback sessions
     * @param instructors                a map of courseId to the instructorAttributes for the current user
     * @param defaultFormValues          the feedback session which values are used as the default values in the form
     * @param sessionTemplateType        "TEAMEVALUATION" or "STANDARD"
     * @param highlightedFeedbackSession the feedback session to highlight in the sessions table
     */
    public void init(List<CourseAttributes> courses, String courseIdForNewSession,
                     List<FeedbackSessionAttributes> existingFeedbackSessions,
                     List<FeedbackSessionAttributes> softDeletedFeedbackSessions,
                     Map<String, InstructorAttributes> instructors,
                     FeedbackSessionAttributes defaultFormValues, String sessionTemplateType,
                     String highlightedFeedbackSession) {

        this.instructorsForCourses = instructors;

        FeedbackSessionAttributes.sortFeedbackSessionsByCreationTimeDescending(existingFeedbackSessions);

        buildNewForm(courses, courseIdForNewSession,
                     instructors, defaultFormValues,
                     sessionTemplateType);

        buildFsList(courseIdForNewSession, existingFeedbackSessions,
                    instructors, highlightedFeedbackSession);

        buildCopyFromModal(courses, courseIdForNewSession, existingFeedbackSessions, instructors,
                           defaultFormValues, highlightedFeedbackSession);

        courseAttributes = courses;

        this.softDeletedFsList = convertToSoftDeletedFeedbackSessionsTable(softDeletedFeedbackSessions);
    }

    public void initWithoutHighlightedRow(List<CourseAttributes> courses, String courseIdForNewSession,
                                          List<FeedbackSessionAttributes> existingFeedbackSessions,
                                          List<FeedbackSessionAttributes> softDeletedFeedbackSessions,
                                          Map<String, InstructorAttributes> instructors,
                                          FeedbackSessionAttributes defaultFormValues, String sessionTemplateType) {

        init(courses, courseIdForNewSession, existingFeedbackSessions, softDeletedFeedbackSessions, instructors,
                defaultFormValues, sessionTemplateType, null);
    }

    public void initWithoutDefaultFormValues(List<CourseAttributes> courses, String courseIdForNewSession,
                                             List<FeedbackSessionAttributes> existingFeedbackSessions,
                                             List<FeedbackSessionAttributes> softDeletedFeedbackSessions,
                                             Map<String, InstructorAttributes> instructors,
                                             String highlightedFeedbackSession) {

        init(courses, courseIdForNewSession, existingFeedbackSessions, softDeletedFeedbackSessions, instructors,
                null, null, highlightedFeedbackSession);
    }

    private void buildCopyFromModal(List<CourseAttributes> courses, String courseIdForNewSession,
                                    List<FeedbackSessionAttributes> existingFeedbackSessions,
                                    Map<String, InstructorAttributes> instructors,
                                    FeedbackSessionAttributes newFeedbackSession,
                                    String feedbackSessionNameForSessionList) {
        List<FeedbackSessionAttributes> filteredFeedbackSessions = new ArrayList<>();
        for (FeedbackSessionAttributes existingFeedbackSession : existingFeedbackSessions) {
            if (instructors.get(existingFeedbackSession.getCourseId())
                           .isAllowedForPrivilege(
                                  Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION)) {
                filteredFeedbackSessions.add(existingFeedbackSession);
            }
        }

        List<FeedbackSessionsTableRow> filteredFeedbackSessionsRow = convertFeedbackSessionAttributesToSessionRows(
                                                                        filteredFeedbackSessions,
                                                                        instructors, feedbackSessionNameForSessionList,
                                                                        courseIdForNewSession);

        String fsName = newFeedbackSession == null ? "" : newFeedbackSession.getFeedbackSessionName();

        List<ElementTag> courseIdOptions =
                getCourseIdOptions(courses, courseIdForNewSession, instructors, newFeedbackSession);

        addPlaceholderIfEmpty(courseIdOptions, determinePlaceholderMessage(!courses.isEmpty()));

        copyFromModal = new FeedbackSessionsCopyFromModal(filteredFeedbackSessionsRow,
                                                          fsName, courseIdOptions);
    }

    private void buildFsList(String courseIdToHighlight, List<FeedbackSessionAttributes> existingFeedbackSessions,
                             Map<String, InstructorAttributes> instructors, String feedbackSessionNameToHighlight) {

        List<FeedbackSessionsTableRow> existingFeedbackSessionsRow =
                convertFeedbackSessionAttributesToSessionRows(existingFeedbackSessions, instructors,
                                                              feedbackSessionNameToHighlight, courseIdToHighlight);
        fsList = new FeedbackSessionsTable(existingFeedbackSessionsRow,
                                           feedbackSessionNameToHighlight,
                                           courseIdToHighlight);
    }

    private void buildNewForm(List<CourseAttributes> courses, String courseIdForNewSession,
                              Map<String, InstructorAttributes> instructors,
                              FeedbackSessionAttributes newFeedbackSession, String sessionTemplateType) {
        List<String> courseIds = new ArrayList<>();
        for (CourseAttributes course : courses) {
            courseIds.add(course.getId());
        }

        FeedbackSessionsAdditionalSettingsFormSegment additionalSettings = buildFormAdditionalSettings(newFeedbackSession);
        newFsForm = buildBasicForm(courses, courseIdForNewSession, instructors,
                                   newFeedbackSession, sessionTemplateType,
                                   courseIds,
                                   additionalSettings);
    }

    private FeedbackSessionsForm buildBasicForm(List<CourseAttributes> courses, String courseIdForNewSession,
                                                Map<String, InstructorAttributes> instructors,
                                                FeedbackSessionAttributes newFeedbackSession, String sessionTemplateType,
                                                List<String> courseIds,
                                                FeedbackSessionsAdditionalSettingsFormSegment additionalSettings) {

        List<ElementTag> courseIdOptions =
                getCourseIdOptions(courses, courseIdForNewSession, instructors, newFeedbackSession);
        boolean isSubmitButtonDisabled = courseIdOptions.isEmpty();

        addPlaceholderIfEmpty(courseIdOptions, determinePlaceholderMessage(!courses.isEmpty()));

        return FeedbackSessionsForm.getFormForNewFs(
                                        newFeedbackSession,
                                        getSessionTemplateTypeOptions(sessionTemplateType),
                                        courseIdForNewSession,
                                        courseIds, courseIdOptions,
                                        instructors,
                                        additionalSettings, isSubmitButtonDisabled);
    }

    private FeedbackSessionsAdditionalSettingsFormSegment buildFormAdditionalSettings(
                                              FeedbackSessionAttributes newFeedbackSession) {
        if (newFeedbackSession == null) {
            return FeedbackSessionsAdditionalSettingsFormSegment.getDefaultFormSegment();
        }
        return FeedbackSessionsAdditionalSettingsFormSegment.getFormSegmentWithExistingValues(newFeedbackSession);

    }

    private List<FeedbackSessionsTableRow> convertFeedbackSessionAttributesToSessionRows(
                                                 List<FeedbackSessionAttributes> sessions,
                                                 Map<String, InstructorAttributes> instructors,
                                         String feedbackSessionNameForSessionList, String courseIdForNewSession) {

        List<FeedbackSessionsTableRow> rows = new ArrayList<>();

        for (FeedbackSessionAttributes session : sessions) {
            String courseId = session.getCourseId();
            String name = sanitizeForHtml(session.getFeedbackSessionName());
            String submissionsTooltip = getInstructorSubmissionsTooltipForFeedbackSession(session);
            String publishedTooltip = getInstructorPublishedTooltipForFeedbackSession(session);
            String submissionStatus = getInstructorSubmissionStatusForFeedbackSession(session);
            String publishedStatus = getInstructorPublishedStatusForFeedbackSession(session);
            String href = getInstructorFeedbackStatsLink(session.getCourseId(), session.getFeedbackSessionName());

            InstructorFeedbackSessionActions actions =
                    getInstructorFeedbackSessionActions(session, Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE,
                                                        instructors.get(courseId));

            ElementTag elementAttributes;
            if (session.getCourseId().equals(courseIdForNewSession)
                    && session.getFeedbackSessionName().equals(feedbackSessionNameForSessionList)) {
                elementAttributes = new ElementTag("class", "sessionsRow warning");
            } else {
                elementAttributes = new ElementTag("class", "sessionsRow");
            }

            rows.add(new FeedbackSessionsTableRow(courseId, name, submissionsTooltip, publishedTooltip, submissionStatus,
                                                  publishedStatus, href, actions, elementAttributes));
        }

        return rows;
    }

    public FeedbackSessionsTable getFsList() {
        return fsList;
    }

    public SoftDeletedFeedbackSessionsTable getSoftDeletedFsList() {
        return softDeletedFsList;
    }

    public FeedbackSessionsForm getNewFsForm() {
        return newFsForm;
    }

    public FeedbackSessionsCopyFromModal getCopyFromModal() {
        return copyFromModal;
    }

    public List<CourseAttributes> getCourseAttributes() {
        return courseAttributes;
    }

    public boolean isInstructorAllowedToModify() {
        return getSoftDeletedFsList().getRows().stream()
                .allMatch(session -> instructorsForCourses.get(session.getCourseId())
                        .isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
    }

    /**
     * Creates a list of options (STANDARD and TEAMEVALUATION). If defaultSessionTemplateType is null,
     *     TEAMEVALUATION is selected by default.
     * @param defaultSessionTemplateType  either STANDARD or TEAMEVALUATION, the option that is selected on page load
     */
    private List<ElementTag> getSessionTemplateTypeOptions(String defaultSessionTemplateType) {
        ArrayList<ElementTag> result = new ArrayList<>();

        ElementTag standardFeedbackSession = createOption("session with my own questions", "STANDARD",
                                                          "STANDARD".equals(defaultSessionTemplateType));
        ElementTag evaluationFeedbackSession =
                createOption("session using template: team peer evaluation", "TEAMEVALUATION",
                        defaultSessionTemplateType == null || "TEAMEVALUATION".equals(defaultSessionTemplateType));

        result.add(standardFeedbackSession);
        result.add(evaluationFeedbackSession);

        return result;
    }

    private List<ElementTag> getCourseIdOptions(List<CourseAttributes> courses, String courseIdForNewSession,
                                                     Map<String, InstructorAttributes> instructors,
                                                     FeedbackSessionAttributes newFeedbackSession) {
        ArrayList<ElementTag> result = new ArrayList<>();

        for (CourseAttributes course : courses) {

            // True if this is a submission of the filled 'new session' form
            // for this course:
            boolean isFilledFormForSessionInThisCourse =
                    newFeedbackSession != null && course.getId().equals(newFeedbackSession.getCourseId());

            // True if this is for displaying an empty form for creating a
            // session for this course:
            boolean isEmptyFormForSessionInThisCourse =
                                            course.getId().equals(courseIdForNewSession);

            if (instructors.get(course.getId()).isAllowedForPrivilege(
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION)) {
                ElementTag option = createOption(course.getId(), course.getId(),
                                                 isFilledFormForSessionInThisCourse || isEmptyFormForSessionInThisCourse);
                result.add(option);
            }
        }

        return result;
    }

    /**
     * Determines the message for placeholder depending on whether the instructor has any active courses.
     * @param hasActiveCourses true if instructor have active courses
     * @return no active courses or no modify courses' sessions permission message
     */
    private String determinePlaceholderMessage(boolean hasActiveCourses) {
        return hasActiveCourses ? Const.StatusMessages.INSTRUCTOR_NO_MODIFY_PERMISSION_FOR_ACTIVE_COURSES_SESSIONS
                                : Const.StatusMessages.INSTRUCTOR_NO_ACTIVE_COURSES;
    }

    /**
     * Adds the placeholder option to the list of select options if the list is empty.
     * @param selectOptions list containing all the options
     * @param message the message of the placeholder
     */
    private void addPlaceholderIfEmpty(List<ElementTag> selectOptions, String message) {
        if (!selectOptions.isEmpty()) {
            return;
        }

        ElementTag placeholder = createOption(message, "", true);
        selectOptions.add(placeholder);
    }

    /**
     * Retrieves the link to submit the request to remind particular students.
     * Also contains feedbacks page link to return after the action.
     * @return form submit action link
     */
    public String getRemindParticularStudentsLink() {
        return getInstructorFeedbackRemindParticularStudentsLink(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE);
    }

    /**
     * Retrieves the link to submit the request for copy of session.
     * Also contains feedback page link to return after the action.
     * @return form submit action link
     */
    public String getEditCopyActionLink() {
        return getInstructorFeedbackEditCopyActionLink(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE);
    }

    /**
     * Retrieves the link to submit the request for resending the session published email.
     * Also contains feedback page link to return to after the action.
     * @return form submit action link
     */
    public String getSessionResendPublishedEmailLink() {
        return getInstructorFeedbackResendPublishedEmailLink(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE);
    }

    public void setUsingAjax(boolean isUsingAjax) {
        this.isUsingAjax = isUsingAjax;
    }

    private SoftDeletedFeedbackSessionsTable convertToSoftDeletedFeedbackSessionsTable(
            List<FeedbackSessionAttributes> sessions) {
        SoftDeletedFeedbackSessionsTable softDeletedFeedbackSessionsTable = new SoftDeletedFeedbackSessionsTable();

        int idx = -1;

        for (FeedbackSessionAttributes session : sessions) {
            idx++;

            List<ElementTag> actionsParam = new ArrayList<>();

            String restoreLink = getInstructorFeedbackRestoreSoftDeletedSessionLink(session.getCourseId(),
                    session.getSessionName());
            Boolean hasRestorePermission = instructorsForCourses.get(session.getCourseId()).isAllowedForPrivilege(
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
            ElementTag restoreButton = createButton("Restore", "btn btn-default btn-xs t_session_restore" + idx, "",
                    restoreLink, Const.Tooltips.FEEDBACK_SESSION_RESTORE, !hasRestorePermission);

            String deleteLink = getInstructorFeedbackDeleteSoftDeletedSessionLink(session.getCourseId(),
                    session.getSessionName());
            Boolean hasDeletePermission = instructorsForCourses.get(session.getCourseId()).isAllowedForPrivilege(
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
            ElementTag deleteButton = createButton("Delete Permanently", "btn btn-default btn-xs t_session_delete"
                    + idx, "fsDeleteLink", deleteLink, Const.Tooltips.FEEDBACK_SESSION_DELETE, !hasDeletePermission);
            deleteButton.setAttribute("data-course-id", session.getCourseId());
            deleteButton.setAttribute("data-feedback-session-name", session.getSessionName());
            deleteButton.setAttribute("style", "color: red");

            actionsParam.add(restoreButton);
            actionsParam.add(deleteButton);

            SoftDeletedFeedbackSessionsTableRow row = new SoftDeletedFeedbackSessionsTableRow(
                    SanitizationHelper.sanitizeForHtml(session.getCourseId()),
                    SanitizationHelper.sanitizeForHtml(session.getSessionName()),
                    session.getCreatedTimeDateString(),
                    session.getCreatedTimeDateStamp(),
                    session.getCreatedTimeFullDateTimeString(),
                    session.getDeletedTimeDateString(),
                    session.getDeletedTimeDateStamp(),
                    session.getDeletedTimeFullDateTimeString(),
                    this.getInstructorCourseStatsLink(session.getCourseId()),
                    actionsParam);
            softDeletedFeedbackSessionsTable.getRows().add(row);
        }

        return softDeletedFeedbackSessionsTable;
    }

    private ElementTag createButton(String content, String buttonClass, String id, String href, String title,
            boolean isDisabled) {
        ElementTag button = new ElementTag(content);

        button.setAttribute("class", buttonClass);

        if (id != null && !id.isEmpty()) {
            button.setAttribute("id", id);
        }

        if (href != null && !href.isEmpty()) {
            button.setAttribute("href", href);
        }

        if (title != null && !title.isEmpty()) {
            button.setAttribute("title", title);
            button.setAttribute("data-toggle", "tooltip");
            button.setAttribute("data-placement", "top");
        }

        if (isDisabled) {
            button.setAttribute("disabled", null);
        }
        return button;
    }
}
