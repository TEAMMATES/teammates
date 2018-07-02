package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.ui.template.CourseEditInstructorPanel;
import teammates.ui.template.ElementTag;

public class InstructorCourseEditPageData extends PageData {
    private int instructorToShowIndex;
    private ElementTag editCourseButton;
    private ElementTag deleteCourseButton;
    private CourseAttributes course;
    private List<CourseEditInstructorPanel> instructorPanelList;
    private CourseEditInstructorPanel addInstructorPanel;
    private ElementTag addInstructorButton;

    public InstructorCourseEditPageData(AccountAttributes account, String sessionToken, CourseAttributes course,
                                        List<InstructorAttributes> instructorList,
                                        InstructorAttributes currentInstructor, int instructorToShowIndex,
                                        List<String> sectionNames, List<String> feedbackNames) {
        super(account, sessionToken);
        this.course = course;
        //TODO: [CourseAttribute] remove desanitization after data migration
        //creating a new course with possibly desanitized name as course name cannot be accessed directly
        this.course = CourseAttributes
                .builder(course.getId(),
                        SanitizationHelper.desanitizeIfHtmlSanitized(course.getName()),
                        course.getTimeZone())
                 .build();
        this.course.createdAt = course.createdAt;

        this.instructorToShowIndex = instructorToShowIndex;

        createCourseRelatedButtons(currentInstructor);
        boolean isShowingAllInstructors = instructorToShowIndex == -1;
        if (isShowingAllInstructors) {
            instructorPanelList = createInstructorPanelList(currentInstructor, instructorList, sectionNames,
                                                            feedbackNames);
        } else {
            instructorPanelList = createInstructorPanelForSingleInstructor(
                                            currentInstructor, instructorList.get(0), instructorToShowIndex,
                                            sectionNames, feedbackNames);
        }
        addInstructorPanel = createInstructorPanel(currentInstructor, instructorPanelList.size() + 1, null,
                                                   sectionNames, feedbackNames);
    }

    private List<CourseEditInstructorPanel> createInstructorPanelList(InstructorAttributes currentInstructor,
                                           List<InstructorAttributes> instructorList,
                                           List<String> sectionNames, List<String> feedbackNames) {
        List<CourseEditInstructorPanel> panelList = new ArrayList<>();
        int instructorIndex = 0;
        for (InstructorAttributes instructor : instructorList) {
            instructorIndex++;
            CourseEditInstructorPanel instructorPanel = createInstructorPanel(currentInstructor,
                                                                              instructorIndex, instructor,
                                                                              sectionNames, feedbackNames);
            panelList.add(instructorPanel);
        }
        return panelList;
    }

    private List<CourseEditInstructorPanel> createInstructorPanelForSingleInstructor(InstructorAttributes currentInstructor,
                                    InstructorAttributes instructorForPanel, int instructorIndex,
                                    List<String> sectionNames, List<String> feedbackNames) {
        List<CourseEditInstructorPanel> panelList = new ArrayList<>();
        CourseEditInstructorPanel instructorPanel = createInstructorPanel(
                                                            currentInstructor,
                                                            instructorIndex, instructorForPanel,
                                                            sectionNames, feedbackNames);
        panelList.add(instructorPanel);

        return panelList;
    }

    private void createCourseRelatedButtons(InstructorAttributes currentInstructor) {
        boolean isEditDeleteCourseButtonDisabled = !currentInstructor.isAllowedForPrivilege(
                                                       Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);

        editCourseButton = createEditCourseButton(isEditDeleteCourseButtonDisabled);
        deleteCourseButton = createDeleteCourseButton(isEditDeleteCourseButtonDisabled);

        boolean isAddInstructorButtonDisabled = !currentInstructor.isAllowedForPrivilege(
                                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
        addInstructorButton = createAddInstructorButton(isAddInstructorButtonDisabled);
    }

    private CourseEditInstructorPanel createInstructorPanel(InstructorAttributes currentInstructor,
                                                            int instructorIndex,
                                                            InstructorAttributes instructor,
                                                            List<String> sectionNames,
                                                            List<String> feedbackNames) {
        CourseEditInstructorPanel instructorPanel = new CourseEditInstructorPanel(instructorToShowIndex,
                                                                          instructorIndex, instructor,
                                                                          sectionNames, feedbackNames);

        if (instructor == null) {
            instructorPanel.setCancelAddInstructorButton(createCancelAddInstructorButton());
        } else {
            int panelIndex = instructorPanel.getIndex();
            boolean isDisabled = !currentInstructor.isAllowedForPrivilege(
                                         Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);

            if (instructor.googleId == null) {
                instructorPanel.setResendInviteButton(createRemindInstructorButton(instructor, panelIndex, isDisabled));
            }

            instructorPanel.setEditButton(createEditInstructorButton(panelIndex, isDisabled));

            instructorPanel.setCancelButton(createCancelEditInstructorButton(panelIndex, isDisabled));

            instructorPanel.setDeleteButton(createDeleteInstructorButton(instructor, panelIndex, isDisabled));
        }

        return instructorPanel;
    }

    public ElementTag getEditCourseButton() {
        return editCourseButton;
    }

    public ElementTag getDeleteCourseButton() {
        return deleteCourseButton;
    }

    public ElementTag getAddInstructorButton() {
        return addInstructorButton;
    }

    public CourseEditInstructorPanel getAddInstructorPanel() {
        return addInstructorPanel;
    }

    public CourseAttributes getCourse() {
        return course;
    }

    public List<CourseEditInstructorPanel> getInstructorPanelList() {
        return instructorPanelList;
    }

    public int getInstructorToShowIndex() {
        return instructorToShowIndex;
    }

    private ElementTag createAddInstructorButton(boolean isDisabled) {
        // addInstructorButton is an actual <input> button and is thus created differently from the rest of
        // the buttons created with <a> tags
        ElementTag button = new ElementTag();
        button.setAttribute("type", "button");
        button.setAttribute("id", "btnShowNewInstructorForm");
        button.setAttribute("class", "btn btn-primary");
        if (isDisabled) {
            button.setAttribute("disabled", null);
        }

        return button;
    }

    private ElementTag createEditCourseButton(boolean isDisabled) {
        String buttonContent = "<span class=\"glyphicon glyphicon-pencil\"></span> Edit";
        String buttonId = "courseEditLink";
        return createBasicButton(buttonContent, buttonId, "javascript:;", Const.Tooltips.COURSE_INFO_EDIT,
                                 isDisabled);
    }

    private ElementTag createDeleteCourseButton(boolean isDisabled) {
        String buttonContent = "<span class=\"glyphicon glyphicon-trash\"></span> Delete";
        String buttonId = "courseDeleteLink";
        String href = getInstructorCourseDeleteLink(course.getId(), false);

        ElementTag button = createBasicButton(buttonContent, buttonId, href, Const.Tooltips.COURSE_DELETE,
                                              isDisabled);
        button.setAttribute("data-course-id", course.getId());
        String existingClasses = button.removeAttribute("class");
        button.setAttribute("class", existingClasses + " course-delete-link");

        return button;
    }

    private ElementTag createRemindInstructorButton(InstructorAttributes instructor, int panelIndex, boolean isDisabled) {
        String buttonContent = "<span class=\"glyphicon glyphicon-envelope\"></span> Resend Invite";
        String buttonId = "instrRemindLink" + panelIndex;
        String href = getInstructorCourseRemindInstructorLink(instructor.courseId, instructor.email);

        ElementTag button = createBasicButton(buttonContent, buttonId, href, Const.Tooltips.COURSE_INSTRUCTOR_REMIND,
                                              isDisabled);
        button.setAttribute("data-instructor-name", instructor.getName());
        button.setAttribute("data-course-id", instructor.getCourseId());

        return button;
    }

    private ElementTag createDeleteInstructorButton(InstructorAttributes instructor, int panelIndex, boolean isDisabled) {
        String buttonContent = "<span class=\"glyphicon glyphicon-trash\"></span> Delete";
        String buttonId = "instrDeleteLink" + panelIndex;
        String href = getInstructorCourseInstructorDeleteLink(instructor.courseId, instructor.email);
        boolean isDeleteSelf = instructor.email.equals(this.account.email);

        ElementTag button = createBasicButton(buttonContent, buttonId, href, Const.Tooltips.COURSE_INSTRUCTOR_DELETE,
                                              isDisabled);
        button.setAttribute("data-is-delete-self", String.valueOf(isDeleteSelf));
        button.setAttribute("data-instructor-name", instructor.getName());
        button.setAttribute("data-course-id", instructor.getCourseId());

        return button;
    }

    private ElementTag createEditInstructorButton(int panelIndex, boolean isDisabled) {
        String buttonContent = "<span class=\"glyphicon glyphicon-pencil\"></span> Edit";
        String buttonId = "instrEditLink" + panelIndex;

        return createBasicButton(buttonContent, buttonId, "javascript:;", Const.Tooltips.COURSE_INSTRUCTOR_EDIT, isDisabled);
    }

    private ElementTag createCancelEditInstructorButton(int panelIndex, boolean isDisabled) {
        String buttonContent = "<span class=\"glyphicon glyphicon-remove\"></span> Cancel";
        String buttonId = "instrCancelLink" + panelIndex;

        return createBasicButton(buttonContent, buttonId, "javascript:;", Const.Tooltips.COURSE_INSTRUCTOR_CANCEL_EDIT,
                                 isDisabled);
    }

    private ElementTag createCancelAddInstructorButton() {
        String buttonContent = "<span class=\"glyphicon glyphicon-remove\"></span> Cancel";
        String buttonId = "cancelAddInstructorLink";

        return createBasicButton(buttonContent, buttonId, "javascript:;", Const.Tooltips.COURSE_INSTRUCTOR_CANCEL_ADD,
                false);
    }

    /**
     * Creates a basic bootstrap button for use in {@code <a></a>} tags in panel header.
     */
    private ElementTag createBasicButton(String buttonText, String buttonId, String href, String tooltipText,
                                         boolean isDisabled) {
        ElementTag button = new ElementTag(buttonText);
        button.setAttribute("type", "button");
        button.setAttribute("class", "btn btn-primary btn-xs");

        if (buttonId != null) {
            button.setAttribute("id", buttonId);
        }
        if (href != null) {
            button.setAttribute("href", href);
        }
        if (tooltipText != null) {
            button.setAttribute("title", tooltipText);
            button.setAttribute("data-toggle", "tooltip");
            button.setAttribute("data-placement", "top");
        }
        if (isDisabled) {
            button.setAttribute("disabled", null);
        }

        return button;
    }
}
