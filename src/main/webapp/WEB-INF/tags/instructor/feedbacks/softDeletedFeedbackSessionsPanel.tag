<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbacks - Soft-deleted feedback sessions table panel" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbacks" prefix="feedbacks" %>
<%@ tag import="teammates.common.util.Const" %>

<h2 class="text-muted">
  <span class="glyphicon glyphicon-trash"></span> Deleted feedback sessions
</h2>
<div class="panel">
  <div id="softDeletedSessionsHeading" class="panel-heading ajax_submit fill-default" data-target="#softDeletedSessionsBodyCollapse">
    <div class="pull-right margin-left-7px">
      <span class="glyphicon ajax_submit glyphicon-chevron-down"></span>
    </div>
    <a class="btn btn-default btn-xs pull-right pull-down margin-left-7px session-delete-all-link color-negative<c:if test="${not data.instructorAllowedToModify}"> disabled</c:if>"
       id="btn-session-deleteall"
       href="${data.instructorFeedbackDeleteAllSoftDeletedSessionsLink}"
       title="<%= Const.Tooltips.FEEDBACK_SESSION_DELETE_ALL %>"
       data-toggle="tooltip"
       data-placement="top">
      <span class="glyphicon glyphicon-remove"></span>
      <strong>Delete All</strong>
    </a>
    <a class="btn btn-default btn-xs pull-right pull-down<c:if test="${not data.instructorAllowedToModify}"> disabled</c:if>"
       id="btn-session-restoreall"
       href="${data.instructorFeedbackRestoreAllSoftDeletedSessionsLink}"
       title="<%= Const.Tooltips.FEEDBACK_SESSION_RESTORE_ALL %>"
       data-toggle="tooltip"
       data-placement="top">
      <span class="glyphicon glyphicon-ok"></span>
      <strong>Restore All</strong>
    </a>
    <strong class="ajax_submit">
      Recycle Bin
    </strong>
  </div>
  <div id="softDeletedSessionsBodyCollapse" class="panel-collapse collapse">
    <div class="panel-body padding-0">
      <feedbacks:softDeletedFeedbackSessionsTable softDeletedFsList="${data.softDeletedFsList}"/>
    </div>
  </div>
</div>
