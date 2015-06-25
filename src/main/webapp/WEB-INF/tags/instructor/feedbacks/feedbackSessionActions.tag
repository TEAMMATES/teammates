<%@ tag description="instructorFeedbacks and instructorHome - Feedback Session actions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbacks" prefix="tif" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="actions" type="teammates.ui.template.InstructorFeedbackSessionActions" required="true" %>
<a class="btn btn-default btn-xs btn-tm-actions session-view-for-test"
   href="${actions.resultsLink}"
   title="<%= Const.Tooltips.FEEDBACK_SESSION_RESULTS %>"
   data-toggle="tooltip"
   data-placement="top">
    View Results
</a>
<a class="btn btn-default btn-xs btn-tm-actions session-edit-for-test"
   href="${actions.editLink}" 
   title="<%= Const.Tooltips.FEEDBACK_SESSION_EDIT %>"
   data-toggle="tooltip"
   data-placement="top"
   <c:if test="${not actions.allowedToEdit}">disabled="disabled"</c:if>>
    Edit
</a>
<a class="btn btn-default btn-xs btn-tm-actions session-delete-for-test"
   href="${actions.deleteLink}"
   title="<%= Const.Tooltips.FEEDBACK_SESSION_DELETE %>"
   data-toggle="tooltip"
   data-placement="top"
   onclick="return toggleDeleteFeedbackSessionConfirmation(${actions.toggleDeleteFeedbackSessionParams});"
   <c:if test="${not actions.allowedToDelete}">disabled="disabled"</c:if>>
    Delete
</a>
<a class="btn btn-default btn-xs btn-tm-actions session-copy-for-test"
   href="#" 
   title="<%= Const.Tooltips.FEEDBACK_SESSION_COPY %>"
   data-actionlink="${actions.editCopyLink}"
   data-courseid="${actions.courseId}"
   data-fsname="${actions.fsName}"
   data-toggle="modal"
   data-target="#fsCopyModal"
   data-placement="top"
   id="button_fscopy-${actions.courseId}-${actions.fsName}">
    Copy
</a>
<div title="<%= Const.Tooltips.FEEDBACK_SESSION_SUBMIT %>"
     data-toggle="tooltip"
     data-placement="top"
     style="display: inline-block; padding-right: 5px;">
    <a class="btn btn-default btn-xs btn-tm-actions session-submit-for-test<c:if test="${not actions.hasSubmit}"> disabled</c:if>"
       href="${actions.submitLink}"
       <c:if test="${not actions.hasSubmit}">onclick="return false"</c:if>
       <c:if test="${not actions.allowedToSubmit}">disabled="disabled"</c:if>>
        Submit
    </a>
</div> 
<c:if test="${not actions.privateSession}">
    <div title="<%= Const.Tooltips.FEEDBACK_SESSION_REMIND %>"
         data-toggle="tooltip"
         data-placement="top"
         style="display: inline-block; padding-right: 5px;">
        <div class="btn-group">
            <a class="btn btn-default btn-xs btn-tm-actions session-remind-for-test<c:if test="${not actions.allowedToRemind}"> disabled</c:if>"
               href="${actions.remindLink}"
               <c:if test="${actions.allowedToRemind}">onclick="return toggleRemindStudents(${actions.toggleRemindStudentsParams});"</c:if>
               <c:if test="${not actions.allowedToRemind}">onclick="return false"</c:if>
               <c:if test="${not actions.allowedToRemind}">disabled="disabled"</c:if>>
                Remind
            </a>
            <button type="button"
                    class="btn btn-default btn-xs btn-tm-actions dropdown-toggle session-remind-options-for-test"
                    data-toggle="dropdown"
                    aria-expanded="false"
                    <c:if test="${not actions.allowedToRemind}">disabled="disabled"</c:if>>
                <span class="caret"></span>
            </button>
            <ul class="dropdown-menu" role="menu">
                <li>
                    <a href="${actions.remindLink}"
                       class="session-remind-inner-for-test"
                       <c:if test="${actions.hasRemind}">onclick="return toggleRemindStudents(${actions.toggleRemindStudentsParams});"</c:if>
                       <c:if test="${not actions.allowedToRemind}">disabled="disabled"</c:if>>
                        Remind all students
                    </a>
                </li>
                <li>
                    <a href="#"
                       data-actionlink="${actions.remindParticularStudentsLink}"
                       class="session-remind-particular-for-test"
                       data-courseid="${actions.courseId}"
                       data-fsname="${actions.fsName}"
                       data-toggle="modal"
                       data-target="#remindModal"
                       <c:if test="${not actions.allowedToRemind}">disabled="disabled"</c:if>>
                        Remind particular students
                    </a>
                </li>
            </ul>
        </div>
    </div>
    <tif:feedbackSessionPublishButton publishButton="${actions.publishButton}" buttonType="btn-default btn-xs" />
</c:if>
