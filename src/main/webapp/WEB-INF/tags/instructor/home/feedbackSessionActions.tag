<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorHome - Feedback Session actions" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbacks" prefix="tif" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="actions" type="teammates.ui.template.InstructorFeedbackSessionActions" required="true" %>

<a class="btn btn-default btn-xs btn-tm-actions session-edit-for-test margin-bottom-7px<c:if test="${not actions.allowedToEdit}"> disabled</c:if>"
    href="${actions.allowedToEdit ? actions.editLink : 'javascript:;'}"
    title="<%= Const.Tooltips.FEEDBACK_SESSION_EDIT %>"
    data-toggle="tooltip"
    data-placement="top">
  Edit
</a>
<a class="btn btn-default btn-xs btn-tm-actions session-delete-for-test margin-bottom-7px<c:if test="${not actions.allowedToDelete}"> disabled</c:if>"
    href="${actions.allowedToDelete ? actions.deleteLink : 'javascript:;'}"
    title="<%= Const.Tooltips.FEEDBACK_SESSION_MOVE_TO_RECYCLE_BIN %>"
    data-toggle="tooltip"
    data-placement="top"
    data-courseid="${actions.courseId}"
    data-fsname="${actions.fsName}">
  Delete
</a>
<div title="<%= Const.Tooltips.FEEDBACK_SESSION_COPY %>"
    data-toggle="tooltip"
    data-placement="top"
    style="display: inline-block; padding-right: 5px;">
  <a class="btn btn-default btn-xs btn-tm-actions session-copy-for-test margin-bottom-7px"
      href="javascript:;"
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
  <a class="btn btn-default btn-xs btn-tm-actions session-submit-for-test margin-bottom-7px<c:if test="${not actions.allowedToSubmit}"> disabled</c:if>"
      href="${actions.allowedToSubmit ? actions.submitLink : 'javascript:;'}">
    Submit
  </a>
</div>
<div title="<%= Const.Tooltips.FEEDBACK_SESSION_RESULTS %>"
    data-toggle="tooltip"
    data-placement="top"
    style="display: inline-block; padding-right: 5px;">
  <div class="btn-group margin-bottom-7px">
    <a class="btn btn-default btn-xs btn-tm-actions session-view-for-test"
        href="${actions.resultsLink}">
      Results
    </a>
    <button type="button"
        class="btn btn-default btn-xs btn-tm-actions dropdown-toggle session-results-options"
        data-toggle="dropdown"
        aria-expanded="false"
        <c:if test="${not actions.publishButton.actionAllowed}">disabled</c:if>>
      <span class="caret"></span>
    </button>
    <ul class="dropdown-menu" role="menu">
      <li>
        <a class="session-view-for-test" href="${actions.resultsLink}">View Results</a>
      </li>
      <li>
        <tif:feedbackSessionPublishButton publishButton="${actions.publishButton}" showTooltip="false"/>
      </li>
      <c:if test="${actions.allowedToResendPublishedEmail}">
        <li>
          <a href="javascript:;"
              data-actionlink="${actions.sessionResendPublishedEmailPageLink}"
              class="session-resend-published-email-for-test"
              data-courseid="${actions.courseId}"
              data-fsname="${actions.fsName}"
              data-toggle="modal"
              data-target="#resendPublishedEmailModal">
            Resend link to view results
          </a>
        </li>
      </c:if>
      <li>
        <a>
          <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD%>">
            <input type="submit" class="btn-tm-actions session-results-download"
                   name="<%=Const.ParamsNames.FEEDBACK_RESULTS_UPLOADDOWNLOADBUTTON%>" value="Download Results"
                   style="border: none; padding: 0; background: none;">
            <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
            <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="${actions.fsName}">
            <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${actions.courseId}">
            <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS %>" value="true">
            <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES %>" value="true">
          </form>
        </a>
      </li>
    </ul>
  </div>
</div>
<div title="<%= Const.Tooltips.FEEDBACK_SESSION_REMIND %>"
    data-toggle="tooltip"
    data-placement="top"
    style="display: inline-block; padding-right: 5px;">
  <div class="btn-group margin-bottom-7px">
    <a class="btn btn-default btn-xs btn-tm-actions session-remind-for-test<c:if test="${not actions.allowedToRemind}"> disabled</c:if>"
        href="${actions.allowedToRemind ? actions.remindLink : 'javascript:;'}"
        data-fsname="${actions.fsName}">
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
            data-fsname="${actions.fsName}">
          Remind all students
        </a>
      </li>
      <li>
        <a href="javascript:;"
            data-actionlink="${actions.remindParticularStudentsPageLink}"
            class="session-remind-particular-for-test"
            data-courseid="${actions.courseId}"
            data-fsname="${actions.fsName}"
            data-toggle="modal"
            data-target="#remindModal">
          Remind particular students
        </a>
      </li>
    </ul>
  </div>
</div>
