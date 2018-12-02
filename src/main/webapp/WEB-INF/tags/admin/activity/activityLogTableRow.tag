<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Activity Log Table in Admin Activity Log Page" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="log" type="teammates.ui.template.AdminActivityLogTableRow" required="true" %>
<%@ tag import="teammates.common.util.Const" %>

<tr>
  <td class="${log.isActionTimeTakenModerate ? "warning"
      : log.isActionTimeTakenSlow ? "danger" : "" }">
    <a class="log-entry" data-logtime="${log.logTime}" data-googleid="${log.userGoogleId}" data-displayedrole="${log.displayedRole}">
      ${log.displayedLogTime}
    </a>
    <p class="localTime"></p>
    <p class="${log.isActionTimeTakenModerate ? "text-warning"
        : log.isActionTimeTakenSlow ? "text-danger" : "" }">
      <strong>${log.displayedLogTimeTaken}</strong>
    </p>
  </td>
  <td class="${log.isActionTimeTakenModerate ? "warning"
      : log.isActionTimeTakenSlow ? "danger" : "" }">
    <form method="get" action="<%= Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE %>">
      <h4 class="list-group-item-heading">
        <c:choose>
          <c:when test="${log.isUserAdmin}">
            <span class="glyphicon glyphicon-user text-danger"></span>
          </c:when>
          <c:when test="${log.isUserInstructor}">
            <span class="glyphicon glyphicon-user text-primary"></span>
          </c:when>
          <c:when test="${log.isUserStudent}">
            <span class="glyphicon glyphicon-user text-warning"></span>
          </c:when>
          <c:when test="${log.isUserAuto}">
            <span class="glyphicon glyphicon-cog"></span>
          </c:when>
          <c:when test="${log.isUserUnregistered}">
            <span class="glyphicon glyphicon-user"></span>
          </c:when>
        </c:choose>
        <c:if test="${log.isMasqueradeUserRole}">
          <span class="glyphicon glyphicon-eye-open text-danger"></span>
        </c:if>
        <a href="${log.displayedActionUrl}"
            class="${log.isActionFailure || log.isActionErrorReport ? "text-danger" : "" }"
            target="_blank">
          ${log.displayedActionName}
        </a>
        <small>
          id: ${log.logId} [${log.userName}
          <c:choose>
            <c:when test="${log.hasUserHomeLink}">
              <a href="${log.userHomeLink}" target="_blank">${log.userGoogleId}</a>
            </c:when>
            <c:otherwise>
              <%-- Display user role for user who don't have home link --%>
              ${log.displayedRole}
            </c:otherwise>
          </c:choose>
          <c:choose>
            <c:when test="${log.hasUserEmail}">
              <a href="mailto:${fn:escapeXml(log.userEmail)}" target="_blank">${fn:escapeXml(log.userEmail)}</a>
            </c:when>
            <c:otherwise>
              <%= Const.ActivityLog.UNKNOWN %>
            </c:otherwise>
          </c:choose>
          ]
        </small>
        <button type="submit" class="btn btn-xs ${log.isActionFailure ? "btn-warning"
            : log.isActionErrorReport ? "btn-danger" : "btn-info"}">
          <span class="glyphicon glyphicon-zoom-in"></span>
        </button>
        <input type="hidden" name="filterQuery" value="person:${fn:escapeXml(log.userIdentity)}">
        <input class="ifShowAll_for_person" type="hidden" name="all" value="false">
        <input class="ifShowTestData_for_person" type="hidden" name="testdata" value="false">
      </h4>
      <div class="log-message">${log.displayedMessage}</div>
    </form>
  </td>
</tr>
