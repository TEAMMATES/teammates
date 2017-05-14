<%@ tag description="instructorFeedbacks and instructorHome - Feedback Session actions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbacks" prefix="tif" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="actions" type="teammates.ui.template.InstructorFeedbackSessionActions" required="true" %>
<a class="btn btn-default btn-xs btn-tm-actions session-view-for-test margin-bottom-7px"
   href="${actions.resultsLink}"
   title="<%= Const.Tooltips.FEEDBACK_SESSION_RESULTS %>"
   data-toggle="tooltip"
   data-placement="top">
    View Results
</a>
<a class="btn btn-default btn-xs btn-tm-actions session-edit-for-test margin-bottom-7px"
   href="${actions.editLink}" 
   title="<%= Const.Tooltips.FEEDBACK_SESSION_EDIT %>"
   data-toggle="tooltip"
   data-placement="top"
   <c:if test="${not actions.allowedToEdit}">disabled</c:if>>
    Edit
</a>
<a class="btn btn-default btn-xs btn-tm-actions session-delete-for-test margin-bottom-7px"
   href="${actions.deleteLink}"
   title="<%= Const.Tooltips.FEEDBACK_SESSION_DELETE %>"
   data-toggle="tooltip"
   data-placement="top"
   data-courseid="${actions.courseId}"
   data-fsname="${actions.fsName}"
   <c:if test="${not actions.allowedToDelete}">disabled</c:if>>
    Delete
</a>
<div title="<%= Const.Tooltips.FEEDBACK_SESSION_COPY %>" 
     data-toggle="tooltip"
     data-placement="top" 
     style="display: inline-block; padding-right: 5px;">
    <a class="btn btn-default btn-xs btn-tm-actions session-copy-for-test margin-bottom-7px"
       href="#"
       data-actionlink="${actions.editCopyLink}"
       data-courseid="${actions.courseId}"
       data-fsname="${actions.fsName}"
       data-toggle="modal"
       data-target="#fsCopyModal"
       id="button_fscopy-${actions.courseId}-${actions.fsName}">
        Copy
    </a>
</div>
<div title="<%= Const.Tooltips.FEEDBACK_SESSION_SUBMIT %>"
     data-toggle="tooltip"
     data-placement="top"
     style="display: inline-block; padding-right: 5px;">
    <a class="btn btn-default btn-xs btn-tm-actions session-submit-for-test margin-bottom-7px"
       href="${actions.submitLink}"
       <c:if test="${not actions.allowedToSubmit}">disabled</c:if>>
        Submit
    </a>
</div> 
<c:if test="${not actions.privateSession}">
    <div title="<%= Const.Tooltips.FEEDBACK_SESSION_REMIND %>"
         data-toggle="tooltip"
         data-placement="top"
         style="display: inline-block; padding-right: 5px;">
        <div class="btn-group margin-bottom-7px">
            <a class="btn btn-default btn-xs btn-tm-actions session-remind-for-test"
               href="${actions.remindLink}"
               data-fsname="${actions.fsName}"
               <c:if test="${not actions.allowedToRemind}">disabled</c:if>>
                Remind
            </a>
            <button type="button"
                    class="btn btn-default btn-xs btn-tm-actions dropdown-toggle session-remind-options-for-test"
                    data-toggle="dropdown"
                    aria-expanded="false"
                    <c:if test="${not actions.allowedToRemind}">disabled</c:if>>
                <span class="caret"></span>
            </button>
            <ul class="dropdown-menu" role="menu">
                <li>
                    <a href="${actions.remindLink}"
                       class="session-remind-inner-for-test"
                       data-fsname="${actions.fsName}"
                       <c:if test="${not actions.allowedToRemind}">disabled</c:if>>
                        Remind all students
                    </a>
                </li>
                <li>
                    <a href="#"
                       data-actionlink="${actions.remindParticularStudentsPageLink}"
                       class="session-remind-particular-for-test"
                       data-courseid="${actions.courseId}"
                       data-fsname="${actions.fsName}"
                       data-toggle="modal"
                       data-target="#remindModal"
                       <c:if test="${not actions.allowedToRemind}">disabled</c:if>>
                        Remind particular students
                    </a>
                </li>
            </ul>
        </div>
    </div>
    <tif:feedbackSessionPublishButton publishButton="${actions.publishButton}" buttonType="btn-default btn-xs margin-bottom-7px" />
</c:if>
