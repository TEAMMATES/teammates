<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbacks - feedback sessions table/list" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbacks" prefix="tif" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>

<%@ attribute name="fsList" type="teammates.ui.template.FeedbackSessionsTable" required="true"%>

<table class="table-responsive table table-striped table-bordered" id="table-sessions">
  <thead>
    <tr class="fill-primary">
      <th id="button_sortid" class="button-sort-ascending course-id-table-width toggle-sort">
        Course ID <span class="icon-sort unsorted"></span>
      </th>
      <th id="button_sortname" class="button-sort-none session-name-table-width toggle-sort">
        Session Name <span class="icon-sort unsorted"></span>
      </th>
      <th>Submissions</th>
      <th>Responses</th>
      <th>
        <span title="<%= Const.Tooltips.FEEDBACK_SESSION_RESPONSE_RATE %>" data-toggle="tooltip" data-placement="top">
          Response Rate
        </span>
      </th>
      <th class="no-print">Action(s)</th>
    </tr>
  </thead>
  <c:choose>
    <c:when test="${not empty fsList.existingFeedbackSessions}">
      <c:forEach items="${fsList.existingFeedbackSessions}" var="sessionRow" varStatus="i">
        <tr id="session${i.index}" ${sessionRow.rowAttributes.attributesToString}>
          <td>${sessionRow.courseId}</td>
          <td>${sessionRow.name}</td>
          <td>
            <span title="${sessionRow.submissionsTooltip}" data-toggle="tooltip" data-placement="top">
              ${sessionRow.submissionStatus}
            </span>
          </td>
          <td>
            <span title="${sessionRow.publishedTooltip}" data-toggle="tooltip" data-placement="top">
              ${sessionRow.publishedStatus}
            </span>
          </td>
          <td class="session-response-for-test">
            <a oncontextmenu="return false;" href="${sessionRow.href}">Show</a>
          </td>
          <td class="no-print">
            <tif:feedbackSessionActions actions="${sessionRow.actions}" />
          </td>
        </tr>
      </c:forEach>
    </c:when>
    <c:otherwise>
      <tr>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
    </c:otherwise>
  </c:choose>
</table>
<p class="col-md-12 text-muted">Note: The table above doesn't contain sessions from archived courses. To view sessions from an archived course, unarchive the course first.</p>
<br><br><br>
<c:if test="${empty fsList.existingFeedbackSessions}">
  <div class="align-center">No records found.</div>
  <br><br><br>
</c:if>
