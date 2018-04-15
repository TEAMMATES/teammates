<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/activity" prefix="activity" %>

<c:set var="jsIncludes">
  <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY_HIGHLIGHT %>"></script>
  <script type="text/javascript" src="/js/adminActivityLog.js"></script>
</c:set>

<ta:adminPage title="Admin Activity Log" jsIncludes="${jsIncludes}">
  <activity:filterPanel excludedLogRequestURIs="${data.excludedLogRequestUris}" actionListAsHtml="${data.actionListAsHtml}"
      shouldShowAllLogs="${data.shouldShowAllLogs}" shouldShowTestData="${data.shouldShowTestData}" filterQuery="${data.filterQuery}"
      queryKeywordsForInfo="${data.queryKeywordsForInfo}"/>

  <c:if test="${not empty data.queryMessage}">
    <div class="alert alert-danger" id="queryMessage">
      <span class="glyphicon glyphicon-warning-sign"></span>
      ${data.queryMessage}
    </div>
  </c:if>

  <br> <br>

  <activity:activityLogTable logs="${data.logs}" />

  <t:statusMessage doNotFocusToStatus="${true}" statusMessagesToUser="${data.statusMessagesToUser}" />

  <br>

  <a href="javascript:;" class="back-to-top-left"><span class="glyphicon glyphicon-arrow-up"></span>&nbsp;Top</a>

  <a href="javascript:;" class="back-to-top-right">Top&nbsp;<span class="glyphicon glyphicon-arrow-up"></span></a>

  <br> <br>

</ta:adminPage>
